package com.tm.androidcopysdk.firebas_realtime_db;

import android.content.Context;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ktx.DatabaseKt;
import com.google.firebase.ktx.Firebase;
import com.tm.androidcopysdk.AudioSettingsManager;
import com.tm.androidcopysdk.Models.AudioSettings;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.logger.Log;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
/* compiled from: RealTimeDbListener.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��:\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n��\bÆ\u0002\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u000f\u001a\u00020\u0010H\u0002J\u000e\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0007\u001a\u00020\bJ\u0016\u0010\u0012\u001a\u00020\u00102\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014H\u0002R\u0014\u0010\u0003\u001a\u00020\u0004X\u0086D¢\u0006\b\n��\u001a\u0004\b\u0005\u0010\u0006R\u000e\u0010\u0007\u001a\u00020\bX\u0082.¢\u0006\u0002\n��R\u000e\u0010\t\u001a\u00020\nX\u0082.¢\u0006\u0002\n��R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n��\u001a\u0004\b\r\u0010\u000e¨\u0006\u0016"}, d2 = {"Lcom/tm/androidcopysdk/firebas_realtime_db/RealTimeDbListener;", "", "()V", "TAG", "", "getTAG", "()Ljava/lang/String;", "context", "Landroid/content/Context;", "database", "Lcom/google/firebase/database/DatabaseReference;", "postListener", "Lcom/google/firebase/database/ValueEventListener;", "getPostListener", "()Lcom/google/firebase/database/ValueEventListener;", "setDb", "", "setObserver", "updateFirebaseAudioSettings", "audioSettingsList", "", "Lcom/tm/androidcopysdk/Models/AudioSettings;", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/firebas_realtime_db/RealTimeDbListener.class */
public final class RealTimeDbListener {
    private static DatabaseReference database;
    private static Context context;
    @NotNull
    public static final RealTimeDbListener INSTANCE = new RealTimeDbListener();
    @NotNull
    private static final String TAG = "RealTimeDbListener";
    @NotNull
    private static final ValueEventListener postListener = new ValueEventListener() { // from class: com.tm.androidcopysdk.firebas_realtime_db.RealTimeDbListener$postListener$1
        public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
            Intrinsics.checkNotNullParameter(dataSnapshot, "dataSnapshot");
            try {
                Log.d(RealTimeDbListener.INSTANCE.getTAG(), "onDataChange dataSnapshot = " + dataSnapshot);
                List fireBaseAudioSettings = (List) dataSnapshot.getValue(new GenericTypeIndicator<List<? extends AudioSettings>>() { // from class: com.tm.androidcopysdk.firebas_realtime_db.RealTimeDbListener$postListener$1$onDataChange$$inlined$getValue$1
                });
                if (fireBaseAudioSettings != null) {
                    RealTimeDbListener.INSTANCE.updateFirebaseAudioSettings(fireBaseAudioSettings);
                }
                Log.d(RealTimeDbListener.INSTANCE.getTAG(), "onDataChange fireBaseAudioSettings = " + fireBaseAudioSettings);
            } catch (DatabaseException e) {
                Log.d(RealTimeDbListener.INSTANCE.getTAG(), "onDataChange DatabaseException, key  = " + dataSnapshot.getKey());
            }
        }

        public void onCancelled(@NotNull DatabaseError databaseError) {
            Intrinsics.checkNotNullParameter(databaseError, "databaseError");
            Log.d(RealTimeDbListener.INSTANCE.getTAG(), "loadPost:onCancelled", databaseError.toException());
        }
    };

    private RealTimeDbListener() {
    }

    @NotNull
    public final String getTAG() {
        return TAG;
    }

    public final void setObserver(@NotNull Context context2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        context = context2;
        setDb();
    }

    private final void setDb() {
        Log.d(TAG, "setDb");
        DatabaseReference reference = DatabaseKt.getDatabase(Firebase.INSTANCE).getReference();
        Intrinsics.checkNotNullExpressionValue(reference, "getReference(...)");
        database = reference;
        DatabaseReference databaseReference = database;
        if (databaseReference == null) {
            Intrinsics.throwUninitializedPropertyAccessException("database");
            databaseReference = null;
        }
        databaseReference.addValueEventListener(postListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updateFirebaseAudioSettings(List<? extends AudioSettings> list) {
        Log.d(TAG, "updateFirebaseAudioSettings");
        Context context2 = context;
        if (context2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("context");
            context2 = null;
        }
        AudioSettingsManager audioSettingsManager = AudioSettingsManager.getInstance(context2);
        Context context3 = context;
        if (context3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("context");
            context3 = null;
        }
        audioSettingsManager.clearFirebaseAudioSettingsManagerPreferences(context3);
        Context context4 = context;
        if (context4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("context");
            context4 = null;
        }
        AudioSettingsManager.getInstance(context4).resetInstance();
        Context context5 = context;
        if (context5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("context");
            context5 = null;
        }
        AudioSettingsManager audioSettingsManager2 = AudioSettingsManager.getInstance(context5);
        Context context6 = context;
        if (context6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("context");
            context6 = null;
        }
        audioSettingsManager2.updateAudioSettings(context6, list, true);
        Context context7 = context;
        if (context7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("context");
            context7 = null;
        }
        PrefManager.setBooleanPref(context7, "firebase_done", true);
    }

    @NotNull
    public final ValueEventListener getPostListener() {
        return postListener;
    }
}
