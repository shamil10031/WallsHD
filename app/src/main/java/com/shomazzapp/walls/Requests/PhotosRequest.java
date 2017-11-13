package com.shomazzapp.walls.Requests;

import com.shomazzapp.walls.Utils.Constants;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;

import org.json.JSONObject;

import java.util.ArrayList;

public class PhotosRequest {

    private ArrayList<VKApiPhoto> photos;

    private int album_id;

    public PhotosRequest(int album_id) {
        this.album_id = album_id;
        this.photos = new ArrayList<>();
        loadPhotos();
    }

    public void loadPhotos() {
        VKRequest request = new VKRequest("photos.get", VKParameters.from(
                VKApiConst.OWNER_ID, Constants.COMMUNITY_ID,
                VKApiConst.ACCESS_TOKEN, Constants.ACCES_TOKEN,
                VKApiConst.ALBUM_ID, album_id));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    for (int i = 0; i < response.json.getJSONObject("response").getInt("count"); i++)
                        photos.add(new VKApiPhoto((JSONObject) response.json.getJSONObject("response")
                                .getJSONArray("items").get(i)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                System.out.println(error);
            }
        });
    }

    public ArrayList<VKApiPhoto> getPhotos() {
        return photos;
    }

}
