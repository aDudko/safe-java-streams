package com.dudko.tools.safejavastreams.function;

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
