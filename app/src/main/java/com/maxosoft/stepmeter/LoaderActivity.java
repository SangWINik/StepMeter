package com.maxosoft.stepmeter;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.maxosoft.stepmeter.api.AccountApiService;
import com.maxosoft.stepmeter.api.DataApiService;
import com.maxosoft.stepmeter.dto.AccountDto;
import com.maxosoft.stepmeter.services.ClassificationModelService;

import java.util.Date;

import weka.classifiers.Classifier;

public class LoaderActivity extends Activity {
    private static final long MODEL_UPDATE_INTERVAL = 1000 * 60 * 60 * 24 * 3; // 3 days
    private static final int REQUEST_CODE_EMAIL = 1234;
    private static final int INIT_COMPETE_CODE = 4321;

    private AccountApiService accountApiService;
    private ClassificationModelService classificationModelService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        accountApiService = new AccountApiService();
        classificationModelService = new ClassificationModelService(this);

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Toast.makeText(this, accountName,
                    Toast.LENGTH_LONG).show();

            Context context = this;
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    switch (msg.what) {
                        case INIT_COMPETE_CODE:
                        AccountDto account = (AccountDto) msg.obj;
                        Intent i = new Intent(context, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra("account", account);
                        startActivity(i);
                    }
                }
            };

            Runnable runnable = () -> {
                AccountDto account = accountApiService.getAccountByEmail(accountName);
                if (account == null) {
                    accountApiService.saveAccount(accountName);
                    account = accountApiService.getAccountByEmail(accountName);
                }

                Date lastModelUpdate = classificationModelService.getModelLastUpdateDate();
                if (lastModelUpdate == null || new Date().getTime() - lastModelUpdate.getTime() > MODEL_UPDATE_INTERVAL) {
                    classificationModelService.saveOrUpdateModelForAccount(account.getId());
                }
                Message message = handler.obtainMessage(INIT_COMPETE_CODE, account);
                message.sendToTarget();
            };
            Thread thread = new Thread(runnable);
            thread.start();

        } else {
            Toast.makeText(this, "Failed to retrieve account information", Toast.LENGTH_LONG).show();
        }
    }

    private void init() {
        try {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                    new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null);
            startActivityForResult(intent, REQUEST_CODE_EMAIL);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
