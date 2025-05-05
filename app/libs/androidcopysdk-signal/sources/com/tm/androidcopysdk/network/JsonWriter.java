package com.tm.androidcopysdk.network;

import android.util.Base64;
import com.tm.androidcopysdk.database.DBMessagesHelperSignal;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import okio.BufferedSink;
import org.apache.commons.lang3.ArrayUtils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/JsonWriter.class */
public final class JsonWriter implements Closeable {
    private final BufferedSink out;
    private final List<JsonScope> stack = new ArrayList();
    private String indent;
    private String separator;
    private boolean lenient;
    private StringBuilder _sb;
    static final int bufferSize = 58;

    public JsonWriter(BufferedSink out) {
        this.stack.add(JsonScope.EMPTY_DOCUMENT);
        this.separator = ":";
        if (out == null) {
            throw new NullPointerException("out == null");
        }
        this.out = out;
        this._sb = new StringBuilder();
    }

    public void setIndent(String indent) {
        if (indent.isEmpty()) {
            this.indent = null;
            this.separator = ":";
            return;
        }
        this.indent = indent;
        this.separator = ": ";
    }

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public boolean isLenient() {
        return this.lenient;
    }

    public JsonWriter beginArray() throws IOException {
        return open(JsonScope.EMPTY_ARRAY, "[");
    }

    public JsonWriter endArray() throws IOException {
        return close(JsonScope.EMPTY_ARRAY, JsonScope.NONEMPTY_ARRAY, "]");
    }

    public JsonWriter beginObject() throws IOException {
        return open(JsonScope.EMPTY_OBJECT, "{");
    }

    public JsonWriter endObject() throws IOException {
        return close(JsonScope.EMPTY_OBJECT, JsonScope.NONEMPTY_OBJECT, "}");
    }

    private JsonWriter open(JsonScope empty, String openBracket) throws IOException {
        beforeValue(true);
        this.stack.add(empty);
        this.out.writeUtf8(openBracket);
        this._sb.append(openBracket);
        return this;
    }

    private JsonWriter close(JsonScope empty, JsonScope nonempty, String closeBracket) throws IOException {
        JsonScope context = peek();
        if (context != nonempty && context != empty) {
            throw new IllegalStateException("Nesting problem: " + this.stack);
        }
        this.stack.remove(this.stack.size() - 1);
        if (context == nonempty) {
            newline();
        }
        this.out.writeUtf8(closeBracket);
        this._sb.append(closeBracket);
        return this;
    }

    private JsonScope peek() {
        return this.stack.get(this.stack.size() - 1);
    }

    private void replaceTop(JsonScope topOfStack) {
        this.stack.set(this.stack.size() - 1, topOfStack);
    }

