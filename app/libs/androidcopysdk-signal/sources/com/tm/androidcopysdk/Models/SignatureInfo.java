package com.tm.androidcopysdk.Models;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/Models/SignatureInfo.class */
public class SignatureInfo {
    SignatureMode signatureMode;
    String signature;

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/Models/SignatureInfo$SignatureMode.class */
    public enum SignatureMode {
        placeHolder,
        signatureOff,
        signatureAlways,
        signatureFirstMessageThread,
        signatureDaily
    }

    public SignatureInfo(SignatureMode signatureMode, String signature) {
        this.signatureMode = signatureMode;
        this.signature = signature;
    }

    public SignatureMode getSignatureMode() {
        return this.signatureMode;
    }

    public void setSignatureMode(SignatureMode signatureMode) {
        this.signatureMode = signatureMode;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
