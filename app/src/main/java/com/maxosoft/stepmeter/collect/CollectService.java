package com.maxosoft.stepmeter.collect;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.maxosoft.stepmeter.MainActivity;
import com.maxosoft.stepmeter.R;
import com.maxosoft.stepmeter.util.FileUtil;

import java.io.FileOutputStream;

import static com.maxosoft.stepmeter.App.CHANNEL_ID;

public class CollectService extends Service implements SensorEventListener {

    private SensorManager sensorManager = null;
    private Sensor accelerometer = null;
    private Sensor gyroscope = null;
    private FileOutputStream outputStream;
    private Thread demoThread = null;
    private Notification notification;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getBooleanExtra("stop", false)) {
            System.out.println("stopping service");
            if (demoThread != null) {
                demoThread.interrupt();
            }
            stopSelf();
        } else {
            System.out.println("starting service");

            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);

            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Warning")
                    .setContentText("Initiating spy protocol")
                    .setSmallIcon(R.drawable.ic_android)
                    .setContentIntent(pendingIntent)
                    .build();


            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (sensorManager != null) {
                outputStream = FileUtil.openFileOutputStream();

                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            /*sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);*/
            }

            demoThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                        for (int i = 0; i < 60; i++) {
                            System.out.println(i);
                            Thread.sleep(1000);
                            tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            demoThread.start();
        }

        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
