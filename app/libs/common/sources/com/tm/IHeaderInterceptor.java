package com.tm;

import com.tm.utils.Definitions;
import kotlin.Metadata;
import okhttp3.Interceptor;
import org.jetbrains.annotations.Nullable;
/* compiled from: IHeaderInterceptor.kt */
@Metadata(mv = {1, Definitions.sendSubject, 0}, k = 1, xi = 48, d1 = {"��\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\f\bf\u0018��2\u00020\u0001J\u001c\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005H&J\u0012\u0010\u0007\u001a\u00020\u00032\b\u0010\b\u001a\u0004\u0018\u00010\u0005H&J\u0012\u0010\t\u001a\u00020\u00032\b\u0010\n\u001a\u0004\u0018\u00010\u0005H&J\u0012\u0010\u000b\u001a\u00020\u00032\b\u0010\f\u001a\u0004\u0018\u00010\u0005H&J\u0012\u0010\r\u001a\u00020\u00032\b\u0010\u000e\u001a\u0004\u0018\u00010\u0005H&J\u0012\u0010\u000f\u001a\u00020\u00032\b\u0010\u0010\u001a\u0004\u0018\u00010\u0005H&¨\u0006\u0011"}, d2 = {"Lcom/tm/IHeaderInterceptor;", "Lokhttp3/Interceptor;", "setAuthentication", "", "userName", "", Definitions.adPassword, "setDate", Definitions.tDate, "setMessageId", "messageId", "setSignature", "signature", "setToken", "token", "setUid", "uid", "common_release"})
/* loaded from: input.aar:classes.jar:com/tm/IHeaderInterceptor.class */
public interface IHeaderInterceptor extends Interceptor {
    void setAuthentication(@Nullable String str, @Nullable String str2);

    void setMessageId(@Nullable String str);

    void setSignature(@Nullable String str);

    void setDate(@Nullable String str);

    void setUid(@Nullable String str);

    void setToken(@Nullable String str);
}
