package com.game.server.auth.registration;

import com.game.server.auth.database.AccountDao;
import com.game.server.auth.database.AccountRecord;
import com.game.server.auth.login.PasswordHasher;
import com.game.shared.util.Result;
import com.game.shared.util.Validation;

/**
 * Coordinates the early registration flow.
 *
 * @since 0.1.0
 */
public final class RegistrationService {
    private final AccountDao accountDao;
    private final AccountValidator accountValidator;
    private final PasswordHasher passwordHasher;

    /**
     * Creates a registration service with its validator dependency.
     *
     * @param accountDao the account dao
     * @param accountValidator the account validator
     * @param passwordHasher the password hasher
     */
    public RegistrationService(
            AccountDao accountDao,
            AccountValidator accountValidator,
            PasswordHasher passwordHasher
    ) {
        this.accountDao = accountDao;
        this.accountValidator = accountValidator;
        this.passwordHasher = passwordHasher;
    }

    /**
     * Validates registration inputs.
     *
     * @param username the username to validate
     * @param password the password to validate
     * @return the first failing validation or success when both inputs are acceptable
     */
    public Validation validateRegistration(String username, String password) {
        Validation usernameValidation = accountValidator.validateUsername(username);
        if (!usernameValidation.valid()) {
            return usernameValidation;
        }
        Validation passwordValidation = accountValidator.validatePassword(password);
        if (!passwordValidation.valid()) {
            return passwordValidation;
        }
        if (accountDao.findByUsername(username).isPresent()) {
            return Validation.failure("Username is already taken");
        }
        return Validation.ok();
    }

    /**
     * Registers a new account when validation succeeds.
     *
     * @param username the requested username
     * @param password the requested password
     * @return the persisted account or a validation error
     */
    public Result<AccountRecord, String> register(String username, String password) {
        Validation validation = validateRegistration(username, password);
        if (!validation.valid()) {
            return Result.failure(validation.message());
        }

        AccountRecord account = new AccountRecord(0L, username, passwordHasher.hash(password), false);
        return Result.success(accountDao.save(account));
    }
}
