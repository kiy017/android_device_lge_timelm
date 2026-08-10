package com.lge.fpdim;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.BufferedReader;
import java.io.FileReader;

public class FpDimService extends Service {
    private static final String TAG = "FpDim";
    private static final String LHBM_PATH = "/sys/devices/virtual/panel/brightness/fp_lhbm";
    private static final int MAX_BRIGHTNESS = 255;
    private static final int POLL_INTERVAL_MS = 150;

    private WindowManager mWindowManager;
    private View mOverlayView;
    private Handler mMainHandler;
    private PollingThread mPollingThread;

    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mMainHandler = new Handler(Looper.getMainLooper());
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, buildNotification());
        startPolling();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPolling();
        hideOverlay();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                "fp_dim",
                "Fingerprint Dimmer",
                NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("Service running");
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.createNotificationChannel(channel);
    }

    private Notification buildNotification() {
        return new Notification.Builder(this, "fp_dim")
                .setContentTitle("Fingerprint Dimmer")
                .setContentText("Ready")
                .setSmallIcon(android.R.drawable.ic_menu_view)
                .setOngoing(true)
                .build();
    }

    private void startPolling() {
        if (mPollingThread != null) return;
        mPollingThread = new PollingThread();
        mPollingThread.start();
    }

    private void stopPolling() {
        if (mPollingThread != null) {
            mPollingThread.interrupt();
            mPollingThread = null;
        }
    }

    private int readLhbmValue() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LHBM_PATH))) {
            String line = reader.readLine();
            if (line != null) return Integer.parseInt(line.trim());
        } catch (Exception e) {
            // SELinux may block – will fix
        }
        return 0;
    }

    private int getCurrentBrightness() {
        try {
            return Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            return MAX_BRIGHTNESS;
        }
    }

    private void showOverlay(float alpha) {
        if (mOverlayView != null) {
            mOverlayView.setAlpha(alpha);
            return;
        }
        mOverlayView = new FrameLayout(this);
        mOverlayView.setBackgroundColor(0xFF000000);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,  // <-- covers status bar
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.setTitle("FP Dimmer");

        try {
            mWindowManager.addView(mOverlayView, params);
            mOverlayView.setAlpha(alpha);
            Log.d(TAG, "Overlay added, alpha=" + alpha);
        } catch (Exception e) {
            Log.e(TAG, "Failed to add overlay", e);
            mOverlayView = null;
        }
    }

    private void hideOverlay() {
        if (mOverlayView != null) {
            mWindowManager.removeView(mOverlayView);
            mOverlayView = null;
            Log.d(TAG, "Overlay removed");
        }
    }

    private class PollingThread extends Thread {
        private int mLastValue = 0;

        @Override
        public void run() {
            while (!isInterrupted()) {
                int value = readLhbmValue();
                if (value != mLastValue) {
                    mLastValue = value;
                    final int finalValue = value;
                    mMainHandler.post(() -> {
                        if (finalValue != 0) {
                            int brightness = getCurrentBrightness();
                            float alpha = 1.0f - ((float) brightness / MAX_BRIGHTNESS);
                            alpha = Math.max(0.0f, Math.min(1.0f, alpha));
                            showOverlay(alpha);
                        } else {
                            hideOverlay();
                        }
                    });
                }
                try {
                    Thread.sleep(POLL_INTERVAL_MS);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
