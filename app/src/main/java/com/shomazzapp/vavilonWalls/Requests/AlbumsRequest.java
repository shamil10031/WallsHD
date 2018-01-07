package com.shomazzapp.vavilonWalls.Requests;

import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import org.json.JSONObject;

import java.util.ArrayList;

public class AlbumsRequest {

    private ArrayList<VKApiPhotoAlbum> albums;

    public AlbumsRequest() {
        albums = new ArrayList<>();
        loadAlbums();
    }

    public void loadAlbums() {
        VKRequest request = new VKRequest("photos.getAlbums", VKParameters.from(
                VKApiConst.OWNER_ID, Constants.COMMUNITY_ID, VKApiConst.ACCESS_TOKEN, Constants.ACCES_TOKEN));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    for (int i = 0; i < response.json.getJSONObject("response").getInt("count"); i++) {
                        VKApiPhotoAlbum a = new VKApiPhotoAlbum((JSONObject) response.json.getJSONObject("response")
                                .getJSONArray("items").get(i));
                        if (a.size > 0) albums.add(a);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError er) {
                super.onError(er);
                System.out.println(er);
            }
        });
    }

    public ArrayList<VKApiPhotoAlbum> getAlbums() {
        return albums;
    }

}
