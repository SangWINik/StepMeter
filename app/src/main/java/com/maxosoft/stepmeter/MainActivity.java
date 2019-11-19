package com.maxosoft.stepmeter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.maxosoft.stepmeter.collect.CollectService;
import com.maxosoft.stepmeter.dto.AccountDto;

public class MainActivity extends Activity {

    private Button collectButton;

    private AccountDto account = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        collectButton = findViewById(R.id.collectButton);

        if (getIntent().getExtras() != null) {
            account = (AccountDto) getIntent().getExtras().getSerializable("account");
        }

        if (this.isMyServiceRunning(CollectService.class)) {
            collectButton.setText("Stop Collecting");
        }

        this.initEvents();
    }

    private void initEvents() {
        collectButton.setOnClickListener(view -> {
            if (!this.isMyServiceRunning(CollectService.class)) {
                collectButton.setText("Stop Collecting");
                startCollecting();
            } else {
                collectButton.setText("Collect Data");
                stopCollecting();
            }
        });
    }

    private void startCollecting() {
        Intent intent = new Intent(this, CollectService.class);
        intent.putExtra("filesDir", getFilesDir().getAbsolutePath());
        intent.putExtra("accountId", account.getId());
        startService(intent);
    }

    private void stopCollecting() {
        Intent intent = new Intent(this, CollectService.class);
        intent.putExtra("stop", true);
        startService(intent);
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
