package com.dudko.tools.safejavastreams.function;

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
