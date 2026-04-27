package com.game.shared.util;

/**
 * Small validation result used for shared input and configuration checks.
 *
 * @param valid whether validation succeeded
 * @param message the validation message, usually empty on success
 * @since 0.1.0
 */
public record Validation(boolean valid, String message) {
    /**
     * Returns a successful validation with no error message.
     *
     * @return a successful validation result
     */
    public static Validation ok() {
        return new Validation(true, "");
    }

    /**
     * Returns a failed validation with a human-readable message.
     *
     * @param message the failure message
     * @return a failed validation result
     */
    public static Validation failure(String message) {
        return new Validation(false, message);
    }
}
