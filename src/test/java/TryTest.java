import com.dudko.tools.safejavastreams.Try;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TryTest {

    @Test
    void success_shouldContainValue() {
        Try<Integer> result = Try.of(() -> 42);
        assertTrue(result.isSuccess());
        assertEquals(42, result.getOrElse(-1));
    }

    @Test
    void failure_shouldContainException() {
        Try<Integer> result = Try.of(() -> {
            throw new IOException("Boom");
        });
        assertTrue(result.isFailure());
        assertTrue(result.isFailureOf(IOException.class));
    }

    @Test
    void map_shouldTransformValue() {
        Try<Integer> result = Try.of(() -> 10).map(x -> x * 2);
        assertEquals(20, result.getOrElse(-1));
    }

    @Test
    void flatMap_shouldChainComputation() {
        Try<String> result = Try.of(() -> 5)
                .flatMap(x -> Try.of(() -> "Value: " + x));
        assertEquals("Value: 5", result.getOrElse("fail"));
    }

    @Test
    void recover_shouldReturnFallbackValue() {
        Try<Object> result = Try.of(() -> {
            throw new RuntimeException("oops");
        }).recover(ex -> 999);
        assertEquals(999, result.getOrElse(-1));
    }

    @Test
    void recoverWith_shouldReturnFallbackTry() {
        Try<Object> result = Try.of(() -> {
            throw new RuntimeException("fail");
        }).recoverWith(ex -> Try.of(() -> 123));
        assertEquals(123, result.getOrElse(-1));
    }

    @Test
    void fold_shouldHandleSuccess() {
        String msg = Try.of(() -> 7).fold(Throwable::getMessage, x -> "Got: " + x);
        assertEquals("Got: 7", msg);
    }

    @Test
    void fold_shouldHandleFailure() {
        Try<Integer> result = Try.of(() -> {
            throw new IllegalStateException("bad");
        });
        String msg = result.fold(Throwable::getMessage, Object::toString);
        assertEquals("bad", msg);
    }

    @Test
    void peek_shouldExecuteOnSuccess() {
        AtomicBoolean called = new AtomicBoolean(false);
        Try.of(() -> 1).peek(v -> called.set(true));
        assertTrue(called.get());
    }

    @Test
    void peekFailure_shouldExecuteOnFailure() {
        AtomicBoolean called = new AtomicBoolean(false);
        Try.of(() -> {
            throw new RuntimeException();
        }).peekFailure(ex -> called.set(true));
        assertTrue(called.get());
    }

    @Test
    void toOptional_shouldReturnOptional() {
        Optional<Integer> opt = Try.of(() -> 123).toOptional();
        assertEquals(123, opt.orElse(-1));
    }

    @Test
    void filter_shouldConvertToFailure() {
        Try<Integer> result = Try.of(() -> 10)
                .filter(x -> x > 20, () -> new IllegalArgumentException("Too small"));
        assertTrue(result.isFailure());
        assertTrue(result.isFailureOf(IllegalArgumentException.class));
    }

    @Test
    void getOrThrow_shouldThrowIfFailure() {
        Try<Integer> result = Try.of(() -> {
            throw new IOException("IO problem");
        });
        RuntimeException ex = assertThrows(RuntimeException.class, result::getOrThrow);
        assertEquals("java.io.IOException: IO problem", ex.getMessage());
    }

    @Test
    void toStream_shouldProduceStream() {
        long count = Try.of(() -> 123).toStream().count();
        assertEquals(1, count);
    }

    @Test
    void streamIntegrationExample() {
        var values = Stream.of("1", "oops", "3")
                .map(s -> Try.of(() -> Integer.parseInt(s)))
                .filter(Try::isSuccess)
                .map(Try::getOrThrow)
                .toList();

        assertEquals(2, values.size());
        assertEquals(1, values.get(0));
        assertEquals(3, values.get(1));
    }
}
