package de.muhmuhhum.einsamerwanderer;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
    public static int DISTANCE = 0;
    public static String BENUNAME;
    public static int ID;
    public static boolean firstStart = true;
    private static final String REGISTER_URL = "http://www.huima.de/post_nameandDistance.php"; //http://www.huima.de/post_nameandDistance.php
    public static Activity mainApp;

    public static final String BROADCAST_ACTION =
            "com.example.android.threadsample.BROADCAST";

    // Defines the key for the status "extra" in an Intent
    public static final String STATUS = "Status";

    private SharedPreferences sharedPreferences;
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
                                Log.i("response", response);
                                String[] splited = response.split(";");
                                ID = Integer.parseInt(splited[1]);
                                Log.i("ID", ID + "");
                                int distance = Integer.parseInt(splited[2]);
                                sharedPreferences = mainApp.getPreferences(Context.MODE_PRIVATE);
                                int dbDistance = 0;
                                try {
                                    dbDistance = mainApp.getResources().getInteger(ID);
                                }catch(Resources.NotFoundException e){

                                }
                                if(distance > dbDistance){
                                    SharedPreferences.Editor edit = sharedPreferences.edit();
                                    edit.putInt(ID+"",distance);
                                    edit.commit();
                                }
                                Log.i("Distance", DISTANCE + "");
                            }


                            localIntent =
                            new Intent(BROADCAST_ACTION)

                                    .putExtra(STATUS, response);

                            LocalBroadcastManager.getInstance(mainApp).sendBroadcast(localIntent);
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
                    if(firstStart){
                        params.put("benuname", BENUNAME);
                        Log.i("benuname",BENUNAME);
                        firstStart = false;
                    }else{
                        params.put("id", ID+"");
                        sharedPreferences = mainApp.getPreferences(Context.MODE_PRIVATE);
                        int distane = mainApp.getResources().getInteger(ID);
                        params.put("distance", distane+"");

                    }
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

    }


}
