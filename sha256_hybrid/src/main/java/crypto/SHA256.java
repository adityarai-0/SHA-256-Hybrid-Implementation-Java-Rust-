// Java SHA-256 Implementation
package main.java.crypto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class SHA256 {
    // SHA-256 Constants - first 32 bits of the fractional parts of the cube roots of the first 64 primes
    private static final int[] K = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    // Initial hash values - first 32 bits of the fractional parts of the square roots of the first 8 primes
    private static final int[] H0 = {
            0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
    };

    // Working variables
    private int a, b, c, d, e, f, g, h;
    private final int[] H = new int[8];

    // Message schedule array
    private final int[] W = new int[64];

    public byte[] hash(byte[] message) {
        // Initialize hash values
        System.arraycopy(H0, 0, H, 0, H0.length);

        // Pre-processing: padding the message
        byte[] paddedMessage = padMessage(message);

        // Process the message in 512-bit chunks
        for (int i = 0; i < paddedMessage.length; i += 64) {
            processChunk(paddedMessage, i);
        }

        // Produce the final hash value
        byte[] hash = new byte[32];
        for (int i = 0; i < 8; i++) {
            intToBytes(H[i], hash, i * 4);
        }

        return hash;
    }

    private void processChunk(byte[] data, int start) {
        // Prepare the message schedule
        for (int t = 0; t < 16; t++) {
            W[t] = bytesToInt(data, start + t * 4);
        }

        for (int t = 16; t < 64; t++) {
            W[t] = sigma1(W[t - 2]) + W[t - 7] + sigma0(W[t - 15]) + W[t - 16];
        }

        // Initialize working variables
        a = H[0];
        b = H[1];
        c = H[2];
        d = H[3];
        e = H[4];
        f = H[5];
        g = H[6];
        h = H[7];

        // Main loop
        for (int t = 0; t < 64; t++) {
            int T1 = h + Sigma1(e) + Ch(e, f, g) + K[t] + W[t];
            int T2 = Sigma0(a) + Maj(a, b, c);

            h = g;
            g = f;
            f = e;
            e = d + T1;
            d = c;
            c = b;
            b = a;
            a = T1 + T2;
        }

        // Update hash values
        H[0] += a;
        H[1] += b;
        H[2] += c;
        H[3] += d;
        H[4] += e;
        H[5] += f;
        H[6] += g;
        H[7] += h;
    }

    // SHA-256 functions
    private int Ch(int x, int y, int z) {
        return (x & y) ^ (~x & z);
    }

    private int Maj(int x, int y, int z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }

    private int Sigma0(int x) {
        return Integer.rotateRight(x, 2) ^ Integer.rotateRight(x, 13) ^ Integer.rotateRight(x, 22);
    }

    private int Sigma1(int x) {
        return Integer.rotateRight(x, 6) ^ Integer.rotateRight(x, 11) ^ Integer.rotateRight(x, 25);
    }

    private int sigma0(int x) {
        return Integer.rotateRight(x, 7) ^ Integer.rotateRight(x, 18) ^ (x >>> 3);
    }

    private int sigma1(int x) {
        return Integer.rotateRight(x, 17) ^ Integer.rotateRight(x, 19) ^ (x >>> 10);
    }

    // Padding the message according to SHA-256 specification
    private byte[] padMessage(byte[] message) {
        int originalLength = message.length;
        int paddingLength = (originalLength % 64 < 56) ? (64 - originalLength % 64) : (128 - originalLength % 64);

        // Add at least 9 bytes of padding (1 + at least 8 for length)
        if (paddingLength < 9) {
            paddingLength += 64;
        }

        byte[] paddedMessage = new byte[originalLength + paddingLength];
        System.arraycopy(message, 0, paddedMessage, 0, originalLength);

        // Append a single '1' bit
        paddedMessage[originalLength] = (byte) 0x80;

        // Append the length in bits as a 64-bit big-endian integer
        long lengthInBits = (long) originalLength * 8;
        for (int i = 0; i < 8; i++) {
            paddedMessage[paddedMessage.length - 8 + i] = (byte) (lengthInBits >>> (56 - 8 * i));
        }

        return paddedMessage;
    }

    // Utility methods for converting between byte arrays and integers
    private int bytesToInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) |
                ((bytes[offset + 1] & 0xFF) << 16) |
                ((bytes[offset + 2] & 0xFF) << 8) |
                (bytes[offset + 3] & 0xFF);
    }

    private void intToBytes(int value, byte[] bytes, int offset) {
        bytes[offset] = (byte) (value >>> 24);
        bytes[offset + 1] = (byte) (value >>> 16);
        bytes[offset + 2] = (byte) (value >>> 8);
        bytes[offset + 3] = (byte) value;
    }

    // Utility method to convert hash to hex string
    public static String toHexString(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Example usage
    public static void main(String[] args) {
        SHA256 sha = new SHA256();
        String input = "Hello, World!";
        byte[] hash = sha.hash(input.getBytes());
        System.out.println("SHA-256 hash of '" + input + "': " + toHexString(hash));
    }
}