package com.maxosoft.stepmeter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.maxosoft.stepmeter.collect.CollectService;
import com.maxosoft.stepmeter.collect.IdentificationService;
import com.maxosoft.stepmeter.dto.AccountDto;
import com.maxosoft.stepmeter.dto.IdResultDto;
import com.maxosoft.stepmeter.services.ClassificationModelService;

import weka.classifiers.Classifier;

public class MainActivity extends Activity {

    private static final int MODEL_REFRESH_CODE = 83632;

    private Button collectButton;
    private Button identificationButton;
    private Button refreshModelButton;
    private Switch badDataSwitch;
    private Switch autoUpdatesSwitch;

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

    private ClassificationModelService classificationModelService;

    private String accountEmail = null;
    private AccountDto account = null;
    private IdResultDto currentResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        collectButton = findViewById(R.id.collectButton);
        identificationButton = findViewById(R.id.identificationButton);
        refreshModelButton = findViewById(R.id.refreshModelButton);
        badDataSwitch = findViewById(R.id.badDataSwitch);
        autoUpdatesSwitch = findViewById(R.id.autoUpdatesSwitch);

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

        classificationModelService = new ClassificationModelService(this);

        if (getIntent().getExtras() != null) {
            account = (AccountDto) getIntent().getExtras().getSerializable("account");
            if (account != null && account.getIsAdmin() != null && account.getIsAdmin()) {
                badDataSwitch.setVisibility(View.VISIBLE);
                refreshModelButton.setVisibility(View.VISIBLE);
            }
            accountEmail = getIntent().getExtras().getString("accountEmail");
        }

        if (account == null && accountEmail == null) {
            collectButton.setEnabled(false);
            Toast.makeText(this, "Failed to retrieve account. Data Collection is disabled.",
                    Toast.LENGTH_LONG).show();
        }

        if (classificationModelService.getModel() == null) {
            identificationButton.setEnabled(false);
            Toast.makeText(this, "Failed to load classification model. Identification is disabled",
                    Toast.LENGTH_LONG).show();
        }

/*        if (this.isMyServiceRunning(CollectService.class)) {
            collectButton.setText("Stop Collecting");
        }

        badDataSwitch.setChecked(this.isMyServiceRunning(IdentificationService.class));
        resultLayout.setVisibility(View.INVISIBLE);*/

        this.initEvents();
    }

    private void initEvents() {
        Context context = this;

        collectButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, CollectActivity.class);
            if (account != null) {
                intent.putExtra("accountId", account.getId());
            }
            intent.putExtra("accountEmail", accountEmail);
            intent.putExtra("badData", badDataSwitch.isChecked());
            startActivity(intent);
        });

        identificationButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, IdentificationActivity.class);
            intent.putExtra("welcomeMessage", "Custom Welcome Message");
            if (account != null) {
                intent.putExtra("accountId", account.getId());
            }
            intent.putExtra("accountEmail", accountEmail);
            intent.putExtra("allowUpdates", autoUpdatesSwitch.isChecked());
            startActivityForResult(intent, 666);
        });

        refreshModelButton.setOnClickListener(view -> {
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    switch (msg.what) {
                        case MODEL_REFRESH_CODE:
                            String message = msg.obj.toString();
                            Toast.makeText(context, message,
                                    Toast.LENGTH_LONG).show();
                    }
                }
            };

            Thread thread = new Thread(() -> {
                Classifier classifier = classificationModelService.saveOrUpdateModelForAccount(account.getId());
                String toastMessage;
                if (classifier != null) {
                    toastMessage = "Model Refreshed!";
                } else {
                    toastMessage = "Failed to refresh model :(";
                }
                Message message = handler.obtainMessage(MODEL_REFRESH_CODE, toastMessage);
                message.sendToTarget();
            });
            thread.start();
        });

/*        badDataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    startIdentifying();
                } else {
                    stopIdentifying();
                }
            }
        });*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == resultCode) {
            if (resultCode == 666) {
                currentResult = (IdResultDto) data.getExtras().getSerializable("currentResult");
                if (currentResult != null) {
                    populateResult(currentResult);
                }
            }
        }
    }

    private void startCollecting() {
        Intent intent = new Intent(this, CollectService.class);
        intent.putExtra("filesDir", getFilesDir().getAbsolutePath());
        intent.putExtra("accountId", account.getId());
        startForegroundService(intent);
    }

    private void stopCollecting() {
        Intent intent = new Intent(this, CollectService.class);
        intent.putExtra("stop", true);
        startForegroundService(intent);
    }

    private void startIdentifying() {
        Intent intent = new Intent(this, IdentificationService.class);
        intent.putExtra("welcomeMessage", "Custom Welcome Message");
        intent.putExtra("accountId", account.getId());
        intent.putExtra("dir", getFilesDir().getAbsolutePath());
        startForegroundService(intent);
    }

    private void stopIdentifying() {
        Intent intent = new Intent(this, IdentificationService.class);
        intent.putExtra("stop", true);
        startForegroundService(intent);
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
