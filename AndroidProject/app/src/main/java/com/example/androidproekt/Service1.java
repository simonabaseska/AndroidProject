package com.example.androidproekt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import java.util.Timer;
import java.util.TimerTask;

public class Service1 extends Service {

    public static final String RESTART_INTENT = "com.example.androidproekt.RESTART_INTENT";
    protected static final int NOTIFICATION_ID = 12345;
    private int counter = 0;
    public static NetworkInfo networkInfo;
    public SharedPreferences prefs;

    public Service1() {
        super();
    }

    @Override
    public void onCreate() {
        Log.i("TEST", "Service onCreate");
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        this.networkInfo=networkInfo;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TEST", "Service onStartCommand");
        super.onStartCommand(intent, flags, startId);
        prefs= getSharedPreferences("com.example.androidproekt", MODE_PRIVATE);
        if (prefs.getInt("counter", 0)!=0) {
            counter=prefs.getInt("counter", 0);
        };
        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            Log.i("TEST", "Killed by android and restarted");
            StarterClass starterClass = new StarterClass();
            starterClass.launchService(this);
        }
        // make sure you call the startForeground on onStartCommand because otherwise when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }
        startTimer();
        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY;
    }

   @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("TEST", "restarting foreground");
            try {
                NotificationManager mNotifyManager= (NotificationManager)
                        getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle("Service notification")
                        .setContentText("This is the service's notification")
                        .setSmallIcon(R.drawable.ic_sleep);
                Notification myNotification = mNotifyBuilder.build();
                startForeground(NOTIFICATION_ID, myNotification);
                startTimer();
            } catch (Exception e) {
                Log.i("TEST", "Error in notification " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.i("TEST", "onDestroy called");
        super.onDestroy();
        try {prefs= getSharedPreferences("com.example.androidproekt", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("counter", counter);
            editor.apply();
        } catch (NullPointerException e) {
            Log.e("TEST", e.getMessage());
        }
        Intent broadcastIntent = new Intent(RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    //this is called when the process is killed by Android
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent broadcastIntent = new Intent(RESTART_INTENT);
        sendBroadcast(broadcastIntent);
    }

    //static to avoid multiple timers to be created when the service is called several times
    private static Timer timer;
    private static TimerTask timerTask;

    public void startTimer() {
        Log.i("TEST", "Starting timer");
        //set a new Timer - if one is already running, cancel it to avoid two running at the same time
        stoptimertask();
        timer = new Timer();
        initializeTimerTask();
        Log.i("TEST", "Scheduling...");
        timer.schedule(timerTask, 5000, 5000);

        if (connectivity()) {
            Log.i("TEST", "network okay, about to call AsyncTask");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        new JobAsyncTask(getApplicationContext()).execute();
                        try {
                            Thread.sleep(600000); //10min
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.start();
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter+=5));
            }
        };
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static boolean connectivity() {
        return networkInfo != null && networkInfo.isConnected();
    }
}