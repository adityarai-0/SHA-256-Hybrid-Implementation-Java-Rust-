#!/bin/bash
# Build script for SHA-256 hybrid Java-Rust implementation

# Create project structure
mkdir -p sha256_hybrid/src/main/java/crypto
mkdir -p sha256_hybrid/src/main/rust/src
mkdir -p sha256_hybrid/target

# Copy Java files
cat > sha256_hybrid/src/main/java/crypto/SHA256.java << 'EOL'
// Insert the Java SHA-256 implementation here
EOL

cat > sha256_hybrid/src/main/java/crypto/RustSHA256Bridge.java << 'EOL'
// Insert the JNI Bridge Java code here
EOL

# Create Rust project
cd sha256_hybrid/src/main/rust
cat > Cargo.toml << 'EOL'
[package]
name = "sha256_rust"
version = "0.1.0"
edition = "2021"

[lib]
name = "rusthash"
crate-type = ["cdylib"]

[dependencies]
jni = "0.19.0"
EOL

cat > src/lib.rs << 'EOL'
// Insert the Rust SHA-256 implementation here
EOL

# Create build file
cd ../../../
cat > build.sh << 'EOL'
#!/bin/bash

# Build Rust library
echo "Building Rust library..."
cd src/main/rust
cargo build --release

# Determine OS and set dynamic library extension
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    LIB_EXT="so"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    LIB_EXT="dylib"
elif [[ "$OSTYPE" == "cygwin" || "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    LIB_EXT="dll"
else
    echo "Unsupported OS"
    exit 1
fi

# Copy Rust library to project target directory
mkdir -p ../../../target/libs
cp target/release/librusthash.${LIB_EXT} ../../../target/libs/

# Compile Java code
echo "Compiling Java code..."
cd ../../../
javac -d target/classes src/main/java/crypto/*.java

# Create JAR file
echo "Creating JAR file..."
jar cvf target/sha256-hybrid.jar -C target/classes .

echo "Build completed successfully!"
echo "Run with: java -Djava.library.path=target/libs -cp target/sha256-hybrid.jar crypto.RustSHA256Bridge"
EOL

chmod +x build.sh

# Create README
cat > README.md << 'EOL'
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
EOL

echo "Project structure created successfully!"