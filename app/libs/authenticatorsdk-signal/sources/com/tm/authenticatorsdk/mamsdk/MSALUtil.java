package com.tm.authenticatorsdk.mamsdk;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import com.microsoft.identity.client.AcquireTokenParameters;
import com.microsoft.identity.client.AcquireTokenSilentParameters;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ICurrentAccountResult;
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.Logger;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.tm.authenticatorsdk.R;
import java.util.Arrays;
import java.util.logging.Logger;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/MSALUtil.class */
public final class MSALUtil {
    private static final Logger LOGGER = Logger.getLogger(MSALUtil.class.getName());
    private static IPublicClientApplication mMsalClientApplication;

    private MSALUtil() {
    }

    @WorkerThread
    public static void acquireToken(@NonNull Activity fromActivity, @NonNull String[] scopes, String loginHint, @NonNull com.microsoft.identity.client.AuthenticationCallback callback) throws MsalException, InterruptedException {
        initializeMsalClientApplication(fromActivity.getApplicationContext());
        AcquireTokenParameters params = new AcquireTokenParameters.Builder().withScopes(Arrays.asList(scopes)).withCallback(callback).startAuthorizationFromActivity(fromActivity).withLoginHint(loginHint).build();
        mMsalClientApplication.acquireToken(params);
    }

    @WorkerThread
    public static void acquireTokenSilent(@NonNull Context appContext, @NonNull String aadId, @NonNull String[] scopes, @NonNull com.microsoft.identity.client.AuthenticationCallback callback) throws MsalException, InterruptedException {
        initializeMsalClientApplication(appContext.getApplicationContext());
        IAccount account = getAccount(aadId);
        if (account == null) {
            LOGGER.severe("Failed to acquire token: no account found for " + aadId);
            callback.onError(new MsalUiRequiredException("no_account_found", "no account found for " + aadId));
            return;
        }
        AcquireTokenSilentParameters params = new AcquireTokenSilentParameters.Builder().forAccount(account).fromAuthority(account.getAuthority()).withScopes(Arrays.asList(scopes)).withCallback(callback).build();
        mMsalClientApplication.acquireTokenSilentAsync(params);
    }

    @WorkerThread
    public static IAuthenticationResult acquireTokenSilentSync(@NonNull Context appContext, @NonNull String aadId, @NonNull String[] scopes) throws MsalException, InterruptedException {
        initializeMsalClientApplication(appContext);
        IAccount account = getAccount(aadId);
        if (account == null) {
            LOGGER.severe("Failed to acquire token: no account found for " + aadId);
            throw new MsalUiRequiredException("no_account_found", "no account found for " + aadId);
        }
        AcquireTokenSilentParameters params = new AcquireTokenSilentParameters.Builder().forAccount(account).fromAuthority(account.getAuthority()).withScopes(Arrays.asList(scopes)).build();
        return mMsalClientApplication.acquireTokenSilent(params);
    }

    public static void signOutAccount(@NonNull Context appContext, @NonNull String aadId) throws MsalException, InterruptedException {
        initializeMsalClientApplication(appContext);
        IAccount account = getAccount(aadId);
        if (account == null) {
            LOGGER.warning("Failed to sign out account: No account found for " + aadId);
        } else if (mMsalClientApplication instanceof IMultipleAccountPublicClientApplication) {
            IMultipleAccountPublicClientApplication multiAccountPCA = mMsalClientApplication;
            multiAccountPCA.removeAccount(account);
        } else {
            ISingleAccountPublicClientApplication singleAccountPCA = mMsalClientApplication;
            singleAccountPCA.signOut();
        }
    }

    private static IAccount getAccount(String aadId) throws InterruptedException, MsalException {
        IAccount account = null;
        if (mMsalClientApplication instanceof IMultipleAccountPublicClientApplication) {
            IMultipleAccountPublicClientApplication multiAccountPCA = mMsalClientApplication;
            account = multiAccountPCA.getAccount(aadId);
        } else {
            ISingleAccountPublicClientApplication singleAccountPCA = mMsalClientApplication;
            ICurrentAccountResult accountResult = singleAccountPCA.getCurrentAccount();
            if (accountResult != null) {
                account = accountResult.getCurrentAccount();
                if (account != null && !account.getId().equals(aadId)) {
                    account = null;
                }
            }
        }
        return account;
    }

    private static synchronized void initializeMsalClientApplication(Context appContext) throws MsalException, InterruptedException {
        if (mMsalClientApplication == null) {
            com.microsoft.identity.client.Logger msalLogger = com.microsoft.identity.client.Logger.getInstance();
            msalLogger.setEnableLogcatLog(true);
            msalLogger.setLogLevel(Logger.LogLevel.VERBOSE);
            msalLogger.setEnablePII(true);
            mMsalClientApplication = PublicClientApplication.create(appContext, R.raw.auth_config);
        }
    }
}
