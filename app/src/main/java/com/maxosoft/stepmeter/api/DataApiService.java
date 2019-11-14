package com.maxosoft.stepmeter.api;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxosoft.stepmeter.dto.DataWindowDto;
import com.maxosoft.stepmeter.dto.RecordingSessionDto;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DataApiService {

    private Context context;

    public DataApiService(Context context) {
        this.context = context;
    }

    public void saveRecordingSessions(List<RecordingSessionDto> recordingSessions) {
        VolleyLog.DEBUG = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                saveSessions(recordingSessions);
            }
        });

        thread.start();
    }

    public void getDataWindowsForAccount(Long accountId) {
        //TODO use OK HTTP or something similar here. Volley does not work with large responses

/*      VolleyLog.DEBUG = true;
        RequestFuture<String> future = RequestFuture.newFuture();
        List<DataWindowDto> dataWindows;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://stepmeter.best/data-windows-for-account/" + accountId,
                future,
                error -> System.err.println(error.getMessage()));

        RequestQueue queue = Volley.newRequestQueue(this.context);
        queue.add(stringRequest);
        queue.start();

        try {
            String response = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void saveSessions(List<RecordingSessionDto> recordingSessions) {
        List<StringRequest> requests = new ArrayList<>();
        for (RecordingSessionDto recordingSession: recordingSessions) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://stepmeter.best/recording-session",
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
