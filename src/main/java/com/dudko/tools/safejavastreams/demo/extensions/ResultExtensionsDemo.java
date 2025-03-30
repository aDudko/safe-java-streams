package com.dudko.tools.safejavastreams.demo.extensions;

import com.dudko.tools.safejavastreams.core.Result;
import com.dudko.tools.safejavastreams.extensions.ResultExtensions;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ResultExtensionsDemo {
    public static void main(String[] args) {
        // 1. From Optional (present)
        Optional<String> some = Optional.of("hello");
        Result<String, String> present = ResultExtensions.fromOptional(some, () -> "Value was not present");
        System.out.println("Present Optional: " + present);

        // 2. From Optional (empty)
        Optional<String> none = Optional.empty();
        Result<String, String> empty = ResultExtensions.fromOptional(none, () -> "Missing value");
        System.out.println("Empty Optional: " + empty);

        // 3. From Supplier (success)
        Supplier<String> supplier = () -> "computed";
        Result<String, String> computed = ResultExtensions.fromSupplier(supplier, Throwable::getMessage);
        System.out.println("Supplier success: " + computed);

        // 4. From Supplier (failure)
        Supplier<String> failingSupplier = () -> {
            throw new IllegalStateException("boom");
        };
        Result<String, String> failed = ResultExtensions.fromSupplier(failingSupplier, Throwable::getMessage);
        System.out.println("Supplier failure: " + failed);

        // 5. From CompletableFuture (success)
        CompletableFuture<String> future = CompletableFuture.completedFuture("async");
        Result<String, String> asyncResult = ResultExtensions.fromFuture(future, Throwable::getMessage);
        System.out.println("Future success: " + asyncResult);

        // 6. From CompletableFuture (exceptional)
        CompletableFuture<String> broken = new CompletableFuture<>();
        broken.completeExceptionally(new RuntimeException("kaboom"));
        Result<String, String> brokenResult = ResultExtensions.fromFuture(broken, Throwable::getMessage);
        System.out.println("Future failure: " + brokenResult);
    }
}