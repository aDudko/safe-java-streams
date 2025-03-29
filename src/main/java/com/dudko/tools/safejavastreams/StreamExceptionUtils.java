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
     * Functional interface like Function<T, R> but allows checked exception
     */
    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }

    /**
     * BiFunction version with checked exception
     */
    @FunctionalInterface
    public interface ThrowingBiFunction<T, U, R> {
        R apply(T t, U u) throws Exception;
    }

    /**
     * Consumer with checked exception
     */
    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T t) throws Exception;
    }

    /**
     * BiConsumer with checked exception
     */
    @FunctionalInterface
    public interface ThrowingBiConsumer<T, U> {
        void accept(T t, U u) throws Exception;
    }

    /**
     * Supplier with checked exception
     */
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    /**
     * Runnable with checked exception
     */
    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    /**
     * Wraps a ThrowingFunction and rethrows as unchecked RuntimeException
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
     * Wraps a ThrowingBiFunction and rethrows as unchecked
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
     * Wraps a ThrowingConsumer into a regular Consumer
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
     * Wraps a ThrowingBiConsumer into a regular BiConsumer
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
     * Wraps a ThrowingSupplier into a regular Supplier
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
     * Wraps a ThrowingRunnable into a regular Runnable
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
     * Converts a ThrowingFunction into one that returns Either<Exception, R>.
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
     * Propagates a checked exception as unchecked RuntimeException
     */
    private static RuntimeException propagate(Exception e) {
        return (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
    }

}

