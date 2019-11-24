package com.maxosoft.stepmeter.api;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxosoft.stepmeter.dto.DataWindowDto;
import com.maxosoft.stepmeter.dto.RecordingSessionDto;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.List;

public class DataApiService {
    private static final String SERVICE = "http://stepmeter.best";

    private Context context;

    public DataApiService(Context context) {
        this.context = context;
    }

    public void saveRecordingSessions(List<RecordingSessionDto> recordingSessions, String email) {
        VolleyLog.DEBUG = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                saveSessions(recordingSessions, email);
            }
        });

        thread.start();
    }

    public List<DataWindowDto> getDataWindowsForAccount(Long accountId) {
        List<DataWindowDto> dataWindows = new ArrayList<>();

        try {
            OkHttpClient client = new OkHttpClient();
            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(SERVICE + "/data-windows-for-account/" + accountId)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                dataWindows = new ObjectMapper().readValue(response.body().string(), new TypeReference<List<DataWindowDto>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataWindows;
    }

    public List<DataWindowDto> getDataWindowsExceptAccount(Long accountId) {
        List<DataWindowDto> dataWindows = new ArrayList<>();

        try {
            OkHttpClient client = new OkHttpClient();
            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(SERVICE + "/data-windows-except-account/" + accountId)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                dataWindows = new ObjectMapper().readValue(response.body().string(), new TypeReference<List<DataWindowDto>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataWindows;
    }

    private void saveSessions(List<RecordingSessionDto> recordingSessions, String email) {
        List<StringRequest> requests = new ArrayList<>();
        for (RecordingSessionDto recordingSession : recordingSessions) {
            String url = SERVICE + "/recording-session";
            if (email != null && !email.isEmpty()) {
                url += "?email=" + email;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    System.out::println,
                    error -> System.err.println(error.getMessage())) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    String jsonBody = "";
                    try {
                        jsonBody = new ObjectMapper().writeValueAsString(recordingSession);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return jsonBody.getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            requests.add(stringRequest);
        }

        RequestQueue queue = Volley.newRequestQueue(this.context);
        requests.forEach(queue::add);

        queue.start();
    }
}
