package com.dudko.tools.safejavastreams.function;

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
