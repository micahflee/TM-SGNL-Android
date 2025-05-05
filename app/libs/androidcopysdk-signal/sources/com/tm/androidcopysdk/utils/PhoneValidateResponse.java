package com.tm.androidcopysdk.utils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/PhoneValidateResponse.class */
public class PhoneValidateResponse {
    private String Phone;
    private String Code;
    private boolean isValid;

    public String getPhone() {
        return this.Phone;
    }

    public void setPhone(String phone) {
        this.Phone = phone;
    }

    public String getCode() {
        return this.Code;
    }

    public void setCode(String code) {
        this.Code = code;
    }

    public boolean isValid() {
        return this.isValid;
    }

    public void setValid(boolean valid) {
        this.isValid = valid;
    }
}
