/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest.example;

import android.app.ProgressDialog;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public class IODialogFragment extends DialogFragment {

    protected static final String CREDENTIALS = "example.credentials";

    protected ProgressDialog dialog;

    public void sendException(final Exception ex) {
        dialog.dismiss();
        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
    }
}
