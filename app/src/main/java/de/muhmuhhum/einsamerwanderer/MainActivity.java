package de.muhmuhhum.einsamerwanderer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
    private LinearLayout login_layout ;

    private LinearLayout play_layout;
    private Button start;
    private Button stop;


    private boolean firstLocation = true;
    private Location ort1;
    private LocationManager locationManager;
    private LocationListener locationListener;


    private Intent mServiceIntent ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                if(firstLocation){
                    ort1 = location;
                    firstLocation = false;
                }else{
                    SchnittstelleMitVariablen.distance += ort1.distanceTo(location);
                    ort1 = location;
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

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


                }else{
                    login_layout.setVisibility(View.INVISIBLE);
                    play_layout.setVisibility(View.VISIBLE);
                    SchnittstelleMitVariablen.benuname = ed.getText().toString();
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

               /* if (t == null) {
                    t = new GpsThread();
                    t.start();
                    isStartAlreadyClicked = true;
                }*/

                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){


                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30, 0, locationListener);

                }else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30, 0, locationListener);

                }else{
                    Intent gpsOptionsIntent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsOptionsIntent);
                }

                isStartAlreadyClicked = true;


            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isStartAlreadyClicked) {
                    mServiceIntent = new Intent(MainActivity.this, SendDataToServer.class);
                    startService(mServiceIntent);

                } else {

                }

            }

        });

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

    private void marshmallowGPSPremissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && MainActivity.this.checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && MainActivity.this.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
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

