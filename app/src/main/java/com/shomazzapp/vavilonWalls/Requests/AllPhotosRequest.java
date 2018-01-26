package com.shomazzapp.vavilonWalls.Requests;

import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;

import org.json.JSONObject;

import java.util.ArrayList;

public class AllPhotosRequest {

    private int offset;
    private int count;

    private ArrayList<VKApiPhoto> photos;

    public AllPhotosRequest(int offset, int count) {
        this.photos = new ArrayList<>();
        this.offset = offset;
        this.count = count;
        loadPhotos();
    }

    public void loadPhotos() {
        System.out.println("From All photos request : token = " + Constants.ACCES_TOKEN);
        VKRequest request = new VKRequest("photos.getAll", VKParameters.from(
                VKApiConst.OWNER_ID, Constants.COMMUNITY_ID,
                VKApiConst.ACCESS_TOKEN, Constants.ACCES_TOKEN,
                VKApiConst.OFFSET, offset,
                VKApiConst.COUNT, count,
                "rev", 1));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    //  count of all photos:
                    //    int count = response.json.getJSONObject("response").getInt("count");
                    for (int i = 0; i < count; i++)
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
