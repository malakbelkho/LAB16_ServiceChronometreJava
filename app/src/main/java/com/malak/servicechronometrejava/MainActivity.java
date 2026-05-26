package com.malak.servicechronometrejava;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_CODE = 301;

    private TextView txtTimer;
    private TextView txtStatus;
    private Button btnLaunch;
    private Button btnHalt;

    private PulseChronoService pulseService;
    private boolean isServiceBound = false;

    private final Handler screenHandler = new Handler(Looper.getMainLooper());

    private final Runnable liveTimerUpdater = new Runnable() {
        @Override
        public void run() {
            if (isServiceBound && pulseService != null && pulseService.isChronoActive()) {
                txtTimer.setText(pulseService.getFormattedTime());
                txtStatus.setText("● Service actif");
            }

            screenHandler.postDelayed(this, 500);
        }
    };

    private final ServiceConnection pulseConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PulseChronoService.PulseBinder binder = (PulseChronoService.PulseBinder) service;
            pulseService = binder.getServiceInstance();
            isServiceBound = true;

            txtStatus.setText("● Service actif");
            screenHandler.post(liveTimerUpdater);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
            pulseService = null;
            txtStatus.setText("● Service déconnecté");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTimer = findViewById(R.id.txtTimer);
        txtStatus = findViewById(R.id.txtStatus);
        btnLaunch = findViewById(R.id.btnLaunch);
        btnHalt = findViewById(R.id.btnHalt);

        askNotificationPermissionIfNeeded();

        btnLaunch.setOnClickListener(view -> startPulseService());
        btnHalt.setOnClickListener(view -> stopPulseService());
    }

    private void askNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean permissionNotGranted =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED;

            if (permissionNotGranted) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE
                );
            }
        }
    }

    private void startPulseService() {
        Intent pulseIntent = new Intent(this, PulseChronoService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(pulseIntent);
        } else {
            startService(pulseIntent);
        }

        bindService(pulseIntent, pulseConnection, Context.BIND_AUTO_CREATE);

        txtStatus.setText("● Démarrage du service...");
        Toast.makeText(this, "PulseChrono lancé", Toast.LENGTH_SHORT).show();
    }

    private void stopPulseService() {
        if (isServiceBound && pulseService != null) {
            pulseService.stopChronoSafely();
        } else {
            Intent stopIntent = new Intent(this, PulseChronoService.class);
            stopIntent.setAction(PulseChronoService.ACTION_STOP_PULSE);
            startService(stopIntent);
        }

        if (isServiceBound) {
            unbindService(pulseConnection);
            isServiceBound = false;
        }

        pulseService = null;
        txtTimer.setText("00:00");
        txtStatus.setText("● Service arrêté");
        screenHandler.removeCallbacks(liveTimerUpdater);

        Toast.makeText(this, "PulseChrono arrêté", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        screenHandler.removeCallbacks(liveTimerUpdater);

        if (isServiceBound) {
            unbindService(pulseConnection);
            isServiceBound = false;
        }

        super.onDestroy();
    }
}