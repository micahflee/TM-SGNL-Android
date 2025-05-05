package com.tm.androidcopysdk.database;

import android.content.Context;
import com.tm.androidcopysdk.utils.TMCredentialsStore;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/UriUtils.class */
public class UriUtils {
    public static final String TAG = "UriUtils";

    public static InputStream getDecipheredInStream(Context context, InputStream cipheredStream) {
        String username = TMCredentialsStore.getInstance(context).userName(context);
        String password = TMCredentialsStore.getInstance(context).password(context);
        Cipher decipher = null;
        try {
            decipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e2) {
            e2.printStackTrace();
        }
        byte[] secret = (username + password).getBytes();
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e3) {
            e3.printStackTrace();
        }
        SecretKeySpec secretKey = new SecretKeySpec(sha.digest(secret), "AES");
        try {
            Properties properties = new Properties();
            try {
                InputStream configStream = context.getAssets().open("config.properties");
                properties.load(configStream);
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            String ivString = properties.getProperty("encryption_iv");
            byte[] iv = hexStringToByteArray(ivString);
            IvParameterSpec IV = new IvParameterSpec(iv);
            try {
                decipher.init(2, secretKey, IV);
            } catch (InvalidAlgorithmParameterException e5) {
                e5.printStackTrace();
            }
        } catch (InvalidKeyException e6) {
            e6.printStackTrace();
        }
        CipherInputStream cis = new CipherInputStream(cipheredStream, decipher);
        return cis;
    }

    public static OutputStream getDecipheredOutStream(Context context, OutputStream cipheredStream) {
        Cipher encipher = null;
        try {
            encipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e2) {
            e2.printStackTrace();
        }
        String username = TMCredentialsStore.getInstance(context).userName(context);
        String password = TMCredentialsStore.getInstance(context).password(context);
        byte[] secret = (username + password).getBytes();
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e3) {
            e3.printStackTrace();
        }
        SecretKeySpec secretKey = new SecretKeySpec(sha.digest(secret), "AES");
        try {
            Properties properties = new Properties();
            try {
                InputStream configStream = context.getAssets().open("config.properties");
                properties.load(configStream);
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            String ivString = properties.getProperty("encryption_iv");
            byte[] iv = hexStringToByteArray(ivString);
            IvParameterSpec IV = new IvParameterSpec(iv);
            try {
                encipher.init(1, secretKey, IV);
            } catch (InvalidAlgorithmParameterException e5) {
                e5.printStackTrace();
            }
        } catch (InvalidKeyException e6) {
            e6.printStackTrace();
        }
        CipherOutputStream cis = new CipherOutputStream(cipheredStream, encipher);
        return cis;
    }

    public static byte[] hexStringToByteArray(String s) {
        try {
            return Hex.decodeHex(s.toCharArray());
        } catch (DecoderException e1) {
            e1.printStackTrace();
            return null;
        }
    }
}
