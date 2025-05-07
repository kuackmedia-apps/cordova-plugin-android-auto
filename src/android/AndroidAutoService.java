package com.kuackmedia.androidauto;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.content.SharedPreferences;
import android.app.Activity;
import android.util.Log;
import android.media.AudioAttributes;

public class AndroidAutoService extends MediaBrowserServiceCompat {

    private static final String TAG = "AndroidAutoService";
    private static final String ROOT_ID = "root";
    private MediaSessionCompat mediaSession;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("NativeStorage", Activity.MODE_PRIVATE);

        String refreshToken = prefs.getString("REFRESH_TOKEN_KEY", null);
        String accessToken = prefs.getString("AT_TOKEN_KEY", null);
        String currentTrack = prefs.getString("current_track", null);
        String playlistItemsQueue = prefs.getString("playlist_itmes", null);
        String playlistData = prefs.getString("playlist_data", null);

        Log.i(TAG, "RefreshToken: " + refreshToken);
        Log.i(TAG, "AccessToken: " + accessToken);
        Log.i(TAG, "currentTrack: " + currentTrack);
        Log.i(TAG, "playListItems: " + playlistItemsQueue);
        Log.i(TAG, "playlistData: " + playlistData);
        //JSON parse currentTrack

        mediaSession = new MediaSessionCompat(this, "AndroidAutoService");
        mediaSession.setActive(true);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                Log.i(TAG, "onPlay llamado");
                startPlayback();
            }

            @Override
            public void onPause() {
                Log.i(TAG, "onPause llamado");
                pausePlayback();
            }
            @Override
            public void onStop() {
                Log.i(TAG, "onStop llamado");
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                mediaSession.setActive(false);
            }

        });

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
        mediaPlayer = new MediaPlayer();
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mediaPlayer.setAudioAttributes(audioAttributes);
    }
    private void startPlayback() {
        if (!mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.reset();
                // Usa un archivo demo local o remoto claramente
                Uri uri = Uri.parse("https://d35a8meha201do.cloudfront.net/1400000-1420000/1401415.mp3?Expires=1746636564&Signature=bTdfKO8rSLQIobkrzMWxd~mgXKwkAO2~ipeEKT5Ia6qTQgUBUoF~xOkQjmX-ifm57FT3earQIpCQv6SETGucRizZmI4X~tiEt-R5RmFYCuDZfMphfMzzjyMU8qJIz4kFprxhN2HGT3N~Rvc77CTCmNeSqOtA7nOp4i74bdOh21UF~s~aZ87riHUDRg3mrPDSdsPcTs5FXaiDyfthZTa0MC1tg4xHqjP-xw2Ns4~VEzara3wa7N8LRhqqkqS9rBgUgvMgGITfztYJ90Kt55K15hFq5X1MnRHMH0QRaJB8WpfCSEHMDnUFOPJq1BV31IhafPuDuytKxkueHfs1Ez9G2A__&Key-Pair-Id=APKAJCLTXNKSKLRY2BBQ");
                mediaPlayer.setDataSource(getApplicationContext(), uri);
                mediaPlayer.prepareAsync();

                mediaPlayer.setOnPreparedListener(mp -> {
                    Log.i(TAG, "✅ MediaPlayer preparado, iniciando reproducción");
                    mp.start();
                });

                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "🚨 Error en MediaPlayer - what: " + what + ", extra: " + extra);
                    return true;
                });

                mediaPlayer.setOnCompletionListener(mp -> {
                    Log.i(TAG, "🎵 Reproducción completada exitosamente");
                    mp.stop();
                });

                mediaPlayer.setOnInfoListener((mp, what, extra) -> {
                    Log.i(TAG, "ℹ️ MediaPlayer Info - what: " + what + ", extra: " + extra);
                    return true;
                });

                mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
                    Log.i(TAG, "⏳ MediaPlayer Buffering Update: " + percent + "%");
                });
                mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1f)
                        .build());

            } catch (Exception e) {
                Log.e(TAG, "Error iniciando reproducción", e);
                e.printStackTrace();
            }
        }
    }

    private void pausePlayback() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Log.i(TAG, "Reproducción pausada");
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PAUSED, mediaPlayer.getCurrentPosition(), 1f)
                    .build());
        }
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
        Log.i(TAG, "onLoadChildren llamado, parentMediaId: " + parentMediaId);
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
            .setMediaId("demo_track")
            .setTitle("Canción de ejemplo")
            .setSubtitle("Artista Ejemplo")
            .build();

        mediaItems.add(new MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));

        MediaDescriptionCompat description2 = new MediaDescriptionCompat.Builder()
            .setMediaId("demo_track_2")
            .setTitle("Otra canción de ejemplo")
            .setSubtitle("Otro artista ejemplo")

            .build();
        MediaBrowserCompat.MediaItem mediaItem2 = new MediaBrowserCompat.MediaItem(description2, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
        mediaItems.add(mediaItem2);

        result.sendResult(mediaItems);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy llamado");
        mediaSession.release();
        super.onDestroy();
    }
}

//MediaConstants.BROWSER_SERVICE_EXTRAS_KEY_SEARCH_SUPPORTED
