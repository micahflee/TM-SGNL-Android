package com.tm.androidcopysdk;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/AndroidCopySettings.class */
public class AndroidCopySettings {
    Range range;
    DataSaving data;
    long type;

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/AndroidCopySettings$DataSaving.class */
    public enum DataSaving {
        WIFIOnly,
        WIFI3G
    }

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/AndroidCopySettings$Range.class */
    public enum Range {
        now,
        all
    }

    public Range getRange() {
        return this.range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public DataSaving getData() {
        return this.data;
    }

    public void setData(DataSaving data) {
        this.data = data;
    }

    public long getType() {
        return this.type;
    }

    public void setType(long type) {
        this.type = type;
    }

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/AndroidCopySettings$DataType.class */
    public enum DataType {
        SMS(1),
        MMS(2),
        CallLogs(4);
        
        private long statusDataType;

        DataType(long statusDataType) {
            this.statusDataType = statusDataType;
        }

        public long getStatusDataType() {
            return this.statusDataType;
        }
    }
}
