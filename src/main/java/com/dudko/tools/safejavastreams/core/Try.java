package com.dudko.tools.safejavastreams.core;

import com.dudko.tools.safejavastreams.function.ThrowingSupplier;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Try is a container for a computation that may either result in a value (Success) or an exception (Failure).
 * Similar to {@code Either<Throwable, T>}.
 */
public abstract class Try<T> {

    /**
     * Returns true if this is a Success.
     */
    public abstract boolean isSuccess();

    /**
     * Returns true if this is a Failure.
     */
    public boolean isFailure() {
        return !isSuccess();
    }

    /**
     * Returns the value if this is a Success, otherwise throws the exception.
     */
    public abstract T get() throws Throwable;

    /**
     * Applies a function to the value if Success, otherwise propagates the Failure.
     *
     * @param mapper function to apply
     * @param <U>    mapped value type
     * @return new Try with mapped value or the original Failure
     */
    public abstract <U> Try<U> map(Function<? super T, ? extends U> mapper);

    /**
     * Applies a function that returns Try if Success, otherwise propagates the Failure.
     *
     * @param mapper function to apply
     * @param <U>    mapped value type
     * @return result of function or the original Failure
     */
    public abstract <U> Try<U> flatMap(Function<? super T, Try<U>> mapper);

    /**
     * Applies a recovery function if this is a Failure.
     *
     * @param recovery recovery function
     * @return Success with recovered value or original Success
     */
    public abstract Try<T> recover(Function<? super Throwable, ? extends T> recovery);

    /**
     * Applies a recovery function that returns Try if this is a Failure.
     *
     * @param recovery recovery function
     * @return Try from recovery or original Success
     */
    public abstract Try<T> recoverWith(Function<? super Throwable, Try<T>> recovery);

    /**
     * Applies one of two functions depending on whether it's a Success or Failure.
     *
     * @param failureMapper mapper for Failure
     * @param successMapper mapper for Success
     * @param <U>           return type
     * @return result of applying the corresponding function
     */
    public abstract <U> U fold(Function<? super Throwable, ? extends U> failureMapper,
                               Function<? super T, ? extends U> successMapper);

    /**
     * Performs side-effecting operation on Success.
     *
     * @param action consumer to run if Success
     * @return the same Try instance
     */
    public abstract Try<T> peek(Consumer<? super T> action);

    /**
     * Performs side-effecting operation on Failure.
     *
     * @param action consumer to run if Failure
     * @return the same Try instance
     */
    public abstract Try<T> peekFailure(Consumer<? super Throwable> action);

    /**
     * Returns the value as Optional if Success, otherwise empty.
     */
    public abstract Optional<T> toOptional();

    public abstract Throwable getError();

    public abstract <L> Either<L, T> toEither(Function<? super Throwable, ? extends L> mapper);

    /**
     * Checks if this Try is a Failure of specific exception type.
     *
     * @param type exception type to check
     * @return true if this is Failure with given type
     */
    public boolean isFailureOf(Class<? extends Throwable> type) {
        return this instanceof Failure<?> f && type.isInstance(f.exception);
    }

    /**
     * Executes an action if Success.
     */
    public Try<T> onSuccess(Consumer<? super T> action) {
        if (this instanceof Success<T> s) action.accept(s.value);
        return this;
    }

    /**
     * Executes an action if Failure.
     */
    public Try<T> onFailure(Consumer<? super Throwable> action) {
        if (this instanceof Failure<?> f) action.accept(f.exception);
        return this;
    }

    /**
     * Returns value or default if Failure.
     */
    public T getOrElse(T defaultValue) {
        return toOptional().orElse(defaultValue);
    }

    /**
     * Returns value or result of supplier if Failure.
     */
    public T getOrElseGet(Function<Throwable, ? extends T> supplier) {
        return fold(supplier, Function.identity());
    }

    /**
     * Returns value or throws RuntimeException if Failure.
     */
    public T getOrThrow() {
        return getOrThrow(RuntimeException::new);
    }

    /**
     * Returns value or throws custom RuntimeException from mapper.
     */
    public T getOrThrow(Function<Throwable, ? extends RuntimeException> mapper) {
        if (this instanceof Success<T> s) return s.value;
        else throw mapper.apply(((Failure<?>) this).exception);
    }

