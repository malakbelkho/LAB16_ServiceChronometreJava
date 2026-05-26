package com.malak.servicechronometrejava;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PulseChronoService extends Service {

    public static final String ACTION_STOP_PULSE = "com.example.servicechronometrejava.STOP_PULSE";

    private static final String CHANNEL_ID = "pulse_chrono_channel";
    private static final int NOTIFICATION_ID = 2407;

    private final IBinder serviceBridge = new PulseBinder();

    private ScheduledExecutorService pulseExecutor;
    private NotificationManager notificationManager;

    private int elapsedSeconds = 0;
    private boolean chronoActive = false;

    public class PulseBinder extends Binder {
        public PulseChronoService getServiceInstance() {
            return PulseChronoService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createPulseNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String receivedAction = intent != null ? intent.getAction() : null;

        if (ACTION_STOP_PULSE.equals(receivedAction)) {
            stopChronoSafely();
            return START_NOT_STICKY;
        }

        if (!chronoActive) {
            chronoActive = true;
            startForeground(NOTIFICATION_ID, buildPulseNotification());
            launchChronoLoop();
        }

        return START_STICKY;
    }

    private void launchChronoLoop() {
        pulseExecutor = Executors.newSingleThreadScheduledExecutor();

        pulseExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                elapsedSeconds++;
                refreshPulseNotification();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void createPulseNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "PulseChrono Live Service",
                    NotificationManager.IMPORTANCE_LOW
            );

            channel.setDescription("Notification persistante du chronomètre actif.");
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification buildPulseNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_recent_history)
                .setContentTitle("PulseChrono actif")
                .setContentText("Temps écoulé : " + getFormattedTime())
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void refreshPulseNotification() {
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, buildPulseNotification());
        }
    }

    public String getFormattedTime() {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public boolean isChronoActive() {
        return chronoActive;
    }

    public void stopChronoSafely() {
        chronoActive = false;

        if (pulseExecutor != null) {
            pulseExecutor.shutdownNow();
            pulseExecutor = null;
        }

        stopForeground(true);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceBridge;
    }

    @Override
    public void onDestroy() {
        chronoActive = false;

        if (pulseExecutor != null) {
            pulseExecutor.shutdownNow();
            pulseExecutor = null;
        }

        stopForeground(true);
        super.onDestroy();
    }
}
