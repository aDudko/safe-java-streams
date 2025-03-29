package com.dudko.tools.safejavastreams;

import java.util.Optional;
import java.util.function.*;

/**
 * Utility class for working with lambdas and stream functions that throw checked exceptions.
 * <p>
 * This enables using Stream API and functional interfaces in a safer and cleaner way
 * without wrapping try/catch manually in every lambda.
 */
public class StreamExceptionUtils {

    /**
     * Like {@code Function<T, R>} but allows checked exceptions.
     *
     * @param <T> input type
     * @param <R> result type
     */
    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        /**
         * Applies the function.
         *
         * @param t input value
         * @return result
         * @throws Exception if any error occurs
         */
        R apply(T t) throws Exception;
    }

    /**
     * Like {@code BiFunction<T, U, R>} but allows checked exceptions.
     *
     * @param <T> first input
     * @param <U> second input
     * @param <R> result
     */
    @FunctionalInterface
    public interface ThrowingBiFunction<T, U, R> {
        /**
         * Applies the function.
         *
         * @param t first value
         * @param u second value
         * @return result
         * @throws Exception if any error occurs
         */
        R apply(T t, U u) throws Exception;
    }

    /**
     * Like {@code Consumer<T>} but allows checked exceptions.
     *
     * @param <T> consumed type
     */
    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        /**
         * Performs the action.
         *
         * @param t value
         * @throws Exception if any error occurs
         */
        void accept(T t) throws Exception;
    }

    /**
     * Like {@code BiConsumer<T, U>} but allows checked exceptions.
     *
     * @param <T> first input
     * @param <U> second input
     */
    @FunctionalInterface
    public interface ThrowingBiConsumer<T, U> {
        /**
         * Performs the action.
         *
         * @param t first value
         * @param u second value
         * @throws Exception if any error occurs
         */
        void accept(T t, U u) throws Exception;
    }

    /**
     * Like {@code Supplier<T>} but allows checked exceptions.
     *
     * @param <T> result type
     */
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        /**
         * Supplies a value.
         *
         * @return result
         * @throws Exception if any error occurs
         */
        T get() throws Exception;
    }

    /**
     * Like {@code Runnable} but allows checked exceptions.
     */
    @FunctionalInterface
    public interface ThrowingRunnable {
        /**
         * Executes the runnable.
         *
         * @throws Exception if any error occurs
         */
        void run() throws Exception;
    }

    /**
     * Wraps a ThrowingFunction and rethrows as unchecked RuntimeException.
     *
     * @param function lambda with checked exception
     * @param <T> input type
     * @param <R> result type
     * @return safe Function
     */
    public static <T, R> Function<T, R> wrapFunction(ThrowingFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw propagate(e);
            }
        };
    }

    /**
     * Wraps a ThrowingBiFunction and rethrows as unchecked RuntimeException.
     *
     * @param function lambda with checked exception
     * @param <T> first input
     * @param <U> second input
     * @param <R> result type
     * @return safe BiFunction
     */
    public static <T, U, R> BiFunction<T, U, R> wrapBiFunction(ThrowingBiFunction<T, U, R> function) {
        return (t, u) -> {
            try {
                return function.apply(t, u);
            } catch (Exception e) {
                throw propagate(e);
            }
        };
    }

    /**
     * Wraps a ThrowingConsumer into a regular Consumer.
     *
     * @param consumer lambda with checked exception
     * @param <T> consumed type
     * @return safe Consumer
     */
    public static <T> Consumer<T> wrapConsumer(ThrowingConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                throw propagate(e);
            }
        };
    }

    /**
     * Wraps a ThrowingBiConsumer into a regular BiConsumer.
     *
     * @param consumer lambda with checked exception
     * @param <T> first input
     * @param <U> second input
     * @return safe BiConsumer
     */
    public static <T, U> BiConsumer<T, U> wrapBiConsumer(ThrowingBiConsumer<T, U> consumer) {
        return (t, u) -> {
            try {
                consumer.accept(t, u);
            } catch (Exception e) {
                throw propagate(e);
            }
        };
    }

    /**
     * Wraps a ThrowingSupplier into a regular Supplier.
     *
     * @param supplier lambda with checked exception
     * @param <T> result type
     * @return safe Supplier
     */
    public static <T> Supplier<T> wrapSupplier(ThrowingSupplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw propagate(e);
            }
        };
    }

    /**
     * Wraps a ThrowingRunnable into a regular Runnable.
     *
     * @param runnable lambda with checked exception
     * @return safe Runnable
     */
    public static Runnable wrapRunnable(ThrowingRunnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw propagate(e);
            }
        };
    }

    /**
     * Converts a ThrowingFunction into one that returns Optional.
     * Returns Optional.empty() in case of exception.
     *
     * @param function function that may throw
     * @param <T> input type
     * @param <R> result type
     * @return Optional-producing safe function
     */
    public static <T, R> Function<T, Optional<R>> safeFunctionOptional(ThrowingFunction<T, R> function) {
        return t -> {
            try {
                return Optional.ofNullable(function.apply(t));
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }

    /**
     * Converts a ThrowingFunction into one that returns {@code Either<Exception, R>}.
     *
     * @param function function that may throw
     * @param <T> input type
     * @param <R> result type
     * @return Either-producing function
     */
    public static <T, R> Function<T, Either<Exception, R>> safeFunctionEither(ThrowingFunction<T, R> function) {
        return t -> {
            try {
                return Either.right(function.apply(t));
            } catch (Exception e) {
                return Either.left(e);
            }
        };
    }

    /**
     * Propagates a checked exception as unchecked RuntimeException.
     *
     * @param e exception to propagate
     * @return RuntimeException
     */
    private static RuntimeException propagate(Exception e) {
        return (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
    }
}