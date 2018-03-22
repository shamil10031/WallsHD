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
import java.util.HashSet;

public class AllPhotosRequest {

    private HashSet<Integer> ids = null;
    private int hidedWallsCount = 0;
    private int offset;
    private int count;
    private int allPhotosCount;

    private ArrayList<VKApiPhoto> photos;

    public AllPhotosRequest(int offset, int count) {
        this.photos = new ArrayList<>();
        this.offset = offset;
        this.count = count;
        loadPhotos();
    }

    public AllPhotosRequest(int offset, int count, HashSet<Integer> ids) {
        this.photos = new ArrayList<>();
        this.offset = offset;
        this.count = count;
        this.ids = ids;
        loadPhotos();
    }

    public void loadPhotos() {
        VKRequest request = new VKRequest("photos.getAll", VKParameters.from(
                VKApiConst.OWNER_ID, Constants.COMMUNITY_ID,
                VKApiConst.OFFSET, offset,
                VKApiConst.COUNT, count,
                "rev", 1));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    allPhotosCount = response.json.getJSONObject("response").getInt("count");
                    for (int i = 0; i < count; i++) {
                        VKApiPhoto photo = new VKApiPhoto((JSONObject) response.json.getJSONObject("response")
                                .getJSONArray("items").get(i));
                       /* Log.d("AllPhotosRequest", "photo [" + i + "]: albumId = "
                                + photo .album_id + "; "+photo.photo_807);*/
                        if (ids == null || !ids.contains(photo.album_id))
                            photos.add(photo);
                        else if (ids != null && ids.contains(photo.album_id))
                            hidedWallsCount++;
                    }
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

    public int getAllPhotosCount() {
        return allPhotosCount;
    }

    public ArrayList<VKApiPhoto> getPhotos() {
        return photos;
    }

    public int getHidedWallsCount() {
        return hidedWallsCount;
    }
}
