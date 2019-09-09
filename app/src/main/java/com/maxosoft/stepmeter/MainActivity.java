package com.maxosoft.stepmeter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.maxosoft.stepmeter.collect.CollectService;
import com.maxosoft.stepmeter.data.RawDataCollection;
import com.maxosoft.stepmeter.data.Window;
import com.maxosoft.stepmeter.util.FileUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.os.StrictMode;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String EMAIL_TO = "go1oborodko97@gmail.com";
    private final static String SUBJECT = "Collected Data";

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
        collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (collectBtn.getText().equals("Collect")) {
                    collectBtn.setText("Stop");
                    startCollecting();
                } else {
                    collectBtn.setText("Collect");
                    stopCollecting();
                }
            }
        });

        final Button shareBtn = findViewById(R.id.buttonShare);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File dir = new File(getFilesDir().getAbsolutePath() + "/collect");
                File[] files = dir.listFiles();
                sendEmail(files);
                deleteFiles(files);
            }
        });

        final Button readBtn = findViewById(R.id.buttonRead);
        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File[] myFiles = FileUtil.getFilesFromDirectory(getFilesDir().getAbsolutePath() + "/data/Me");
                File[] otherFiles = FileUtil.getFilesFromDirectory(getFilesDir().getAbsolutePath() + "/data/Others");
                List<RawDataCollection> myData = new ArrayList<>();
                RawDataCollection collection = new RawDataCollection(FileUtil.getRawDataFromFile(myFiles[0]));
                List<Window> windows = collection.getWindows();
            }
        });

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
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
}
