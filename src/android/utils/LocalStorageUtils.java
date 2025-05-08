package com.kuackmedia.androidauto.utils;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import org.json.JSONTokener;

public class LocalStorageUtils {

    private static final String TAG = "LocalStorageUtils";

    // Verifica claramente si una imagen está guardada localmente
    public static boolean isImageAvailableLocally(Context context, MediaBrowserCompat.MediaItem item) {
        String imageId = item.getDescription().getMediaId() + ".jpg";
        File imageFile = new File(context.getFilesDir(), "images/" + imageId);

        boolean exists = imageFile.exists();
        Log.i(TAG, "Image " + imageId + " local: " + exists);
        return exists;
    }

    // Devuelve la URI claramente según disponibilidad local o remota
    public static Uri getImageUri(Context context, MediaBrowserCompat.MediaItem item) {
        String imageId = item.getDescription().getMediaId() + ".jpg";
        File imageFile = new File(context.getFilesDir(), "images/" + imageId);

        if (imageFile.exists()) {
            Log.i(TAG, "Using local image: " + imageFile.getAbsolutePath());
            return Uri.fromFile(imageFile);
        } else {
            Uri remoteUri = item.getDescription().getIconUri();
            Log.i(TAG, "Using remote image: " + remoteUri);
            return remoteUri;
        }
    }

    // Verifica claramente si un track está guardado localmente
    public static boolean isTrackAvailableLocally(Context context, MediaBrowserCompat.MediaItem item) {
        String trackId = item.getDescription().getMediaId() + ".mp3";
        File trackFile = new File(context.getFilesDir(), "tracks/" + trackId);

        boolean exists = trackFile.exists();
        Log.i(TAG, "Track " + trackId + " local: " + exists);
        return exists;
    }

    // Devuelve la URI claramente según disponibilidad local o remota
    public static Uri getTrackUri(Context context, MediaBrowserCompat.MediaItem item, Uri remoteTrackUri) {
        String trackId = item.getDescription().getMediaId() + ".mp3";
        File trackFile = new File(context.getFilesDir(), "tracks/" + trackId);

        if (trackFile.exists()) {
            Log.i(TAG, "Using local track: " + trackFile.getAbsolutePath());
            return Uri.fromFile(trackFile);
        } else {
            Log.i(TAG, "Using remote track: " + remoteTrackUri);
            return remoteTrackUri;
        }
    }
    // Recibe un JSon stringify y devuelve un JSON object
    public static JSONObject parseJson(String jsonString) {
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convierte un string JSON en un List de JSONObject.
     *
     * @param jsonString el string que representa el JSON Array.
     * @return una lista con los objetos JSON del array.
     * @throws JSONException si el string no es un JSON válido.
     */
    public static List<JSONObject> parseEscapedJsonArray(String escapedJsonString) throws JSONException {
        // Remueve escapes de comillas dobles y comillas externas adicionales
        String cleanedJson = escapedJsonString.replace("\\\"", "\"").trim();

        // Si comienza y termina con comillas extra, quítalas
        if (cleanedJson.startsWith("\"") && cleanedJson.endsWith("\"")) {
            cleanedJson = cleanedJson.substring(1, cleanedJson.length() - 1);
        }

        // Ahora sí parsea correctamente
        JSONArray jsonArray = new JSONArray(cleanedJson);
        List<JSONObject> jsonObjects = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObjects.add(jsonArray.getJSONObject(i));
        }

        return jsonObjects;
    }
}
