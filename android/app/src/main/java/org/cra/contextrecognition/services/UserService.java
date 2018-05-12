package org.cra.contextrecognition.services;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;

public class UserService {
    private static final String MY_PREFS_NAME = "cra_prefs";
    private static final String API_TOKEN_KEY = "cra_prefs";


    public void saveApiToken(Activity activity, String apiToken) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(API_TOKEN_KEY, apiToken);
        editor.apply();
    }

    @Nullable
    public String getApiToken(Activity activity){
        SharedPreferences preffs = activity.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return preffs.getString(API_TOKEN_KEY, null);
    }
}
