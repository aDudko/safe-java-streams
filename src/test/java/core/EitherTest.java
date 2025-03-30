package core;

import com.dudko.tools.safejavastreams.core.Either;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EitherTest {

    @Test
    void rightShouldContainValue() {
        Either<String, Integer> right = Either.right(42);
        assertTrue(right.isRight());
        assertEquals(42, right.getRight());
        assertEquals(Optional.of(42), right.toOptional());
    }

    @Test
    void leftShouldContainError() {
        Either<String, Integer> left = Either.left("Error");
        assertTrue(left.isLeft());
        assertEquals("Error", left.getLeft());
        assertEquals(Optional.empty(), left.toOptional());
    }

    @Test
    void mapShouldTransformRight() {
        Either<String, Integer> right = Either.right(10);
        Either<String, Integer> mapped = right.map(i -> i * 2);
        assertEquals(20, mapped.getRight());
    }

    @Test
    void flatMapShouldChainRight() {
        Either<String, Integer> right = Either.right(5);
        Either<String, Integer> result = right.flatMap(i -> Either.right(i + 3));
        assertEquals(8, result.getRight());
    }

    @Test
    void recoverShouldReturnDefaultFromLeft() {
        Either<String, Integer> left = Either.left("fail");
        Either<String, Integer> recovered = left.recover(err -> 999);
        assertEquals(999, recovered.getRight());
    }

    @Test
    void foldShouldApplyCorrectFunction() {
        Either<String, Integer> right = Either.right(1);
        Either<String, Integer> left = Either.left("oops");

        assertEquals("OK", right.fold(l -> "FAIL", r -> "OK"));
        assertEquals("FAIL", left.fold(l -> "FAIL", r -> "OK"));
    }
}

