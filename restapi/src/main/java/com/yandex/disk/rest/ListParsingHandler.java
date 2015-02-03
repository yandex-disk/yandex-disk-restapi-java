
package com.yandex.disk.rest;

import com.yandex.disk.rest.json.Resource;

public abstract class ListParsingHandler {

    public void handleSelf(Resource item) {
    }

    public void handleItem(Resource item) {
    }

    public void onFinished(int itemsOnPage) {
    }
}
