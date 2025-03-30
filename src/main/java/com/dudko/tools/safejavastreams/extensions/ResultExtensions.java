package com.dudko.tools.safejavastreams.extensions;

import com.dudko.tools.safejavastreams.core.Result;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility methods to convert from other types to {@link Result}.
 */
public final class ResultExtensions {

    private ResultExtensions() {
        throw new AssertionError("No instances for you!");
    }

    /**
     * Converts an {@link Optional} to a {@link Result}.
     *
     * @param optional the optional value
     * @param errorSupplier supplier for error if value is empty
     * @param <T> type of value
     * @param <E> type of error
     * @return Result with value or error
     */
    public static <T, E> Result<T, E> fromOptional(Optional<T> optional, Supplier<? extends E> errorSupplier) {
        return optional.map(Result::<T, E>success)
                .orElseGet(() -> Result.failure(errorSupplier.get()));
    }

    /**
     * Converts a {@link CompletableFuture} into a {@link Result}. Blocks the current thread.
     *
     * @param future the future
     * @param errorMapper mapper from Throwable to error type
     * @param <T> type of value
     * @param <E> type of error
     * @return Result from future
     */
    public static <T, E> Result<T, E> fromFuture(CompletableFuture<T> future, Function<Throwable, ? extends E> errorMapper) {
        try {
            return Result.success(future.get());
        } catch (Throwable t) {
            Throwable cause = (t instanceof ExecutionException && t.getCause() != null) ? t.getCause() : t;
            return Result.failure(errorMapper.apply(cause));
        }
    }

    /**
     * Wraps a supplier that may throw, producing a {@link Result}.
     *
     * @param supplier the supplier to execute
     * @param errorMapper a function mapping a {@link Throwable} to an error value
     * @param <T> the result type
     * @param <E> the error type
     * @return a successful {@link Result} with the supplier's value, or a failed {@link Result} with the mapped error
     */
    public static <T, E> Result<T, E> fromSupplier(Supplier<T> supplier, Function<Throwable, ? extends E> errorMapper) {
        try {
            return Result.success(supplier.get());
        } catch (Throwable t) {
            return Result.failure(errorMapper.apply(t));
        }
    }

    /**
     * Wraps a supplier that may throw, producing a {@link Result}.
     * Alias for {@link #fromSupplier(Supplier, Function)}.
     *
     * @param supplier the supplier
     * @param errorMapper mapper from Throwable to error type
     * @param <T> type of value
     * @param <E> type of error
     * @return Result with value or error
     */
    public static <T, E> Result<T, E> fromChecked(Supplier<T> supplier, Function<Throwable, ? extends E> errorMapper) {
        return fromSupplier(supplier, errorMapper);
    }
}
