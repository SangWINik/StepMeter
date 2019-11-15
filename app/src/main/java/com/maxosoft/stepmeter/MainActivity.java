package com.maxosoft.stepmeter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import com.maxosoft.stepmeter.api.DataApiService;
import com.maxosoft.stepmeter.collect.CollectService;
import com.maxosoft.stepmeter.data.ClassificationHelper;
import com.maxosoft.stepmeter.data.FeatureSuit;
import com.maxosoft.stepmeter.data.Window;
import com.maxosoft.stepmeter.dto.DataWindowDto;
import com.maxosoft.stepmeter.dto.RecordingSessionDto;
import com.maxosoft.stepmeter.services.DataWindowService;
import com.maxosoft.stepmeter.util.FileUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class MainActivity extends AppCompatActivity {
    private final static String EMAIL_TO = "go1oborodko97@gmail.com";
    private final static String SUBJECT = "Collected Data";

    private DataApiService dataApiService = new DataApiService(this);
    private DataWindowService dataWindowService = new DataWindowService(this);

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button collectBtn = findViewById(R.id.buttonCollect);
        if (this.isMyServiceRunning(CollectService.class)) {
            collectBtn.setText("Stop");
        }
        collectBtn.setOnClickListener(view -> {
            if (collectBtn.getText().equals("Collect")) {
                collectBtn.setText("Stop");
                startCollecting();
            } else {
                collectBtn.setText("Collect");
                stopCollecting();
            }
        });

        final Button shareBtn = findViewById(R.id.buttonShare);
        shareBtn.setOnClickListener(view -> {
            File dir = new File(getFilesDir().getAbsolutePath() + "/collect");
            File[] files = dir.listFiles();
            sendEmail(files);
            deleteFiles(files);
        });

        final Button readBtn = findViewById(R.id.buttonRead);
        readBtn.setOnClickListener(view -> {
            /*File[] myFiles = FileUtil.getFilesFromDirectory(getFilesDir().getAbsolutePath() + "/data/Me");
            File[] otherFiles = FileUtil.getFilesFromDirectory(getFilesDir().getAbsolutePath() + "/data/Others");*/
            Long accountId = 2L;
            Thread classificationThread = new Thread(() -> {
                List<DataWindowDto> ownerData = dataApiService.getDataWindowsForAccount(accountId);
                List<DataWindowDto> otherData = dataApiService.getDataWindowsExceptAccount(accountId);
                this.runClassification(new IBk(), ownerData, otherData);
                this.runClassification(new NaiveBayes(), ownerData, otherData);
                this.runClassification(new J48(), ownerData, otherData);
/*                this.runClassification(myFiles, otherFiles, new IBk(), FeatureSuit.MIN_MAX_ACC);
                this.runClassification(myFiles, otherFiles, new NaiveBayes(), FeatureSuit.MIN_MAX_ACC);
                this.runClassification(myFiles, otherFiles, new J48(), FeatureSuit.MIN_MAX_ACC);
                this.runClassification(myFiles, otherFiles, new IBk(), FeatureSuit.GENERAL_CHARACTERISTICS_ACC);
                this.runClassification(myFiles, otherFiles, new NaiveBayes(), FeatureSuit.GENERAL_CHARACTERISTICS_ACC);
                this.runClassification(myFiles, otherFiles, new J48(), FeatureSuit.GENERAL_CHARACTERISTICS_ACC);
                this.runClassification(myFiles, otherFiles, new IBk(), FeatureSuit.ZERO_MEAN_ACC);
                this.runClassification(myFiles, otherFiles, new NaiveBayes(), FeatureSuit.ZERO_MEAN_ACC);
                this.runClassification(myFiles, otherFiles, new J48(), FeatureSuit.ZERO_MEAN_ACC);
                this.runClassification(myFiles, otherFiles, new IBk(), FeatureSuit.ALL_ACC);
                this.runClassification(myFiles, otherFiles, new NaiveBayes(), FeatureSuit.ALL_ACC);
                this.runClassification(myFiles, otherFiles, new J48(), FeatureSuit.ALL_ACC);*/
            });
            classificationThread.start();
        });

        final Button uploadBtn = findViewById(R.id.buttonUpload);
        uploadBtn.setOnClickListener(view -> {
            this.dataApiService.getDataWindowsForAccount(2L);
            /*File[] files = FileUtil.getFilesFromDirectory(getFilesDir().getAbsolutePath() + "/data/upload");
            List<RecordingSessionDto> recordingSessions = new ArrayList<>();
            for (File file: files) {
                List<Window> windows = ClassificationHelper.getWindowsFromFile(file, true, FeatureSuit.ALL);
                if (windows.isEmpty()) {
                    continue;
                }
                RecordingSessionDto recordingSessionDto = new RecordingSessionDto();
                recordingSessionDto.setAccountId(2L);
                recordingSessionDto.setDeviceId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                recordingSessionDto.setDateStart(windows.get(0).getDateStart());
                recordingSessionDto.setDateEnd(windows.get(windows.size() - 1).getDateEnd());
                recordingSessionDto.setDataWindows(new ArrayList<>());
                for (Window window: windows) {
                    recordingSessionDto.getDataWindows().add(new DataWindowDto(window, null));
                }
                recordingSessions.add(recordingSessionDto);
            }

            this.dataApiService.saveRecordingSessions(recordingSessions);*/
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startCollecting() {
        Intent intent = new Intent(MainActivity.this, CollectService.class);
        intent.putExtra("filesDir", getFilesDir().getAbsolutePath());
        startService(intent);
    }

    private void stopCollecting() {
        Intent intent = new Intent(MainActivity.this, CollectService.class);
        intent.putExtra("stop", true);
        startService(intent);
    }

    private void sendEmail(File[] files) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        System.out.println("Send email");

        Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL_TO});
        email.putExtra(Intent.EXTRA_SUBJECT, SUBJECT);
        email.putExtra(Intent.EXTRA_TEXT, "");
        ArrayList<Uri> uris = new ArrayList<>();
        for (File file: files) {
            try {
                String name = file.getName().lastIndexOf(".") > 0 ? file.getName()
                        .substring(0, file.getName().lastIndexOf(".")) : file.getName();
                File tempFile = File.createTempFile(name + "_", ".txt", getExternalCacheDir());
                FileWriter fw = new FileWriter(tempFile);

                FileReader fr = new FileReader(file);
                int c = fr.read();
                while (c != -1) {
                    fw.write(c);
                    c = fr.read();
                }
                fr.close();

                fw.flush();
                fw.close();
                uris.add(Uri.fromFile(tempFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        email.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    private void deleteFiles(File[] files) {
        for (File file: files) {
            file.delete();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void runClassification(File[] ownerFiles, File[] otherFiles, Classifier classifier, FeatureSuit featureSuit) {
        List<Window> allWindows = ClassificationHelper.getAllWindows(ownerFiles, otherFiles, featureSuit);
        File dataFile = FileUtil.createCSVFile(getFilesDir().getAbsolutePath(), allWindows);

        try {
            Instances data = ClassificationHelper.getSourceData(dataFile);
            long time = System.currentTimeMillis();
            Evaluation evaluation = ClassificationHelper.getClassificationResults(classifier, data);
            System.out.println(classifier.getClass().toString() + "evaluation");
            System.out.println("Feature Suit: " + featureSuit.name());
            System.out.println("Processing Time: " + (System.currentTimeMillis() - time));
            System.out.println(evaluation.toSummaryString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runClassification(Classifier classifier, List<DataWindowDto> ownerData, List<DataWindowDto> otherData) {
        File dataFile = FileUtil.createCSVFile(getFilesDir().getAbsolutePath(), ownerData, otherData);

        try {
            Instances data = ClassificationHelper.getSourceData(dataFile);
            long time = System.currentTimeMillis();
            Evaluation evaluation = ClassificationHelper.getClassificationResults(classifier, data);
            System.out.println(classifier.getClass().toString() + "evaluation");
            System.out.println("Processing Time: " + (System.currentTimeMillis() - time));
            System.out.println(evaluation.toSummaryString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
