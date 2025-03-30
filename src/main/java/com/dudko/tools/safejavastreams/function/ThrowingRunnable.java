package com.dudko.tools.safejavastreams.function;

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
