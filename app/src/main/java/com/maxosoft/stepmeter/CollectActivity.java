package com.maxosoft.stepmeter;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;

import com.maxosoft.stepmeter.api.DataApiService;
import com.maxosoft.stepmeter.data.ClassificationHelper;
import com.maxosoft.stepmeter.data.FeatureSuit;
import com.maxosoft.stepmeter.data.Window;
import com.maxosoft.stepmeter.dto.DataWindowDto;
import com.maxosoft.stepmeter.dto.RecordingSessionDto;

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

public class CollectActivity extends Activity implements SensorEventListener {

    private final static Long BAD_DATA_ACCOUNT_ID = 5L;
    private final static Long SIZE_LIMIT = 5000000L; // 5MB limit
    private final static Integer DELAY = 1000*5; // 5 seconds delay

    private DataApiService dataApiService = new DataApiService(this);

    private SensorManager sensorManager = null;
    private File file = null;
    private FileOutputStream outputStream;
    private Thread collectThread = null;
    private Long accountId = null;
    private String accountEmail = null;
    private Boolean badData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        System.out.println("starting collecting");
        accountId = getIntent().getLongExtra("accountId", -1);
        if (accountId == -1) {
            accountId = null;
        }
        accountEmail = getIntent().getStringExtra("accountEmail");
        badData = getIntent().getBooleanExtra("badData", false);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Starting delayed collecting task");
                start();
            }
        };
        timer.schedule(timerTask, DELAY);
    }

    @Override
    public void onBackPressed() {
        this.stop();

        finish();
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
                    finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void start() {
        final SensorEventListener context = this;

        collectThread = new Thread(() -> {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (sensorManager != null) {
                try {
                    new File(getFilesDir().getAbsolutePath() + "/collect").mkdirs();
                    file = new File(getFilesDir().getAbsolutePath() + "/collect/recordedData.txt");
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
    }

    private void saveRecordedData() {
        List<Window> windows = ClassificationHelper.getWindowsFromFile(file, true, FeatureSuit.ALL);
        if (!windows.isEmpty()) {
            RecordingSessionDto recordingSession = new RecordingSessionDto();
            recordingSession.setAccountId(badData ? BAD_DATA_ACCOUNT_ID : accountId);
            recordingSession.setDeviceId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
            recordingSession.setDateStart(windows.get(0).getDateStart());
            recordingSession.setDateEnd(windows.get(windows.size() - 1).getDateEnd());
            recordingSession.setDataWindows(new ArrayList<>());
            for (Window window: windows) {
                recordingSession.getDataWindows().add(new DataWindowDto(window, null));
            }
            dataApiService.saveRecordingSessions(Collections.singletonList(recordingSession), accountEmail);
        }
    }
}
