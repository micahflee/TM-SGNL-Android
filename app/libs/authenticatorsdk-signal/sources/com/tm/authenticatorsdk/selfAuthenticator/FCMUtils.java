package com.tm.authenticatorsdk.selfAuthenticator;

import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import com.tm.authenticatorsdk.socgen.signup.CryptoUtil;
import com.tm.logger.Log;
import com.tm.utils.ApplicationInterface;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: FCMUtils.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��\f\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0003\u0018�� \u00032\u00020\u0001:\u0001\u0003B\u0005¢\u0006\u0002\u0010\u0002¨\u0006\u0004"}, d2 = {"Lcom/tm/authenticatorsdk/selfAuthenticator/FCMUtils;", "", "()V", "Companion", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/FCMUtils.class */
public final class FCMUtils {
    @NotNull
    public static final Companion Companion = new Companion(null);

    /* compiled from: FCMUtils.kt */
    @Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��&\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n��\n\u0002\u0010\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004J\u0018\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nJ\u001e\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u0004¨\u0006\u000f"}, d2 = {"Lcom/tm/authenticatorsdk/selfAuthenticator/FCMUtils$Companion;", "", "()V", "getDateFormatted", "", "getFCMToken", "", "applicationInterface", "Lcom/tm/utils/ApplicationInterface;", "fcmTokenCallBack", "Lcom/tm/authenticatorsdk/selfAuthenticator/IFCMTokenArrived;", "getSignature", "mobile", "pushNotificationToken", "dateFormat", "authenticatorsdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/FCMUtils$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }

        public final void getFCMToken(@NotNull ApplicationInterface applicationInterface, @Nullable IFCMTokenArrived fcmTokenCallBack) {
            Intrinsics.checkNotNullParameter(applicationInterface, "applicationInterface");
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener((v2) -> {
                getFCMToken$lambda$0(r1, r2, v2);
            });
        }

        private static final void getFCMToken$lambda$0(IFCMTokenArrived $fcmTokenCallBack, ApplicationInterface $applicationInterface, Task task) {
            Intrinsics.checkNotNullParameter($applicationInterface, "$applicationInterface");
            Intrinsics.checkNotNullParameter(task, "task");
            if (!task.isSuccessful()) {
                Log.w("SelfAuthenticatorM", "Fetching FCM registration token failed", task.getException());
                return;
            }
            String token = (String) task.getResult();
            if ($fcmTokenCallBack != null) {
                Intrinsics.checkNotNull(token);
                $fcmTokenCallBack.onFCMTokenArrived($applicationInterface, token);
            }
        }

        @NotNull
        public final String getSignature(@NotNull String mobile, @NotNull String pushNotificationToken, @NotNull String dateFormat) {
            Intrinsics.checkNotNullParameter(mobile, "mobile");
            Intrinsics.checkNotNullParameter(pushNotificationToken, "pushNotificationToken");
            Intrinsics.checkNotNullParameter(dateFormat, "dateFormat");
            StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
            Object[] objArr = {mobile, pushNotificationToken, dateFormat};
            String stc = String.format("%s%s%s", Arrays.copyOf(objArr, objArr.length));
            Intrinsics.checkNotNullExpressionValue(stc, "format(...)");
            String signature = CryptoUtil.encodeHmac512(AuthenticatorConstants.Companion.getEncodeKey(), stc);
            Log.d("getFCMToken", "dateFormatted = " + dateFormat + "  signature = " + signature);
            Intrinsics.checkNotNull(signature);
            return signature;
        }

        @NotNull
        public final String getDateFormatted() {
            Date date = new Date();
            date.setTime(System.currentTimeMillis());
            String format = new SimpleDateFormat("EEE, dd LLL yyyy HH:mm:ss Z", Locale.ENGLISH).format(date);
            Intrinsics.checkNotNullExpressionValue(format, "format(...)");
            return format;
        }
    }
}
