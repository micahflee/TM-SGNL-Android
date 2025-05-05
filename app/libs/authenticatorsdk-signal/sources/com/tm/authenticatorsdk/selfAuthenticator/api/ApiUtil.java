package com.tm.authenticatorsdk.selfAuthenticator.api;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import com.tm.authenticatorsdk.selfAuthenticator.AuthenticatorConstants;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ApiUtil.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��\f\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0003\u0018�� \u00032\u00020\u0001:\u0001\u0003B\u0005¢\u0006\u0002\u0010\u0002¨\u0006\u0004"}, d2 = {"Lcom/tm/authenticatorsdk/selfAuthenticator/api/ApiUtil;", "", "()V", "Companion", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/api/ApiUtil.class */
public final class ApiUtil {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @Nullable
    private static AlertDialog levelDialog;

    /* compiled from: ApiUtil.kt */
    @Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��\u001e\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u0002\n��\n\u0002\u0018\u0002\n��\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bR\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e¢\u0006\u0002\n��¨\u0006\t"}, d2 = {"Lcom/tm/authenticatorsdk/selfAuthenticator/api/ApiUtil$Companion;", "", "()V", "levelDialog", "Landroid/app/AlertDialog;", "selectServerEnvironment", "", "context", "Landroid/content/Context;", "authenticatorsdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/api/ApiUtil$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }

        public final void selectServerEnvironment(@NotNull Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            boolean isDebuggable = (context.getApplicationInfo().flags & 2) != 0;
            if (!isDebuggable) {
                AuthenticatorConstants.Companion.setBASE_URL(AuthenticatorConstants.Companion.getCharlieURL());
                AuthenticatorConstants.Companion.setBASE_URL_MDM(AuthenticatorConstants.Companion.getProductionUrlMdm());
                return;
            }
            CharSequence[] items = {"CharlieProduction", "CharlieQA", "Integration"};
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Environment");
            builder.setSingleChoiceItems(items, -1, Companion::selectServerEnvironment$lambda$0);
            ApiUtil.levelDialog = builder.create();
            AlertDialog alertDialog = ApiUtil.levelDialog;
            Intrinsics.checkNotNull(alertDialog);
            alertDialog.show();
        }

        private static final void selectServerEnvironment$lambda$0(DialogInterface dialog, int item) {
            switch (item) {
                case BuildConfig.DEBUG /* 0 */:
                    AuthenticatorConstants.Companion.setBASE_URL(AuthenticatorConstants.Companion.getCharlieURL());
                    AuthenticatorConstants.Companion.setBASE_URL_MDM(AuthenticatorConstants.Companion.getProductionUrlMdm());
                    AlertDialog alertDialog = ApiUtil.levelDialog;
                    Intrinsics.checkNotNull(alertDialog);
                    alertDialog.dismiss();
                    return;
                case 1:
                    AuthenticatorConstants.Companion.setBASE_URL(AuthenticatorConstants.Companion.getCharlieQaURL());
                    AuthenticatorConstants.Companion.setBASE_URL_MDM(AuthenticatorConstants.Companion.getIntegrationUrlMdm());
                    AlertDialog alertDialog2 = ApiUtil.levelDialog;
                    Intrinsics.checkNotNull(alertDialog2);
                    alertDialog2.dismiss();
                    return;
                case 2:
                    AuthenticatorConstants.Companion.setBASE_URL(AuthenticatorConstants.Companion.getIntegrationURL());
                    AuthenticatorConstants.Companion.setBASE_URL_MDM(AuthenticatorConstants.Companion.getIntegrationUrlMdm());
                    AlertDialog alertDialog3 = ApiUtil.levelDialog;
                    Intrinsics.checkNotNull(alertDialog3);
                    alertDialog3.dismiss();
                    return;
                default:
                    return;
            }
        }
    }
}
