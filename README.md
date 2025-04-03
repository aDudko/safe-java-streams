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

`safe-java-streams` is a lightweight Java library that adds functional utilities for exception handling and `Either`/`Try`/`Result` style value processing, enabling cleaner, safer, and more predictable code.

It helps:

- Eliminate boilerplate `try/catch` in `Stream API`
- Handle errors without exceptions using `Either` / `Result`
- Represent computations that may fail using `Try`
- Bridge Java's checked exceptions into functional pipelines

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

### ✅ `Either<L, R>`
A disjoint union of `Left` and `Right`, representing success or error.
- `map`, `flatMap`, `fold`, `toOptional`
- `recover`, `recoverWith`, `peek`
- `getOrElse`, `orElseThrow`

### 🔄 `Try<T>`
Encapsulates a computation that may fail:
- `Success` or `Failure`
- Fluent chaining with `map`, `flatMap`, `recover`
- Transform to `Optional` / `Either`

### 📊 `Result<T, E>`
Inspired by Rust/Scala. Encapsulates `success` or `failure` with separate types:
- `map`, `flatMap`, `mapError`, `recover`
- `get`, `getOrElse`, `toEither`, `toOptional`

### 🧰 `CheckedAdapters`
Adapter utilities for using checked-exception-throwing lambdas in `Stream API`:
- `wrapFunction`, `wrapConsumer`, `wrapSupplier` etc.
- `safeFunctionOptional`, `safeFunctionEither`

---

## 💡 Usage [Examples](src/main/java/com/dudko/tools/safejavastreams/demo)

---

## 🧪 Test Coverage

- Tests written with `JUnit 5`
- Covers all core features: `Either`, `Try`, `Result`, `CheckedAdapters`

---

## 📘 JavaDoc

👉 [Browse Online Documentation](https://adudko.github.io/safe-java-streams/)

---

## 🚀 Roadmap

- [x] Add `Try<T>` and `Result<T, E>`
- [x] Improve error handling ergonomics
- [ ] Add `Validated` and `NonEmptyList`
- [ ] Add monadic and DSL-style builders
- [ ] Gradle & Kotlin support

---

## If you find this useful...

...give it a star, fork it, or mention it in your next data project!

## ✍️ Author

**Anatoly Dudko**  
[GitHub @aDudko](https://github.com/aDudko) • [LinkedIn](https://www.linkedin.com/in/dudko-anatol/)

