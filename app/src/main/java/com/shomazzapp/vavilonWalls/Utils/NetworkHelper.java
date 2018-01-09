package com.shomazzapp.vavilonWalls.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkHelper {

    public static boolean isOnLine(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Log.d("Network", "isOnLine ? " + (activeNetwork != null && activeNetwork.isConnectedOrConnecting()));
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
