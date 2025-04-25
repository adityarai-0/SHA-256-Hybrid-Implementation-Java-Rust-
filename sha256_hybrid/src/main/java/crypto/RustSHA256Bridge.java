// JNI Bridge to connect Java with Rust implementation
package main.java.crypto;
import java.util.Arrays;
public class RustSHA256Bridge {
    static {
        System.loadLibrary("rusthash");
    }

    // Native method that calls into Rust code
    public native byte[] hashWithRust(byte[] data);

    // Method to compare Java and Rust implementations
    public boolean compareImplementations(String input) {
        byte[] data = input.getBytes();

        // Hash with Java
        SHA256 javaSha = new SHA256();
        byte[] javaHash = javaSha.hash(data);

        // Hash with Rust via JNI
        byte[] rustHash = hashWithRust(data);

        // Compare results
        boolean match = Arrays.equals(javaHash, rustHash);
        System.out.println("Java hash: " + SHA256.toHexString(javaHash));
        System.out.println("Rust hash: " + SHA256.toHexString(rustHash));
        System.out.println("Hash match: " + match);

        return match;
    }

    public static void main(String[] args) {
        RustSHA256Bridge bridge = new RustSHA256Bridge();
        bridge.compareImplementations("Hello, World!");
    }
}