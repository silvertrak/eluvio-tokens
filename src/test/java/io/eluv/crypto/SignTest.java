package io.eluv.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;

public class SignTest {

    @Test
    void testECKeyPairSign() throws Exception {
        ECKeyPair keyPair = KeysTest.staticPrivateKey();
        
        String message = "hello";
        byte[] signature = Crypto.sign(message.getBytes(), keyPair);
        signature = Crypto.adjustSignedBytes(signature);
        assertEquals(
            "e58f5a0fd01032c607103fde4ea65be179fdb81ba09d401206d2828f458e92ca06b776009320cb8e6a4db48ec1ea71c6034c3a91656a0b850ebf413aaf24d2a401",
            Hex.toHexString(signature));
    }
    
    @Test
    void testPrivateKeySign() throws Exception {
        PrivateKey kp = new PrivateKey(KeysTest.staticPrivateKey());
        
        String message = "hello";
        byte[] signature = Crypto.sign(message.getBytes(), kp);
        signature = Crypto.adjustSignedBytes(signature);
        assertEquals(
            "e58f5a0fd01032c607103fde4ea65be179fdb81ba09d401206d2828f458e92ca06b776009320cb8e6a4db48ec1ea71c6034c3a91656a0b850ebf413aaf24d2a401",
            Hex.toHexString(signature));
    }
    

    public static ECKeyPair createRandomPrivateKey() throws Exception {
        BigInteger privKey = Keys.createEcKeyPair().getPrivateKey();
        BigInteger pubKey = Sign.publicKeyFromPrivate(privKey);
        return new ECKeyPair(privKey, pubKey);
    }
    

    @Test
    void testSigningSample() throws Exception {
        ECKeyPair keyPair = createRandomPrivateKey();

        String msg = "Message for signing";
        byte[] msgHash = Hash.sha3(msg.getBytes());
        Sign.SignatureData signature = Sign.signMessage(msgHash, keyPair, false);

        BigInteger pubKeyRecovered = Sign.signedMessageToKey(msg.getBytes(), signature);

        boolean validSig = keyPair.getPublicKey().equals(pubKeyRecovered);
        assertTrue(validSig);
    }    
    
}
