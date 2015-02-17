package com.yandex.disk.rest.example;

import com.yandex.disk.rest.json.Resource;

public class ListItemUtils {

    /*
        name -> displayName
        path -> fullPath
        md5 ->  etag
        mime_type -> contentType
        public_url -> publicUrl
        media_type -> mediaType
        ? -> ownerName
        ? -> mpfsFileId
        ? -> folderType

        boolean:
        type -> isCollection
        ? -> aliasEnabled
        ? -> shared
        ? -> readOnly
        ? -> visible
        ? -> hasThumbnail

        long:
        size -> contentLength
        modified -> lastUpdated
     */
    public static ListItem convert(Resource resource) {
        ListItem.Builder builder = new ListItem.Builder();
        builder.setDisplayName(resource.getName());
        builder.setFullPath(resource.getPath().getPath());
        builder.setEtag(resource.getMd5());
        builder.setContentType(resource.getMimeType());
        builder.setPublicUrl(resource.getPublicUrl());
        builder.setMediaType(resource.getMediaType());
        if (resource.isDir()) {
            builder.addCollection();
        }
        builder.setContentLength(resource.getSize());
        builder.setLastModified(resource.getModified() != null ? resource.getModified().getTime() : 0);
        return builder.build();
    }

}
