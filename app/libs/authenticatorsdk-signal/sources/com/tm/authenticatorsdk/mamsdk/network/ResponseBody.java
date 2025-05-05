package com.tm.authenticatorsdk.mamsdk.network;

import com.google.gson.annotations.SerializedName;
import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import java.util.ArrayList;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ResponseBody.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��.\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\b\n��\n\u0002\u0010\u000e\n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018��2\u00020\u0001BC\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0018\b\u0002\u0010\u0006\u001a\u0012\u0012\u0004\u0012\u00020\b0\u0007j\b\u0012\u0004\u0012\u00020\b`\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0005¢\u0006\u0002\u0010\u000bJ\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u0003HÆ\u0003¢\u0006\u0002\u0010\u0011J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u0019\u0010\u001d\u001a\u0012\u0012\u0004\u0012\u00020\b0\u0007j\b\u0012\u0004\u0012\u00020\b`\tHÆ\u0003J\u000b\u0010\u001e\u001a\u0004\u0018\u00010\u0005HÆ\u0003JL\u0010\u001f\u001a\u00020��2\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0018\b\u0002\u0010\u0006\u001a\u0012\u0012\u0004\u0012\u00020\b0\u0007j\b\u0012\u0004\u0012\u00020\b`\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0005HÆ\u0001¢\u0006\u0002\u0010 J\u0013\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010$\u001a\u00020\u0003HÖ\u0001J\t\u0010%\u001a\u00020\u0005HÖ\u0001R \u0010\n\u001a\u0004\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\"\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0010\n\u0002\u0010\u0014\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R \u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0015\u0010\r\"\u0004\b\u0016\u0010\u000fR.\u0010\u0006\u001a\u0012\u0012\u0004\u0012\u00020\b0\u0007j\b\u0012\u0004\u0012\u00020\b`\t8\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001a¨\u0006&"}, d2 = {"Lcom/tm/authenticatorsdk/mamsdk/network/ResponseBody;", "", "resultCode", "", "resultDescription", "", "userFields", "Ljava/util/ArrayList;", "Lcom/tm/authenticatorsdk/mamsdk/network/UserFields;", "Lkotlin/collections/ArrayList;", "contentSize", "(Ljava/lang/Integer;Ljava/lang/String;Ljava/util/ArrayList;Ljava/lang/String;)V", "getContentSize", "()Ljava/lang/String;", "setContentSize", "(Ljava/lang/String;)V", "getResultCode", "()Ljava/lang/Integer;", "setResultCode", "(Ljava/lang/Integer;)V", "Ljava/lang/Integer;", "getResultDescription", "setResultDescription", "getUserFields", "()Ljava/util/ArrayList;", "setUserFields", "(Ljava/util/ArrayList;)V", "component1", "component2", "component3", "component4", "copy", "(Ljava/lang/Integer;Ljava/lang/String;Ljava/util/ArrayList;Ljava/lang/String;)Lcom/tm/authenticatorsdk/mamsdk/network/ResponseBody;", "equals", "", "other", "hashCode", "toString", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/network/ResponseBody.class */
public final class ResponseBody {
    @SerializedName("resultCode")
    @Nullable
    private Integer resultCode;
    @SerializedName("resultDescription")
    @Nullable
    private String resultDescription;
    @SerializedName("userFields")
    @NotNull
    private ArrayList<UserFields> userFields;
    @SerializedName("contentSize")
    @Nullable
    private String contentSize;

    @Nullable
    public final Integer component1() {
        return this.resultCode;
    }

    @Nullable
    public final String component2() {
        return this.resultDescription;
    }

    @NotNull
    public final ArrayList<UserFields> component3() {
        return this.userFields;
    }

    @Nullable
    public final String component4() {
        return this.contentSize;
    }

    @NotNull
    public final ResponseBody copy(@Nullable Integer resultCode, @Nullable String resultDescription, @NotNull ArrayList<UserFields> arrayList, @Nullable String contentSize) {
        Intrinsics.checkNotNullParameter(arrayList, "userFields");
        return new ResponseBody(resultCode, resultDescription, arrayList, contentSize);
    }

    public static /* synthetic */ ResponseBody copy$default(ResponseBody responseBody, Integer num, String str, ArrayList arrayList, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            num = responseBody.resultCode;
        }
        if ((i & 2) != 0) {
            str = responseBody.resultDescription;
        }
        if ((i & 4) != 0) {
            arrayList = responseBody.userFields;
        }
        if ((i & 8) != 0) {
            str2 = responseBody.contentSize;
        }
        return responseBody.copy(num, str, arrayList, str2);
    }

    @NotNull
    public String toString() {
        return "ResponseBody(resultCode=" + this.resultCode + ", resultDescription=" + this.resultDescription + ", userFields=" + this.userFields + ", contentSize=" + this.contentSize + ')';
    }

    public int hashCode() {
        int result = this.resultCode == null ? 0 : this.resultCode.hashCode();
        return (((((result * 31) + (this.resultDescription == null ? 0 : this.resultDescription.hashCode())) * 31) + this.userFields.hashCode()) * 31) + (this.contentSize == null ? 0 : this.contentSize.hashCode());
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ResponseBody) {
            ResponseBody responseBody = (ResponseBody) other;
            return Intrinsics.areEqual(this.resultCode, responseBody.resultCode) && Intrinsics.areEqual(this.resultDescription, responseBody.resultDescription) && Intrinsics.areEqual(this.userFields, responseBody.userFields) && Intrinsics.areEqual(this.contentSize, responseBody.contentSize);
        }
        return false;
    }

    public ResponseBody() {
        this(null, null, null, null, 15, null);
    }

    public ResponseBody(@Nullable Integer resultCode, @Nullable String resultDescription, @NotNull ArrayList<UserFields> arrayList, @Nullable String contentSize) {
        Intrinsics.checkNotNullParameter(arrayList, "userFields");
        this.resultCode = resultCode;
        this.resultDescription = resultDescription;
        this.userFields = arrayList;
        this.contentSize = contentSize;
    }

    public /* synthetic */ ResponseBody(Integer num, String str, ArrayList arrayList, String str2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : num, (i & 2) != 0 ? null : str, (i & 4) != 0 ? new ArrayList() : arrayList, (i & 8) != 0 ? null : str2);
    }

    @Nullable
    public final Integer getResultCode() {
        return this.resultCode;
    }

    public final void setResultCode(@Nullable Integer num) {
        this.resultCode = num;
    }

    @Nullable
    public final String getResultDescription() {
        return this.resultDescription;
    }

    public final void setResultDescription(@Nullable String str) {
        this.resultDescription = str;
    }

    @NotNull
    public final ArrayList<UserFields> getUserFields() {
        return this.userFields;
    }

    public final void setUserFields(@NotNull ArrayList<UserFields> arrayList) {
        Intrinsics.checkNotNullParameter(arrayList, "<set-?>");
        this.userFields = arrayList;
    }

    @Nullable
    public final String getContentSize() {
        return this.contentSize;
    }

    public final void setContentSize(@Nullable String str) {
        this.contentSize = str;
    }
}
