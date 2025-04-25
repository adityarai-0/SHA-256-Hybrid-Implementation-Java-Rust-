# SHA-256 Hybrid Implementation (Java + Rust)

This project demonstrates a hybrid implementation of the SHA-256 hash algorithm using both Java and Rust, with JNI (Java Native Interface) as the bridge between them.

## Project Structure

```
sha256_hybrid/
├── src/
│   └── main/
│       ├── java/
│       │   └── crypto/
│       │       ├── SHA256.java         # Pure Java implementation
│       │       └── RustSHA256Bridge.java # JNI bridge to Rust
│       └── rust/
│           ├── Cargo.toml
│           └── src/
│               └── lib.rs              # Rust implementation with JNI bindings
├── target/                             # Build output
└── build.sh                            # Build script
```

## Prerequisites

- JDK 11 or higher
- Rust and Cargo (latest stable)
- For Windows: Visual Studio build tools
- For Linux: gcc and development headers

## Building the Project

1. Run the build script:
   ```
   ./build.sh
   ```

2. This will:
   - Compile the Rust library
   - Compile Java classes
   - Create a JAR file
   - Copy necessary native libraries to target/libs

## Running the Demo

```
java -Djava.library.path=target/libs -cp target/sha256-hybrid.jar crypto.RustSHA256Bridge
```

## How it Works

1. The Java `SHA256` class provides a pure Java implementation of SHA-256.
2. The Rust implementation in `lib.rs` provides the same SHA-256 algorithm but written in Rust.
3. The `RustSHA256Bridge` Java class uses JNI to call into the Rust code.
4. The `main` method of `RustSHA256Bridge` demonstrates comparing the output of both implementations.

## Performance Comparison

You can compare the performance of the Java and Rust implementations by using large input data. Typically, the Rust implementation will be faster due to Rust's efficiency and lack of garbage collection overhead.
