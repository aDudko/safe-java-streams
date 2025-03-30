package extensions;

import com.dudko.tools.safejavastreams.core.Result;
import com.dudko.tools.safejavastreams.extensions.ResultExtensions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class ResultExtensionsTest {

    @Test
    void testFromOptionalSuccess() {
        Optional<String> optional = Optional.of("value");
        Result<String, String> result = ResultExtensions.fromOptional(optional, () -> "missing");

        assertTrue(result.isSuccess());
        assertEquals("value", result.get());
    }

    @Test
    void testFromOptionalFailure() {
        Optional<String> optional = Optional.empty();
        Result<String, String> result = ResultExtensions.fromOptional(optional, () -> "missing");

        assertTrue(result.isFailure());
        assertEquals("missing", result.getError());
    }

    @Test
    void testFromFutureSuccess() {
        CompletableFuture<String> future = CompletableFuture.completedFuture("done");
        Result<String, String> result = ResultExtensions.fromFuture(future, Throwable::getMessage);

        assertTrue(result.isSuccess());
        assertEquals("done", result.get());
    }

    @Test
    void testFromFutureFailure() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("future boom"));

        Result<String, String> result = ResultExtensions.fromFuture(future, Throwable::getMessage);
        assertTrue(result.isFailure());
        assertEquals("future boom", result.getError());
    }

    @Test
    void testFromSupplierSuccess() {
        Result<String, String> result = ResultExtensions.fromSupplier(() -> "generated", Throwable::getMessage);
        assertTrue(result.isSuccess());
        assertEquals("generated", result.get());
    }

    @Test
    void testFromSupplierFailure() {
        Result<String, String> result = ResultExtensions.fromSupplier(() -> {
            throw new IllegalStateException("fail hard");
        }, Throwable::getMessage);

        assertTrue(result.isFailure());
        assertEquals("fail hard", result.getError());
    }

    @Test
    void testFromCheckedIsAlias() {
        Supplier<String> supplier = () -> "checked";
        Result<String, String> result = ResultExtensions.fromChecked(supplier, Throwable::getMessage);

        assertTrue(result.isSuccess());
        assertEquals("checked", result.get());
    }
}
