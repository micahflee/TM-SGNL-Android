package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.Contact;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveRecipient.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��(\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n��\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\b\u0018�� \u001e2\u00020\u0001:\u0001\u001eB3\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003¢\u0006\u0002\u0010\u0007J\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0003HÆ\u0003J9\u0010\u0014\u001a\u00020��2\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003HÆ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0018\u001a\u00020\u0019HÖ\u0001J\u0006\u0010\u001a\u001a\u00020\u001bJ\t\u0010\u001c\u001a\u00020\u0003HÖ\u0001J\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u0003*\u0004\u0018\u00010\u0003H\u0002R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\b\u0010\tR\u000e\u0010\n\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n��R\u0011\u0010\u000b\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\f\u0010\tR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\r\u0010\tR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u000e\u0010\tR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u000f\u0010\t¨\u0006\u001f"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveRecipient;", "", "id", "", "address", "firstName", "lastName", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getAddress", "()Ljava/lang/String;", "addressOrUnknown", "cleanAddress", "getCleanAddress", "getFirstName", "getId", "getLastName", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toContact", "Lcom/tm/androidcopysdk/utils/Contact;", "toString", "cleanup", "Companion", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nArchiveRecipient.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArchiveRecipient.kt\ncom/tm/androidcopysdk/model/ArchiveRecipient\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,53:1\n1#2:54\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveRecipient.class */
public final class ArchiveRecipient {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @Nullable
    private final String id;
    @Nullable
    private final String address;
    @Nullable
    private final String firstName;
    @Nullable
    private final String lastName;
    @NotNull
    private final String addressOrUnknown;
    @NotNull
    private final String cleanAddress;
    @NotNull
    public static final String DEFAULT = "UNKNOWN";

    @Nullable
    public final String component1() {
        return this.id;
    }

    @Nullable
    public final String component2() {
        return this.address;
    }

    @Nullable
    public final String component3() {
        return this.firstName;
    }

    @Nullable
    public final String component4() {
        return this.lastName;
    }

    @NotNull
    public final ArchiveRecipient copy(@Nullable String id, @Nullable String address, @Nullable String firstName, @Nullable String lastName) {
        return new ArchiveRecipient(id, address, firstName, lastName);
    }

    public static /* synthetic */ ArchiveRecipient copy$default(ArchiveRecipient archiveRecipient, String str, String str2, String str3, String str4, int i, Object obj) {
        if ((i & 1) != 0) {
            str = archiveRecipient.id;
        }
        if ((i & 2) != 0) {
            str2 = archiveRecipient.address;
        }
        if ((i & 4) != 0) {
            str3 = archiveRecipient.firstName;
        }
        if ((i & 8) != 0) {
            str4 = archiveRecipient.lastName;
        }
        return archiveRecipient.copy(str, str2, str3, str4);
    }

    @NotNull
    public String toString() {
        return "ArchiveRecipient(id=" + this.id + ", address=" + this.address + ", firstName=" + this.firstName + ", lastName=" + this.lastName + ')';
    }

    public int hashCode() {
        int result = this.id == null ? 0 : this.id.hashCode();
        return (((((result * 31) + (this.address == null ? 0 : this.address.hashCode())) * 31) + (this.firstName == null ? 0 : this.firstName.hashCode())) * 31) + (this.lastName == null ? 0 : this.lastName.hashCode());
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ArchiveRecipient) {
            ArchiveRecipient archiveRecipient = (ArchiveRecipient) other;
            return Intrinsics.areEqual(this.id, archiveRecipient.id) && Intrinsics.areEqual(this.address, archiveRecipient.address) && Intrinsics.areEqual(this.firstName, archiveRecipient.firstName) && Intrinsics.areEqual(this.lastName, archiveRecipient.lastName);
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x004e, code lost:
        if (r1 == null) goto L16;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public ArchiveRecipient(@org.jetbrains.annotations.Nullable java.lang.String r9, @org.jetbrains.annotations.Nullable java.lang.String r10, @org.jetbrains.annotations.Nullable java.lang.String r11, @org.jetbrains.annotations.Nullable java.lang.String r12) {
        /*
            r8 = this;
            r0 = r8
            r0.<init>()
            r0 = r8
            r1 = r9
            r0.id = r1
            r0 = r8
            r1 = r10
            r0.address = r1
            r0 = r8
            r1 = r11
            r0.firstName = r1
            r0 = r8
            r1 = r12
            r0.lastName = r1
            r0 = r8
            r1 = r8
            java.lang.String r1 = r1.address
            r2 = r1
            if (r2 == 0) goto L51
            r13 = r1
            r1 = r13
            r14 = r1
            r16 = r0
            r0 = 0
            r15 = r0
            r0 = r14
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            boolean r0 = kotlin.text.StringsKt.isBlank(r0)
            if (r0 != 0) goto L3c
            r0 = 1
            goto L3d
        L3c:
            r0 = 0
        L3d:
            r17 = r0
            r0 = r16
            r1 = r17
            if (r1 == 0) goto L4c
            r1 = r13
            goto L4d
        L4c:
            r1 = 0
        L4d:
            r2 = r1
            if (r2 != 0) goto L5f
        L51:
        L52:
            r1 = r8
            com.tm.androidcopysdk.utils.Contact r1 = r1.toContact()
            java.lang.String r1 = r1.toString()
            r2 = r1
            java.lang.String r3 = "toString(...)"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r2, r3)
        L5f:
            r0.addressOrUnknown = r1
            r0 = r8
            r1 = r8
            java.lang.String r1 = r1.addressOrUnknown
            java.lang.String r2 = "+"
            java.lang.String r3 = ""
            r4 = 0
            r5 = 4
            r6 = 0
            java.lang.String r1 = kotlin.text.StringsKt.replace$default(r1, r2, r3, r4, r5, r6)
            r0.cleanAddress = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.model.ArchiveRecipient.<init>(java.lang.String, java.lang.String, java.lang.String, java.lang.String):void");
    }

    public /* synthetic */ ArchiveRecipient(String str, String str2, String str3, String str4, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, (i & 2) != 0 ? null : str2, (i & 4) != 0 ? null : str3, (i & 8) != 0 ? null : str4);
    }

    @Nullable
    public final String getId() {
        return this.id;
    }

    @Nullable
    public final String getAddress() {
        return this.address;
    }

    @Nullable
    public final String getFirstName() {
        return this.firstName;
    }

    @Nullable
    public final String getLastName() {
        return this.lastName;
    }

    @NotNull
    public final String getCleanAddress() {
        return this.cleanAddress;
    }

    @NotNull
    public final Contact toContact() {
        String cleanup = cleanup(this.firstName);
        if (cleanup == null) {
            cleanup = DEFAULT;
        }
        String str = this.lastName;
        return new Contact(cleanup, str != null ? cleanup(str) : null);
    }

    private final String cleanup(String $this$cleanup) {
        String replace$default;
        String replace$default2;
        String it;
        if ($this$cleanup == null || (replace$default = StringsKt.replace$default($this$cleanup, "\u2068", "", false, 4, (Object) null)) == null || (replace$default2 = StringsKt.replace$default(replace$default, "\u2069", "", false, 4, (Object) null)) == null || (it = StringsKt.replace$default(replace$default2, "\u202c", "", false, 4, (Object) null)) == null) {
            return null;
        }
        if (!StringsKt.isBlank(it)) {
            return it;
        }
        return null;
    }

    /* compiled from: ArchiveRecipient.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u001a\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n��\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\"\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u00042\u0006\u0010\b\u001a\u00020\u00042\b\u0010\t\u001a\u0004\u0018\u00010\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��¨\u0006\n"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveRecipient$Companion;", "", "()V", "DEFAULT", "", "forLongName", "Lcom/tm/androidcopysdk/model/ArchiveRecipient;", "id", "address", "longName", "androidcopysdk_signalRelease"})
    @SourceDebugExtension({"SMAP\nArchiveRecipient.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArchiveRecipient.kt\ncom/tm/androidcopysdk/model/ArchiveRecipient$Companion\n+ 2 Strings.kt\nkotlin/text/StringsKt__StringsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,53:1\n107#2:54\n79#2,22:55\n107#2:88\n79#2,22:89\n731#3,9:77\n37#4,2:86\n*S KotlinDebug\n*F\n+ 1 ArchiveRecipient.kt\ncom/tm/androidcopysdk/model/ArchiveRecipient$Companion\n*L\n36#1:54\n36#1:55,22\n43#1:88\n43#1:89,22\n36#1:77,9\n36#1:86,2\n*E\n"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveRecipient$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final ArchiveRecipient forLongName(@Nullable String id, @NotNull String address, @Nullable String longName) {
            Collection emptyList;
            boolean z;
            Intrinsics.checkNotNullParameter(address, "address");
            String firstName = null;
            String lastName = null;
            String str = longName;
            if (!(str == null || str.length() == 0)) {
                String $this$trim$iv$iv = longName;
                int startIndex$iv$iv = 0;
                int endIndex$iv$iv = $this$trim$iv$iv.length() - 1;
                boolean startFound$iv$iv = false;
                while (startIndex$iv$iv <= endIndex$iv$iv) {
                    int index$iv$iv = !startFound$iv$iv ? startIndex$iv$iv : endIndex$iv$iv;
                    char it = $this$trim$iv$iv.charAt(index$iv$iv);
                    boolean match$iv$iv = Intrinsics.compare(it, 32) <= 0;
                    if (startFound$iv$iv) {
                        if (!match$iv$iv) {
                            break;
                        }
                        endIndex$iv$iv--;
                    } else if (match$iv$iv) {
                        startIndex$iv$iv++;
                    } else {
                        startFound$iv$iv = true;
                    }
                }
                List $this$dropLastWhile$iv = new Regex(" ").split($this$trim$iv$iv.subSequence(startIndex$iv$iv, endIndex$iv$iv + 1).toString(), 0);
                if (!$this$dropLastWhile$iv.isEmpty()) {
                    ListIterator iterator$iv = $this$dropLastWhile$iv.listIterator($this$dropLastWhile$iv.size());
                    while (iterator$iv.hasPrevious()) {
                        String it2 = (String) iterator$iv.previous();
                        if (it2.length() == 0) {
                            z = true;
                            continue;
                        } else {
                            z = false;
                            continue;
                        }
                        if (!z) {
                            emptyList = CollectionsKt.take($this$dropLastWhile$iv, iterator$iv.nextIndex() + 1);
                            break;
                        }
                    }
                }
                emptyList = CollectionsKt.emptyList();
                Collection $this$toTypedArray$iv = emptyList;
                String[] names = (String[]) $this$toTypedArray$iv.toArray(new String[0]);
                if (names.length > 1) {
                    firstName = names[0];
                    String last = "";
                    for (int i = 1; i < names.length; i++) {
                        last = last + names[i] + ' ';
                    }
                    String $this$trim$iv = last;
                    String $this$trim$iv$iv2 = $this$trim$iv;
                    int startIndex$iv$iv2 = 0;
                    int endIndex$iv$iv2 = $this$trim$iv$iv2.length() - 1;
                    boolean startFound$iv$iv2 = false;
                    while (startIndex$iv$iv2 <= endIndex$iv$iv2) {
                        int index$iv$iv2 = !startFound$iv$iv2 ? startIndex$iv$iv2 : endIndex$iv$iv2;
                        char it3 = $this$trim$iv$iv2.charAt(index$iv$iv2);
                        boolean match$iv$iv2 = Intrinsics.compare(it3, 32) <= 0;
                        if (startFound$iv$iv2) {
                            if (!match$iv$iv2) {
                                break;
                            }
                            endIndex$iv$iv2--;
                        } else if (match$iv$iv2) {
                            startIndex$iv$iv2++;
                        } else {
                            startFound$iv$iv2 = true;
                        }
                    }
                    lastName = $this$trim$iv$iv2.subSequence(startIndex$iv$iv2, endIndex$iv$iv2 + 1).toString();
                } else if (names.length == 1) {
                    firstName = names[0];
                    lastName = "";
                }
            }
            return new ArchiveRecipient(id, address, firstName, lastName);
        }
    }
}
