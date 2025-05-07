package com.kuackmedia.androidauto.utils;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import java.io.File;

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
}
