package com.maxosoft.stepmeter.collect;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.maxosoft.stepmeter.MainActivity;
import com.maxosoft.stepmeter.R;
import com.maxosoft.stepmeter.api.DataApiService;
import com.maxosoft.stepmeter.data.ClassificationHelper;
import com.maxosoft.stepmeter.data.FeatureSuit;
import com.maxosoft.stepmeter.data.LimitedList;
import com.maxosoft.stepmeter.data.RawDataEntry;
import com.maxosoft.stepmeter.data.Window;
import com.maxosoft.stepmeter.dto.DataWindowDto;
import com.maxosoft.stepmeter.dto.RecordingSessionDto;
import com.maxosoft.stepmeter.services.ClassificationModelService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import weka.classifiers.Classifier;

import static com.maxosoft.stepmeter.App.CHANNEL_ID;

public class IdentificationService extends Service implements SensorEventListener {
    private final static Integer SUCCESS_RATE = 70; // in %
    private final static Integer DELAY = 1*0*1000; // 3 second delay
    private final static Long LENGTH_MILLIS = 1*30*1000L; // 30 second length

    private DataApiService dataApiService = new DataApiService(this);
    private ClassificationModelService classificationModelService;

    private SensorManager sensorManager = null;
    private Thread collectThread = null;
    private BroadcastReceiver broadcastReceiver;
    private LimitedList<RawDataEntry> data = null;
    private Long accountId = null;
    private Classifier classifier = null;
    private String welcomeMessage = "Default welcome";

    private Integer changeCount = 0;
    private String filesDir;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        classificationModelService = new ClassificationModelService(this);

        if (intent.getBooleanExtra("stop", false)) {
            System.out.println("stopping identification service");

            this.stop();
        } else {
            System.out.println("starting identification service");
            accountId = intent.getLongExtra("accountId", -1);
            if (intent.getStringExtra("welcomeMessage") != null) {
                welcomeMessage = intent.getStringExtra("welcomeMessage");
            }
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Starting delayed task");
                    start();
                }
            };
            timer.schedule(timerTask, DELAY);

            filesDir = intent.getStringExtra("dir");
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                2, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("StepMeter")
                .setContentText("Real Time identification is active")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        System.out.println("Identification Service Destroyed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        changeCount++;

        RawDataEntry rawDataEntry = new RawDataEntry();
        rawDataEntry.setSensorType(sensorEvent.sensor.getType());
        rawDataEntry.setDate(new Date());
        rawDataEntry.setX(sensorEvent.values[0]);
        rawDataEntry.setY(sensorEvent.values[1]);
        rawDataEntry.setZ(sensorEvent.values[2]);
        data.add(rawDataEntry);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void start() {
        this.initScreenEvents();

        data = new LimitedList<>(LENGTH_MILLIS);
        classifier = classificationModelService.getModel();

        collectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                if (sensorManager != null) {
                    startSensors();
                }
            }
        });
        collectThread.start();
    }

    private void stop() {
        if (collectThread != null) {
            collectThread.interrupt();
        }
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        data = null;
        unregisterReceiver(broadcastReceiver);
        stopSelf();
    }

    private void startSensors() {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
    }

    private void initScreenEvents() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    System.out.println("screen is off");
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    System.out.println("screen is on");
                    onScreenOn(context);
                    changeCount = 0;
                    data.clear();
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void onScreenOn(Context context) {
        sensorManager.unregisterListener(this);

        try {
            File logFile = new File(filesDir + "log.txt");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(logFile);
            for (RawDataEntry d: data) {
                os.write((d.toString() + "\n").getBytes());
            }
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<DataWindowDto> windows = ClassificationHelper.getWindowsFromRawEntries(data);
        startSensors();

        List<Boolean> results = new ArrayList<>();
        windows.forEach(w -> results.add(ClassificationHelper.classifyWindow(classifier, w)));

        long successCount = results.stream().filter(r -> r).count();

        String infoMessage = String.format("Success: %s, all: %s, changeCount: %s, entries: %s, first: %s, last: %s",
                successCount, results.size(), changeCount, data.size(), data.get(0).getDate(), data.get(data.size() - 1).getDate());

        String title = "Who are you?";
        String message = "I don't know you";

        if (((float)successCount / results.size()) * 100 > SUCCESS_RATE && windows.size() > 10) {
            title = "Hello!";
            message = welcomeMessage;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(infoMessage))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(3, builder.build());
    }
}
