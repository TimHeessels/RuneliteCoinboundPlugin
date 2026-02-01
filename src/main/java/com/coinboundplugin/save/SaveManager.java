package com.coinboundplugin.save;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
@Singleton
public class SaveManager {
    private final SaveStorage storage;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r ->
            {
                Thread t = new Thread(r, "yourplugin-save-debounce");
                t.setDaemon(true);
                return t;
            });

    private static final long DEBOUNCE_MS = 1500;

    private final Object lock = new Object();

    private String activeAccountKey = null;
    private SaveData active = new SaveData();

    private boolean dirty = false;
    private ScheduledFuture<?> pendingSave = null;

    @Inject
    public SaveManager(SaveStorage storage) {
        this.storage = storage;
    }

    public SaveData get() {
        synchronized (lock) {
            return active;
        }
    }

    // Schedules a save after DEBOUNCE_MS since the last change.
    public void markDirty() {
        synchronized (lock) {
            dirty = true;

            if (activeAccountKey == null)
                return;

            // reset debounce timer
            if (pendingSave != null)
                pendingSave.cancel(false);

            pendingSave = scheduler.schedule(this::flushSafely, DEBOUNCE_MS, TimeUnit.MILLISECONDS);
        }
    }

    /*
     * Switches which account save is active.
     * Flushes pending changes for the old account immediately.
     */
    public void onAccountChanged(String newAccountKey) {
        // flush current account immediately before switching
        flushNow();

        synchronized (lock) {
            activeAccountKey = newAccountKey;
            active = storage.load(newAccountKey);
            dirty = false;
        }
    }

     //Force an immediate save (used on shutdown/logout/account swap).
    public void flushNow() {
        // cancel pending debounce, then save immediately
        ScheduledFuture<?> toCancel;
        synchronized (lock) {
            toCancel = pendingSave;
            pendingSave = null;
        }
        if (toCancel != null)
            toCancel.cancel(false);

        flushSafely();
    }

    //Stop background thread; call on plugin shutdown.
    public void shutdown() {
        flushNow();
        scheduler.shutdown();
    }

    //Actual flush method (safe for calling from scheduler thread).
    private void flushSafely() {
        String key;
        SaveData dataToWrite;

        synchronized (lock) {
            if (!dirty || activeAccountKey == null)
                return;

            key = activeAccountKey;
            dataToWrite = active;

            dirty = false;
        }

        try {
            storage.save(key, dataToWrite);
        } catch (Exception e) {
            log.warn("Failed to save data", e);
            // if it failed, mark dirty again so the next tick/save attempts again
            synchronized (lock) {
                dirty = true;
            }
        }
    }
}