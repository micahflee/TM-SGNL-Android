package com.tm.authenticatorsdk.socgen.signup;

import android.app.Application;
import com.tm.utils.Util;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/signup/CryptoUtil.class */
public class CryptoUtil {
    public static SecretKeyMac generateSecretKeyMac(String algorithm, byte[] secretKey) {
        return new SecretKeyMac(algorithm, secretKey);
    }

    public static SecretKeyMac generateSecretKeyMacSha512(byte[] secretKey) {
        return generateSecretKeyMac("HmacSHA512", secretKey);
    }

    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/signup/CryptoUtil$SecretKeyMac.class */
    public static class SecretKeyMac {
        private Mac mac;

        SecretKeyMac(String algorithm, byte[] secretKey) {
            try {
                SecretKeySpec signingKey = new SecretKeySpec(secretKey, algorithm);
                this.mac = Mac.getInstance(algorithm);
                this.mac.init(signingKey);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e2) {
                e2.printStackTrace();
            }
        }

        public String generateHexDigest(Application application, String content) {
            return generateHexDigest(application, content.getBytes());
        }

        public String generateHexDigest(Application ap, byte[] content) {
            return Util.encodeHexString(ap, this.mac.doFinal(content));
        }
    }

    public static String encodeHmac512(String key, String data) {
        try {
            Mac HMACSHA512 = Mac.getInstance("HMACSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HMACSHA512");
            HMACSHA512.init(secret_key);
            return new String(Hex.encodeHex(HMACSHA512.doFinal(data.getBytes("UTF-8"))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e2) {
            e2.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e3) {
            e3.printStackTrace();
            return null;
        }
    }
}
