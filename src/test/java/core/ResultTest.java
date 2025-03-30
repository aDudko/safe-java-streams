package core;

import com.dudko.tools.safejavastreams.core.Result;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void successResult_shouldBeSuccess() {
        Result<String, String> result = Result.success("OK");
        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertEquals("OK", result.get());
    }

    @Test
    void failureResult_shouldBeFailure() {
        Result<String, String> result = Result.failure("Error");
        assertFalse(result.isSuccess());
        assertTrue(result.isFailure());
        assertEquals("Error", result.getError());
    }

    @Test
    void map_shouldTransformSuccess() {
        Result<Integer, String> result = Result.<String, String>success("42").map(Integer::parseInt);
        assertTrue(result.isSuccess());
        assertEquals(42, result.get());
    }

    @Test
    void map_shouldNotAffectFailure() {
        Result<Integer, String> result = Result.<String, String>failure("Error").map(String::length);
        assertTrue(result.isFailure());
        assertEquals("Error", result.getError());
    }

    @Test
    void mapError_shouldTransformError() {
        Result<String, Integer> result = Result.<String, String>failure("123").mapError(Integer::parseInt);
        assertTrue(result.isFailure());
        assertEquals(123, result.getError());
    }

    @Test
    void mapError_shouldNotAffectSuccess() {
        Result<String, Integer> result = Result.success("yes").mapError(s -> 1);
        assertTrue(result.isSuccess());
        assertEquals("yes", result.get());
    }

    @Test
    void flatMap_shouldChainSuccess() {
        Result<Integer, String> result = Result.<String, String>success("42")
                .flatMap(s -> Result.success(Integer.parseInt(s)));
        assertEquals(42, result.get());
    }

    @Test
    void flatMap_shouldBypassOnFailure() {
        Result<Integer, String> result = Result.<String, String>failure("fail").flatMap(s -> Result.success(s.length()));
        assertTrue(result.isFailure());
    }

    @Test
    void getOrElse_shouldReturnValueForSuccess() {
        Result<String, String> result = Result.success("hello");
        assertEquals("hello", result.getOrElse("default"));
    }

    @Test
    void getOrElse_shouldReturnDefaultForFailure() {
        Result<String, String> result = Result.failure("oops");
        assertEquals("default", result.getOrElse("default"));
    }

    @Test
    void toOptional_shouldReturnNonEmptyForSuccess() {
        Result<String, String> result = Result.success("done");
        assertEquals(Optional.of("done"), result.toOptional());
    }

    @Test
    void toOptional_shouldReturnEmptyForFailure() {
        Result<String, String> result = Result.failure("fail");
        assertEquals(Optional.empty(), result.toOptional());
    }

    @Test
    void fold_shouldHandleBothBranches() {
        Result<String, String> success = Result.success("hi");
        Result<String, String> failure = Result.failure("boom");

        Function<Result<String, String>, String> handler = r -> r.fold(
                err -> "Error: " + err,
                val -> "OK: " + val
        );

        assertEquals("OK: hi", handler.apply(success));
        assertEquals("Error: boom", handler.apply(failure));
    }

    @Test
    void testRecover() {
        Result<String, String> failure = Result.failure("err");
        Result<String, String> recovered = failure.recover(err -> "default");
        assertTrue(recovered.isSuccess());
        assertEquals("default", recovered.get());
    }

    @Test
    void testRecoverWith() {
        Result<String, String> failure = Result.failure("boom");
        Result<String, String> recovered = failure.recoverWith(err -> Result.success("safe"));
        assertTrue(recovered.isSuccess());
        assertEquals("safe", recovered.get());
    }

    @Test
    void testOrElseThrowSuccess() {
        Result<String, String> result = Result.success("done");
        assertEquals("done", result.orElseThrow(IllegalArgumentException::new));
    }

    @Test
    void testOrElseThrowFailure() {
        Result<String, String> result = Result.failure("fail");
        assertThrows(IllegalStateException.class, () -> result.orElseThrow(IllegalStateException::new));
    }

    @Test
    void testToEither() {
        Result<String, String> ok = Result.success("ok");
        Result<String, String> fail = Result.failure("fail");

        assertTrue(ok.toEither().isRight());
        assertEquals("ok", ok.toEither().getRight());

        assertTrue(fail.toEither().isLeft());
        assertEquals("fail", fail.toEither().getLeft());
    }

    @Test
    void testPeek() {
        AtomicBoolean called = new AtomicBoolean(false);
        Result<String, String> result = Result.<String, String>success("value").peek(r -> called.set(true));
        assertTrue(called.get());
        assertEquals("value", result.get());
    }
}
