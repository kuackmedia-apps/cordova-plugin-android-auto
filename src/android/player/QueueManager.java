package com.kuackmedia.androidauto.player;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class QueueManager {
    private static final String TAG = "QueueManager";
    private List<String> trackQueue = new ArrayList<>();
    private int currentPosition = 0;

    public void setQueue(List<String> tracks) {
        trackQueue.clear();
        trackQueue.addAll(tracks);
        currentPosition = 0;
        Log.i(TAG, "Cola actualizada: " + trackQueue);
    }

    public String getCurrentTrack() {
        if (trackQueue.isEmpty()) return null;
        return trackQueue.get(currentPosition);
    }

    public String nextTrack() {
        if (trackQueue.isEmpty()) return null;
        currentPosition = (currentPosition + 1) % trackQueue.size();
        Log.i(TAG, "Track siguiente: " + getCurrentTrack());
        return getCurrentTrack();
    }

    public String previousTrack() {
        if (trackQueue.isEmpty()) return null;
        currentPosition = (currentPosition - 1 + trackQueue.size()) % trackQueue.size();
        Log.i(TAG, "Track anterior: " + getCurrentTrack());
        return getCurrentTrack();
    }

    public List<String> getQueue() {
        return new ArrayList<>(trackQueue);
    }
}
