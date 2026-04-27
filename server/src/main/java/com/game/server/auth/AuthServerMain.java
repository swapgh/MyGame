package com.game.server.auth;

import com.game.server.auth.characters.CharacterCreateService;
import com.game.server.auth.characters.CharacterDeleteService;
import com.game.server.auth.characters.CharacterListService;
import com.game.server.auth.database.AccountDao;
import com.game.server.auth.database.AccountRecord;
import com.game.server.auth.database.AuthDatabase;
import com.game.server.auth.database.BanDao;
import com.game.server.auth.database.CharacterDao;
import com.game.server.auth.database.CharacterRecord;
import com.game.server.auth.database.InMemoryAccountDao;
import com.game.server.auth.database.InMemoryBanDao;
import com.game.server.auth.database.InMemoryCharacterDao;
import com.game.server.auth.login.LoginRateLimiter;
import com.game.server.auth.login.LoginService;
import com.game.server.auth.login.PasswordHasher;
import com.game.server.auth.network.AuthPacketHandlers;
import com.game.server.auth.network.AuthPacketRouter;
import com.game.server.auth.network.AuthSocketServer;
import com.game.server.auth.registration.AccountValidator;
import com.game.server.auth.registration.RegistrationService;
import com.game.server.auth.sessions.AuthSessionStore;
import com.game.server.auth.sessions.SessionTokenService;
import com.game.server.shared.config.ServerConfigLoader;
import com.game.server.shared.database.DatabaseConfig;
import com.game.server.shared.database.DatabasePool;
import com.game.server.shared.database.TransactionManager;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Phase 2 entry point for bootstrapping the authentication server.
 *
 * <p>At this stage the class only validates configuration loading and startup wiring. Networking,
 * authentication flow, and persistence are added in later Phase 2 steps.</p>
 *
 * @since 0.1.0
 */
public final class AuthServerMain {
    private static final Path DEFAULT_CONFIG_PATH = Path.of("config", "auth-server.yaml");
    private static final Path DEFAULT_DATABASE_CONFIG_PATH = Path.of("config", "database.yaml");

    private AuthServerMain() {
    }

    /**
     * Loads the authentication server configuration and reports the startup target.
     *
     * @param args optional first argument overriding the config file path
     * @throws IOException if the config file cannot be read
     * @throws InterruptedException if the server wait loop is interrupted
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        Path configPath = args.length > 0 ? Path.of(args[0]) : DEFAULT_CONFIG_PATH;
        AuthServerConfig config = ServerConfigLoader.loadAuthServerConfig(configPath);
        DatabaseConfig databaseConfig = ServerConfigLoader.loadDatabaseConfig(DEFAULT_DATABASE_CONFIG_PATH);
        AuthPacketRouter packetRouter = new AuthPacketRouter();
        AuthDatabase authDatabase = createAuthDatabase(databaseConfig);
        AuthApplication application = createAuthApplication(authDatabase);
        AuthPacketHandlers.register(packetRouter, application);

        try (AuthSocketServer socketServer = new AuthSocketServer(config, packetRouter)) {
            socketServer.start();
            System.out.printf(
                    "Auth server bootstrap ready: %s listening on %s:%d using %s%n",
                    config.name(),
                    config.host(),
                    config.port(),
                    authDatabase.databasePool().jdbcUrl()
            );
            seedDevelopmentData(authDatabase, application.passwordHasher());
            socketServer.awaitShutdown(Duration.ofSeconds(1));
        }
    }

    private static AuthDatabase createAuthDatabase(DatabaseConfig databaseConfig) {
        DatabasePool databasePool = new DatabasePool(databaseConfig);
        TransactionManager transactionManager = new TransactionManager();
        AccountDao accountDao = new InMemoryAccountDao();
        CharacterDao characterDao = new InMemoryCharacterDao();
        BanDao banDao = new InMemoryBanDao();
        return new AuthDatabase(databasePool, transactionManager, accountDao, characterDao, banDao);
    }

    private static AuthApplication createAuthApplication(AuthDatabase authDatabase) {
        PasswordHasher passwordHasher = new PasswordHasher();
        LoginRateLimiter loginRateLimiter = new LoginRateLimiter();
        SessionTokenService sessionTokenService = new SessionTokenService();
        AuthSessionStore authSessionStore = new AuthSessionStore();
        LoginService loginService = new LoginService(
                authDatabase.accountDao(),
                authDatabase.banDao(),
                passwordHasher,
                loginRateLimiter,
                sessionTokenService,
                authSessionStore
        );
        AccountValidator accountValidator = new AccountValidator();
        RegistrationService registrationService = new RegistrationService(
                authDatabase.accountDao(),
                accountValidator,
                passwordHasher
        );
        CharacterListService characterListService = new CharacterListService(authDatabase.characterDao());
        CharacterCreateService characterCreateService = new CharacterCreateService(authDatabase.characterDao());
        CharacterDeleteService characterDeleteService = new CharacterDeleteService(authDatabase.characterDao());

        return new AuthApplication(
                authDatabase,
                passwordHasher,
                loginRateLimiter,
                sessionTokenService,
                authSessionStore,
                loginService,
                accountValidator,
                registrationService,
                characterListService,
                characterCreateService,
                characterDeleteService
        );
    }

    private static void seedDevelopmentData(AuthDatabase authDatabase, PasswordHasher passwordHasher) {
        AccountDao accountDao = authDatabase.accountDao();
        CharacterDao characterDao = authDatabase.characterDao();

        AccountRecord devAccount = accountDao.findByUsername("dev").orElseGet(() ->
                accountDao.save(new AccountRecord(0L, "dev", passwordHasher.hash("dev-password"), false))
        );
        if (characterDao.findByAccountId(devAccount.id()).isEmpty()) {
            characterDao.save(new CharacterRecord(0L, devAccount.id(), "DevKnight"));
        }
    }

    /**
     * Small bootstrap bundle for auth application collaborators.
     *
     * @param authDatabase the auth database bundle
     * @param passwordHasher the password hasher
     * @param loginRateLimiter the login rate limiter
     * @param sessionTokenService the session token generator
     * @param authSessionStore the in-memory auth session store
     * @param loginService the login service
     * @param accountValidator the registration validator
     * @param registrationService the registration service
     * @param characterListService the character list service
     * @param characterCreateService the character create service
     * @param characterDeleteService the character delete service
     * @since 0.1.0
     */
    public record AuthApplication(
            AuthDatabase authDatabase,
            PasswordHasher passwordHasher,
            LoginRateLimiter loginRateLimiter,
            SessionTokenService sessionTokenService,
            AuthSessionStore authSessionStore,
            LoginService loginService,
            AccountValidator accountValidator,
            RegistrationService registrationService,
            CharacterListService characterListService,
            CharacterCreateService characterCreateService,
            CharacterDeleteService characterDeleteService
    ) {
    }
}
