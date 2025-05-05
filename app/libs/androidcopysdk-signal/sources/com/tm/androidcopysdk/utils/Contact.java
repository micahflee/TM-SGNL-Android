package com.tm.androidcopysdk.utils;

import android.content.Context;
import android.text.TextUtils;
import com.tm.androidcopysdk.model.ArchiveChat;
import com.tm.androidcopysdk.model.ChatType;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/Contact.class */
public class Contact {
    public String firstName;
    public String lastName;

    public Contact(String firstName, String lastName) {
        if (!TextUtils.isEmpty(firstName)) {
            this.firstName = firstName;
        } else {
            this.firstName = "";
        }
        if (TextUtils.isEmpty(lastName)) {
            this.lastName = "";
        } else {
            this.lastName = lastName;
        }
    }

    public Contact(String longName) {
        this.firstName = "";
        this.lastName = "";
        if (!TextUtils.isEmpty(longName)) {
            String[] names = longName.trim().split(" ");
            if (names.length <= 1) {
                if (names.length == 1) {
                    this.firstName = names[0];
                    this.lastName = "";
                    return;
                }
                return;
            }
            this.firstName = names[0];
            String last = "";
            for (int i = 1; i < names.length; i++) {
                last = last + names[i] + " ";
            }
            this.lastName = last.trim();
        }
    }

    public Contact() {
        this.firstName = "";
        this.lastName = "";
    }

    public static boolean isEmpty(Contact contact) {
        return contact == null || (TextUtils.isEmpty(contact.firstName) && TextUtils.isEmpty(contact.lastName));
    }

    public String toString() {
        if (!this.firstName.isEmpty() && !this.lastName.isEmpty()) {
            return this.firstName + "," + this.lastName;
        }
        if (!this.firstName.isEmpty()) {
            return this.firstName;
        }
        if (!this.lastName.isEmpty()) {
            return this.lastName;
        }
        return "";
    }

    public String toStringForArchiving() {
        if (!this.firstName.isEmpty() && !this.lastName.isEmpty()) {
            return this.firstName + " " + this.lastName;
        }
        if (!this.firstName.isEmpty()) {
            return this.firstName;
        }
        if (!this.lastName.isEmpty()) {
            return this.lastName;
        }
        return "";
    }

    public static Contact getContactFromString(String str) {
        Contact ret = new Contact();
        if (!TextUtils.isEmpty(str)) {
            String[] names = str.split(",");
            if (names.length == 1) {
                ret.firstName = names[0];
            } else if (names.length > 1) {
                ret.firstName = names[0];
                String last = "";
                for (int i = 1; i < names.length; i++) {
                    last = last + " " + names[i];
                }
                ret.lastName = last.trim();
            }
        }
        return ret;
    }

    public static Contact getMyContact(Context context) {
        String name = PrefManager.getStringPref(context, "pref_my_name_contact", "");
        Contact ret = new Contact();
        if (!TextUtils.isEmpty(name)) {
            String[] names = name.split(",");
            if (names.length == 1) {
                ret.firstName = names[0];
            } else if (names.length > 1) {
                ret.firstName = names[0];
                String last = "";
                for (int i = 1; i < names.length; i++) {
                    last = last + " " + names[i];
                }
                ret.lastName = last.trim();
            }
        }
        return ret;
    }

    public static Contact fromChat(ArchiveChat chat) {
        if (chat.getType() == ChatType.Chat) {
            return new Contact(chat.getName());
        }
        return new Contact(chat.getName(), "");
    }
}
