package com.game.shared.util;

import java.util.Objects;
import java.util.function.Function;

/**
 * Minimal success-or-failure container used in shared code without external dependencies.
 *
 * @param <T> the success value type
 * @param <E> the error value type
 * @since 0.1.0
 */
public final class Result<T, E> {
    private final T value;
    private final E error;

    private Result(T value, E error) {
        this.value = value;
        this.error = error;
    }

    /**
     * Creates a successful result containing a value.
     *
     * @param value the success value
     * @param <T> the success value type
     * @param <E> the error value type
     * @return a successful result
     */
    public static <T, E> Result<T, E> success(T value) {
        return new Result<>(Objects.requireNonNull(value, "value"), null);
    }

    /**
     * Creates a failed result containing an error.
     *
     * @param error the failure value
     * @param <T> the success value type
     * @param <E> the error value type
     * @return a failed result
     */
    public static <T, E> Result<T, E> failure(E error) {
        return new Result<>(null, Objects.requireNonNull(error, "error"));
    }

    /**
     * Returns {@code true} when this result contains a value.
     *
     * @return {@code true} if this result is successful
     */
    public boolean isSuccess() {
        return error == null;
    }

    /**
     * Returns {@code true} when this result contains an error.
     *
     * @return {@code true} if this result is a failure
     */
    public boolean isFailure() {
        return error != null;
    }

    /**
     * Returns the successful value or throws if this result is a failure.
     *
     * @return the successful value
     * @throws IllegalStateException if this result is a failure
     */
    public T value() {
        if (isFailure()) {
            throw new IllegalStateException("Cannot read value from a failure result");
        }
        return value;
    }

    /**
     * Returns the failure value or throws if this result is a success.
     *
     * @return the failure value
     * @throws IllegalStateException if this result is a success
     */
    public E error() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot read error from a success result");
        }
        return error;
    }

    /**
     * Maps a successful value while preserving any failure unchanged.
     *
     * @param mapper the mapper applied to the success value
     * @param <R> the mapped success value type
     * @return a mapped success result or the original failure
     */
    public <R> Result<R, E> map(Function<T, R> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        if (isFailure()) {
            return Result.failure(error);
        }
        return Result.success(mapper.apply(value));
    }
}
