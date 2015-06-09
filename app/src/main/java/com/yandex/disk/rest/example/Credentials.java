/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.example;

import android.os.Parcel;
import android.os.Parcelable;

public class Credentials extends com.yandex.disk.rest.Credentials implements Parcelable {

    public Credentials(String user, String token) {
        super(user, token);
    }

    public static final Parcelable.Creator<Credentials> CREATOR = new Parcelable.Creator<Credentials>() {

        public Credentials createFromParcel(Parcel parcel) {
            return new Credentials(parcel.readString(), parcel.readString());
        }

        public Credentials[] newArray(int size) {
            return new Credentials[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(user);
        parcel.writeString(token);
    }
}