    /**
     * Applies a predicate to value if Success. Returns Failure if predicate fails.
     */
    public Try<T> filter(Predicate<? super T> predicate, Supplier<Throwable> ifInvalid) {
        if (this instanceof Success<T> s && !predicate.test(s.value)) return new Failure<>(ifInvalid.get());
        return this;
    }

    /**
     * Converts to Java Stream with 0 or 1 element.
     */
    public Stream<T> toStream() {
        return toOptional().stream();
    }

    /**
     * Executes given code block and wraps result in Try.
     */
    public static <T> Try<T> of(ThrowingSupplier<T> supplier) {
        try {
            return new Success<>(supplier.get());
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    /**
     * Creates a Success instance.
     *
     * @param value the value
     * @param <T>   type of the value
     * @return Success with the value
     */
    public static <T> Try<T> success(T value) {
        return new Success<>(value);
    }

    /**
     * Creates a Failure instance.
     *
     * @param exception the exception
     * @param <T>       type of the value
     * @return Failure with the exception
     */
    public static <T> Try<T> failure(Throwable exception) {
        return new Failure<>(exception);
    }

    /**
     * Represents a successful computation.
     *
     * @param <T> type of the value
     */
    public static final class Success<T> extends Try<T> {
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
        public <U> Try<U> map(Function<? super T, ? extends U> mapper) {
            return Try.of(() -> mapper.apply(value));
        }

        @Override
        public <U> Try<U> flatMap(Function<? super T, Try<U>> mapper) {
            try {
                return mapper.apply(value);
            } catch (Throwable t) {
                return new Failure<>(t);
            }
        }

        @Override
        public Try<T> recover(Function<? super Throwable, ? extends T> recovery) {
            return this;
        }

        @Override
        public Try<T> recoverWith(Function<? super Throwable, Try<T>> recovery) {
            return this;
        }

        @Override
        public <U> U fold(Function<? super Throwable, ? extends U> failureMapper,
                          Function<? super T, ? extends U> successMapper) {
            return successMapper.apply(value);
        }

        @Override
        public Try<T> peek(Consumer<? super T> action) {
            action.accept(value);
            return this;
        }

        @Override
        public Try<T> peekFailure(Consumer<? super Throwable> action) {
            return this;
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.of(value);
        }

        @Override
        public Throwable getError() {
            throw new IllegalStateException("No error in Success");
        }

        @Override
        public <L> Either<L, T> toEither(Function<? super Throwable, ? extends L> mapper) {
            return Either.right(value);
        }
    }

    /**
     * Represents a failed computation.
     *
     * @param <T> type of the value
     */
    public static final class Failure<T> extends Try<T> {
        private final Throwable exception;

        public Failure(Throwable exception) {
            this.exception = exception;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T get() throws Throwable {
            throw exception;
        }

        @Override
        public <U> Try<U> map(Function<? super T, ? extends U> mapper) {
            return new Failure<>(exception);
        }

        @Override
        public <U> Try<U> flatMap(Function<? super T, Try<U>> mapper) {
            return new Failure<>(exception);
        }

        @Override
        public Try<T> recover(Function<? super Throwable, ? extends T> recovery) {
            return Try.of(() -> recovery.apply(exception));
        }

        @Override
        public Try<T> recoverWith(Function<? super Throwable, Try<T>> recovery) {
            return Try.of(() -> recovery.apply(exception)).flatMap(Function.identity());
        }

        @Override
        public <U> U fold(Function<? super Throwable, ? extends U> failureMapper,
                          Function<? super T, ? extends U> successMapper) {
            return failureMapper.apply(exception);
        }

        @Override
        public Try<T> peek(Consumer<? super T> action) {
            return this;
        }

        @Override
        public Try<T> peekFailure(Consumer<? super Throwable> action) {
            action.accept(exception);
            return this;
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.empty();
        }

        @Override
        public Throwable getError() {
            return exception;
        }

        @Override
        public <L> Either<L, T> toEither(Function<? super Throwable, ? extends L> mapper) {
            return Either.left(mapper.apply(exception));
        }
    }

}
