package com.yandex.disk.rest.example;

import com.yandex.disk.rest.json.Resource;

import java.util.ArrayList;
import java.util.List;

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

    public static List<ListItem> convert(List<Resource> resourceList) {
        List<ListItem> result = new ArrayList<>(resourceList.size());
        for (Resource resource : resourceList) {
            result.add(convert(resource));
        }
        return result;
    }

}
