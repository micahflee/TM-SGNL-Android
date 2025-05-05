package com.tm.androidcopysdk.network;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/GetAuthenticationToken.class */
public class GetAuthenticationToken extends BodyBase {
    private final String user;
    private final String pass;

    public GetAuthenticationToken(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public String getUser() {
        return this.user;
    }

    public String getPass() {
        return this.pass;
    }
}
