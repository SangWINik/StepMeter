package com.maxosoft.stepmeter;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maxosoft.stepmeter.api.DataApiService;
import com.maxosoft.stepmeter.data.ClassificationHelper;
import com.maxosoft.stepmeter.data.LimitedList;
import com.maxosoft.stepmeter.data.RawDataEntry;
import com.maxosoft.stepmeter.dto.DataWindowDto;
import com.maxosoft.stepmeter.dto.IdResultDto;
import com.maxosoft.stepmeter.dto.RecordingSessionDto;
import com.maxosoft.stepmeter.services.ClassificationModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import weka.classifiers.Classifier;

import static com.maxosoft.stepmeter.App.CHANNEL_ID;

public class IdentificationActivity extends Activity implements SensorEventListener {

    private static final int IDENTIFICATION_RESULT_CODE = 43234551;
    private final static Integer SUCCESS_RATE = 70; // in %
    private final static Long LENGTH_MILLIS = 1*1000L*30; // 30 second length
    private final static Long ID_FREQ = 1*1000L*10; // 10 seconds

    private DataApiService dataApiService = new DataApiService(this);
    private ClassificationModelService classificationModelService;

    private SensorManager sensorManager = null;
    private Timer timer;
    private LimitedList<RawDataEntry> data = null;
    private Long accountId = null;
    private String accountEmail = null;
    private Classifier classifier = null;
    private String welcomeMessage = "Default welcome";
    private boolean allowAutoUpdates = false;

    private IdResultDto currentResult = null;

    private Integer changeCount = 0;

