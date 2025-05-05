package com.tm.androidcopysdk.device;

import com.tm.androidcopysdk.CommonUtils;
import com.tm.androidcopysdk.Models.MessageDetailsArchive;
import com.tm.androidcopysdk.api.SdkModule;
import com.tm.androidcopysdk.converter.MessageDetailsConverter;
import com.tm.androidcopysdk.model.ArchiveAttachment;
import com.tm.androidcopysdk.model.ArchiveCallInfo;
import com.tm.androidcopysdk.model.ArchiveMessage;
import com.tm.androidcopysdk.model.ArchiveMessageType;
import com.tm.androidcopysdk.model.CallRtcMode;
import com.tm.androidcopysdk.model.MessageAttachmentStatus;
import com.tm.androidcopysdk.model.MessageStatus;
import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.logger.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveMessagesProcessor.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��V\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018��*\u0004\b��\u0010\u00012\u00020\u0002B\u0013\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028��0\u0004¢\u0006\u0002\u0010\u0005J\u001a\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u000fH\u0002J\u001a\u0010\u0011\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0002J\u001a\u0010\u0014\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u000fH\u0002J\u001a\u0010\u0015\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u000fH\u0002J\u001a\u0010\u0016\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u000fH\u0002J\u001a\u0010\u0017\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u000fH\u0002J\u001a\u0010\u0018\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u000fH\u0002J\u0014\u0010\u0019\u001a\u0004\u0018\u00010\u00132\b\u0010\u001a\u001a\u0004\u0018\u00010\u0013H\u0002J\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u001d\u001a\u00020\u001cH\u0002J\u0018\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"H\u0002J\u0010\u0010#\u001a\u00020\u001c2\u0006\u0010!\u001a\u00020\"H\u0002J\b\u0010$\u001a\u00020\u001cH\u0002J\b\u0010%\u001a\u00020\u001cH\u0002J \u0010&\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010'\u001a\u00020(H\u0002J\u001a\u0010)\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u000fH\u0014J \u0010*\u001a\u00020\u001c2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010'\u001a\u00020(2\u0006\u0010+\u001a\u00020\tH\u0002R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n��R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028��0\u0004X\u0082\u0004¢\u0006\u0002\n��R\u0014\u0010\b\u001a\u00020\tX\u0096D¢\u0006\b\n��\u001a\u0004\b\n\u0010\u000b¨\u0006,"}, d2 = {"Lcom/tm/androidcopysdk/device/ArchiveMessagesProcessor;", "T", "Lcom/tm/androidcopysdk/device/MessageStoreProcessor;", "module", "Lcom/tm/androidcopysdk/api/SdkModule;", "(Lcom/tm/androidcopysdk/api/SdkModule;)V", "detailsConverter", "Lcom/tm/androidcopysdk/converter/MessageDetailsConverter;", "tag", "", "getTag", "()Ljava/lang/String;", "archiveCallMessage", "", "message", "Lcom/tm/androidcopysdk/model/ArchiveMessage;", "existing", "archiveCallRecordedFile", "recordedFile", "Ljava/io/File;", "archiveDeletedMessage", "archiveEditMessage", "archiveEventMessage", "archiveMessage", "archiveMmsMessage", "decodeRecordingFile", "file", "isArchivingSupported", "", "isNewEdit", "isCallLogSupported", "rtcMode", "Lcom/tm/androidcopysdk/model/CallRtcMode;", "recordingType", "Lcom/tm/androidcopysdk/CommonUtils$RECORDING_TYPE;", "isCallRecordingSupported", "isVideoCallSupported", "isVoiceCallSupported", "maybeUpdateAttachmentsFileMms", CallLogMessageRecorder.attachment_key, "Lcom/tm/androidcopysdk/model/ArchiveAttachment;", "processAfterMessageStateChanged", "streamIntoFile", "targetPath", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nArchiveMessagesProcessor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArchiveMessagesProcessor.kt\ncom/tm/androidcopysdk/device/ArchiveMessagesProcessor\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,225:1\n1549#2:226\n1620#2,3:227\n1855#2,2:232\n288#2,2:234\n37#3,2:230\n1#4:236\n*S KotlinDebug\n*F\n+ 1 ArchiveMessagesProcessor.kt\ncom/tm/androidcopysdk/device/ArchiveMessagesProcessor\n*L\n92#1:226\n92#1:227,3\n101#1:232,2\n106#1:234,2\n92#1:230,2\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/device/ArchiveMessagesProcessor.class */
public final class ArchiveMessagesProcessor<T> extends MessageStoreProcessor {
    @NotNull
    private final SdkModule<T> module;
    @NotNull
    private final String tag;
    @NotNull
    private final MessageDetailsConverter detailsConverter;

    /* compiled from: ArchiveMessagesProcessor.kt */
    @Metadata(mv = {1, 9, 0}, k = 3, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK)
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/device/ArchiveMessagesProcessor$WhenMappings.class */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[ArchiveMessageType.values().length];
            try {
                iArr[ArchiveMessageType.Sms.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[ArchiveMessageType.Mms.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[ArchiveMessageType.Call.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public ArchiveMessagesProcessor(@org.jetbrains.annotations.NotNull com.tm.androidcopysdk.api.SdkModule<T> r7) {
        /*
            r6 = this;
            r0 = r7
            java.lang.String r1 = "module"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r0, r1)
            r0 = r6
            com.tm.androidcopysdk.model.ArchiveMessageType[] r1 = com.tm.androidcopysdk.model.ArchiveMessageType.values()
            r8 = r1
            r1 = r8
            r2 = r8
            int r2 = r2.length
            java.lang.Object[] r1 = java.util.Arrays.copyOf(r1, r2)
            com.tm.androidcopysdk.model.ArchiveMessageType[] r1 = (com.tm.androidcopysdk.model.ArchiveMessageType[]) r1
            r0.<init>(r1)
            r0 = r6
            r1 = r7
            r0.module = r1
            r0 = r6
            java.lang.String r1 = "ArchiveMessagesProcessor"
            r0.tag = r1
            r0 = r6
            com.tm.androidcopysdk.converter.MessageDetailsConverter r1 = new com.tm.androidcopysdk.converter.MessageDetailsConverter
            r2 = r1
            r3 = r6
            com.tm.androidcopysdk.api.SdkModule<T> r3 = r3.module
            com.tm.androidcopysdk.model.ClientType r3 = r3.getClientType()
            r4 = r6
            com.tm.androidcopysdk.api.SdkModule<T> r4 = r4.module
            com.tm.androidcopysdk.api.IFiler r4 = r4.getFiler()
            r2.<init>(r3, r4)
            r0.detailsConverter = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.device.ArchiveMessagesProcessor.<init>(com.tm.androidcopysdk.api.SdkModule):void");
    }

    @Override // com.tm.androidcopysdk.device.MessageStoreProcessor
    @NotNull
    public String getTag() {
        return this.tag;
    }

    @Override // com.tm.androidcopysdk.device.MessageStoreProcessor
    protected void processAfterMessageStateChanged(@NotNull ArchiveMessage message, @Nullable ArchiveMessage existing) {
        Intrinsics.checkNotNullParameter(message, "message");
        String cleanAccountPhoneNumber = message.getCleanAccountPhoneNumber();
        if (cleanAccountPhoneNumber == null || cleanAccountPhoneNumber.length() == 0) {
            Log.d(getTag(), "ignoring archive message " + message.getArchiveId() + ", account phone number is missing.");
            return;
        }
        boolean isNewEdit = message.isNewEdit(existing);
        if (!isArchivingSupported(message, isNewEdit) || message.getTimestamp().getValue() <= 0) {
            Log.d(getTag(), "ignoring unsupported message " + message.getArchiveId() + " of type " + message.getType() + " created at " + message.getTimestamp().getValue() + '.');
        } else if (message.hasDeletions()) {
            archiveDeletedMessage(message, existing);
        } else if (isNewEdit) {
            archiveEditMessage(message, existing);
        } else {
            ArchiveMessageType type = message.getType();
            switch (type == null ? -1 : WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
                case 1:
                    archiveMessage(message, existing);
                    return;
                case 2:
                    archiveMmsMessage(message, existing);
                    return;
                case 3:
                    archiveCallMessage(message, existing);
                    return;
                default:
                    Log.w(getTag(), "not sure how to handle " + message + ' ' + existing + ' ' + this.detailsConverter.convert(message, existing));
                    return;
            }
        }
    }

    private final void archiveDeletedMessage(ArchiveMessage message, ArchiveMessage existing) {
        if (message.isDeleted()) {
            if (existing != null ? existing.isDeleted() : false) {
                return;
            }
        }
        if (message.isRemoteDeleted()) {
            if (existing != null ? existing.isRemoteDeleted() : false) {
                return;
            }
        }
        MessageDetailsArchive details = this.detailsConverter.convert(message, existing);
        Log.d(getTag(), "archiveDeletedMessage " + message + ' ' + details);
        this.module.getDataGrabber().setMessage(details);
    }

    private final void archiveEditMessage(ArchiveMessage message, ArchiveMessage existing) {
        if (message.getStatus() == MessageStatus.Sending && !message.getChat().isSecret()) {
            Log.d(getTag(), "message " + message.getArchiveId() + " is still in 'Sending' state, skipping.");
            return;
        }
        MessageDetailsArchive details = this.detailsConverter.convert(message, existing);
        Log.d(getTag(), "archiveEditMessage " + message + ' ' + details);
        this.module.getDataGrabber().setMessage(details);
    }

    private final void archiveMessage(ArchiveMessage message, ArchiveMessage existing) {
        if (existing != null) {
            Log.d(getTag(), "message " + message.getArchiveId() + " was already archived, skipping.");
        } else if (message.getStatus() == MessageStatus.Sending && !message.getChat().isSecret()) {
            Log.d(getTag(), "message " + message.getArchiveId() + " is still in 'Sending' state, skipping.");
        } else {
            MessageDetailsArchive details = this.detailsConverter.convert(message, existing);
            Log.d(getTag(), "archiveMessage " + message + ' ' + details);
            this.module.getDataGrabber().setMessage(details);
        }
    }

    private final void archiveMmsMessage(ArchiveMessage message, ArchiveMessage existing) {
        ArchiveMessage archiveMessage = existing;
        if (archiveMessage == null) {
            MessageDetailsArchive details = this.detailsConverter.convert(message, archiveMessage);
            Iterable $this$map$iv = message.getAttachments();
            Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
            for (Object item$iv$iv : $this$map$iv) {
                ArchiveAttachment it = (ArchiveAttachment) item$iv$iv;
                destination$iv$iv.add(this.module.getFiler().getAttachmentFile(message, it));
            }
            Collection $this$toTypedArray$iv = (List) destination$iv$iv;
            File[] files = (File[]) $this$toTypedArray$iv.toArray(new File[0]);
            Log.d(getTag(), "setMmsMessage " + message + ' ' + ArraysKt.joinToString$default(files, (CharSequence) null, (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, new Function1<File, CharSequence>() { // from class: com.tm.androidcopysdk.device.ArchiveMessagesProcessor$archiveMmsMessage$1
                @NotNull
                public final CharSequence invoke(@NotNull File it2) {
                    Intrinsics.checkNotNullParameter(it2, "it");
                    String absolutePath = it2.getAbsolutePath();
                    Intrinsics.checkNotNullExpressionValue(absolutePath, "getAbsolutePath(...)");
                    return absolutePath;
                }
            }, 31, (Object) null));
            this.module.getDataGrabber().setMmsMessage(details.getProtocol(), details.getToPhonesArray(), details.getFromPhoneNumber(), details.getBody(), details.getId(), details.getDate(), details.getSubject(), message.getCleanAccountPhoneNumber(), details.getChatMode(), details.getChatName(), details.getChatId(), details.getFromName(), details.getFromValue(), details.getToNameArray(), details.getToPhoneNumberArrayValue(), files);
            ArchiveMessage find = this.module.getArchiverDatabase().messageDao().find(message.getArchiveIdentifier());
            if (find == null) {
                return;
            }
            archiveMessage = find;
        }
        Iterable $this$forEach$iv = message.getAttachments();
        for (Object element$iv : $this$forEach$iv) {
            ArchiveAttachment it2 = (ArchiveAttachment) element$iv;
            maybeUpdateAttachmentsFileMms(message, archiveMessage, it2);
        }
    }

    private final void maybeUpdateAttachmentsFileMms(ArchiveMessage message, ArchiveMessage existing, ArchiveAttachment attachment) {
        Object obj;
        String targetPath = this.module.getFiler().getAttachmentFile(message, attachment).getAbsolutePath();
        Iterable $this$firstOrNull$iv = existing.getAttachments();
        Iterator<T> it = $this$firstOrNull$iv.iterator();
        while (true) {
            if (it.hasNext()) {
                Object element$iv = it.next();
                ArchiveAttachment it2 = (ArchiveAttachment) element$iv;
                if (Intrinsics.areEqual(it2.getSourcePath(), targetPath)) {
                    obj = element$iv;
                    break;
                }
            } else {
                obj = null;
                break;
            }
        }
        ArchiveAttachment existingAttachment = (ArchiveAttachment) obj;
        if (existingAttachment == null) {
            return;
        }
        String archivePath = existingAttachment.getArchivePath();
        if (!(archivePath == null || archivePath.length() == 0) || existingAttachment.getStatus() != MessageAttachmentStatus.Loading) {
            Log.d(getTag(), "message " + message.getArchiveId() + " - " + attachment.getId() + " was already archived, skipping.");
        } else if (attachment.getStatus() != MessageAttachmentStatus.Success || attachment.getSourcePath() == null) {
            Log.d(getTag(), "message " + message.getArchiveId() + " - " + attachment.getId() + " has source of " + attachment.getSourcePath() + ", and is in '" + attachment.getStatus() + "' state, skipping.");
        } else {
            File sourceFile = new File(targetPath);
            if (sourceFile.exists() && sourceFile.length() > 0) {
                Log.d(getTag(), "message " + message.getArchiveId() + " - " + attachment.getId() + " source file already exists, skipping.");
                return;
            }
            Intrinsics.checkNotNull(targetPath);
            streamIntoFile(message, attachment, targetPath);
            if (!sourceFile.exists() || sourceFile.length() <= 0) {
                Log.d(getTag(), "message " + message.getArchiveId() + " - " + attachment.getId() + " source file failed to stream, skipping.");
                return;
            }
            Log.d(getTag(), "updateFileMms - " + attachment.getId() + " false " + message + ' ' + existing);
            this.module.getDataGrabber().updateFileMms(sourceFile.getName(), false);
            if (sourceFile.exists()) {
                sourceFile.delete();
            }
        }
    }

    private final boolean streamIntoFile(ArchiveMessage message, ArchiveAttachment attachment, String targetPath) {
        boolean z;
        try {
            this.module.getFiler().streamIntoFile(message, ArchiveAttachment.copy$default(attachment, null, null, null, null, targetPath, null, false, 111, null));
            z = true;
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(getClass().getSimpleName(), "Failed to populate preset archive file content.", e);
            z = false;
        }
        return z;
    }

    private final void archiveCallMessage(ArchiveMessage message, ArchiveMessage existing) {
        if (existing != null) {
            Log.d(getTag(), "call " + message.getArchiveId() + " was already archived, skipping.");
            return;
        }
        ArchiveCallInfo callInfo = message.requireFirstCallInfo();
        if (callInfo.getAnswerType() == null) {
            Log.d(getTag(), "call " + message.getArchiveId() + " is ongoing, skipping.");
            return;
        }
        CommonUtils.RECORDING_TYPE recordingType = this.module.getSdk().getRecordingStatus();
        CallRtcMode rtcMode = callInfo.getRtcMode();
        Intrinsics.checkNotNull(recordingType);
        boolean isCallLogSupported = isCallLogSupported(rtcMode, recordingType);
        boolean isRecordingSupported = isCallRecordingSupported(recordingType);
        File recordedFile = this.module.getFiler().findCallRecording(callInfo.getId());
        Log.d(getTag(), "archiveCallMessage: " + message.getArchiveId() + " recordedFile " + recordedFile + ", isCallSupported " + isCallLogSupported + " isRecordingSupported: " + isRecordingSupported + ", recordingType: " + recordingType);
        if (!isCallLogSupported || !isRecordingSupported) {
            if (recordedFile != null ? recordedFile.exists() : false) {
                recordedFile.delete();
            }
            if (!isCallLogSupported) {
                return;
            }
            recordedFile = null;
        }
        File recordedFile2 = decodeRecordingFile(recordedFile);
        Log.d(getTag(), "archiveCallMessage " + message + ' ' + existing);
        archiveCallRecordedFile(message, recordedFile2);
    }

    private final File decodeRecordingFile(File file) {
        if (file == null) {
            return null;
        }
        String wavFilePath = this.module.getFiler().filesDir().getPath() + '/' + FilesKt.getNameWithoutExtension(file) + ".wav";
        PcmAudioFileDecoder decoder = new PcmAudioFileDecoder();
        String absolutePath = file.getAbsolutePath();
        Intrinsics.checkNotNullExpressionValue(absolutePath, "getAbsolutePath(...)");
        File decodedFile = decoder.decodeToWav(absolutePath, wavFilePath);
        file.delete();
        return decodedFile;
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x0091, code lost:
        if (r4 == null) goto L29;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final void archiveCallRecordedFile(com.tm.androidcopysdk.model.ArchiveMessage r12, java.io.File r13) {
        /*
            Method dump skipped, instructions count: 308
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.device.ArchiveMessagesProcessor.archiveCallRecordedFile(com.tm.androidcopysdk.model.ArchiveMessage, java.io.File):void");
    }

    private final void archiveEventMessage(ArchiveMessage message, ArchiveMessage existing) {
    }

    private final boolean isArchivingSupported(ArchiveMessage message, boolean isNewEdit) {
        return this.module.settings().isArchivingSupported(message, isNewEdit);
    }

    private final boolean isCallLogSupported(CallRtcMode rtcMode, CommonUtils.RECORDING_TYPE recordingType) {
        if (recordingType == CommonUtils.RECORDING_TYPE.NONE) {
            return false;
        }
        if (rtcMode == CallRtcMode.Voice) {
            return isVoiceCallSupported();
        }
        return isVideoCallSupported();
    }

    private final boolean isCallRecordingSupported(CommonUtils.RECORDING_TYPE recordingType) {
        return recordingType == CommonUtils.RECORDING_TYPE.RECORDING;
    }

    private final boolean isVoiceCallSupported() {
        return true;
    }

    private final boolean isVideoCallSupported() {
        return true;
    }
}
