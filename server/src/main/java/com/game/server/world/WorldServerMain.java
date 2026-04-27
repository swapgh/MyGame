package com.game.server.world;

import com.game.server.world.ecs.EntityManager;
import com.game.server.world.ecs.SystemRegistry;
import com.game.server.world.loop.GameLoop;
import com.game.server.world.map.ZoneLoader;
import com.game.server.world.network.WorldPacketHandlers;
import com.game.server.world.network.WorldPacketRouter;
import com.game.server.world.network.WorldSocketServer;
import com.game.server.shared.config.ServerConfigLoader;
import com.game.shared.time.TickRate;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
/**
 * Phase 3 entry point for bootstrapping the world server.
 * <p>Wires together the ECS core, game loop, zone loader, and network layer, then
 * starts ticking. The world is empty in Phase 3; player entities and movement are
 * added in Phase 5.</p>
 * @since 0.1.0
 */
public final class WorldServerMain {
    private static final Path DEFAULT_CONFIG_PATH = Path.of("config", "world-server.yaml");

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

        EntityManager entityManager = new EntityManager();
        SystemRegistry systemRegistry = new SystemRegistry();
        TickRate tickRate = new TickRate(config.ticksPerSecond());
        GameLoop gameLoop = new GameLoop(tickRate, entityManager, systemRegistry);

        ZoneLoader zoneLoader = new ZoneLoader();
        zoneLoader.load();

        WorldPacketRouter packetRouter = new WorldPacketRouter();
        WorldApplication application = new WorldApplication(
                entityManager,
                systemRegistry,
                gameLoop,
                zoneLoader
        );
        WorldPacketHandlers.register(packetRouter, application);

        gameLoop.start();

        try (WorldSocketServer socketServer = new WorldSocketServer(config, packetRouter)) {
            socketServer.start();
            System.out.printf(
                    "World server ready: %s listening on %s:%d at %d ticks/s — %d zone(s) loaded%n",
                    config.name(),
                    config.host(),
                    config.port(),
                    config.ticksPerSecond(),
                    zoneLoader.count()
            );
            socketServer.awaitShutdown(Duration.ofSeconds(1));
        } finally {
            gameLoop.stop();
        }
    }
    /**
     * Bootstrap bundle for world application collaborators.
     * @param entityManager  the shared entity manager
     * @param systemRegistry the ordered system registry
     * @param gameLoop       the fixed-timestep game loop
     * @param zoneLoader     the loaded zone registry
     * @since 0.1.0
     */
    public record WorldApplication(
            EntityManager entityManager,
            SystemRegistry systemRegistry,
            GameLoop gameLoop,
            ZoneLoader zoneLoader
    ) {
    }
}