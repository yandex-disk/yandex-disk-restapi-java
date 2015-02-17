package com.yandex.disk.rest.example;

import com.yandex.disk.rest.json.Resource;

public class ListItemUtils {

    public static ListItem convert(Resource resource) {
        ListItem.Builder builder = new ListItem.Builder();
        builder.setDisplayName(resource.getName());
        builder.setFullPath(resource.getPath().getPath());
        if ("dir".equalsIgnoreCase(resource.getType())) {
            builder.addCollection();
        }
        builder.setContentLength(resource.getSize());
        return builder.build();
    }

}
