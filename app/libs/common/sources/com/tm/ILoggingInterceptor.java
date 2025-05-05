package com.tm;

import com.tm.utils.Definitions;
import kotlin.Metadata;
import okhttp3.Interceptor;
/* compiled from: ILoggingInterceptor.kt */
@Metadata(mv = {1, Definitions.sendSubject, 0}, k = 1, xi = 48, d1 = {"��\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u0002\n\u0002\b\u0002\bf\u0018��2\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0003H&¨\u0006\u0005"}, d2 = {"Lcom/tm/ILoggingInterceptor;", "Lokhttp3/Interceptor;", "disable", "", "enable", "common_release"})
/* loaded from: input.aar:classes.jar:com/tm/ILoggingInterceptor.class */
public interface ILoggingInterceptor extends Interceptor {
    void enable();

    void disable();
}
