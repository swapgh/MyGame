package com.game.server.world.app;

import com.game.server.world.ecs.EntityManager;
import com.game.server.world.ecs.SystemRegistry;
import com.game.server.world.ecs.WorldContext;
import com.game.server.world.config.WorldServerConfig;
import com.game.server.world.definitions.LootTableDefinition;
import com.game.server.world.definitions.NpcDefinition;
import com.game.server.world.definitions.NpcSpawnEntry;
import com.game.server.world.definitions.WorldDefinitionLoader;
import com.game.server.world.loop.WorldGameLoop;
import com.game.server.world.factories.NpcFactory;
import com.game.server.world.map.MapLoader;
import com.game.server.world.map.World;
import com.game.server.world.map.ZoneLoader;
import com.game.server.world.network.WorldConnectionManager;
import com.game.server.world.network.WorldPacketHandlers;
import com.game.server.world.network.WorldPacketRouter;
import com.game.server.world.network.WorldSocketServer;
import com.game.server.world.systems.CollisionSystem;
import com.game.server.world.systems.CombatSystem;
import com.game.server.world.systems.EmptyWorldSystem;
import com.game.server.world.systems.LootDropSystem;
import com.game.server.world.systems.MovementSystem;
import com.game.server.world.systems.NpcAiSystem;
import com.game.server.world.systems.RespawnSystem;
import com.game.server.world.systems.SnapshotSystem;
import com.game.server.shared.config.ServerConfigLoader;
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
        WorldDefinitionLoader definitionLoader = new WorldDefinitionLoader();
        Map<String, NpcDefinition> npcDefinitions = definitionLoader.loadNpcDefinitions(NPC_DEFINITIONS_PATH);
        List<NpcSpawnEntry> npcSpawnEntries = definitionLoader.loadNpcSpawnEntries(NPC_SPAWN_TABLES_PATH);
        Map<String, LootTableDefinition> lootTables = definitionLoader.loadLootTables(LOOT_TABLES_PATH);

        EntityManager entityManager = new EntityManager();
        SystemRegistry systemRegistry = new SystemRegistry();
        WorldConnectionManager connectionManager = new WorldConnectionManager();
        systemRegistry.register(new MovementSystem());
        systemRegistry.register(new CollisionSystem());
        systemRegistry.register(new NpcAiSystem());
        systemRegistry.register(new CombatSystem());
        systemRegistry.register(new LootDropSystem());
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
                lootTables
        );
        spawnInitialNpcs(entityManager, worldContext);

        WorldPacketRouter packetRouter = new WorldPacketRouter();
        WorldApplication application = new WorldApplication(
                worldContext,
                gameLoop,
                packetRouter,
                connectionManager
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
                    worldContext.entityManager().storeOf(com.game.server.world.components.NpcComponent.class).size()
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
            LootTableDefinition lootTable = definition == null ? null : worldContext.lootTables().get(definition.lootTableId());
            if (definition == null || lootTable == null || worldContext.world().findZone(entry.zoneId()).isEmpty()) {
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
     * @since 0.1.0
     */
    public record WorldApplication(
            WorldContext worldContext,
            WorldGameLoop gameLoop,
            WorldPacketRouter packetRouter,
            WorldConnectionManager connectionManager
    ) {
    }
}
