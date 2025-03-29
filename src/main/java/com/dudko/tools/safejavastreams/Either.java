package com.dudko.tools.safejavastreams;

import java.util.*;
import java.util.function.*;

/**
 * Functional-style container type that represents a value of one of two possible types.
 * <p>
 * Either is often used to indicate a computation that may result in a value (Right) or an error (Left).
 * This is a functional alternative to throwing exceptions and is similar to Try, Result, etc.
 *
 * @param <L> type of the Left (typically an error or failure reason)
 * @param <R> type of the Right (typically a success value)
 */
public abstract class Either<L, R> {

    /**
     * @return true if this is a Right instance
     */
    public abstract boolean isRight();

    /**
     * @return true if this is a Left instance
     */
    public abstract boolean isLeft();

    /**
     * @return Left value or throws if not present
     */
    public abstract L getLeft();

    /**
     * @return Right value or throws if not present
     */
    public abstract R getRight();

    /**
     * Maps Right value to another type if present.
     * If Left, the result remains Left.
     */
    public abstract <T> Either<L, T> map(Function<? super R, ? extends T> mapper);

    /**
     * Applies a function that returns another Either to Right value.
     * If Left, the result remains unchanged.
     */
    public abstract <T> Either<L, T> flatMap(Function<? super R, Either<L, T>> mapper);

    /**
     * Executes action if this is Right.
     */
    public abstract Either<L, R> ifRight(Consumer<? super R> action);

    /**
     * Executes action if this is Left.
     */
    public abstract Either<L, R> ifLeft(Consumer<? super L> action);

    /**
     * Reduces this Either into a single value by applying one of the provided functions.
     *
     * @param leftMapper function to apply to Left
     * @param rightMapper function to apply to Right
     * @return reduced value
     */
    public abstract <T> T fold(Function<? super L, ? extends T> leftMapper,
                               Function<? super R, ? extends T> rightMapper);

    /**
     * Converts Left into Right using a recovery function.
     */
    public abstract Either<L, R> recover(Function<? super L, ? extends R> recovery);

    /**
     * Converts Left into another Either using a recovery function.
     */
    public abstract Either<L, R> recoverWith(Function<? super L, Either<L, R>> recovery);

    /**
     * Allows observing the value (for logging, debugging, etc.).
     */
    public abstract Either<L, R> peek(Consumer<? super Either<L, R>> inspector);

    /**
     * Converts this Either into Optional: Right becomes value, Left becomes empty.
     */
    public abstract Optional<R> toOptional();

    /**
     * Creates a Right instance.
     */
    public static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    /**
     * Creates a Left instance.
     */
    public static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }

    private static final class Left<L, R> extends Either<L, R> {
        private final L value;

        Left(L value) {
            this.value = Objects.requireNonNull(value);
        }

        public boolean isRight() {
            return false;
        }

        public boolean isLeft() {
            return true;
        }

        public L getLeft() {
            return value;
        }

        public R getRight() {
            throw new NoSuchElementException("No right value");
        }

        public <T> Either<L, T> map(Function<? super R, ? extends T> mapper) {
            return left(value);
        }

        public <T> Either<L, T> flatMap(Function<? super R, Either<L, T>> mapper) {
            return left(value);
        }

        public Either<L, R> ifRight(Consumer<? super R> action) {
            return this;
        }

        public Either<L, R> ifLeft(Consumer<? super L> action) {
            action.accept(value);
            return this;
        }

        public <T> T fold(Function<? super L, ? extends T> leftMapper,
                          Function<? super R, ? extends T> rightMapper) {
            return leftMapper.apply(value);
        }

        public Either<L, R> recover(Function<? super L, ? extends R> recovery) {
            return right(recovery.apply(value));
        }

        public Either<L, R> recoverWith(Function<? super L, Either<L, R>> recovery) {
            return Objects.requireNonNull(recovery.apply(value));
        }

        public Either<L, R> peek(Consumer<? super Either<L, R>> inspector) {
            inspector.accept(this);
            return this;
        }

        public Optional<R> toOptional() {
            return Optional.empty();
        }

        public String toString() {
            return "Left(" + value + ")";
        }
    }

    private static final class Right<L, R> extends Either<L, R> {
        private final R value;

        Right(R value) {
            this.value = Objects.requireNonNull(value);
        }

        public boolean isRight() {
            return true;
        }

        public boolean isLeft() {
            return false;
        }

        public L getLeft() {
            throw new NoSuchElementException("No left value");
        }

        public R getRight() {
            return value;
        }

        public <T> Either<L, T> map(Function<? super R, ? extends T> mapper) {
            return right(mapper.apply(value));
        }

        public <T> Either<L, T> flatMap(Function<? super R, Either<L, T>> mapper) {
            return Objects.requireNonNull(mapper.apply(value));
        }

        public Either<L, R> ifRight(Consumer<? super R> action) {
            action.accept(value);
            return this;
        }

        public Either<L, R> ifLeft(Consumer<? super L> action) {
            return this;
        }

        public <T> T fold(Function<? super L, ? extends T> leftMapper,
                          Function<? super R, ? extends T> rightMapper) {
            return rightMapper.apply(value);
        }

        public Either<L, R> recover(Function<? super L, ? extends R> recovery) {
            return this;
        }

        public Either<L, R> recoverWith(Function<? super L, Either<L, R>> recovery) {
            return this;
        }

        public Either<L, R> peek(Consumer<? super Either<L, R>> inspector) {
            inspector.accept(this);
            return this;
        }

        public Optional<R> toOptional() {
            return Optional.of(value);
        }

        public String toString() {
            return "Right(" + value + ")";
        }
    }

}
