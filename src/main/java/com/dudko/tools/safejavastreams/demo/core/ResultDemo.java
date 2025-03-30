package com.dudko.tools.safejavastreams.demo.core;

import com.dudko.tools.safejavastreams.core.Result;

import java.util.Optional;

public class ResultDemo {
    public static void main(String[] args) {
        // 1. Success case
        Result<Integer, String> result = Result.success(42);

        // 2. Access value
        if (result.isSuccess()) {
            System.out.println("Value: " + result.get());
        }

        // 3. Failure case
        Result<Integer, String> errorResult = Result.failure("Something went wrong");

        // 4. Handle with fold
        String message = errorResult.fold(
                err -> "Error: " + err,
                val -> "Success: " + val
        );
        System.out.println(message);

        // 5. map & flatMap
        Result<Integer, Object> mapped = Result.success(5).map(i -> i * 10);
        System.out.println("Mapped value: " + mapped.get());

        Result<String, String> flatMapped = Result.<String, String>success("input")
                .flatMap(s -> s.length() > 3
                        ? Result.success("valid")
                        : Result.failure("too short"));

        System.out.println("FlatMapped: " + flatMapped);

        // 6. recover
        Result<Integer, String> recovered = Result.<Integer, String>failure("fail").recover(err -> 0);
        System.out.println("Recovered: " + recovered.get());

        // 7. recoverWith
        Result<Integer, String> recoveredWith = Result.<Integer, String>failure("boom")
                .recoverWith(err -> Result.success(err.length()));
        System.out.println("RecoveredWith: " + recoveredWith.get());

        // 8. toEither
        System.out.println("As Either: " + result.toEither());

        // 9. getOrElse and getOrElseGet
        Result<String, String> ok = Result.success("hello");
        Result<String, String> fail = Result.failure("error");
        System.out.println("getOrElse (ok): " + ok.getOrElse("default"));
        System.out.println("getOrElse (fail): " + fail.getOrElse("default"));
        System.out.println("getOrElseGet: " + fail.getOrElseGet(err -> "recovered: " + err));

        // 10. orElseThrow
        try {
            fail.orElseThrow(IllegalStateException::new);
        } catch (Exception ex) {
            System.out.println("orElseThrow exception: " + ex.getMessage());
        }

        // 11. peek, onSuccess, onFailure
        Result<String, Object> peeked = Result.success("done")
                .onSuccess(val -> System.out.println("Success value: " + val))
                .onFailure(err -> System.err.println("Error value: " + err))
                .peek(r -> System.out.println("Peeked: " + r));

        // 12. filter
        Result<Integer, Object> filtered = Result.success(3)
                .filter(i -> i > 5, i -> "Too small: " + i);
        System.out.println("Filtered isSuccess: " + filtered.isSuccess());
        filtered.onFailure(System.out::println);

        // 13. mapError
        Result<Integer, String> mappedError = Result.<Integer, String>failure("oops")
                .mapError(e -> "Mapped error: " + e);
        System.out.println("Mapped error: " + mappedError.getError());

        // 14. toOptional
        Optional<Integer> optional = result.toOptional();
        System.out.println("Optional: " + optional);
    }
}