    public JsonWriter name(String name) throws IOException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        beforeName();
        string(name);
        return this;
    }

    public JsonWriter value(String value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        beforeValue(false);
        string(value);
        return this;
    }

    public int value(InputStream value) throws IOException {
        if (value == null) {
            return 0;
        }
        beforeValue(false);
        return string(value);
    }

    public JsonWriter nullValue() throws IOException {
        beforeValue(false);
        this.out.writeUtf8("null");
        this._sb.append("null");
        return this;
    }

    public JsonWriter value(boolean value) throws IOException {
        beforeValue(false);
        this.out.writeUtf8(value ? "true" : "false");
        this._sb.append(value ? "true" : "false");
        return this;
    }

    public JsonWriter value(double value) throws IOException {
        if (!this.lenient && (Double.isNaN(value) || Double.isInfinite(value))) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        beforeValue(false);
        this.out.writeUtf8(Double.toString(value));
        this._sb.append(Double.toString(value));
        return this;
    }

    public JsonWriter value(long value) throws IOException {
        beforeValue(false);
        this.out.writeUtf8(Long.toString(value));
        this._sb.append(Long.toString(value));
        return this;
    }

    public JsonWriter value(Number value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        String string = value.toString();
        if (!this.lenient && (string.equals("-Infinity") || string.equals("Infinity") || string.equals("NaN"))) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        beforeValue(false);
        this.out.writeUtf8(string);
        this._sb.append(string);
        return this;
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.out.close();
        if (peek() != JsonScope.NONEMPTY_DOCUMENT) {
            throw new IOException("Incomplete document");
        }
    }

    public byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        int len = inputStream.read(buffer);
        if (len != -1) {
            byteBuffer.write(buffer, 0, len);
            return byteBuffer.toByteArray();
        }
        return null;
    }

    private int string(InputStream value) throws IOException {
        int total;
        ByteBuffer allocate;
        this.out.writeUtf8("\"");
        this._sb.append("\"");
        ByteBuffer lastIterationBytesRemaining = ByteBuffer.allocate(0);
        int i = 0;
        while (true) {
            total = i;
            byte[] bytes = readBytes(value);
            if (bytes == null) {
                break;
            }
            byte[] bytesWithLastIterationRemaining = ArrayUtils.addAll(lastIterationBytesRemaining.array(), bytes);
            int remaining = bytesWithLastIterationRemaining.length % 3;
            byte[] bytesToEncode = Arrays.copyOfRange(bytesWithLastIterationRemaining, 0, bytesWithLastIterationRemaining.length - remaining);
            String str = Base64.encodeToString(bytesToEncode, 3);
            this.out.writeUtf8(str);
            this.out.flush();
            if (remaining != 0) {
                allocate = ByteBuffer.wrap(Arrays.copyOfRange(bytesWithLastIterationRemaining, bytesWithLastIterationRemaining.length - remaining, bytesWithLastIterationRemaining.length));
            } else {
                allocate = ByteBuffer.allocate(0);
            }
            lastIterationBytesRemaining = allocate;
            i = total + bytes.length;
        }
        if (lastIterationBytesRemaining.array().length > 0) {
            byte[] bytes2 = lastIterationBytesRemaining.array();
            String str2 = Base64.encodeToString(bytes2, 3);
            this.out.writeUtf8(str2);
            this.out.flush();
            total += bytes2.length;
        }
        this._sb.append("base64 file value");
        this.out.writeUtf8("\"");
        this._sb.append("\"");
        return total;
    }

    private void string(String value) throws IOException {
        this.out.writeUtf8("\"");
        this._sb.append("\"");
        int length = value.length();
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\b':
                    this.out.writeUtf8("\\b");
                    this._sb.append("\\b");
                    break;
                case '\t':
                    this.out.writeUtf8("\\t");
                    this._sb.append("\\t");
                    break;
                case '\n':
                    this.out.writeUtf8("\\n");
                    this._sb.append("\\n");
                    break;
                case '\f':
                    this.out.writeUtf8("\\f");
                    this._sb.append("\\f");
                    break;
                case DBMessagesHelperSignal.DATABASE_VERSION /* 13 */:
                    this.out.writeUtf8("\\r");
                    this._sb.append("\\r");
                    break;
                case '\"':
                case '\\':
                    this.out.writeUtf8("\\");
                    this.out.writeUtf8("" + c);
                    this._sb.append('\\');
                    this._sb.append(c);
                    break;
                case 8232:
                case 8233:
                    this.out.writeUtf8(String.format("\\u%04x", Integer.valueOf(c)));
                    this._sb.append(String.format("\\u%04x", Integer.valueOf(c)));
                    break;
                default:
                    if (c <= 31) {
                        this.out.writeUtf8(String.format("\\u%04x", Integer.valueOf(c)));
                        this._sb.append(String.format("\\u%04x", Integer.valueOf(c)));
                        break;
                    } else {
                        this.out.writeUtf8("" + c);
                        this._sb.append(c);
                        break;
                    }
            }
        }
        this.out.writeUtf8("\"");
        this._sb.append("\"");
    }

    private void newline() throws IOException {
        if (this.indent == null) {
            return;
        }
        this.out.writeUtf8("\n");
        this._sb.append("\n");
        for (int i = 1; i < this.stack.size(); i++) {
            this.out.writeUtf8(this.indent);
            this._sb.append(this.indent);
        }
    }

    private void beforeName() throws IOException {
        JsonScope context = peek();
        if (context == JsonScope.NONEMPTY_OBJECT) {
            this.out.writeUtf8(",");
            this._sb.append(',');
        } else if (context != JsonScope.EMPTY_OBJECT) {
            throw new IllegalStateException("Nesting problem: " + this.stack);
        }
        newline();
        replaceTop(JsonScope.DANGLING_NAME);
    }

    private void beforeValue(boolean root) throws IOException {
        switch (peek()) {
            case EMPTY_DOCUMENT:
                if (!this.lenient && !root) {
                    throw new IllegalStateException("JSON must start with an array or an object.");
                }
                replaceTop(JsonScope.NONEMPTY_DOCUMENT);
                return;
            case EMPTY_ARRAY:
                replaceTop(JsonScope.NONEMPTY_ARRAY);
                newline();
                return;
            case NONEMPTY_ARRAY:
                this.out.writeUtf8(",");
                this._sb.append(',');
                newline();
                return;
            case DANGLING_NAME:
                this.out.writeUtf8(this.separator);
                this._sb.append(this.separator);
                replaceTop(JsonScope.NONEMPTY_OBJECT);
                return;
            case NONEMPTY_DOCUMENT:
                throw new IllegalStateException("JSON must have only one top-level value.");
            default:
                throw new IllegalStateException("Nesting problem: " + this.stack);
        }
    }

    public String toString() {
        return this._sb.toString();
    }
}
