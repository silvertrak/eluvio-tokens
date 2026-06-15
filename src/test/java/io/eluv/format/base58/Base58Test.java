package io.eluv.format.base58;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Base58Test {

    // Test vectors from Bitcoin base58 specification
    // Source: https://en.bitcoin.it/wiki/Base58Check_encoding
    
    @Test
    void testEncodeEmpty() {
        byte[] input = new byte[0];
        String encoded = Base58.encode(input);
        assertEquals("", encoded, "Empty input should encode to empty string");
    }

    @Test
    void testDecodeEmpty() {
        byte[] decoded = Base58.decode("");
        assertEquals(0, decoded.length, "Empty string should decode to empty array");
    }

    @Test
    void testEncodeSingleZero() {
        byte[] input = new byte[] {0x00};
        String encoded = Base58.encode(input);
        assertEquals("1", encoded, "Single zero byte should encode to '1'");
    }

    @Test
    void testDecodeSingleOne() {
        byte[] decoded = Base58.decode("1");
        assertArrayEquals(new byte[] {0x00}, decoded, "'1' should decode to single zero byte");
    }

    @Test
    void testEncodeMultipleZeros() {
        byte[] input = new byte[] {0x00, 0x00, 0x00};
        String encoded = Base58.encode(input);
        assertEquals("111", encoded, "Three zero bytes should encode to '111'");
    }

    @Test
    void testDecodeMultipleOnes() {
        byte[] decoded = Base58.decode("111");
        assertArrayEquals(new byte[] {0x00, 0x00, 0x00}, decoded, "'111' should decode to three zero bytes");
    }

    @Test
    void testEncodeSimpleValue() {
        // 0x01 -> "2"
        byte[] input = new byte[] {0x01};
        String encoded = Base58.encode(input);
        assertEquals("2", encoded);
    }

    @Test
    void testDecodeSimpleValue() {
        byte[] decoded = Base58.decode("2");
        assertArrayEquals(new byte[] {0x01}, decoded);
    }

    @Test
    void testEncodeWithLeadingZeros() {
        byte[] input = new byte[] {0x00, 0x00, 0x01};
        String encoded = Base58.encode(input);
        assertEquals("112", encoded, "Leading zeros should be preserved as '1' chars");
    }

    @Test
    void testDecodeWithLeadingOnes() {
        byte[] decoded = Base58.decode("112");
        assertArrayEquals(new byte[] {0x00, 0x00, 0x01}, decoded, "Leading '1's should decode to zero bytes");
    }

    @Test
    void testTenZeros() {
        byte[] input = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        String encoded = Base58.encode(input);
        assertEquals("1111111111", encoded);
        byte[] decoded = Base58.decode("1111111111");
        assertArrayEquals(input, decoded);
    }
    
    @Test
    void testLargerRoundTrip() {
        byte[] input = hexToBytes("0488B21E000000000000000000873DFF81C02F525623");
        String encoded = Base58.encode(input);
        byte[] decoded = Base58.decode(encoded);
        assertArrayEquals(input, decoded);
    }

    // Additional test vectors
    @Test
    void testVector1() {
        byte[] input = hexToBytes("0000C4D8C3B67");
        String encoded = Base58.encode(input);
        assertEquals("11DbHv", encoded);
    }

    @Test
    void testDecodeVector1() {
        byte[] decoded = Base58.decode("11DbHv");
        assertArrayEquals(hexToBytes("0000C4D8C3B67"), decoded);
    }

    @Test
    void testVector2() {
        // Testing a larger value
        byte[] input = new byte[] {
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, 
            (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05
        };
        String encoded = Base58.encode(input);
        byte[] decoded = Base58.decode(encoded);
        assertArrayEquals(input, decoded, "Round-trip should produce original value");
    }

    @Test
    void testFullAlphabet() {
        // Test that all characters in the base58 alphabet work
        String encoded = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        byte[] decoded = Base58.decode(encoded);
        String reEncoded = Base58.encode(decoded);
        assertEquals(encoded, reEncoded, "Round-trip of full alphabet should match");
    }

    @Test
    void testMaxByteValue() {
        byte[] input = new byte[] {(byte) 0xFF};
        String encoded = Base58.encode(input);
        byte[] decoded = Base58.decode(encoded);
        assertArrayEquals(input, decoded);
    }

    @Test
    void testLargeInput() {
        // Test with a larger random input
        byte[] input = new byte[100];
        for (int i = 0; i < input.length; i++) {
            input[i] = (byte) (i * 7 % 256);  // predictable pseudo-random
        }
        String encoded = Base58.encode(input);
        byte[] decoded = Base58.decode(encoded);
        assertArrayEquals(input, decoded, "Large input round-trip should work");
    }

    @Test
    void testInvalidCharacterI() {
        assertThrows(RuntimeException.class, () -> {
            Base58.decode("IAmInvalid");
        }, "Character 'I' should not be valid in base58");
    }

    @Test
    void testInvalidCharacterO() {
        assertThrows(RuntimeException.class, () -> {
            Base58.decode("Oops");
        }, "Character 'O' should not be valid in base58");
    }

    @Test
    void testInvalidCharacter0() {
        assertThrows(RuntimeException.class, () -> {
            Base58.decode("Zero0");
        }, "Character '0' should not be valid in base58");
    }

    @Test
    void testInvalidCharacterLowercaseL() {
        assertThrows(RuntimeException.class, () -> {
            Base58.decode("hellol");
        }, "Character 'l' should not be valid in base58");
    }

    @Test
    void testInvalidAscii() {
        assertThrows(RuntimeException.class, () -> {
            Base58.decode("test!@#$");
        }, "Special characters should not be valid in base58");
    }

    // Base58Encoder tests
    @Test
    void testBase58Encoder() {
        byte[] input = new byte[] {0x01, 0x02, 0x03};
        String encoded = Base58Encoder.encode(input);
        byte[] decoded = Base58.decode(encoded);
        assertArrayEquals(input, decoded, "Base58Encoder.encode should produce valid base58");
    }

    @Test
    void testBase58EncoderWithZeros() {
        byte[] input = new byte[] {0x00, 0x00, 0x01};
        String encoded = Base58Encoder.encode(input);
        assertEquals("112", encoded);
    }

    // Helper method to convert hex string to byte array
    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                 + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
