package com.tm.androidcopysdk.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.text.TextUtils;
import android.util.Base64;
import com.tm.logger.Log;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/TMCredentialsStore.class */
public class TMCredentialsStore {
    private static TMCredentialsStore mInstance = null;
    private static final String TAG = "TMCredentialsStore";
    private static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String TYPE_RSA = "RSA";
    private Context context;
    private String mUserName = "";
    private String mPassword = "";
    private String mAlias = "alias";
    private Object lockObj = new Object();

    public static synchronized TMCredentialsStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TMCredentialsStore(context);
        }
        return mInstance;
    }

    public TMCredentialsStore(Context context) {
        this.context = context;
    }

    public void loadFirstTimeSync(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                createKeys(context);
            } catch (InvalidAlgorithmParameterException e) {
                Log.e(TAG, "", e);
            } catch (NoSuchAlgorithmException e2) {
                Log.e(TAG, "", e2);
            } catch (NoSuchProviderException e3) {
                Log.e(TAG, "", e3);
            }
        }
    }

    public void setUserName(Context context, String userName) {
        this.mUserName = userName;
        if (userName.length() > 0 && Build.VERSION.SDK_INT >= 23) {
            try {
                userName = encrypt(userName);
            } catch (IOException e) {
                Log.e(TAG, "", e);
            } catch (InvalidAlgorithmParameterException e2) {
                e2.printStackTrace();
            } catch (InvalidKeyException e3) {
                Log.e(TAG, "", e3);
            } catch (KeyStoreException e4) {
                Log.e(TAG, "", e4);
            } catch (NoSuchAlgorithmException e5) {
                Log.e(TAG, "", e5);
            } catch (NoSuchProviderException e6) {
                e6.printStackTrace();
            } catch (UnrecoverableEntryException e7) {
                Log.e(TAG, "", e7);
            } catch (CertificateException e8) {
                Log.e(TAG, "", e8);
            } catch (BadPaddingException e9) {
                Log.e(TAG, "", e9);
            } catch (IllegalBlockSizeException e10) {
                Log.e(TAG, "", e10);
            } catch (NoSuchPaddingException e11) {
                Log.e(TAG, "", e11);
            }
        }
        PrefManager.setStringPref(context, PrefManagerConstants.SHARED_PREFERENCE_SIGNIN_USERNAME_KEY_AA_KEY, userName);
    }

    public void setPassword(Context context, String password) {
        this.mPassword = password;
        if (password.length() > 0 && Build.VERSION.SDK_INT >= 23) {
            try {
                password = encrypt(password);
                PrefManager.setBooleanPref(context, PrefManagerConstants.SHARED_PREFERENCE_USE_KEY_STORE_KEY_AA_KEY, true);
            } catch (IOException e) {
                Log.e(TAG, "", e);
            } catch (InvalidAlgorithmParameterException e2) {
                e2.printStackTrace();
            } catch (InvalidKeyException e3) {
                Log.e(TAG, "", e3);
            } catch (KeyStoreException e4) {
                Log.e(TAG, "", e4);
            } catch (NoSuchAlgorithmException e5) {
                Log.e(TAG, "", e5);
            } catch (NoSuchProviderException e6) {
                e6.printStackTrace();
            } catch (UnrecoverableEntryException e7) {
                Log.e(TAG, "", e7);
            } catch (CertificateException e8) {
                Log.e(TAG, "", e8);
            } catch (BadPaddingException e9) {
                Log.e(TAG, "", e9);
            } catch (IllegalBlockSizeException e10) {
                Log.e(TAG, "", e10);
            } catch (NoSuchPaddingException e11) {
                Log.e(TAG, "", e11);
            }
        }
        PrefManager.setStringPref(context, PrefManagerConstants.SHARED_PREFERENCE_SIGNIN_PASSWORD_KEY_AA_KEY, password);
    }

    public synchronized String userName(Context context) {
        if (TextUtils.isEmpty(this.mUserName)) {
            String userName = PrefManager.getStringPref(context, PrefManagerConstants.SHARED_PREFERENCE_SIGNIN_USERNAME_KEY_AA_KEY, "");
            this.mUserName = userName;
            if (PrefManager.getBooleanPref(context, PrefManagerConstants.SHARED_PREFERENCE_USE_KEY_STORE_KEY_AA_KEY, false) && !TextUtils.isEmpty(this.mUserName)) {
                try {
                    this.mUserName = decrypt(this.mUserName);
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                } catch (InvalidKeyException e2) {
                    Log.e(TAG, "", e2);
                } catch (KeyStoreException e3) {
                    Log.e(TAG, "", e3);
                } catch (NoSuchAlgorithmException e4) {
                    Log.e(TAG, "", e4);
                } catch (UnrecoverableEntryException e5) {
                    Log.e(TAG, "", e5);
                } catch (CertificateException e6) {
                    Log.e(TAG, "", e6);
                } catch (BadPaddingException e7) {
                    Log.e(TAG, "", e7);
                } catch (IllegalBlockSizeException e8) {
                    Log.e(TAG, "", e8);
                } catch (NoSuchPaddingException e9) {
                    Log.e(TAG, "", e9);
                }
            }
        }
        return this.mUserName;
    }

    public synchronized String password(Context context) {
        if (TextUtils.isEmpty(this.mPassword)) {
            String password = PrefManager.getStringPref(context, PrefManagerConstants.SHARED_PREFERENCE_SIGNIN_PASSWORD_KEY_AA_KEY, "");
            this.mPassword = password;
            if (PrefManager.getBooleanPref(context, PrefManagerConstants.SHARED_PREFERENCE_USE_KEY_STORE_KEY_AA_KEY, false) && !TextUtils.isEmpty(this.mPassword)) {
                try {
                    this.mPassword = decrypt(this.mPassword);
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                } catch (InvalidKeyException e2) {
                    Log.e(TAG, "", e2);
                } catch (KeyStoreException e3) {
                    Log.e(TAG, "", e3);
                } catch (NoSuchAlgorithmException e4) {
                    Log.e(TAG, "", e4);
                } catch (UnrecoverableEntryException e5) {
                    Log.e(TAG, "", e5);
                } catch (CertificateException e6) {
                    Log.e(TAG, "", e6);
                } catch (BadPaddingException e7) {
                    Log.e(TAG, "", e7);
                } catch (IllegalBlockSizeException e8) {
                    Log.e(TAG, "", e8);
                } catch (NoSuchPaddingException e9) {
                    Log.e(TAG, "", e9);
                }
            }
        }
        return this.mPassword;
    }

    private String encrypt(String item) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, CertificateException, KeyStoreException, UnrecoverableEntryException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {
        String encodedEncryptedFirstString;
        synchronized (this.lockObj) {
            Log.v(TAG, "encrypt - start");
            KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            ks.load(null);
            ks.getEntry(this.mAlias, null);
            Certificate cer = ks.getCertificate(this.mAlias);
            if (cer == null) {
                createKeys(this.context);
                ks.load(null);
                ks.getEntry(this.mAlias, null);
                cer = ks.getCertificate(this.mAlias);
            }
            PublicKey key = cer.getPublicKey();
            Cipher publicEncryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            publicEncryptCipher.init(1, key);
            byte[] encryptedFirstString = publicEncryptCipher.doFinal(item.getBytes());
            encodedEncryptedFirstString = Base64.encodeToString(encryptedFirstString, 0);
            Log.v(TAG, "encrypt - end");
        }
        return encodedEncryptedFirstString;
    }

    private String decrypt(String encodedEncryptedFirstString) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Log.v(TAG, "decrypt - start");
        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        ks.load(null);
        KeyStore.Entry entry = ks.getEntry(this.mAlias, null);
        if (entry == null) {
            return "";
        }
        Cipher privateEncryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        PrivateKey key = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
        privateEncryptCipher.init(2, key);
        byte[] decodedEncryptedFirstString = Base64.decode(encodedEncryptedFirstString, 0);
        byte[] decryptedFirstStringByte = privateEncryptCipher.doFinal(decodedEncryptedFirstString);
        Log.v(TAG, "decrypt - end");
        return new String(decryptedFirstStringByte);
    }

    @TargetApi(18)
    private void createKeys(Context context) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Log.v(TAG, "createKeys - start");
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        end.add(1, 10);
        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context).setAlias(this.mAlias).setSubject(new X500Principal("CN=" + this.mAlias)).setSerialNumber(BigInteger.valueOf(1337L)).setStartDate(start.getTime()).setEndDate(end.getTime()).build();
        KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(TYPE_RSA, KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        kpGenerator.initialize(spec);
        KeyPair kp = kpGenerator.generateKeyPair();
        Log.d(TAG, "Public Key is: " + kp.getPublic().toString());
        Log.v(TAG, "createKeys - end");
        PrefManager.setBooleanPref(context, PrefManagerConstants.SHARED_PREFERENCE_KEY_STORE_WAS_CREATED_KEY, true);
    }
}
