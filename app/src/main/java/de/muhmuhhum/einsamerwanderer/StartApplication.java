package de.muhmuhhum.einsamerwanderer;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by tomuelle on 01.12.2016.
 */
public class StartApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/celtg.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        Log.i("baum","bin in application");
        // do stuff (prefs, etc)

        // start the initial Activity
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


}
