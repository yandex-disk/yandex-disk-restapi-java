/*
 * Лицензионное соглашение на использование набора средств разработки
 * «SDK Яндекс.Диска» доступно по адресу: http://legal.yandex.ru/sdk_agreement
 *
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
