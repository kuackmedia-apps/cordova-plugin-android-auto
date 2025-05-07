package com.kuackmedia.androidauto.models;

import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

import org.json.JSONArray;
import org.json.JSONObject;

public class MediaItemFactory {

    public static MediaBrowserCompat.MediaItem fromJson(JSONObject json) {
        String itemType = json.optString("itemType", "track");
        String id = String.valueOf(json.optLong("id"));
        String title;
        String subtitle;
        String imageUrl = null;
        int flags;

        switch (itemType) {
            case "playlist":
                title = json.optString("name");
                subtitle = json.optJSONObject("curator") != null ? json.getJSONObject("curator").optString("name") : "Playlist";
                flags = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE;
                imageUrl = getImageUrl(json.optJSONArray("images"));
                break;

            case "album":
                title = json.optString("title");
                subtitle = getArtistsNames(json.optJSONArray("artists"));
                flags = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE;
                imageUrl = getImageUrl(json.optJSONArray("images"));
                break;

            case "artist":
                title = json.optString("name");
                subtitle = "Artist";
                flags = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE;
                imageUrl = getImageUrl(json.optJSONArray("images"));
                break;

            case "tag":
                title = json.optString("name");
                subtitle = json.optString("description", "Tag");
                flags = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE;
                imageUrl = getImageUrl(json.optJSONArray("images"));
                break;

            case "track":
            default:
                title = json.optString("name");
                subtitle = getArtistsNames(json.optJSONArray("artists"));
                flags = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE;
                JSONObject album = json.optJSONObject("album");
                if (album != null) {
                    imageUrl = getImageUrl(album.optJSONArray("images"));
                }
                break;
        }

        MediaDescriptionCompat.Builder descriptionBuilder = new MediaDescriptionCompat.Builder()
                .setMediaId(id)
                .setTitle(title)
                .setSubtitle(subtitle);

        if (imageUrl != null) {
            descriptionBuilder.setIconUri(Uri.parse(imageUrl));
        }

        return new MediaBrowserCompat.MediaItem(descriptionBuilder.build(), flags);
    }

    private static String getImageUrl(JSONArray images) {
        if (images != null && images.length() > 0) {
            JSONObject image = images.optJSONObject(images.length() - 1);
            if (image != null) {
                return image.optString("url");
            }
        }
        return null;
    }

    private static String getArtistsNames(JSONArray artists) {
        if (artists == null) return "Unknown Artist";

        StringBuilder names = new StringBuilder();
        for (int i = 0; i < artists.length(); i++) {
            JSONObject artist = artists.optJSONObject(i);
            if (artist != null) {
                names.append(artist.optString("name"));
                if (i < artists.length() - 1) names.append(", ");
            }
        }
        return names.toString();
    }
}
