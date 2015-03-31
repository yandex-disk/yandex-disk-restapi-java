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

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

abstract class ContextMenuDialogFragment extends DialogFragment {

    protected static final String CREDENTIALS = "example.credentials";
    protected static final String LIST_ITEM = "example.list.item";

    protected Credentials credentials;
    protected ListItem listItem;

    protected static <T extends ContextMenuDialogFragment> T newInstance(T fragment, Credentials credentials, ListItem listItem) {
        Bundle args = new Bundle();
        args.putParcelable(CREDENTIALS, credentials);
        args.putParcelable(LIST_ITEM, listItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        credentials = getArguments().getParcelable(CREDENTIALS);
        listItem = getArguments().getParcelable(LIST_ITEM);
    }
}
