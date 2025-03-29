# Safe Java Streams

![build](https://img.shields.io/github/actions/workflow/status/aDudko/safe-java-streams/ci.yml?branch=master)
![release](https://img.shields.io/github/v/release/aDudko/safe-java-streams)
![license](https://img.shields.io/github/license/aDudko/safe-java-streams)
![issues](https://img.shields.io/github/issues/aDudko/safe-java-streams)
![stars](https://img.shields.io/github/stars/aDudko/safe-java-streams)

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Built with Maven](https://img.shields.io/badge/Built_with-Maven-007396?logo=apache-maven)
[![Javadoc](https://img.shields.io/badge/javadoc-latest-blue)](https://adudko.github.io/safe-java-streams/)
![Coverage](https://img.shields.io/badge/coverage-100%25-brightgreen)
![GitHub Packages](https://img.shields.io/badge/github--packages-enabled-green)

---

`safe-java-streams` is a lightweight Java library that adds functional utilities for exception handling and `Either`-style value processing, enabling cleaner, safer and more predictable code.

It helps:

- Eliminate duplicate `try/catch` blocks in `Stream API`
- Explicitly handle errors without exceptions via `Either`
- Use `Optional` and `Either` instead of `null` and `throw`
- Make Java a bit more functional

---

## 📦 Project Setup (Maven)

### 1. Add GitHub Packages repository

```xml
<repositories>
  <repository>
    <id>github</id>
    <name>GitHub Packages</name>
    <url>https://maven.pkg.github.com/aDudko/safe-java-streams</url>
  </repository>
</repositories>
```

### 2. Add dependency

```xml
<dependency>
  <groupId>io.github.aDudko</groupId>
  <artifactId>safe-java-streams</artifactId>
  <version>1.0.0</version>
</dependency>
```

---

## 🔍 Key Features

### `Either<L, R>`

A container that can hold either a value (`Right`) or an error (`Left`). Supports:

- `map`, `flatMap`, `fold`
- `recover`, `recoverWith`, `ifLeft`, `ifRight`
- `peek`, `toOptional`

### `StreamExceptionUtils`

Set of adapters for working with checked exceptions in `Stream API`:

- `wrapFunction`, `wrapConsumer`, `wrapSupplier` etc.
- `safeFunctionOptional`, `safeFunctionEither`

---

## 💡 Usage Examples

### 1. Value processing via `map`

```
Either<String, Integer> result = Either.right(10)
    .map(i -> i * 2);

System.out.println(result); // Right(20)
```

---

### 2. Error recovery via `recover`

```
Either<String, Integer> result = Either.left("Error")
    .recover(err -> 0);

System.out.println(result); // Right(0)
```

---

### 3. Universal transformation via `fold`

```
String message = result.fold(
    err -> "Error: " + err,
    val -> "Success: " + val
);

System.out.println(message);
```

---

### 4. Using `safeFunctionOptional` in Stream API

```
List<Integer> result = List.of("1", "2", "bad", "3").stream()
    .map(StreamExceptionUtils.safeFunctionOptional(Integer::parseInt))
    .flatMap(Optional::stream)
    .collect(Collectors.toList());

// [1, 2, 3]
```

---

### 5. Using `safeFunctionEither` with error logging

```
List<String> inputs = List.of("123", "fail", "456");

inputs.stream()
    .map(StreamExceptionUtils.safeFunctionEither(s -> {
        if (s.equals("fail")) throw new RuntimeException("boom");
        return Integer.parseInt(s);
    }))
    .forEach(e -> e
        .ifRight(val -> System.out.println("OK: " + val))
        .ifLeft(err -> System.err.println("ERR: " + err.getMessage()))
    );
```

---

### 6. `flatMap` support and nested logic

```
Either<String, Integer> result = Either.right("5")
    .flatMap(s -> {
        try {
            return Either.right(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Either.left("Not a number");
        }
    });
```

---

### 7. Wrapping `ThrowingSupplier` into safe `Supplier`

```
Supplier<String> supplier = StreamExceptionUtils.wrapSupplier(() -> {
    if (Math.random() > 0.5) throw new IOException("Unstable!");
    return "OK";
});

try {
    System.out.println(supplier.get());
} catch (RuntimeException e) {
    System.err.println("Failed: " + e.getMessage());
}
```

---

## 🧪 Test Coverage

- Tests written with `JUnit 5`
- Covers `Either`, adapters, transformations, `Stream` pipelines

---

## 📘 JavaDoc

👉 Available on [GitHub Pages](https://aDudko.github.io/safe-java-streams) if enabled

---

## 🚀 Roadmap

- Add `Try<T>` and `Result<T, E>`
- Support for `mapLeft`, `biMap`, `filter`
- Enhanced integration with `Optional`, `CompletableFuture`, `record`
- Support for `Kotlin`, `Gradle`, `Functional style DSL`

---

## ✍️ Author

**Anatoly Dudko**  
[GitHub @aDudko](https://github.com/aDudko) • [LinkedIn](https://www.linkedin.com/in/dudko-anatol/)

---
