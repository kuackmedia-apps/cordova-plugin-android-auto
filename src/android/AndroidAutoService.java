package com.kuackmedia.androidauto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import androidx.media.MediaBrowserServiceCompat;
import java.util.ArrayList;
import java.util.List;
import androidx.media.utils.MediaConstants;

public class AndroidAutoService extends MediaBrowserServiceCompat {

    private static final String ROOT_ID = "root";
    private MediaSessionCompat mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaSession = new MediaSessionCompat(this, "AndroidAutoService");
        mediaSession.setActive(true);

        setSessionToken(mediaSession.getSessionToken());

        PlaybackStateCompat playbackState = new PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PAUSED, 0, 1f)
            .setActions(
                PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
            .build();

        mediaSession.setPlaybackState(playbackState);

        MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Título Demo")
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Artista Demo")
            .build();

        mediaSession.setMetadata(metadata);
    }

    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        Bundle extras = new Bundle();
        //extras.putBoolean(BrowserRoot.EXTRA_SEARCH_SUPPORTED, true);
        extras.putBoolean(MediaConstants.BROWSER_SERVICE_EXTRAS_KEY_SEARCH_SUPPORTED, true);
        return new BrowserRoot(ROOT_ID, extras);

    }

    @Override
    public void onLoadChildren(String parentMediaId, Result<List<MediaBrowserCompat.MediaItem>> result) {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
            .setMediaId("demo_track")
            .setTitle("Canción de ejemplo")
            .setSubtitle("Artista Ejemplo")
            .build();

        mediaItems.add(new MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));

        result.sendResult(mediaItems);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaSession.release();
        super.onDestroy();
    }
}
