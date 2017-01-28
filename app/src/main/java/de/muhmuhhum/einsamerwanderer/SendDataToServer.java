package de.muhmuhhum.einsamerwanderer;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/** Klasse die Daten an den Server schicken
 * Created by tomuelle on 27.10.2016.
 */
public class SendDataToServer extends IntentService {
    public static String BENUNAME;
    public static int ID;
    public static int counter=0;
    public static boolean firstStart = true;
    private static final String REGISTER_URL = "http://www.huima.de/post_nameandDistance.php"; //http://www.huima.de/post_nameandDistance.php


    public static final String BROADCAST_ACTION =
            "com.example.android.threadsample.BROADCAST";

    // Defines the key for the status "extra" in an Intent
    public static final String STATUS = "Status";
    public static final String ERROR = "Error";

    private SharedPreferences sharedPreferences;
    private Intent localIntent;
    public SendDataToServer(){
        super("SendDataToServer");

    }

    @Override
    protected void onHandleIntent(Intent intent) {



        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.start();

        // Broadcasts the Intent to receivers in this app.

            sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.saved_Distance),Context.MODE_PRIVATE);
            Log.i("sendData", "bin drin");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Nachverfolgung","On response" + response);

                            if(response.contains("ID")){
                                Log.i("response", response);
                                String[] splited = response.split(";");
                                ID = Integer.parseInt(splited[1]);
                                Log.i("ID", ID + "");
                                int dbDistance = Integer.parseInt(splited[2]);

                                int distance = 0;
                                distance = sharedPreferences.getInt(ID+"",distance);


                                if(distance > dbDistance){

                                    Intent baum = new Intent(getBaseContext(), SendDataToServer.class);
                                    startService(baum);

                                }else{
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt(ID+"", distance);
                                    editor.commit();
                                }
                                Log.i("Distance", distance + "");
                            }


                            localIntent =
                            new Intent(BROADCAST_ACTION).putExtra(STATUS, response+counter);
                            counter++;

                            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(localIntent);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("Fehler",error.toString());
                            localIntent = new Intent(BROADCAST_ACTION).putExtra(ERROR, error.getMessage()+counter);
                            counter++;
                            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(localIntent);
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                        if (firstStart) {
                            params.put("benuname", BENUNAME);
                            firstStart = false;
                        } else {
                            params.put("id", ID + "");

                            int distance = 0;
                            distance = sharedPreferences.getInt(ID + "", distance);
                            params.put("distance", distance + "");
                        }
                    return params;
                }
            };
            requestQueue.add(stringRequest);


    }


}
