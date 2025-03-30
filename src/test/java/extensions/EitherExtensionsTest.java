package extensions;

import com.dudko.tools.safejavastreams.core.Either;
import com.dudko.tools.safejavastreams.extensions.EitherExtensions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class EitherExtensionsTest {

    @Test
    void testFromOptionalSuccess() {
        Either<String, String> either = EitherExtensions.fromOptional(Optional.of("value"), () -> "error");
        assertTrue(either.isRight());
        assertEquals("value", either.getRight());
    }

    @Test
    void testFromOptionalFailure() {
        Either<String, String> either = EitherExtensions.fromOptional(Optional.empty(), () -> "error");
        assertTrue(either.isLeft());
        assertEquals("error", either.getLeft());
    }

    @Test
    void testFromFutureSuccess() {
        CompletableFuture<String> future = CompletableFuture.completedFuture("done");
        Either<String, String> either = EitherExtensions.fromFuture(future, Throwable::getMessage);
        assertTrue(either.isRight());
        assertEquals("done", either.getRight());
    }

    @Test
    void testFromFutureFailure() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("boom"));
        Either<String, String> either = EitherExtensions.fromFuture(future, Throwable::getMessage);
        assertTrue(either.isLeft());
        assertEquals("boom", either.getLeft());
    }

    @Test
    void testFromSupplierSuccess() {
        Either<String, String> either = EitherExtensions.fromSupplier(() -> "worked", Throwable::getMessage);
        assertTrue(either.isRight());
        assertEquals("worked", either.getRight());
    }

    @Test
    void testFromSupplierFailure() {
        Either<String, String> either = EitherExtensions.fromSupplier(() -> {
            throw new RuntimeException("crash");
        }, Throwable::getMessage);
        assertTrue(either.isLeft());
        assertEquals("crash", either.getLeft());
    }

    @Test
    void testFromCheckedAlias() {
        Either<String, String> either = EitherExtensions.fromChecked(() -> "alias", Throwable::getMessage);
        assertTrue(either.isRight());
        assertEquals("alias", either.getRight());
    }
}
