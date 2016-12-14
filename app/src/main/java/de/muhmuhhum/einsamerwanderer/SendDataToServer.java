package de.muhmuhhum.einsamerwanderer;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomuelle on 27.10.2016.
 */
public class SendDataToServer extends IntentService {
    public static double DISTANCE = 0.0;
    public static String BENUNAME;
    public static int ID;
    public static boolean firstStart = true;
    private static final String REGISTER_URL = "http://www.huima.de/post_nameandDistance.php"; //http://www.huima.de/post_nameandDistance.php
    public static Activity mainApp;

    public static final String BROADCAST_ACTION =
            "com.example.android.threadsample.BROADCAST";

    // Defines the key for the status "extra" in an Intent
    public static final String STATUS = "Status";

    private Intent localIntent;
    public SendDataToServer(){
        super("SendDataToServer");

    }

    @Override
    protected void onHandleIntent(Intent intent) {



        // Broadcasts the Intent to receivers in this app.


            Log.i("sendData", "bin drin");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if(response.contains("ID")){
                                String[] splited = response.split(";");
                                ID = Integer.parseInt(splited[1]);
                                DISTANCE = Double.parseDouble(splited[2]);
                                return;
                            }

                            localIntent =
                            new Intent(BROADCAST_ACTION)

                                    .putExtra(STATUS, response);

                            LocalBroadcastManager.getInstance(mainApp).sendBroadcast(localIntent);
                            DISTANCE = 0.0;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            localIntent =
                                    new Intent(BROADCAST_ACTION)

                                            .putExtra(STATUS, error.getMessage());

                            LocalBroadcastManager.getInstance(mainApp).sendBroadcast(localIntent);


                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    DecimalFormat df = new DecimalFormat("0.00");
                    if(firstStart){
                        params.put("BENUNAME", BENUNAME);
                    }else{
                        params.put("ID", String.valueOf(ID));
                        params.put("DISTANCE", df.format(DISTANCE));

                    }
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

    }


}
