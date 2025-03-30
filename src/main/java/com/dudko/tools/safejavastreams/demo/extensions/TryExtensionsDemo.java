package com.dudko.tools.safejavastreams.demo.extensions;


import com.dudko.tools.safejavastreams.core.Try;
import com.dudko.tools.safejavastreams.extensions.TryExtensions;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TryExtensionsDemo {
    public static void main(String[] args) {
        // 1. From Optional (success)
        Optional<String> optional = Optional.of("hello");
        Try<String> fromOpt = TryExtensions.fromOptional(optional, () -> new IllegalStateException("Optional was empty"));
        System.out.println("From Optional: " + fromOpt);

        // 2. From Optional (failure)
        Optional<String> empty = Optional.empty();
        Try<String> fromEmpty = TryExtensions.fromOptional(empty, () -> new RuntimeException("Missing value"));
        System.out.println("From Empty Optional: " + fromEmpty);

        // 3. From ThrowingSupplier
        Try<String> wrapped = TryExtensions.from(() -> "Success!");
        Try<String> failed = TryExtensions.from(() -> {
            throw new Exception("Nope!");
        });
        System.out.println("From ThrowingSupplier (success): " + wrapped);
        System.out.println("From ThrowingSupplier (failure): " + failed);

        // 4. From CompletableFuture
        CompletableFuture<String> future = CompletableFuture.completedFuture("Future done");
        Try<String> fromFuture = TryExtensions.fromFuture(future);
        System.out.println("From CompletableFuture: " + fromFuture);

        // 5. From Broken CompletableFuture
        CompletableFuture<String> brokenFuture = new CompletableFuture<>();
        brokenFuture.completeExceptionally(new IllegalStateException("Boom"));
        Try<String> failedFuture = TryExtensions.fromFuture(brokenFuture);
        System.out.println("From Broken CompletableFuture: " + failedFuture);
    }
}
