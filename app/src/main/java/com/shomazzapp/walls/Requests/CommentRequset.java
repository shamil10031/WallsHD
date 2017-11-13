package com.shomazzapp.walls.Requests;

import com.shomazzapp.walls.Utils.Constants;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiComment;

import org.json.JSONObject;

public class CommentRequset {

    private int photo_id;
    private VKApiComment comment;

    public CommentRequset(int photo_id) {
        this.photo_id = photo_id;
        loadComment();
    }

    public void loadComment() {
        VKRequest request = new VKRequest("photos.getComments", VKParameters.from(
                VKApiConst.OWNER_ID, Constants.COMMUNITY_ID,
                "photo_id", photo_id,
                VKApiConst.ACCESS_TOKEN, Constants.ACCES_TOKEN));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    comment = new VKApiComment((JSONObject) response.json.getJSONObject("response")
                            .getJSONArray("items").get(0));
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

    public VKApiComment getComment() {
        return comment;
    }

}
