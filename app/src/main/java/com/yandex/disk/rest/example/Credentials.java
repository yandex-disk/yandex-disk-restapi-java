/*
* Copyright (c) 2015 Yandex
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
