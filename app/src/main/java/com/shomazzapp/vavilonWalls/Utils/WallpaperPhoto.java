package com.shomazzapp.vavilonWalls.Utils;

import com.shomazzapp.vavilonWalls.View.MainActivity;
import com.vk.sdk.api.model.VKApiPhoto;

public class WallpaperPhoto {

    private String link;
    private String text;

    public WallpaperPhoto(String link, String text) {
        this.link = link;
        this.text = text;
    }

    public WallpaperPhoto(VKApiPhoto photo) {
        this.link = MainActivity.getPhotoMaxQualityLink(photo);
        this.text = photo.text;
    }

    public String getLink() {
        return link;
    }

    public String getText() {
        return text;
    }

}
