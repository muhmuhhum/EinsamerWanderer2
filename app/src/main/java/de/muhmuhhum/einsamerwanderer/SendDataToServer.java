package de.muhmuhhum.einsamerwanderer;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.security.Provider;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomuelle on 27.10.2016.
 */
public class SendDataToServer extends IntentService {
    public static double distance = 0.0;
    public static String benuname;
    private static final String REGISTER_URL = "http://www.huima.de/post_nameandDistance.php"; //http://www.huima.de/post_nameandDistance.php

    public SendDataToServer(){
        super("SendDataToServer");

    }

    @Override
    protected void onHandleIntent(Intent intent) {



            Log.i("sendData", "bin drin");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("return", response);
                            AlertDialog.Builder ad = new AlertDialog.Builder(getApplicationContext());

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("Error", error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    DecimalFormat df = new DecimalFormat("0.00");
                    params.put("benuname", benuname);
                    params.put("distance", df.format(distance));
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

    }
}
