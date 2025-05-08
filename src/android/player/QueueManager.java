package com.kuackmedia.androidauto.player;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueueManager {
    private static final String TAG = "QueueManager";
    private final List<QueueItem> queue = new ArrayList<>();
    private int currentIndex = 0;

    public void setQueue(List<QueueItem> items) {
        queue.clear();
        queue.addAll(items);
        currentIndex = 0;
        Log.i(TAG, "Queue actualizada con " + queue.size() + " items");
    }

    public QueueItem getCurrentQueueItem() {
        if (queue.isEmpty()) return null;
        return queue.get(currentIndex);
    }

    public QueueItem skipToNext() {
        if (queue.isEmpty()) return null;
        currentIndex = (currentIndex + 1) % queue.size();
        Log.i(TAG, "Skip next: " + getCurrentQueueItem().getDescription().getTitle());
        return getCurrentQueueItem();
    }

    public QueueItem skipToPrevious() {
        if (queue.isEmpty()) return null;
        currentIndex = (currentIndex - 1 + queue.size()) % queue.size();
        Log.i(TAG, "Skip previous: " + getCurrentQueueItem().getDescription().getTitle());
        return getCurrentQueueItem();
    }

    public List<QueueItem> getQueue() {
        return Collections.unmodifiableList(queue);
    }

    public void setCurrentIndex(int index) {
        if (index >= 0 && index < queue.size()) {
            currentIndex = index;
            Log.i(TAG, "Current index set to " + currentIndex);
        }
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
