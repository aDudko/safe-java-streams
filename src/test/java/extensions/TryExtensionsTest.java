package extensions;

import com.dudko.tools.safejavastreams.core.Either;
import com.dudko.tools.safejavastreams.core.Try;
import com.dudko.tools.safejavastreams.extensions.TryExtensions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class TryExtensionsTest {

    @Test
    void testFromOptionalSuccess() throws Throwable {
        Try<String> result = TryExtensions.fromOptional(Optional.of("value"), () -> new IllegalArgumentException("empty"));
        assertTrue(result.isSuccess());
        assertEquals("value", result.get());
    }

    @Test
    void testFromOptionalFailure() {
        Try<String> result = TryExtensions.fromOptional(Optional.empty(), () -> new IllegalArgumentException("empty"));
        assertTrue(result.isFailure());
        assertTrue(result.getError() instanceof IllegalArgumentException);
        assertEquals("empty", result.getError().getMessage());
    }

    @Test
    void testFromFutureSuccess() throws Throwable {
        CompletableFuture<String> future = CompletableFuture.completedFuture("done");
        Try<String> result = TryExtensions.fromFuture(future);
        assertTrue(result.isSuccess());
        assertEquals("done", result.get());
    }

    @Test
    void testFromFutureFailure() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("future boom"));

        Try<String> result = TryExtensions.fromFuture(future);
        assertTrue(result.isFailure());
        assertTrue(result.getError() instanceof RuntimeException);
        assertEquals("future boom", result.getError().getMessage());
    }

    @Test
    void testFromSuccess() throws Throwable {
        Try<String> result = TryExtensions.from(() -> "Success!");
        assertTrue(result.isSuccess());
        assertEquals("Success!", result.get());
    }

    @Test
    void testFromFailure() {
        Try<String> result = TryExtensions.from(() -> { throw new IllegalStateException("boom"); });
        assertTrue(result.isFailure());
        assertTrue(result.getError() instanceof IllegalStateException);
        assertEquals("boom", result.getError().getMessage());
    }

    @Test
    void testPeekSuccess() throws Throwable {
        AtomicBoolean called = new AtomicBoolean(false);
        Try<String> result = Try.success("hello").peek(val -> called.set(true));
        assertTrue(called.get());
        assertEquals("hello", result.get());
    }

    @Test
    void testPeekFailure() {
        AtomicBoolean called = new AtomicBoolean(false);
        Try<Object> result = Try.failure(new RuntimeException("error"))
                .peek(val -> called.set(true));
        assertFalse(called.get());
        assertTrue(result.isFailure());
    }

    @Test
    void testRecoverSuccess() throws Throwable {
        Try<String> result = Try.success("value")
                .recover(ex -> "fallback");
        assertEquals("value", result.get());
    }

    @Test
    void testRecoverFailure() throws Throwable {
        Try<Object> result = Try.failure(new RuntimeException("fail"))
                .recover(ex -> "fallback");
        assertTrue(result.isSuccess());
        assertEquals("fallback", result.get());
    }

    @Test
    void testRecoverWithSuccess() throws Throwable {
        Try<String> result = Try.success("value")
                .recoverWith(ex -> Try.success("fallback"));
        assertEquals("value", result.get());
    }

    @Test
    void testRecoverWithFailure() throws Throwable {
        Try<Object> result = Try.failure(new RuntimeException("fail"))
                .recoverWith(ex -> Try.success("fallback"));
        assertTrue(result.isSuccess());
        assertEquals("fallback", result.get());
    }

    @Test
    void testToEitherSuccess() {
        Either<String, String> either = Try.success("value")
                .toEither(Throwable::getMessage);
        assertTrue(either.isRight());
        assertEquals("value", either.get());
    }

    @Test
    void testToEitherFailure() {
        Either<String, Object> either = Try.failure(new RuntimeException("fail"))
                .toEither(Throwable::getMessage);
        assertTrue(either.isLeft());
        assertEquals("fail", either.getLeft());
    }
}
