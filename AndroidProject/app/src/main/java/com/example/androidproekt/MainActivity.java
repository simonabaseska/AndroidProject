package com.example.androidproekt;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Log.i("TEST", "version > LOLIPOP");
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            Log.i("TEST", "version < LOLIPOP");
            StarterClass starterClass = new StarterClass();
            starterClass.launchService(getApplicationContext());
        }
    }
}
