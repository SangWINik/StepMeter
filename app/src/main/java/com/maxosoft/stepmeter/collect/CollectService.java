package com.maxosoft.stepmeter.collect;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.maxosoft.stepmeter.MainActivity;
import com.maxosoft.stepmeter.TestActivity;
import com.maxosoft.stepmeter.R;
import com.maxosoft.stepmeter.api.DataApiService;
import com.maxosoft.stepmeter.data.ClassificationHelper;
import com.maxosoft.stepmeter.data.FeatureSuit;
import com.maxosoft.stepmeter.data.Window;
import com.maxosoft.stepmeter.dto.DataWindowDto;
import com.maxosoft.stepmeter.dto.RecordingSessionDto;
import com.maxosoft.stepmeter.util.FileUtil;

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
import java.util.stream.Collectors;

import static com.maxosoft.stepmeter.App.CHANNEL_ID;

public class CollectService extends Service implements SensorEventListener {
    private final static Long SIZE_LIMIT = 5000000L; // 5MB limit
    private final static Integer DELAY = 1*3*1000; // 3 second delay

    private DataApiService dataApiService = new DataApiService(this);

    private SensorManager sensorManager = null;
    private File file = null;
    private FileOutputStream outputStream;
    private Thread collectThread = null;
    private Long accountId = null;

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
            accountId = intent.getLongExtra("accountId", -1);
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Starting delayed task");
                    start(intent.getStringExtra("filesDir"));
                }
            };
            timer.schedule(timerTask, DELAY);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("StepMeter")
                .setContentText("We are collecting your data right now. Please put the phone into your pocket.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        System.out.println("Collect Service Destroyed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        String device = "a";
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
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void start(final String filesDir) {
        final SensorEventListener context = this;

        collectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                if (sensorManager != null) {
                    try {
                        new File(filesDir + "/collect").mkdirs();
                        file = new File(filesDir + "/collect/recordedData.txt");
                        file.createNewFile();
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write("");
                        fileWriter.close();
                        outputStream = new FileOutputStream(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                    sensorManager.registerListener(context, accelerometer, SensorManager.SENSOR_DELAY_GAME);
                    sensorManager.registerListener(context, gyroscope, SensorManager.SENSOR_DELAY_GAME);
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
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.saveRecordedData();
        stopSelf();
    }

    private void saveRecordedData() {
        List<Window> windows = ClassificationHelper.getWindowsFromFile(file, true, FeatureSuit.ALL);
        if (!windows.isEmpty()) {
            RecordingSessionDto recordingSession = new RecordingSessionDto();
            recordingSession.setId(2L);
            recordingSession.setDeviceId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
            recordingSession.setDateStart(windows.get(0).getDateStart());
            recordingSession.setDateEnd(windows.get(windows.size() - 1).getDateEnd());
            recordingSession.setDataWindows(new ArrayList<>());
            for (Window window: windows) {
                recordingSession.getDataWindows().add(new DataWindowDto(window, null));
            }
            dataApiService.saveRecordingSessions(Collections.singletonList(recordingSession));
        }
    }
}
