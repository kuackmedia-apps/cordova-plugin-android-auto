package com.kuackmedia.androidauto.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.*;

public class KuackApiClient {

    private static final String TAG = "KuackApiClient";

    private static final String PREFS_NAME = "NativeStorage";
    private static final String AT_EXP_TIME_KEY = "at_expire_time";
    private static final String REFRESH_TOKEN_KEY = "RefreshToken";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String APP_CODE_KEY = "AppCode";

    private final SharedPreferences prefs;
    private final OkHttpClient client;

    private final String baseUrl = "https://api.yourdomain.com"; // Usa tu URL real

    public KuackApiClient(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        client = new OkHttpClient();
    }

    private boolean shouldRenewAt() {
        long expireTime = prefs.getLong(AT_EXP_TIME_KEY, 0);
        long now = System.currentTimeMillis();
        return expireTime == 0 || now >= expireTime;
    }

    public String getValidAccessToken() {
        if (shouldRenewAt()) {
            return renewAccessToken();
        } else {
            return prefs.getString(ACCESS_TOKEN_KEY, null);
        }
    }

    private String renewAccessToken() {
        try {
            String refreshToken = prefs.getString(REFRESH_TOKEN_KEY, null);
            String appCode = prefs.getString(APP_CODE_KEY, null);

            if (refreshToken == null || appCode == null) {
                Log.e(TAG, "Missing refreshToken or appCode");
                return null;
            }

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("grantType", "refresh_token");
            jsonBody.put("token", refreshToken);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));

            HttpUrl url = HttpUrl.parse(baseUrl + "/auth/token")
                .newBuilder()
                .addQueryParameter("renew_refresh", "1") // Opcional según tu lógica original
                .build();

            Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-KUACK-APP", appCode)
                .post(body)
                .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                Log.e(TAG, "HTTP Error: " + response.code());
                return null;
            }

            if (response.code() == 204) {
                Log.i(TAG, "No Content (204), no action required.");
                return null;
            }

            String responseBody = response.body().string();
            JSONObject responseData = new JSONObject(responseBody);

            String newAccessToken = responseData.getString("accessToken");
            long expiresIn = responseData.getLong("expiresIn");

            long newExpireTime = System.currentTimeMillis() + (expiresIn * 1000);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(ACCESS_TOKEN_KEY, newAccessToken);
            editor.putLong(AT_EXP_TIME_KEY, newExpireTime);

            if (responseData.has("refreshToken")) {
                String newRefreshToken = responseData.getString("refreshToken");
                editor.putString(REFRESH_TOKEN_KEY, newRefreshToken);
                Log.i(TAG, "Refresh token updated.");
            }

            editor.apply();

            Log.i(TAG, "Access token successfully renewed.");

            return newAccessToken;

        } catch (Exception e) {
            Log.e(TAG, "Error renewing access token", e);
            return null;
        }
    }
}
