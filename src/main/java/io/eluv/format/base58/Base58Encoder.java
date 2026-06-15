package io.eluv.format.base58;

public class Base58Encoder {
    
    public static String encode(byte[] input) {
        return Base58.encode(input);
    }

}
