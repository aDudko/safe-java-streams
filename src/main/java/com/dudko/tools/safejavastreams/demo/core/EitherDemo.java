package com.dudko.tools.safejavastreams.demo.core;

import com.dudko.tools.safejavastreams.core.Either;

import java.util.List;
import java.util.stream.Stream;

/**
 * Example of using Either
 * <p>
 * Either is a container that can contain either a value of type T (Right) or an error of type E (Left).
 * This avoids the use of exceptions for error handling.
 * <p>
 * In this example, we create several Either objects and demonstrate their use.
 */
public class EitherDemo {

    public static void main(String[] args) {
        // 1. Successful result
        Either<String, Integer> success = Either.right(42);
        System.out.println(success.map(x -> x + 1)); // Right(43)

        // 2. Error
        Either<String, Integer> failure = Either.left("Ошибка");
        System.out.println(failure.map(x -> x + 1)); // Left(Ошибка)

        // 3. flatMap with string parsing
        Either<String, Integer> parsed = Either.<String, String>right("123")
                .flatMap(s -> {
                    try {
                        return Either.right(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                        return Either.left("Не число: " + s);
                    }
                });
        System.out.println(parsed); // Right(123)

        // 4. recover
        Either<String, Integer> recovered = failure.recover(err -> 0);
        System.out.println(recovered); // Right(0)

        // 5. fold
        String message = parsed.fold(
                err -> "Error: " + err,
                val -> "Successfully: " + val
        );
        System.out.println(message);

        // 6. peek
        parsed
                .peek(i -> System.out.println("peek Right: " + i))
                .ifLeft(err -> System.out.println("peek Left: " + err));

        // 7. Application in Stream
        List<Either<String, ?>> converted = Stream.of("10", "bad", "20")
                .map(s -> {
                    try {
                        return Either.<String, Integer>right(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                        return Either.left("Not a number: " + s);
                    }
                })
                .toList();

        converted.forEach(e -> e.ifRight(val -> System.out.println("Parsed: " + val))
                .ifLeft(err -> System.err.println("Error: " + err)));
    }
}
