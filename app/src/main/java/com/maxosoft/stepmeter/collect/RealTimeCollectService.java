package com.maxosoft.stepmeter.collect;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.maxosoft.stepmeter.TestActivity;
import com.maxosoft.stepmeter.R;
import com.maxosoft.stepmeter.util.FileUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.maxosoft.stepmeter.App.CHANNEL_ID;

public class RealTimeCollectService extends Service implements SensorEventListener {
    private final static Long LENGTH_LIMIT = 20*1000L; // collecting 20 seconds at a time

    private Context context;

    private SensorManager sensorManager = null;
    private Sensor accelerometer = null;
    private Sensor gyroscope = null;
    private FileOutputStream outputStream;
    private Thread demoThread = null;
    private Notification notification;

    public RealTimeCollectService(Context context) {
        this.context = context;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        if (intent.getBooleanExtra("stop", false)) {
            System.out.println("stopping service");
            if (outputStream != null) {
                try {
                    outputStream.write("interrupted\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.stop();
        } else {
            System.out.println("starting service");
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Starting delayed task");
                    start(intent.getStringExtra("filesDir"));
                }
            };
            timer.schedule(timerTask, 123);
        }

        Intent notificationIntent = new Intent(this, TestActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("StepMeter")
                .setContentText("We are collecting your data right now")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();
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
/*        String device = "a";
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            device = "g";
        }
        String dataLine = String.format("%s %s %.3f %.3f %.3f\n",
                device, new Date().getTime(), sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
        if (outputStream != null) {
            try {
                if (outputStream.getChannel().size() < SIZE_LIMIT) {
                    outputStream.write(dataLine.getBytes());
                } else {
                    System.out.println("Size limit reached.");
                    this.stop();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void start(final String filesDir) {
        final SensorEventListener context = this;

        demoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                if (sensorManager != null) {
                    outputStream = FileUtil.openFileOutputStream(filesDir);

                    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                    sensorManager.registerListener(context, accelerometer, SensorManager.SENSOR_DELAY_GAME);
                    sensorManager.registerListener(context, gyroscope, SensorManager.SENSOR_DELAY_GAME);
                }
            }
        });
        demoThread.start();
    }

    private void stop() {
        if (demoThread != null) {
            demoThread.interrupt();
        }
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stopSelf();
    }
}
