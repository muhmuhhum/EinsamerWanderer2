package de.muhmuhhum.einsamerwanderer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {


    private boolean isStartAlreadyClicked;


    private EditText ed;
    private Button send;
    private LinearLayout login_layout;

    private LinearLayout play_layout;
    private Button start;
    private Button stop;


    private boolean firstLocation = true;
    private Location ort1;
    private LocationManager locationManager;
    private LocationListener locationListener;


    private Intent mServiceIntent;


    private final int MIN_DISTANCE = 30000;
    private final int MIN_DURATION = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SendDataToServer.mainApp = this;
        marshmallowGPSPremissionCheck();
        start = (Button) findViewById(R.id.btn_start);
        stop = (Button) findViewById(R.id.btn_stop);
        send = (Button) findViewById(R.id.btn_send);

        ed = (EditText) findViewById(R.id.et_benuname);

        login_layout = (LinearLayout) findViewById(R.id.login_layout);
        play_layout = (LinearLayout) findViewById(R.id.play_layout);
        play_layout.setVisibility(View.INVISIBLE);


        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                if (firstLocation) {
                    ort1 = location;
                    firstLocation = false;
                } else {
                    SendDataToServer.distance += ort1.distanceTo(location);
                    ort1 = location;
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {

            }

            public void onProviderDisabled(String provider) {

                Intent gpsOptionsIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsOptionsIntent);
            }
        };

// Register the listener with the Location Manager to receive location updates


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ed.getText().toString().equals("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Bitte geben sie einen Benutzernamen ein");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });


                    AlertDialog alert11 = builder1.create();
                    alert11.show();


                } else {
                    login_layout.setVisibility(View.INVISIBLE);
                    play_layout.setVisibility(View.VISIBLE);
                    SendDataToServer.benuname = ed.getText().toString();
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        marshmallowGPSPremissionCheck();
                        return;
                    }

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Started");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });


                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    isStartAlreadyClicked = true;

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_DISTANCE, MIN_DURATION, locationListener);

                }else{
                    Intent gpsOptionsIntent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsOptionsIntent);
                }



                }




        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isStartAlreadyClicked) {
                    mServiceIntent = new Intent(MainActivity.this, SendDataToServer.class);
                    startService(mServiceIntent);


                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        marshmallowGPSPremissionCheck();
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_DISTANCE, MIN_DURATION, locationListener);
                    locationManager.removeUpdates(locationListener);
                    isStartAlreadyClicked = false;
                } else {

                }

            }

        });



        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(SendDataToServer.STATUS);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("Status: " + message);
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();



                }


        };
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter(SendDataToServer.BROADCAST_ACTION));

    }

    @Override
    protected void onStart() {
        super.onStart();
        isStartAlreadyClicked = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mServiceIntent == null){
            mServiceIntent = new Intent(MainActivity.this,SendDataToServer.class);
            startService(mServiceIntent);
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SendDataToServer.benuname != null){
            login_layout.setVisibility(View.INVISIBLE);
            play_layout.setVisibility(View.VISIBLE);
        }
    }

    private void marshmallowGPSPremissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && MainActivity.this.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
            //   gps functions.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality
        }else{
            System.exit(0);
        }
    }



}

