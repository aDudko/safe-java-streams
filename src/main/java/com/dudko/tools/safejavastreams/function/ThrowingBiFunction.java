package com.dudko.tools.safejavastreams.function;

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
