package com.shomazzapp.vavilonWalls.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.vk.sdk.VKSdk;

public class NetworkHelper {

    public static boolean isOnLine(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting()
                && VKSdk.isLoggedIn();
    }
}