package com.game.server.auth.login;

import com.game.server.auth.database.AccountDao;
import com.game.server.auth.database.AccountRecord;
import com.game.server.auth.database.BanDao;
import com.game.server.auth.security.PasswordHasher;
import com.game.server.auth.sessions.AuthSession;
import com.game.server.auth.sessions.AuthSessionStore;
import com.game.server.auth.sessions.SessionTokenService;
import com.game.shared.util.Result;

import java.time.Instant;
import java.util.Optional;

/**
 * Coordinates the high-level login flow for the auth server.
 * <p>In this early Phase 2 version the service focuses on session issuance and shared shape. DAO
 * integration and real account validation are added in later steps.</p>
 * @since 0.1.0
 */
public final class LoginService {
    private final AccountDao accountDao;
    private final BanDao banDao;
    private final PasswordHasher passwordHasher;
    private final LoginRateLimiter rateLimiter;
    private final SessionTokenService tokenService;
    private final AuthSessionStore sessionStore;

    /**
     * Creates a login service with the required collaborators.
     * @param accountDao the account dao
     * @param banDao the ban dao
     * @param passwordHasher the password hasher
     * @param rateLimiter the login rate limiter
     * @param tokenService the session token generator
     * @param sessionStore the session storage
     */
    public LoginService(
            AccountDao accountDao,
            BanDao banDao,
            PasswordHasher passwordHasher,
            LoginRateLimiter rateLimiter,
            SessionTokenService tokenService,
            AuthSessionStore sessionStore
    ) {
        this.accountDao = accountDao;
        this.banDao = banDao;
        this.passwordHasher = passwordHasher;
        this.rateLimiter = rateLimiter;
        this.tokenService = tokenService;
        this.sessionStore = sessionStore;
    }
    /**
     * Issues a new session when a login attempt is accepted.
     * @param clientKey the client identifier used for rate limiting
     * @param username the username supplied by the client
     * @param password the raw password supplied by the client
     * @return a successful session result or a login failure
     */
    public Result<AuthSession, LoginResult> login(String clientKey, String username, String password) {
        if (!rateLimiter.allow(clientKey)) {
            return Result.failure(LoginResult.RATE_LIMITED);
        }
        Optional<AccountRecord> account = accountDao.findByUsername(username);
        if (account.isEmpty()) {
            return Result.failure(LoginResult.INVALID_CREDENTIALS);
        }
        if (account.get().locked()) {
            return Result.failure(LoginResult.ACCOUNT_LOCKED);
        }
        if (banDao.findByAccountId(account.get().id()).isPresent()) {
            return Result.failure(LoginResult.ACCOUNT_LOCKED);
        }
        if (!passwordHasher.matches(password, account.get().passwordHash())) {
            return Result.failure(LoginResult.INVALID_CREDENTIALS);
        }

        String token = tokenService.generate();
        AuthSession session = new AuthSession(token, account.get().id(), Instant.now());
        sessionStore.put(session);
        return Result.success(session);
    }
}
