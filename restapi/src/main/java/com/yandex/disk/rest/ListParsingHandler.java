
package com.yandex.disk.rest;

//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.AbstractHttpEntity;
//import org.apache.http.protocol.HttpContext;

import com.yandex.disk.rest.json.Resource;

public abstract class ListParsingHandler {

//    public HttpContext onCreateRequest(HttpPost post, AbstractHttpEntity entity) {
//        post.setEntity(entity);
//        return null;
//    }

    public void onPageFinished(int itemsOnPage) {
    }

    public boolean hasCancelled() {
        return false;
    }

    public abstract boolean handleItem(Resource item);
}
