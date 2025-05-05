package com.tm.androidcopysdk.network;

import android.content.Context;
import android.util.Base64;
import com.tm.androidcopysdk.utils.PrefManager;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/SSLFactoryUtils.class */
public class SSLFactoryUtils {
    public static final String SSL_CERT_ARRAY_AS_STRING = "certArr";

    public static String[] getCertArrayFromPreference(Context context) {
        String certString = PrefManager.getStringPref(context, SSL_CERT_ARRAY_AS_STRING, "");
        return certString.split(",");
    }

    public static void setCertArray(Context context, String... certArray) {
        StringBuilder prefToSave = new StringBuilder();
        for (int i = 0; i < certArray.length; i++) {
            if (i + 1 != certArray.length) {
                prefToSave.append(certArray[i]).append(",");
            } else {
                prefToSave.append(certArray[i]);
            }
        }
        PrefManager.setStringPref(context, SSL_CERT_ARRAY_AS_STRING, prefToSave.toString());
    }

    public static X509TrustManager systemDefaultTrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (GeneralSecurityException e) {
            throw new AssertionError();
        }
    }

    public static SSLSocketFactory createCertificatePinSSLSocketFactory(String... keys) {
        try {
            KeyStore store = KeyStore.getInstance("AndroidKeyStore");
            store.load(null);
            for (int i = 0; i < keys.length; i++) {
                InputStream targetStream = new ByteArrayInputStream(Base64.decode(keys[i], 0));
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate ca = cf.generateCertificate(targetStream);
                store.setCertificateEntry("av-ca" + i, ca);
            }
            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init(store);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, factory.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            return null;
        }
    }
}
