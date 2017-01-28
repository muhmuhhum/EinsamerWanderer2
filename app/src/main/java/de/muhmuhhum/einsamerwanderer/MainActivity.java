package de.muhmuhhum.einsamerwanderer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.toolbox.Volley;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends Activity {


    //TODO: und kein internet beim einloggen lösen


    private static boolean isStartAlreadyClicked = false;

    //region Layout definition


    private Button btn_logdin;
    private Button btn_change;
    private LinearLayout already_login;

    private EditText ed;
    private Button send;
    private LinearLayout login_layout;

    private LinearLayout play_layout;
    private ImageView imageView;
    private Button start;
    private Button stop;


    //endregion

    //region ForLocation definition
    private boolean firstLocation = true;
    private Location ort1;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private final long MIN_TIME= 3000; // milisek.
    private final float MIN_DISTANCE = 5; //meter

    //endregion

    //region mServiceIntent definition
    private Intent mServiceIntent;
    //endregion


    private static int WANNSCHREIBEN = 20;


//region sharedPreferences definition

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_main);

        marshmallowGPSPremissionCheck();


        //region Zuweisung der Ids
        imageView = (ImageView) findViewById(R.id.imageView);


        btn_logdin = (Button) findViewById(R.id.btn_logdin);
        btn_change = (Button) findViewById(R.id.btn_change);
        already_login = (LinearLayout) findViewById(R.id.already_login);

        start = (Button) findViewById(R.id.btn_start);
        stop = (Button) findViewById(R.id.btn_stop);
        send = (Button) findViewById(R.id.btn_send);


        ed = (EditText) findViewById(R.id.et_benuname);

        login_layout = (LinearLayout) findViewById(R.id.login_layout);
        play_layout = (LinearLayout) findViewById(R.id.play_layout);


        already_login.setVisibility(View.INVISIBLE);
        login_layout.setVisibility(View.INVISIBLE);
        play_layout.setVisibility(View.INVISIBLE);
        play_layout.setVisibility(View.INVISIBLE);



        //endregion

        mServiceIntent = new Intent(MainActivity.this, SendDataToServer.class);


        //region sharedPreferences
       sharedPreferences = this.getSharedPreferences(getString(R.string.saved_Distance),Context.MODE_PRIVATE) ;
        editor = sharedPreferences.edit();
        //endregion


        if(sharedPreferences.contains(getString(R.string.benutzerLogdIn))){
            already_login.setVisibility(View.VISIBLE);
        }else{
            login_layout.setVisibility(View.VISIBLE);
        }

        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates

        //region Location Listener
        final LocationListener locationListener = new LocationListener() {
            int oldDistance = 0;
            public void onLocationChanged(Location location) {
                int distance = 0;
                try {
                    distance = getResources().getInteger(SendDataToServer.ID);
                    Log.i("distance",distance+"");
                }catch(Resources.NotFoundException e){

                }
                if (firstLocation) {
                    ort1 = location;
                    firstLocation = false;
                     oldDistance = distance ;
                } else {
                    distance += ort1.distanceTo(location);
                    editor.putInt(SendDataToServer.ID+"",distance);
                    editor.commit();
                    ort1 = location;

                    if(oldDistance/WANNSCHREIBEN < distance/WANNSCHREIBEN){
                        startService(mServiceIntent);
                        oldDistance = distance;


                    }

                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i("prov",provider);
            }

            public void onProviderEnabled(String provider) {

            }

            public void onProviderDisabled(String provider) {

                Intent gpsOptionsIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsOptionsIntent);
            }
        };
        //endregion

// Register the listener with the Location Manager to receive location updates


        //region send OnClickListener
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


                    SendDataToServer.BENUNAME = ed.getText().toString().trim();

                    Log.i("Nachverfolgung","in send else");
                    mServiceIntent = new Intent(MainActivity.this,SendDataToServer.class);

                    startService(mServiceIntent);


                    editor.putString(getString(R.string.benutzerLogdIn),SendDataToServer.BENUNAME);
                    editor.commit();

                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                imageView.setImageResource(R.drawable.campfire_remasterd);
            }

        });

        //endregion

        //region start OnClickListener
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        marshmallowGPSPremissionCheck();
                        return;
                    }



                    isStartAlreadyClicked = true;

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
                    imageView.setImageResource(R.drawable.campfire_aus);
                }else{
                    Intent gpsOptionsIntent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsOptionsIntent);
                }



                }




        });

        //endregion

        //region stop OnClickListener
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Bin im Stop",isStartAlreadyClicked+"");
                if (isStartAlreadyClicked) {

                    startService(mServiceIntent);


                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        marshmallowGPSPremissionCheck();
                        return;
                    }
                    locationManager.removeUpdates(locationListener);
                    isStartAlreadyClicked = false;

                } else {

                }
                imageView.setImageResource(R.drawable.campfire_remasterd);

            }

        });


        btn_logdin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String benuname = sharedPreferences.getString(getString(R.string.benutzerLogdIn),"");
                ed.setText(benuname);
                already_login.setVisibility(View.INVISIBLE);
                send.callOnClick();
            }
        });

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                already_login.setVisibility(View.INVISIBLE);
                login_layout.setVisibility(View.VISIBLE);

            }
        });

        //endregion



        //region Brodcaster for Service Input
        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    String message = intent.getStringExtra(SendDataToServer.STATUS);
                    String error = intent.getStringExtra(SendDataToServer.ERROR);
                    if(error != null) {
                        mServiceIntent = null;
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                        builder2.setMessage("Sie haben ihr internet aus");
                        builder2.setCancelable(true);
                        AlertDialog al = builder2.create();
                        al.show();
                        SendDataToServer.firstStart = true;
                        login_layout.setVisibility(View.VISIBLE);
                        play_layout.setVisibility(View.INVISIBLE);

                    }else{

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


                }


        };
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter(SendDataToServer.BROADCAST_ACTION));
        //endregion
    }

    @Override
    protected void onStart() {
        super.onStart();

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
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SendDataToServer.BENUNAME != null){
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
            //TODO
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }



}

