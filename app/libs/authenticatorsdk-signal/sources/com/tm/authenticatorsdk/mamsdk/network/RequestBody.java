package com.tm.authenticatorsdk.mamsdk.network;

import com.google.gson.annotations.SerializedName;
import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: RequestBody.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��\"\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n\u0002\b0\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018��2\u00020\u0001B\u0089\u0001\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0003¢\u0006\u0002\u0010\u000eJ\u000b\u0010'\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010(\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010)\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010*\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010+\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010,\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010-\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010.\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010/\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u00100\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u00101\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u008d\u0001\u00102\u001a\u00020��2\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0003HÆ\u0001J\u0013\u00103\u001a\u0002042\b\u00105\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u00106\u001a\u000207HÖ\u0001J\t\u00108\u001a\u00020\u0003HÖ\u0001R \u0010\f\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R \u0010\u000b\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0013\u0010\u0010\"\u0004\b\u0014\u0010\u0012R \u0010\r\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0015\u0010\u0010\"\u0004\b\u0016\u0010\u0012R \u0010\t\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0017\u0010\u0010\"\u0004\b\u0018\u0010\u0012R \u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0019\u0010\u0010\"\u0004\b\u001a\u0010\u0012R \u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\u001b\u0010\u0010\"\u0004\b\u001c\u0010\u0012R \u0010\u0005\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\u001d\u0010\u0010\"\u0004\b\u001e\u0010\u0012R \u0010\u0007\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\u001f\u0010\u0010\"\u0004\b \u0010\u0012R \u0010\b\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b!\u0010\u0010\"\u0004\b\"\u0010\u0012R \u0010\n\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b#\u0010\u0010\"\u0004\b$\u0010\u0012R \u0010\u0006\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b%\u0010\u0010\"\u0004\b&\u0010\u0012¨\u00069"}, d2 = {"Lcom/tm/authenticatorsdk/mamsdk/network/RequestBody;", "", "managerName", "", "email", "mobilePhoneNumber", "udId", "requestDate", "signature", "deviceType", "token", "appIdentifier", "appId", "appVersion", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getAppId", "()Ljava/lang/String;", "setAppId", "(Ljava/lang/String;)V", "getAppIdentifier", "setAppIdentifier", "getAppVersion", "setAppVersion", "getDeviceType", "setDeviceType", "getEmail", "setEmail", "getManagerName", "setManagerName", "getMobilePhoneNumber", "setMobilePhoneNumber", "getRequestDate", "setRequestDate", "getSignature", "setSignature", "getToken", "setToken", "getUdId", "setUdId", "component1", "component10", "component11", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "", "toString", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/network/RequestBody.class */
public final class RequestBody {
    @SerializedName("managerName")
    @Nullable
    private String managerName;
    @SerializedName("email")
    @Nullable
    private String email;
    @SerializedName("mobilePhoneNumber")
    @Nullable
    private String mobilePhoneNumber;
    @SerializedName("udId")
    @Nullable
    private String udId;
    @SerializedName("requestDate")
    @Nullable
    private String requestDate;
    @SerializedName("signature")
    @Nullable
    private String signature;
    @SerializedName("deviceType")
    @Nullable
    private String deviceType;
    @SerializedName("token")
    @Nullable
    private String token;
    @SerializedName("appIdentifier")
    @Nullable
    private String appIdentifier;
    @SerializedName("appId")
    @Nullable
    private String appId;
    @SerializedName("appVersion")
    @Nullable
    private String appVersion;

    @Nullable
    public final String component1() {
        return this.managerName;
    }

    @Nullable
    public final String component2() {
        return this.email;
    }

    @Nullable
    public final String component3() {
        return this.mobilePhoneNumber;
    }

    @Nullable
    public final String component4() {
        return this.udId;
    }

    @Nullable
    public final String component5() {
        return this.requestDate;
    }

    @Nullable
    public final String component6() {
        return this.signature;
    }

    @Nullable
    public final String component7() {
        return this.deviceType;
    }

    @Nullable
    public final String component8() {
        return this.token;
    }

    @Nullable
    public final String component9() {
        return this.appIdentifier;
    }

    @Nullable
    public final String component10() {
        return this.appId;
    }

    @Nullable
    public final String component11() {
        return this.appVersion;
    }

    @NotNull
    public final RequestBody copy(@Nullable String managerName, @Nullable String email, @Nullable String mobilePhoneNumber, @Nullable String udId, @Nullable String requestDate, @Nullable String signature, @Nullable String deviceType, @Nullable String token, @Nullable String appIdentifier, @Nullable String appId, @Nullable String appVersion) {
        return new RequestBody(managerName, email, mobilePhoneNumber, udId, requestDate, signature, deviceType, token, appIdentifier, appId, appVersion);
    }

    public static /* synthetic */ RequestBody copy$default(RequestBody requestBody, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, int i, Object obj) {
        if ((i & 1) != 0) {
            str = requestBody.managerName;
        }
        if ((i & 2) != 0) {
            str2 = requestBody.email;
        }
        if ((i & 4) != 0) {
            str3 = requestBody.mobilePhoneNumber;
        }
        if ((i & 8) != 0) {
            str4 = requestBody.udId;
        }
        if ((i & 16) != 0) {
            str5 = requestBody.requestDate;
        }
        if ((i & 32) != 0) {
            str6 = requestBody.signature;
        }
        if ((i & 64) != 0) {
            str7 = requestBody.deviceType;
        }
        if ((i & 128) != 0) {
            str8 = requestBody.token;
        }
        if ((i & 256) != 0) {
            str9 = requestBody.appIdentifier;
        }
        if ((i & 512) != 0) {
            str10 = requestBody.appId;
        }
        if ((i & 1024) != 0) {
            str11 = requestBody.appVersion;
        }
        return requestBody.copy(str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11);
    }

    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RequestBody(managerName=").append(this.managerName).append(", email=").append(this.email).append(", mobilePhoneNumber=").append(this.mobilePhoneNumber).append(", udId=").append(this.udId).append(", requestDate=").append(this.requestDate).append(", signature=").append(this.signature).append(", deviceType=").append(this.deviceType).append(", token=").append(this.token).append(", appIdentifier=").append(this.appIdentifier).append(", appId=").append(this.appId).append(", appVersion=").append(this.appVersion).append(')');
        return sb.toString();
    }

    public int hashCode() {
        int result = this.managerName == null ? 0 : this.managerName.hashCode();
        return (((((((((((((((((((result * 31) + (this.email == null ? 0 : this.email.hashCode())) * 31) + (this.mobilePhoneNumber == null ? 0 : this.mobilePhoneNumber.hashCode())) * 31) + (this.udId == null ? 0 : this.udId.hashCode())) * 31) + (this.requestDate == null ? 0 : this.requestDate.hashCode())) * 31) + (this.signature == null ? 0 : this.signature.hashCode())) * 31) + (this.deviceType == null ? 0 : this.deviceType.hashCode())) * 31) + (this.token == null ? 0 : this.token.hashCode())) * 31) + (this.appIdentifier == null ? 0 : this.appIdentifier.hashCode())) * 31) + (this.appId == null ? 0 : this.appId.hashCode())) * 31) + (this.appVersion == null ? 0 : this.appVersion.hashCode());
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof RequestBody) {
            RequestBody requestBody = (RequestBody) other;
            return Intrinsics.areEqual(this.managerName, requestBody.managerName) && Intrinsics.areEqual(this.email, requestBody.email) && Intrinsics.areEqual(this.mobilePhoneNumber, requestBody.mobilePhoneNumber) && Intrinsics.areEqual(this.udId, requestBody.udId) && Intrinsics.areEqual(this.requestDate, requestBody.requestDate) && Intrinsics.areEqual(this.signature, requestBody.signature) && Intrinsics.areEqual(this.deviceType, requestBody.deviceType) && Intrinsics.areEqual(this.token, requestBody.token) && Intrinsics.areEqual(this.appIdentifier, requestBody.appIdentifier) && Intrinsics.areEqual(this.appId, requestBody.appId) && Intrinsics.areEqual(this.appVersion, requestBody.appVersion);
        }
        return false;
    }

    public RequestBody() {
        this(null, null, null, null, null, null, null, null, null, null, null, 2047, null);
    }

    public RequestBody(@Nullable String managerName, @Nullable String email, @Nullable String mobilePhoneNumber, @Nullable String udId, @Nullable String requestDate, @Nullable String signature, @Nullable String deviceType, @Nullable String token, @Nullable String appIdentifier, @Nullable String appId, @Nullable String appVersion) {
        this.managerName = managerName;
        this.email = email;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.udId = udId;
        this.requestDate = requestDate;
        this.signature = signature;
        this.deviceType = deviceType;
        this.token = token;
        this.appIdentifier = appIdentifier;
        this.appId = appId;
        this.appVersion = appVersion;
    }

    public /* synthetic */ RequestBody(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : str, (i & 2) != 0 ? null : str2, (i & 4) != 0 ? null : str3, (i & 8) != 0 ? null : str4, (i & 16) != 0 ? null : str5, (i & 32) != 0 ? null : str6, (i & 64) != 0 ? null : str7, (i & 128) != 0 ? null : str8, (i & 256) != 0 ? null : str9, (i & 512) != 0 ? null : str10, (i & 1024) != 0 ? null : str11);
    }

    @Nullable
    public final String getManagerName() {
        return this.managerName;
    }

    public final void setManagerName(@Nullable String str) {
        this.managerName = str;
    }

    @Nullable
    public final String getEmail() {
        return this.email;
    }

    public final void setEmail(@Nullable String str) {
        this.email = str;
    }

    @Nullable
    public final String getMobilePhoneNumber() {
        return this.mobilePhoneNumber;
    }

    public final void setMobilePhoneNumber(@Nullable String str) {
        this.mobilePhoneNumber = str;
    }

    @Nullable
    public final String getUdId() {
        return this.udId;
    }

    public final void setUdId(@Nullable String str) {
        this.udId = str;
    }

    @Nullable
    public final String getRequestDate() {
        return this.requestDate;
    }

    public final void setRequestDate(@Nullable String str) {
        this.requestDate = str;
    }

    @Nullable
    public final String getSignature() {
        return this.signature;
    }

    public final void setSignature(@Nullable String str) {
        this.signature = str;
    }

    @Nullable
    public final String getDeviceType() {
        return this.deviceType;
    }

    public final void setDeviceType(@Nullable String str) {
        this.deviceType = str;
    }

    @Nullable
    public final String getToken() {
        return this.token;
    }

    public final void setToken(@Nullable String str) {
        this.token = str;
    }

    @Nullable
    public final String getAppIdentifier() {
        return this.appIdentifier;
    }

    public final void setAppIdentifier(@Nullable String str) {
        this.appIdentifier = str;
    }

    @Nullable
    public final String getAppId() {
        return this.appId;
    }

    public final void setAppId(@Nullable String str) {
        this.appId = str;
    }

    @Nullable
    public final String getAppVersion() {
        return this.appVersion;
    }

    public final void setAppVersion(@Nullable String str) {
        this.appVersion = str;
    }
}
