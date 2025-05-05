package com.tm.authenticatorsdk.mamsdk;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.intune.mam.policy.MAMServiceAuthenticationCallback;
import com.tm.logger.Log;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/AuthenticationCallback.class */
public class AuthenticationCallback implements MAMServiceAuthenticationCallback {
    private final Context mContext;

    public AuthenticationCallback(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Nullable
    public String acquireToken(@NonNull String upn, @NonNull String aadId, @NonNull String resourceId) {
        try {
            String[] scopes = {resourceId + "/.default"};
            IAuthenticationResult result = MSALUtil.acquireTokenSilentSync(this.mContext, aadId, scopes);
            if (result != null) {
                return result.getAccessToken();
            }
            Log.w("AuthenticationCallback", "Failed to get token for MAM Service - no result from MSAL");
            return null;
        } catch (MsalException | InterruptedException e) {
            Log.e("AuthenticationCallback", "Failed to get token for MAM Service", e);
            return null;
        }
    }
}
