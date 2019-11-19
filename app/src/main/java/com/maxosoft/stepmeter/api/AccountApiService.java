package com.maxosoft.stepmeter.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxosoft.stepmeter.dto.AccountDto;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class AccountApiService {
    private static final String SERVICE = "http://stepmeter.best";

    public AccountDto getAccountByEmail(String email) {
        AccountDto account = null;

        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .method("GET", null)
                    .url(SERVICE + "/accounts?email=" + email)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String body = response.body().string();
                if (!body.isEmpty()) {
                    account = new ObjectMapper().readValue(body, AccountDto.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return account;
    }

    public void saveAccount(String email) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .method("POST", RequestBody.create(null, new byte[]{}))
                    .url(SERVICE + "/accounts?email=" + email)
                    .build();

            client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
