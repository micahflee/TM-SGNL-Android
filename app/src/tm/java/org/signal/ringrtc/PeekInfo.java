//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.signal.ringrtc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class PeekInfo {
    public static final short EXPIRED_CALL_LINK_STATUS = 703;
    public static final short INVALID_CALL_LINK_STATUS = 704;
    @NonNull
    private static final String TAG = PeekInfo.class.getSimpleName();
    @NonNull
    private final List<UUID> joinedMembers;
    @Nullable
    private final UUID creator;
    @Nullable
    private final String eraId;
    @Nullable
    private final Long maxDevices;
    private final long deviceCountIncludingPendingDevices;
    private final long deviceCountExcludingPendingDevices;
    @NonNull
    private final List<UUID> pendingUsers;

    public PeekInfo(@NonNull List<UUID> joinedMembers, @Nullable UUID creator, @Nullable String eraId, @Nullable Long maxDevices, long deviceCountIncludingPendingDevices, long deviceCountExcludingPendingDevices, @NonNull List<UUID> pendingUsers) {
        this.joinedMembers = joinedMembers;
        this.creator = creator;
        this.eraId = eraId;
        this.maxDevices = maxDevices;
        this.deviceCountIncludingPendingDevices = deviceCountIncludingPendingDevices;
        this.deviceCountExcludingPendingDevices = deviceCountExcludingPendingDevices;
        this.pendingUsers = pendingUsers;
    }

    @CalledByNative
    private static PeekInfo fromNative(@NonNull List<byte[]> rawJoinedMembers, @Nullable byte[] creator, @Nullable String eraId, @Nullable Long maxDevices, long deviceCountIncludingPendingDevices, long deviceCountExcludingPendingDevices, @NonNull List<byte[]> rawPendingUsers) {
        Log.i(TAG, "fromNative(): joinedMembers.size = " + rawJoinedMembers.size());
        List<UUID> joinedMembers = new ArrayList(rawJoinedMembers.size());
        Iterator var10 = rawJoinedMembers.iterator();

        while(var10.hasNext()) {
            byte[] joinedMember = (byte[])var10.next();
            joinedMembers.add(Util.getUuidFromBytes(joinedMember));
        }

        List<UUID> pendingUsers = new ArrayList(rawPendingUsers.size());
        Iterator var14 = rawPendingUsers.iterator();

        while(var14.hasNext()) {
            byte[] pendingUser = (byte[])var14.next();
            pendingUsers.add(Util.getUuidFromBytes(pendingUser));
        }

        return new PeekInfo(joinedMembers, creator == null ? null : Util.getUuidFromBytes(creator), eraId, maxDevices, deviceCountIncludingPendingDevices, deviceCountExcludingPendingDevices, pendingUsers);
    }

    @NonNull
    public List<UUID> getJoinedMembers() {
        return this.joinedMembers;
    }

    @Nullable
    public UUID getCreator() {
        return this.creator;
    }

    @Nullable
    public String getEraId() {
        return this.eraId;
    }

    @Nullable
    public Long getMaxDevices() {
        return this.maxDevices;
    }

    /** @deprecated */
    @Deprecated
    public long getDeviceCount() {
        return this.deviceCountIncludingPendingDevices;
    }

    public long getDeviceCountIncludingPendingDevices() {
        return this.deviceCountIncludingPendingDevices;
    }

    public long getDeviceCountExcludingPendingDevices() {
        return this.deviceCountExcludingPendingDevices;
    }

    @NonNull
    public List<UUID> getPendingUsers() {
        return this.pendingUsers;
    }
}
