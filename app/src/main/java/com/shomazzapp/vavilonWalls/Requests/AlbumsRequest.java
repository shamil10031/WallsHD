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
import java.util.HashSet;

public class AlbumsRequest {

    private ArrayList<VKApiPhotoAlbum> albums;
    private HashSet<Integer> hashSet;
    private int invisWallsCount = 0;

    public AlbumsRequest() {
        albums = new ArrayList<>();
        hashSet = new HashSet<>();
        loadAlbums();
    }

    public void loadAlbums() {
        VKRequest request = new VKRequest("photos.getAlbums", VKParameters.from(
                VKApiConst.OWNER_ID, Constants.COMMUNITY_ID,
                "need_covers", 1));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    for (int i = 0; i < response.json.getJSONObject("response").getInt("count"); i++) {
                        VKApiPhotoAlbum a = new VKApiPhotoAlbum((JSONObject) response.json.getJSONObject("response")
                                .getJSONArray("items").get(i));
                        if (a != null && a.size > 0) {
                            if (!a.title.startsWith(Constants.ALBUM_HIDED_PREFIX)
                                    && !a.title.equals("navHeader")) albums.add(a);
                            else if (hashSet != null) {
                                hashSet.add(a.id);
                                invisWallsCount += a.size;
                            }
                        }
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

    public int getInvisWallsCount() {
        return invisWallsCount;
    }

    public HashSet<Integer> getIdsHashSet() {
        return hashSet;
    }

    public ArrayList<VKApiPhotoAlbum> getAlbums() {
        return albums;
    }

}
