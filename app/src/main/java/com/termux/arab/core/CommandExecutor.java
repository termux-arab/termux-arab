package com.termux.arab.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandExecutor {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Process currentProcess;

    public interface Listener {
        void onOutput(String line);
        void onComplete(int exitCode);
    }

    public void execute(String command, Context ctx, Listener listener) {
        executor.execute(() -> {
            try {
                String[] shell = {"/system/bin/sh", "-c", command};
                ProcessBuilder pb = new ProcessBuilder(shell);
                pb.directory(new File(ctx.getFilesDir(), "home"));
                pb.redirectErrorStream(true);
                currentProcess = pb.start();

                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(currentProcess.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    final String l = line;
                    mainHandler.post(() -> listener.onOutput(l));
                }
                int code = currentProcess.waitFor();
                mainHandler.post(() -> listener.onComplete(code));
            } catch (Exception e) {
                mainHandler.post(() -> {
                    listener.onOutput("خطأ: " + e.getMessage());
                    listener.onComplete(-1);
                });
            }
        });
    }

    public void stop() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroy();
        }
    }

    public void shutdown() {
        stop();
        executor.shutdown();
    }
}
