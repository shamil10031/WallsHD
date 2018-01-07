package com.shomazzapp.vavilonWalls.Requests;

import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDocument;

import org.json.JSONObject;

public class DocumentRequest {

    private String address;
    private String attachmentString;
    private String docIdentificators;

    public DocumentRequest(String attachmentString) {
        this.attachmentString = attachmentString;
        this.docIdentificators = createDocIdentificator();
        loadAttachmentAddres();
    }

    public void loadAttachmentAddres() {
        VKRequest request = new VKRequest("docs.getById", VKParameters.from(
                "docs", docIdentificators,
                VKApiConst.ACCESS_TOKEN, Constants.ACCES_TOKEN));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    VKApiDocument doc = new VKApiDocument((JSONObject) response.json.
                            getJSONArray("response").get(0));
                    address = doc.url;
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

    //Delete 'doc' from attachmentString
    public String createDocIdentificator() {
        return attachmentString.substring(3, attachmentString.length());
    }

    public String getAddress() {
        return address;
    }

}
