package adapter;

import com.dudko.tools.safejavastreams.core.Either;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.dudko.tools.safejavastreams.adapter.CheckedAdapters.*;
import static org.junit.jupiter.api.Assertions.*;

class CheckedAdaptersTest {

    @Test
    void wrapFunctionShouldPropagateException() {
        Function<String, Integer> func = wrapFunction(input -> {
            if (input.equals("fail")) throw new IOException("IO problem");
            return input.length();
        });

        assertEquals(4, func.apply("test"));
        assertThrows(RuntimeException.class, () -> func.apply("fail"));
    }

    @Test
    void safeFunctionOptionalShouldReturnEmptyOnException() {
        Function<String, Optional<String>> safe = safeFunctionOptional(input -> {
            if (input.equals("x")) throw new Exception("Boom");
            return input.toUpperCase();
        });

        assertEquals(Optional.of("HELLO"), safe.apply("hello"));
        assertEquals(Optional.empty(), safe.apply("x"));
    }

    @Test
    void safeFunctionEitherShouldReturnEither() {
        Function<String, Either<Exception, Integer>> safe = safeFunctionEither(input -> {
            if (input.isEmpty()) throw new IllegalArgumentException("Empty!");
            return input.length();
        });

        Either<Exception, Integer> success = safe.apply("ok");
        Either<Exception, Integer> fail = safe.apply("");

        assertTrue(success.isRight());
        assertEquals(2, success.getRight());
        assertTrue(fail.isLeft());
        assertInstanceOf(IllegalArgumentException.class, fail.getLeft());
    }

    @Test
    void wrapConsumerShouldThrowRuntimeException() {
        Consumer<Integer> consumer = wrapConsumer(input -> {
            if (input < 0) throw new IOException("Oops");
        });

        assertDoesNotThrow(() -> consumer.accept(10));
        assertThrows(RuntimeException.class, () -> consumer.accept(-1));
    }


    @Test
    void canUseInStreamPipeline() {
        List<Integer> result = Stream.of("1", "2", "bad", "3")
                .map(safeFunctionOptional(Integer::parseInt))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        assertEquals(List.of(1, 2, 3), result);
    }
}
