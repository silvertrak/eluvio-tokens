package io.eluv.crypto;

public class KeyFactory {
    
    public static Signer createSigner(String hexEncodedPk) throws KeysException {
        return new PrivateKey(Crypto.KeyPairFrom(hexEncodedPk));
    }

}
