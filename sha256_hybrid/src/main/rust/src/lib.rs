// Rust SHA-256 Implementation
use std::convert::TryInto;

// SHA-256 Constants - first 32 bits of the fractional parts of the cube roots of the first 64 primes
const K: [u32; 64] = [
    0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
    0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
    0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
    0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
    0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
    0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
    0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
    0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
];

// Initial hash values - first 32 bits of the fractional parts of the square roots of the first 8 primes
const H0: [u32; 8] = [
    0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
];

pub struct SHA256 {
    h: [u32; 8],
    w: [u32; 64],
}

impl SHA256 {
    pub fn new() -> SHA256 {
        SHA256 {
            h: H0,
            w: [0; 64],
        }
    }

    pub fn hash(&mut self, message: &[u8]) -> [u8; 32] {
        // Preprocess message with padding
        let padded_message = self.pad_message(message);

        // Process message in 512-bit chunks
        for chunk in padded_message.chunks(64) {
            self.process_chunk(chunk);
        }

        // Produce the final hash value
        let mut hash = [0u8; 32];
        for i in 0..8 {
            hash[i*4] = (self.h[i] >> 24) as u8;
            hash[i*4 + 1] = (self.h[i] >> 16) as u8;
            hash[i*4 + 2] = (self.h[i] >> 8) as u8;
            hash[i*4 + 3] = self.h[i] as u8;
        }

        hash
    }

    fn process_chunk(&mut self, chunk: &[u8]) {
        // Prepare the message schedule
        for t in 0..16 {
            self.w[t] = ((chunk[t*4] as u32) << 24) |
                ((chunk[t*4 + 1] as u32) << 16) |
                ((chunk[t*4 + 2] as u32) << 8) |
                (chunk[t*4 + 3] as u32);
        }

        for t in 16..64 {
            self.w[t] = self.sigma1(self.w[t-2])
                .wrapping_add(self.w[t-7])
                .wrapping_add(self.sigma0(self.w[t-15]))
                .wrapping_add(self.w[t-16]);
        }

        // Initialize working variables
        let mut a = self.h[0];
        let mut b = self.h[1];
        let mut c = self.h[2];
        let mut d = self.h[3];
        let mut e = self.h[4];
        let mut f = self.h[5];
        let mut g = self.h[6];
        let mut h = self.h[7];

        // Main loop
        for t in 0..64 {
            let t1 = h.wrapping_add(self.big_sigma1(e))
                .wrapping_add(self.ch(e, f, g))
                .wrapping_add(K[t])
                .wrapping_add(self.w[t]);
            let t2 = self.big_sigma0(a).wrapping_add(self.maj(a, b, c));

            h = g;
            g = f;
            f = e;
            e = d.wrapping_add(t1);
            d = c;
            c = b;
            b = a;
            a = t1.wrapping_add(t2);
        }

        // Update hash values
        self.h[0] = self.h[0].wrapping_add(a);
        self.h[1] = self.h[1].wrapping_add(b);
        self.h[2] = self.h[2].wrapping_add(c);
        self.h[3] = self.h[3].wrapping_add(d);
        self.h[4] = self.h[4].wrapping_add(e);
        self.h[5] = self.h[5].wrapping_add(f);
        self.h[6] = self.h[6].wrapping_add(g);
        self.h[7] = self.h[7].wrapping_add(h);
    }

    // SHA-256 functions
    fn ch(&self, x: u32, y: u32, z: u32) -> u32 {
        (x & y) ^ (!x & z)
    }

    fn maj(&self, x: u32, y: u32, z: u32) -> u32 {
        (x & y) ^ (x & z) ^ (y & z)
    }

    fn big_sigma0(&self, x: u32) -> u32 {
        x.rotate_right(2) ^ x.rotate_right(13) ^ x.rotate_right(22)
    }

    fn big_sigma1(&self, x: u32) -> u32 {
        x.rotate_right(6) ^ x.rotate_right(11) ^ x.rotate_right(25)
    }

    fn sigma0(&self, x: u32) -> u32 {
        x.rotate_right(7) ^ x.rotate_right(18) ^ (x >> 3)
    }

    fn sigma1(&self, x: u32) -> u32 {
        x.rotate_right(17) ^ x.rotate_right(19) ^ (x >> 10)
    }

    // Pad message according to SHA-256 specification
    fn pad_message(&self, message: &[u8]) -> Vec<u8> {
        let original_len_bits = (message.len() as u64) * 8;
        let mut padded = message.to_vec();

        // Append a single '1' bit
        padded.push(0x80);

        // Append '0' bits until message length in bits ≡ 448 (mod 512)
        while (padded.len() % 64) != 56 {
            padded.push(0);
        }

        // Append original length as a 64-bit big-endian integer
        for i in (0..8).rev() {
            padded.push((original_len_bits >> (i * 8)) as u8);
        }

        padded
    }
}

fn to_hex_string(hash: &[u8]) -> String {
    hash.iter()
        .map(|b| format!("{:02x}", b))
        .collect()
}

fn main() {
    let message = b"Hello, World!";
    let mut hasher = SHA256::new();
    let hash = hasher.hash(message);
    println!("SHA-256 hash of 'Hello, World!': {}", to_hex_string(&hash));
}

// JNI Integration

// Add to Cargo.toml:
// [lib]
// name = "rusthash"
// crate-type = ["cdylib"]
//
// [dependencies]
// jni = "0.19.0"

#[cfg(feature = "jni")]
mod jni_interface {
    use jni::JNIEnv;
    use jni::objects::{JClass, JByteArray};
    use jni::sys::{jbyteArray};
    use super::SHA256;

    #[no_mangle]
    pub extern "system" fn Java_crypto_RustSHA256Bridge_hashWithRust(
        env: JNIEnv,
        _class: JClass,
        data: JByteArray,
    ) -> jbyteArray {
        // Convert Java byte array to Rust slice
        let input = env.convert_byte_array(data).unwrap();

        // Compute hash
        let mut hasher = SHA256::new();
        let hash = hasher.hash(&input);

        // Convert hash to Java byte array
        let output = env.byte_array_from_slice(&hash).unwrap();

        // Return the Java byte array
        output.into_raw()
    }
}