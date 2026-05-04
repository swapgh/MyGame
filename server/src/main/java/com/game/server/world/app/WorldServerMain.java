package com.game.server.world.app;

import com.game.server.ecs.WorldContext;
import com.game.server.ecs.entity.EntityManager;
import com.game.server.ecs.system.SystemRegistry;
import com.game.server.components.npc.NpcComponent;
import com.game.server.systems.combat.CombatSystem;
import com.game.server.systems.loot.LootDropSystem;
import com.game.server.systems.npc.NpcAiSystem;
import com.game.server.systems.npc.RespawnSystem;
import com.game.server.systems.world.CollisionSystem;
import com.game.server.systems.world.EmptyWorldSystem;
import com.game.server.systems.world.MovementSystem;
import com.game.server.systems.world.SnapshotSystem;
import com.game.server.world.config.WorldServerConfig;
import com.game.server.items.definition.ItemDefinition;
import com.game.server.items.definition.ItemDefinitionLoader;
import com.game.server.loot.definition.LootTableDefinition;
import com.game.server.loot.definition.LootTableLoader;
import com.game.server.npc.definition.NpcDefinition;
import com.game.server.npc.definition.NpcDefinitionLoader;
import com.game.server.npc.definition.NpcSpawnEntry;
import com.game.server.npc.definition.NpcSpawnEntryLoader;
import com.game.server.service.VendorInteractionService;
import com.game.server.world.inventory.InventoryService;
import com.game.server.world.loop.WorldGameLoop;
import com.game.server.world.factories.NpcFactory;
import com.game.server.world.map.MapLoader;
import com.game.server.world.map.World;
import com.game.server.world.map.ZoneLoader;
import com.game.server.world.network.WorldConnectionManager;
import com.game.server.world.network.WorldPacketHandlers;
import com.game.server.world.network.WorldPacketRouter;
import com.game.server.world.network.WorldSocketServer;
import com.game.server.config.ServerConfigLoader;
import com.game.shared.time.TickRate;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
/**
 * Phase 3 entry point for bootstrapping the world server.
 * <p>Wires together the ECS core, game loop, zone loader, and network layer, then
 * starts ticking. The world is empty in Phase 3; player entities and movement are
 * added in Phase 5.</p>
 * @since 0.1.0
 */
public final class WorldServerMain {
    private static final Path DEFAULT_CONFIG_PATH = Path.of("config", "world-server.yaml");
    private static final Path NPC_DEFINITIONS_PATH = Path.of("data", "npcs", "npc-definitions.json");
    private static final Path NPC_SPAWN_TABLES_PATH = Path.of("data", "npcs", "spawn-tables.json");
    private static final Path LOOT_TABLES_PATH = Path.of("data", "items", "loot-tables.json");
    private static final Path ITEM_DEFINITIONS_PATH = Path.of("data", "items", "item-definitions.json");

