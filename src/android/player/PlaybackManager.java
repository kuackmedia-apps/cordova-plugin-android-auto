package com.kuackmedia.androidauto.player;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class PlaybackManager {
    private static final String TAG = "PlaybackManager";
    private MediaPlayer mediaPlayer;
    private Context context;
    private Uri currentTrackUri;

    public PlaybackManager(Context ctx) {
        context = ctx.getApplicationContext();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
    }

    public void playTrack(Uri trackUri) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }

            Log.i(TAG, "Preparando track: " + trackUri);
            currentTrackUri = trackUri;
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, trackUri);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);

            mediaPlayer.setOnCompletionListener(mp -> Log.i(TAG, "Track completado"));

        } catch (Exception e) {
            Log.e(TAG, "Error en playTrack", e);
        }
    }

    public void pauseTrack() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Log.i(TAG, "Reproducción pausada");
        }
    }

    public void stopPlayback() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            Log.i(TAG, "Reproducción detenida");
        }
        mediaPlayer.reset();
    }

    public Uri getCurrentTrackUri() {
        return currentTrackUri;
    }

    public void release() {
        mediaPlayer.release();
        mediaPlayer = null;
        Log.i(TAG, "MediaPlayer liberado");
    }
}
