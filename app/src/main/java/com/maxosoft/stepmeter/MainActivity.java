package com.maxosoft.stepmeter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.maxosoft.stepmeter.collect.CollectService;
import com.maxosoft.stepmeter.collect.IdentificationService;
import com.maxosoft.stepmeter.dto.AccountDto;

public class MainActivity extends Activity {

    private Button collectButton;
    private Switch realTimeIdSwitch;

    private AccountDto account = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        collectButton = findViewById(R.id.collectButton);
        realTimeIdSwitch = findViewById(R.id.realTimeIdSwitch);

        if (getIntent().getExtras() != null) {
            account = (AccountDto) getIntent().getExtras().getSerializable("account");
        }

        if (this.isMyServiceRunning(CollectService.class)) {
            collectButton.setText("Stop Collecting");
        }

        realTimeIdSwitch.setChecked(this.isMyServiceRunning(IdentificationService.class));

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

        realTimeIdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    startIdentifying();
                } else {
                    stopIdentifying();
                }
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

    private void startIdentifying() {
        Intent intent = new Intent(this, IdentificationService.class);
        intent.putExtra("welcomeMessage", "Custom Welcome Message");
        intent.putExtra("accountId", account.getId());
        startService(intent);
    }

    private void stopIdentifying() {
        Intent intent = new Intent(this, IdentificationService.class);
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