    private WorldServerMain() {
    }
    /**
     * Loads configuration, wires the world application, and starts the server.
     * @param args optional first argument overriding the config file path
     * @throws IOException          if the config file cannot be read
     * @throws InterruptedException if the server wait loop is interrupted
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        Path configPath = args.length > 0 ? Path.of(args[0]) : DEFAULT_CONFIG_PATH;
        WorldServerConfig config = ServerConfigLoader.loadWorldServerConfig(configPath);
        Map<String, NpcDefinition> npcDefinitions = new NpcDefinitionLoader().load(NPC_DEFINITIONS_PATH);
        List<NpcSpawnEntry> npcSpawnEntries = new NpcSpawnEntryLoader().load(NPC_SPAWN_TABLES_PATH);
        Map<String, LootTableDefinition> lootTables = new LootTableLoader().load(LOOT_TABLES_PATH);
        Map<String, ItemDefinition> itemDefinitions = new ItemDefinitionLoader().load(ITEM_DEFINITIONS_PATH);
        InventoryService inventoryService = new InventoryService(itemDefinitions);
        VendorInteractionService vendorInteractionService = new VendorInteractionService();

        EntityManager entityManager = new EntityManager();
        SystemRegistry systemRegistry = new SystemRegistry();
        WorldConnectionManager connectionManager = new WorldConnectionManager();
        systemRegistry.register(new MovementSystem());
        systemRegistry.register(new CollisionSystem());
        systemRegistry.register(new NpcAiSystem());
        systemRegistry.register(new CombatSystem());
        systemRegistry.register(new LootDropSystem(itemDefinitions));
        systemRegistry.register(new RespawnSystem());
        systemRegistry.register(new SnapshotSystem(connectionManager));
        systemRegistry.register(new EmptyWorldSystem());
        TickRate tickRate = new TickRate(config.ticksPerSecond());
        WorldGameLoop gameLoop = new WorldGameLoop(tickRate, entityManager, systemRegistry);

        ZoneLoader zoneLoader = new ZoneLoader();
        zoneLoader.load();
        MapLoader mapLoader = new MapLoader(16);
        World world = mapLoader.loadWorld(zoneLoader);
        WorldContext worldContext = new WorldContext(
                entityManager,
                systemRegistry,
                zoneLoader,
                world,
                npcDefinitions,
                npcSpawnEntries,
                lootTables,
                itemDefinitions
        );
        spawnInitialNpcs(entityManager, worldContext);

        WorldPacketRouter packetRouter = new WorldPacketRouter();
        WorldApplication application = new WorldApplication(
                worldContext,
                gameLoop,
                packetRouter,
                connectionManager,
                inventoryService,
                vendorInteractionService
        );
        WorldPacketHandlers.register(packetRouter, application);

        gameLoop.start();

        try (WorldSocketServer socketServer = new WorldSocketServer(
                config,
                packetRouter,
                connectionManager,
                entityManager
        )) {
            socketServer.start();
            System.out.printf(
                    "World server ready: %s listening on %s:%d at %d ticks/s — %d zone(s) loaded, %d area grid(s), %d NPC(s)%n",
                    config.name(),
                    config.host(),
                    config.port(),
                    config.ticksPerSecond(),
                    worldContext.zoneLoader().count(),
                    worldContext.world().zoneCount(),
                    worldContext.entityManager().storeOf(NpcComponent.class).size()
            );
            socketServer.awaitShutdown(Duration.ofSeconds(1));
        } finally {
            gameLoop.stop();
        }
    }

    private static void spawnInitialNpcs(EntityManager entityManager, WorldContext worldContext) {
        NpcFactory npcFactory = new NpcFactory();
        for (NpcSpawnEntry entry : worldContext.npcSpawnEntries()) {
            NpcDefinition definition = worldContext.npcDefinitions().get(entry.npcId());
            LootTableDefinition lootTable = definition == null || definition.lootTableId().isBlank()
                    ? null
                    : worldContext.lootTables().get(definition.lootTableId());
            if (definition == null || worldContext.world().findZone(entry.zoneId()).isEmpty()) {
                continue;
            }
            if ((definition.entityType() == com.game.shared.protocol.world.EntityType.ENEMY
                    || definition.entityType() == com.game.shared.protocol.world.EntityType.NPC)
                    && lootTable == null) {
                continue;
            }

            for (int index = 0; index < entry.count(); index++) {
                float offset = entry.spacing() * index;
                npcFactory.createNpc(
                        entityManager,
                        definition,
                        entry,
                        new com.game.shared.math.Vec2(entry.spawnX() + offset, entry.spawnY()),
                        lootTable
                );
            }
        }
    }
    /**
     * Bootstrap bundle for world application collaborators.
     * @param worldContext   the shared world context
     * @param gameLoop       the fixed-timestep game loop
     * @param packetRouter   the world packet router
     * @param connectionManager the world connection manager
     * @param inventoryService the authoritative inventory service
     * @param vendorInteractionService vendor interaction service
     * @since 0.1.0
     */
    public record WorldApplication(
            WorldContext worldContext,
            WorldGameLoop gameLoop,
            WorldPacketRouter packetRouter,
            WorldConnectionManager connectionManager,
            InventoryService inventoryService,
            VendorInteractionService vendorInteractionService
    ) {
    }
}
