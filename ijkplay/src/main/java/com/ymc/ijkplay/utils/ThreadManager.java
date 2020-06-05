package com.ymc.ijkplay.utils;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadManager {

    private static ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            Thread result = new Thread(runnable, "pig_thread");
            result.setDaemon(false);
            return result;
        }
    });

    public static void execute(Runnable runnable) {
        executorService.execute(runnable);
    }

}
