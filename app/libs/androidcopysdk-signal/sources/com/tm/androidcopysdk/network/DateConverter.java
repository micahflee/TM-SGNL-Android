package com.tm.androidcopysdk.network;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/DateConverter.class */
public class DateConverter extends TypeAdapter<Date> {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public void write(com.google.gson.stream.JsonWriter out, Date date) throws IOException {
        if (date == null) {
            out.nullValue();
            return;
        }
        this.simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        out.value(this.simpleDateFormat.format(date));
    }

    /* renamed from: read */
    public Date m75read(JsonReader in) throws IOException {
        return null;
    }
}
