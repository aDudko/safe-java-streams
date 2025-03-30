package com.dudko.tools.safejavastreams.core;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Result is a container for a computation that may either result in a value (Success) or an error (Failure).
 * Similar to {@code Either<E, T>}.
 *
 * @param <T> the type of the success value
 * @param <E> the type of the error value
 */
public abstract class Result<T, E> {

    /**
     * Returns true if this is a success result.
     */
    public abstract boolean isSuccess();

    /**
     * Returns true if this is a failure result.
     */
    public boolean isFailure() {
        return !isSuccess();
    }

    /**
     * Returns the success value or throws if failure.
     */
    public abstract T get();

    /**
     * Returns the error value or throws if success.
     */
    public abstract E getError();

    /**
     * Returns the success value or a default if failure.
     */
    public T getOrElse(T defaultValue) {
        return toOptional().orElse(defaultValue);
    }

    /**
     * Returns the success value or result of a supplier if failure.
     */
    public T getOrElseGet(Function<? super E, ? extends T> supplier) {
        return fold(supplier, Function.identity());
    }

    /**
     * Returns the success value or throws mapped exception if failure.
     */
    public T orElseThrow(Function<? super E, ? extends RuntimeException> exceptionMapper) {
        if (isSuccess()) return get();
        throw exceptionMapper.apply(getError());
    }

    /**
     * Returns success value as Optional or empty if failure.
     */
    public Optional<T> toOptional() {
        return isSuccess() ? Optional.of(get()) : Optional.empty();
    }

    /**
     * Converts this Result to an Either.
     */
    public Either<E, T> toEither() {
        return isSuccess() ? Either.right(get()) : Either.left(getError());
    }

    /**
     * Applies the function to the success value.
     */
    public abstract <U> Result<U, E> map(Function<? super T, ? extends U> mapper);

    /**
     * Applies the function to the success value, returning another Result.
     */
    public abstract <U> Result<U, E> flatMap(Function<? super T, Result<U, E>> mapper);

    /**
     * Applies one of two functions depending on the result type.
     */
    public abstract <U> U fold(Function<? super E, ? extends U> failureMapper,
                               Function<? super T, ? extends U> successMapper);

    /**
     * Executes action if success.
     */
    public Result<T, E> onSuccess(Consumer<? super T> action) {
        if (this instanceof Success<T, E> s) action.accept(s.value);
        return this;
    }

    /**
     * Executes action if failure.
     */
    public Result<T, E> onFailure(Consumer<? super E> action) {
        if (this instanceof Failure<T, E> f) action.accept(f.error);
        return this;
    }

    /**
     * Executes action regardless of result type.
     */
    public Result<T, E> peek(Consumer<? super Result<T, E>> action) {
        action.accept(this);
        return this;
    }

    /**
     * Applies predicate to the value, turning success into failure if predicate fails.
     */
    public Result<T, E> filter(Predicate<? super T> predicate, Function<? super T, ? extends E> onFailure) {
        if (this instanceof Success<T, E> s && !predicate.test(s.value)) {
            return Result.failure(onFailure.apply(s.value));
        }
        return this;
    }

    /**
     * Maps the error value if this is a failure.
     */
    public abstract <F> Result<T, F> mapError(Function<? super E, ? extends F> mapper);

    /**
     * Recovers from failure with a fallback value.
     */
    public Result<T, E> recover(Function<? super E, ? extends T> recovery) {
        return isSuccess() ? this : Result.success(recovery.apply(getError()));
    }

    /**
     * Recovers from failure with another Result.
     */
    public Result<T, E> recoverWith(Function<? super E, Result<T, E>> recovery) {
        return isSuccess() ? this : recovery.apply(getError());
    }

    /**
     * Creates a success result.
     */
    public static <T, E> Result<T, E> success(T value) {
        return new Success<>(value);
    }

    /**
     * Creates a failure result.
     */
    public static <T, E> Result<T, E> failure(E error) {
        return new Failure<>(error);
    }

    /**
     * Success branch implementation.
     */
    public static final class Success<T, E> extends Result<T, E> {
        private final T value;

        public Success(T value) {
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public E getError() {
            throw new IllegalStateException("No error present in Success");
        }

        @Override
        public <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
            return Result.success(mapper.apply(value));
        }

        @Override
        public <U> Result<U, E> flatMap(Function<? super T, Result<U, E>> mapper) {
            return mapper.apply(value);
        }

        @Override
        public <U> U fold(Function<? super E, ? extends U> failureMapper,
                          Function<? super T, ? extends U> successMapper) {
            return successMapper.apply(value);
        }

        @Override
        public <F> Result<T, F> mapError(Function<? super E, ? extends F> mapper) {
            return Result.success(value);
        }
    }

    /**
     * Failure branch implementation.
     */
    public static final class Failure<T, E> extends Result<T, E> {
        private final E error;

        public Failure(E error) {
            this.error = error;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T get() {
            throw new IllegalStateException("No value present in Failure");
        }

        @Override
        public E getError() {
            return error;
        }

        @Override
        public <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
            return Result.failure(error);
        }

        @Override
        public <U> Result<U, E> flatMap(Function<? super T, Result<U, E>> mapper) {
            return Result.failure(error);
        }

        @Override
        public <U> U fold(Function<? super E, ? extends U> failureMapper,
                          Function<? super T, ? extends U> successMapper) {
            return failureMapper.apply(error);
        }

        @Override
        public <F> Result<T, F> mapError(Function<? super E, ? extends F> mapper) {
            return Result.failure(mapper.apply(error));
        }
    }
}
