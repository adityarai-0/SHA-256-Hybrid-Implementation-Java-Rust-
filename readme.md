# SHA-256 Hybrid Implementation (Java + Rust)

This project demonstrates a hybrid implementation of the SHA-256 hash algorithm using both Java and Rust, with JNI (Java Native Interface) as the bridge between them. It provides both a pure Java implementation and a Rust implementation accessible from Java, allowing for performance comparison between the two languages.

## Features

- Pure Java implementation of SHA-256
- Pure Rust implementation of SHA-256
- JNI bridge to connect Java and Rust code
- Comparison functionality to verify both implementations produce identical results
- Support for hashing arbitrary data

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
│           ├── Cargo.toml              # Rust project configuration
│           └── src/
│               └── lib.rs              # Rust implementation with JNI bindings
├── target/                             # Build output
│   └── libs/                           # Native libraries (.so, .dll, .dylib)
└── build.sh                            # Build script
```

## Prerequisites

Before building this project, ensure you have the following installed:

- JDK 11 or higher
- Rust and Cargo (latest stable version)
- C/C++ compiler appropriate for your platform:
  - Windows: Visual Studio Build Tools
  - macOS: Xcode Command Line Tools
  - Linux: GCC and development headers

For IntelliJ IDEA users:
- IntelliJ IDEA (Community or Ultimate)
- Rust plugin (optional but recommended)

## Building the Project

### Using the Build Script

1. Make the build script executable:
   ```
   chmod +x build.sh
   ```

2. Run the build script:
   ```
   ./build.sh
   ```

   This will:
   - Compile the Rust library (`librusthash.so`, `.dll`, or `.dylib` depending on your OS)
   - Compile Java classes
   - Copy the native library to the appropriate location

### Building Manually

If you prefer to build the components manually:

1. Build the Rust library:
   ```
   cd src/main/rust
   cargo build --release
   ```

2. Copy the compiled library to a location in your library path:
   ```
   mkdir -p ../../../target/libs
   cp target/release/librusthash.* ../../../target/libs/
   ```

3. Compile the Java code:
   ```
   javac -d ../../../target/classes src/main/java/crypto/*.java
   ```

## Running the Application

After building, you can run the application using:

```
java -Djava.library.path=target/libs -cp target/classes crypto.RustSHA256Bridge
```

For IntelliJ IDEA users, create a run configuration with:
- Main class: `crypto.RustSHA256Bridge`
- VM options: `-Djava.library.path=target/libs`

## Using the SHA-256 Implementations

### Java Implementation

```java
import crypto.SHA256;

// Create a new SHA-256 instance
SHA256 sha = new SHA256();

// Hash some data
byte[] data = "Hello, World!".getBytes();
byte[] hash = sha.hash(data);

// Convert to hex string
String hexHash = SHA256.toHexString(hash);
System.out.println("Hash: " + hexHash);
```

### Rust Implementation (via JNI)

```java
import crypto.RustSHA256Bridge;

// Create a new bridge instance
RustSHA256Bridge bridge = new RustSHA256Bridge();

// Hash data using the Rust implementation
byte[] data = "Hello, World!".getBytes();
byte[] hash = bridge.hashWithRust(data);

// Convert to hex string
String hexHash = SHA256.toHexString(hash);
System.out.println("Hash: " + hexHash);
```

### Comparing Implementations

```java
import crypto.RustSHA256Bridge;

// Create a bridge instance
RustSHA256Bridge bridge = new RustSHA256Bridge();

// Compare Java and Rust implementations
String input = "Hello, World!";
boolean match = bridge.compareImplementations(input);
System.out.println("Implementations match: " + match);
```

## Performance Considerations

The Rust implementation is generally expected to outperform the Java implementation, especially for large inputs, due to:

- Rust's lack of garbage collection overhead
- Rust's more efficient memory management
- Rust's ability to optimize low-level operations

However, for small inputs, the JNI call overhead may offset some of these advantages.

## Troubleshooting

### Library Not Found

If you encounter `UnsatisfiedLinkError: no rusthash in java.library.path`, ensure:
1. The native library is correctly built
2. The library is in the location specified by `-Djava.library.path`
3. The library name matches what's expected in the `System.loadLibrary()` call

### Compilation Errors

- **Java**: Ensure you're using JDK 11 or higher
- **Rust**: Ensure you have the latest stable Rust compiler and the JNI crate is available

## License

This project is available under the MIT License.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
