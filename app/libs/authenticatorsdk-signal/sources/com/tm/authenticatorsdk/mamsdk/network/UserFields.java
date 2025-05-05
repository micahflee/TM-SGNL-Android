package com.tm.authenticatorsdk.mamsdk.network;

import com.google.gson.annotations.SerializedName;
import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import com.tm.authenticatorsdk.mamsdk.MDMAuthenticatorKt;
import java.util.ArrayList;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: UserFields.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��6\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018��2\u00020\u0001BO\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\u0018\b\u0002\u0010\b\u001a\u0012\u0012\u0004\u0012\u00020\u00030\tj\b\u0012\u0004\u0012\u00020\u0003`\n¢\u0006\u0002\u0010\u000bJ\u000b\u0010\u001d\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u001e\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0010\u0010\u001f\u001a\u0004\u0018\u00010\u0006HÆ\u0003¢\u0006\u0002\u0010\rJ\u000b\u0010 \u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0019\u0010!\u001a\u0012\u0012\u0004\u0012\u00020\u00030\tj\b\u0012\u0004\u0012\u00020\u0003`\nHÆ\u0003JX\u0010\"\u001a\u00020��2\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\u0018\b\u0002\u0010\b\u001a\u0012\u0012\u0004\u0012\u00020\u00030\tj\b\u0012\u0004\u0012\u00020\u0003`\nHÆ\u0001¢\u0006\u0002\u0010#J\u0013\u0010$\u001a\u00020%2\b\u0010&\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010'\u001a\u00020(HÖ\u0001J\t\u0010)\u001a\u00020\u0003HÖ\u0001R\"\u0010\u0005\u001a\u0004\u0018\u00010\u00068\u0006@\u0006X\u0087\u000e¢\u0006\u0010\n\u0002\u0010\u0010\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR \u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R \u0010\u0007\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0015\u0010\u0012\"\u0004\b\u0016\u0010\u0014R \u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0017\u0010\u0012\"\u0004\b\u0018\u0010\u0014R.\u0010\b\u001a\u0012\u0012\u0004\u0012\u00020\u00030\tj\b\u0012\u0004\u0012\u00020\u0003`\n8\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001c¨\u0006*"}, d2 = {"Lcom/tm/authenticatorsdk/mamsdk/network/UserFields;", "", MDMAuthenticatorKt.NAME, "", "value", "date", "", "options", "values", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/String;Ljava/util/ArrayList;)V", "getDate", "()Ljava/lang/Long;", "setDate", "(Ljava/lang/Long;)V", "Ljava/lang/Long;", "getName", "()Ljava/lang/String;", "setName", "(Ljava/lang/String;)V", "getOptions", "setOptions", "getValue", "setValue", "getValues", "()Ljava/util/ArrayList;", "setValues", "(Ljava/util/ArrayList;)V", "component1", "component2", "component3", "component4", "component5", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/String;Ljava/util/ArrayList;)Lcom/tm/authenticatorsdk/mamsdk/network/UserFields;", "equals", "", "other", "hashCode", "", "toString", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/network/UserFields.class */
public final class UserFields {
    @SerializedName(MDMAuthenticatorKt.NAME)
    @Nullable
    private String name;
    @SerializedName("value")
    @Nullable
    private String value;
    @SerializedName("date")
    @Nullable
    private Long date;
    @SerializedName("options")
    @Nullable
    private String options;
    @SerializedName("values")
    @NotNull
    private ArrayList<String> values;

    @Nullable
    public final String component1() {
        return this.name;
    }

    @Nullable
    public final String component2() {
        return this.value;
    }

    @Nullable
    public final Long component3() {
        return this.date;
    }

    @Nullable
    public final String component4() {
        return this.options;
    }

    @NotNull
    public final ArrayList<String> component5() {
        return this.values;
    }

    @NotNull
    public final UserFields copy(@Nullable String name, @Nullable String value, @Nullable Long date, @Nullable String options, @NotNull ArrayList<String> arrayList) {
        Intrinsics.checkNotNullParameter(arrayList, "values");
        return new UserFields(name, value, date, options, arrayList);
    }

    public static /* synthetic */ UserFields copy$default(UserFields userFields, String str, String str2, Long l, String str3, ArrayList arrayList, int i, Object obj) {
        if ((i & 1) != 0) {
            str = userFields.name;
        }
        if ((i & 2) != 0) {
            str2 = userFields.value;
        }
        if ((i & 4) != 0) {
            l = userFields.date;
        }
        if ((i & 8) != 0) {
            str3 = userFields.options;
        }
        if ((i & 16) != 0) {
            arrayList = userFields.values;
        }
        return userFields.copy(str, str2, l, str3, arrayList);
    }

    @NotNull
    public String toString() {
        return "UserFields(name=" + this.name + ", value=" + this.value + ", date=" + this.date + ", options=" + this.options + ", values=" + this.values + ')';
    }

    public int hashCode() {
        int result = this.name == null ? 0 : this.name.hashCode();
        return (((((((result * 31) + (this.value == null ? 0 : this.value.hashCode())) * 31) + (this.date == null ? 0 : this.date.hashCode())) * 31) + (this.options == null ? 0 : this.options.hashCode())) * 31) + this.values.hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof UserFields) {
            UserFields userFields = (UserFields) other;
            return Intrinsics.areEqual(this.name, userFields.name) && Intrinsics.areEqual(this.value, userFields.value) && Intrinsics.areEqual(this.date, userFields.date) && Intrinsics.areEqual(this.options, userFields.options) && Intrinsics.areEqual(this.values, userFields.values);
        }
        return false;
    }

    public UserFields() {
        this(null, null, null, null, null, 31, null);
    }

    public UserFields(@Nullable String name, @Nullable String value, @Nullable Long date, @Nullable String options, @NotNull ArrayList<String> arrayList) {
        Intrinsics.checkNotNullParameter(arrayList, "values");
        this.name = name;
        this.value = value;
        this.date = date;
        this.options = options;
        this.values = arrayList;
    }

    public /* synthetic */ UserFields(String str, String str2, Long l, String str3, ArrayList arrayList, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : str, (i & 2) != 0 ? null : str2, (i & 4) != 0 ? null : l, (i & 8) != 0 ? null : str3, (i & 16) != 0 ? new ArrayList() : arrayList);
    }

    @Nullable
    public final String getName() {
        return this.name;
    }

    public final void setName(@Nullable String str) {
        this.name = str;
    }

    @Nullable
    public final String getValue() {
        return this.value;
    }

    public final void setValue(@Nullable String str) {
        this.value = str;
    }

    @Nullable
    public final Long getDate() {
        return this.date;
    }

    public final void setDate(@Nullable Long l) {
        this.date = l;
    }

    @Nullable
    public final String getOptions() {
        return this.options;
    }

    public final void setOptions(@Nullable String str) {
        this.options = str;
    }

    @NotNull
    public final ArrayList<String> getValues() {
        return this.values;
    }

    public final void setValues(@NotNull ArrayList<String> arrayList) {
        Intrinsics.checkNotNullParameter(arrayList, "<set-?>");
        this.values = arrayList;
    }
}
