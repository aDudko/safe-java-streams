package com.dudko.tools.safejavastreams.demo;

import com.dudko.tools.safejavastreams.Try;

import java.util.List;
import java.util.Optional;

/**
 * Example of using Try
 * <p>
 * Try is a container that can contain either a Success or a Failure.
 * This avoids using exceptions to handle errors.
 * <p>
 * In this example, we create several Try objects and demonstrate their use.
 */
public class TryDemo {

    public static void main(String[] args) {
        // 1. Basic usage
        Try<Integer> parsed = Try.of(() -> Integer.parseInt("123"));
        System.out.println("Parsed: " + parsed.getOrElse(-1));

        // 2. Chain operations
        String message = Try.of(() -> "hello")
                .map(String::toUpperCase)
                .map(String::length)
                .map(len -> "Length is: " + len)
                .getOrElse("Failed");
        System.out.println(message);

        // 3. Recovery
        Try<Integer> recovered = Try.of(() -> Integer.parseInt("oops"))
                .recover(e -> -1);
        System.out.println("Recovered: " + recovered.getOrElse(0));

        // 4. Logging failure
        Try.of(() -> {
                    throw new IllegalStateException("Boom!");
                })
                .peek(val -> System.out.println("Success: " + val))
                .peekFailure(Throwable::printStackTrace);

        // 5. Filtering
        Try<String> filtered = Try.of(() -> "super-token")
                .filter(token -> token.length() > 10, () -> new IllegalArgumentException("Too short"));
        System.out.println("Filtered success: " + filtered.isSuccess());

        // 6. To Optional and Stream
        Optional<String> maybe = Try.of(() -> "value").toOptional();
        System.out.println("Optional: " + maybe);

        List<String> values = Try.of(() -> "streamed")
                .toStream()
                .map(String::toUpperCase)
                .toList();
        System.out.println("Streamed: " + values);

        // 7. Throw custom exception if failed
        try {
            String token = Try.of(TryDemo::getUserToken)
                    .getOrThrow(e -> new RuntimeException("Auth failed", e));
            System.out.println("Token: " + token);
        } catch (RuntimeException e) {
            System.err.println("Custom error: " + e.getMessage());
        }
    }

    private static String getUserToken() {
        throw new UnsupportedOperationException("No token for you!");
    }
}
