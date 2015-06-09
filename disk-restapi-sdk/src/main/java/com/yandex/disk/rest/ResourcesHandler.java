/*
* (C) 2015 Yandex LLC (https://yandex.com/)
*
* The source code of Java SDK for Yandex.Disk REST API
* is available to use under terms of Apache License,
* Version 2.0. See the file LICENSE for the details.
*/

package com.yandex.disk.rest;

import com.yandex.disk.rest.json.Resource;

public abstract class ResourcesHandler {

    public void handleSelf(Resource item) {
    }

    public void handleItem(Resource item) {
    }

    public void onFinished(int itemsOnPage) {
    }
}
