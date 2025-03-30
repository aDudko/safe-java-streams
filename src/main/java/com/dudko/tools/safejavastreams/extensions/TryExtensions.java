package com.dudko.tools.safejavastreams.extensions;

import com.dudko.tools.safejavastreams.core.Try;
import com.dudko.tools.safejavastreams.function.ThrowingSupplier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public final class TryExtensions {

    private TryExtensions() {
        throw new AssertionError("No instances for you!");
    }

    /**
     * Converts an Optional to Try. Returns Failure if empty.
     *
     * @param optional the optional value
     * @param ifEmpty exception supplier if optional is empty
     * @param <T> type of the value
     * @return Try representing the result
     */
    public static <T> Try<T> fromOptional(Optional<T> optional, Supplier<? extends Throwable> ifEmpty) {
        return optional.map(Try::success)
                .orElseGet(() -> Try.failure(ifEmpty.get()));
    }

    /**
     * Wraps a ThrowingSupplier into Try.
     *
     * @param supplier the function that might throw
     * @param <T> result type
     * @return Try with result or exception
     */
    public static <T> Try<T> from(ThrowingSupplier<T> supplier) {
        return Try.of(supplier);
    }

    /**
     * Converts a CompletableFuture into Try synchronously.
     *
     * @param future the future
     * @param <T> type of result
     * @return Try with result or exception
     */
    public static <T> Try<T> fromFuture(CompletableFuture<T> future) {
        try {
            return Try.success(future.get());
        } catch (Throwable t) {
            Throwable cause = (t instanceof ExecutionException && t.getCause() != null) ? t.getCause() : t;
            return Try.failure(cause);
        }
    }

}
