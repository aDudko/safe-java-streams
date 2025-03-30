package com.dudko.tools.safejavastreams.extensions;

import com.dudko.tools.safejavastreams.core.Either;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility methods to convert from other types to {@link Either}.
 */
public final class EitherExtensions {

    private EitherExtensions() {
        throw new AssertionError("No instances for you!");
    }

    /**
     * Converts an {@link Optional} to an {@link Either}.
     *
     * @param optional the optional value
     * @param leftSupplier supplier for the Left value if Optional is empty
     * @param <L> type of Left (error)
     * @param <R> type of Right (success)
     * @return Either with Right if value is present, Left otherwise
     */
    public static <L, R> Either<L, R> fromOptional(Optional<R> optional, Supplier<? extends L> leftSupplier) {
        return optional.map(Either::<L, R>right)
                .orElseGet(() -> Either.left(leftSupplier.get()));
    }

    /**
     * Converts a {@link CompletableFuture} to an {@link Either}. Blocks the current thread.
     *
     * @param future the future
     * @param errorMapper function to convert exceptions to Left
     * @param <L> type of Left (error)
     * @param <R> type of Right (success)
     * @return Either with result or mapped error
     */
    public static <L, R> Either<L, R> fromFuture(
            CompletableFuture<R> future,
            Function<Throwable, ? extends L> errorMapper) {
        try {
            return Either.right(future.get());
        } catch (Throwable t) {
            Throwable root = t.getCause() != null ? t.getCause() : t;
            return Either.left(errorMapper.apply(root));
        }
    }

    /**
     * Wraps a {@link Supplier} that may throw, producing an {@link Either}.
     *
     * @param supplier the supplier
     * @param errorMapper mapper from exception to Left value
     * @param <L> type of Left (error)
     * @param <R> type of Right (success)
     * @return Either with result or mapped error
     */
    public static <L, R> Either<L, R> fromChecked(Supplier<R> supplier, Function<Throwable, ? extends L> errorMapper) {
        try {
            return Either.right(supplier.get());
        } catch (Throwable t) {
            return Either.left(errorMapper.apply(t));
        }
    }

    /**
     * Wraps a supplier that may throw, producing an {@link Either}.
     *
     * @param supplier the supplier to execute
     * @param errorMapper a function mapping a {@link Throwable} to an error value
     * @param <L> the error type (Left)
     * @param <R> the result type (Right)
     * @return a successful {@link Either} with the supplier's value, or a failed {@link Either} with the mapped error
     */
    public static <L, R> Either<L, R> fromSupplier(Supplier<R> supplier, Function<Throwable, ? extends L> errorMapper) {
        try {
            return Either.right(supplier.get());
        } catch (Throwable t) {
            return Either.left(errorMapper.apply(t));
        }
    }

}
