package com.example.checkbud.data;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EntryExecutor {
    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static EntryExecutor instance;
    private final Executor diskIO;

    private EntryExecutor(Executor diskIO, Executor mainThread) {
        this.diskIO = diskIO;
    }

    public static EntryExecutor getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new EntryExecutor(Executors.newSingleThreadExecutor(),
                        new MainThreadExecutor());
            }
        }
        return instance;
    }

    public Executor diskIO() {
        return diskIO;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
