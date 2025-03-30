package com.dudko.tools.safejavastreams.function;

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
