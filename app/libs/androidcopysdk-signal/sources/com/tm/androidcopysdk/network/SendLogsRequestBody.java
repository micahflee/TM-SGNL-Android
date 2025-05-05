package com.tm.androidcopysdk.network;

import java.io.File;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/SendLogsRequestBody.class */
public class SendLogsRequestBody extends BodyBase {
    private File upload_file;
    private String email_subject;
    private String email_to;
    private String tmp_name;
    private String user_name;
    private String first_name;
    private String user_email;
    private String user_phone;
    private String user_text_field;

    public SendLogsRequestBody(File upload_file, String email_to, String tmp_name) {
        this.upload_file = upload_file;
        this.email_to = email_to;
        this.tmp_name = tmp_name;
    }

    public File getUpload_file() {
        return this.upload_file;
    }

    public void setUpload_file(File upload_file) {
        this.upload_file = upload_file;
    }

    public String getEmail_to() {
        return this.email_to;
    }

    public void setEmail_to(String email_to) {
        this.email_to = email_to;
    }

    public String getTmp_name() {
        return this.tmp_name;
    }

    public void setTmp_name(String tmp_name) {
        this.tmp_name = tmp_name;
    }

    public String getUser_name() {
        return this.user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getFirst_name() {
        return this.first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getUser_email() {
        return this.user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_phone() {
        return this.user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_text_field() {
        return this.user_text_field;
    }

    public void setUser_text_field(String user_text_field) {
        this.user_text_field = user_text_field;
    }

    public String getEmail_subject() {
        return this.email_subject;
    }

    public void setEmail_subject(String email_subject) {
        this.email_subject = email_subject;
    }
}
