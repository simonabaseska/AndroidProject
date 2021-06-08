package com.example.androidproekt;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

class StarterClass {
    private static Intent serviceIntent = null;

    public StarterClass(){
    }

    public void launchService(Context context) {
      if (context == null) {
          return;
      }
        serviceIntent = new Intent(context, Service1.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("TEST", "started foreground");
            context.startForegroundService(serviceIntent);
        } else {
            Log.i("TEST", "started regular");
            context.startService(serviceIntent);
        }
        Log.i("TEST", "Launching service");
    }
}