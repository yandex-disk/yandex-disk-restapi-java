/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
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