    private LinearLayout resultLayout;
    private TextView isOwnerResult;
    private TextView totalCountResult;
    private TextView successCountResult;
    private TextView startDateResult;
    private TextView endDateResult;
    private TextView isOwnerLabel;
    private TextView totalCountLabel;
    private TextView successCountLabel;
    private TextView startDateLabel;
    private TextView endDateLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);

        resultLayout = findViewById(R.id.resultLayout);
        isOwnerResult = findViewById(R.id.isOwnerResult);
        totalCountResult = findViewById(R.id.totalCountResult);
        successCountResult = findViewById(R.id.successCountResult);
        startDateResult = findViewById(R.id.startDateResult);
        endDateResult = findViewById(R.id.endDateResult);
        isOwnerLabel = findViewById(R.id.isOwnerLabel);
        totalCountLabel = findViewById(R.id.totalCountLabel);
        successCountLabel = findViewById(R.id.successCountLabel);
        startDateLabel = findViewById(R.id.startDateLabel);
        endDateLabel = findViewById(R.id.endDateLabel);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        classificationModelService = new ClassificationModelService(this);

        Intent intent = getIntent();
        System.out.println("starting identification service");
        accountId = intent.getLongExtra("accountId", -1);
        if (accountId == -1) {
            accountId = null;
        }
        allowAutoUpdates = intent.getBooleanExtra("allowUpdates", false);
        accountEmail = intent.getStringExtra("accountEmail");
        welcomeMessage = intent.getStringExtra("welcomeMessage");

        this.start();
    }

    @Override
    public void onBackPressed() {
        this.stop();

        Intent data = new Intent();
        data.putExtra("currentResult",currentResult);
        setResult(666, data);

        finish();
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
        Context context = this;

        data = new LimitedList<>(LENGTH_MILLIS);
        classifier = classificationModelService.getModel();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            startSensors();
        }

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case IDENTIFICATION_RESULT_CODE: {
                        IdResultDto result = (IdResultDto) msg.obj;
                        currentResult = result;
                        populateResult(currentResult);
                    }
                }
            }
        };

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                IdResultDto result = identify(context);
                Message message = handler.obtainMessage(IDENTIFICATION_RESULT_CODE, result);
                message.sendToTarget();
            }
        };
        timer.schedule(timerTask, ID_FREQ, ID_FREQ);
    }

    private void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (timer != null) {
            timer.cancel();
        }
        data = null;
    }

    private void startSensors() {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
    }

    private IdResultDto identify(Context context) {
        if (data.isEmpty()) {
            return new IdResultDto();
        }

        IdResultDto result = new IdResultDto();

        sensorManager.unregisterListener(this);

/*        try {
            File logFile = new File(this.getFilesDir().getAbsolutePath() + "log.txt");
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
        }*/
        List<DataWindowDto> windows = ClassificationHelper.getWindowsFromRawEntries(data);
        startSensors();

        List<Boolean> results = new ArrayList<>();
        windows.forEach(w -> results.add(ClassificationHelper.classifyWindow(classifier, w)));

        long successCount = results.stream().filter(r -> r).count();

        result.setSuccessCount((int)successCount);
        result.setWindowCount(results.size());
        result.setStartDate(data.get(0).getDate());
        result.setEndDate(data.get(data.size() - 1).getDate());
        result.setUser(false);

        String infoMessage = String.format("Success: %s, all: %s, changeCount: %s, entries: %s, first: %s, last: %s",
                successCount, results.size(), changeCount, data.size(), data.get(0).getDate(), data.get(data.size() - 1).getDate());

        String title = "Who are you?";
        String message = "I don't know you";
        if (((float)successCount / results.size()) * 100 > SUCCESS_RATE && windows.size() > 10) {
            title = "Hello!";
            message = welcomeMessage;
            result.setUser(true);
            if (allowAutoUpdates) {
                this.updateDataForAccount(windows, result.getStartDate(), result.getEndDate());
            }
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

        return result;
    }

    private void updateDataForAccount(List<DataWindowDto> windows, Date dateStart, Date dateEnd) {
        if (!windows.isEmpty()) {
            RecordingSessionDto recordingSession = new RecordingSessionDto();
            recordingSession.setAccountId(accountId);
            recordingSession.setDeviceId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
            recordingSession.setDateStart(dateStart);
            recordingSession.setDateEnd(dateEnd);
            recordingSession.setDataWindows(new ArrayList<>());
            recordingSession.setDataWindows(windows);
            dataApiService.saveRecordingSessions(Collections.singletonList(recordingSession), accountEmail);
        }
    }

    private void populateResult(IdResultDto result) {
        if (result != null) {
            resultLayout.setVisibility(View.VISIBLE);
            if (result.isUser()) {
                isOwnerResult.setTextColor(Color.GREEN);
                totalCountResult.setTextColor(Color.GREEN);
                successCountResult.setTextColor(Color.GREEN);
                startDateResult.setTextColor(Color.GREEN);
                endDateResult.setTextColor(Color.GREEN);
                isOwnerLabel.setTextColor(Color.GREEN);
                totalCountLabel.setTextColor(Color.GREEN);
                successCountLabel.setTextColor(Color.GREEN);
                startDateLabel.setTextColor(Color.GREEN);
                endDateLabel.setTextColor(Color.GREEN);
                isOwnerResult.setText("YES");
            } else {
                isOwnerResult.setTextColor(Color.RED);
                totalCountResult.setTextColor(Color.RED);
                successCountResult.setTextColor(Color.RED);
                startDateResult.setTextColor(Color.RED);
                endDateResult.setTextColor(Color.RED);
                isOwnerLabel.setTextColor(Color.RED);
                totalCountLabel.setTextColor(Color.RED);
                successCountLabel.setTextColor(Color.RED);
                startDateLabel.setTextColor(Color.RED);
                endDateLabel.setTextColor(Color.RED);
                isOwnerResult.setText("NO");
            }
            totalCountResult.setText(String.valueOf(result.getWindowCount()));
            successCountResult.setText(String.valueOf(result.getSuccessCount()));
            startDateResult.setText(result.getStartDateFormatted());
            endDateResult.setText(result.getEndDateFormatted());

        }
    }

}
