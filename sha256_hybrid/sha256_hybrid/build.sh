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
