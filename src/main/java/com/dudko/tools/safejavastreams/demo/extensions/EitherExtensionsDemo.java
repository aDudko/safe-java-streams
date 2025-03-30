package com.dudko.tools.safejavastreams.demo.extensions;

import com.dudko.tools.safejavastreams.core.Either;
import com.dudko.tools.safejavastreams.extensions.EitherExtensions;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class EitherExtensionsDemo {
    public static void main(String[] args) {
        // 1. fromOptional
        Optional<String> opt = Optional.of("Hello");
        Either<String, String> eitherFromOpt = EitherExtensions.fromOptional(opt, () -> "Was empty");
        System.out.println("Either from Optional: " + eitherFromOpt);

        // 2. fromOptional with empty value
        Optional<String> empty = Optional.empty();
        Either<String, String> eitherEmpty = EitherExtensions.fromOptional(empty, () -> "Missing value");
        System.out.println("Either from empty Optional: " + eitherEmpty);

        // 3. fromFuture (success)
        CompletableFuture<String> future = CompletableFuture.completedFuture("Async result");
        Either<String, String> eitherFuture = EitherExtensions.fromFuture(future, Throwable::getMessage);
        System.out.println("Either from Future: " + eitherFuture);

        // 4. fromFuture (failure)
        CompletableFuture<String> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Boom!"));
        Either<String, String> failedEither = EitherExtensions.fromFuture(failedFuture, Throwable::getMessage);
        System.out.println("Either from failed Future: " + failedEither);

        // 5. fromChecked (success)
        Either<String, String> checkedSuccess = EitherExtensions.fromChecked(() -> "Success!", Throwable::getMessage);
        System.out.println("Either from checked (success): " + checkedSuccess);

        // 6. fromChecked (failure)
        Either<String, String> checkedFailure = EitherExtensions.fromChecked(() -> {
            throw new IllegalStateException("Failure happened");
        }, Throwable::getMessage);
        System.out.println("Either from checked (failure): " + checkedFailure);

        // 7. From Supplier (success case)
        Either<String, String> fromSupplierOk = EitherExtensions.fromSupplier(() -> "it works", Throwable::getMessage);
        System.out.println("From supplier success: " + fromSupplierOk);

        // 8. From Supplier (failure case)
        Either<String, String> fromSupplierFail = EitherExtensions.fromSupplier(() -> {
            throw new IllegalStateException("boom from supplier");
        }, Throwable::getMessage);
        System.out.println("From supplier failure: " + fromSupplierFail);
    }
}
