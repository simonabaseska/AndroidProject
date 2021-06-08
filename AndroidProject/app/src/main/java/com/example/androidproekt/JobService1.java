package com.example.androidproekt;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

public class JobService1 extends JobService {
    public static final String RESTART_INTENT = "com.example.androidproekt.RESTART_INTENT";
    private static RestartServiceBroadcastReceiver restartSensorServiceReceiver;
    private static JobService1 instance;
    private static JobParameters jobParameters;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i("TEST", "onStartJob");

        StarterClass starterClass = new StarterClass();
        starterClass.launchService(this);
        registerRestarterReceiver();
        instance= this;
        JobService1.jobParameters= jobParameters;

        return false;
    }

    public void registerRestarterReceiver() {
        Log.i("TEST", "register receiver vo jobservice");
        if (restartSensorServiceReceiver == null)
            restartSensorServiceReceiver = new RestartServiceBroadcastReceiver();
        else try{
            unregisterReceiver(restartSensorServiceReceiver);
        } catch (Exception e){

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                IntentFilter filter = new IntentFilter();
                filter.addAction(RESTART_INTENT);
                try {
                    registerReceiver(restartSensorServiceReceiver, filter);
                } catch (Exception e) {
                    try {
                        getApplicationContext().registerReceiver(restartSensorServiceReceiver, filter);
                    } catch (Exception ex) {

                    }
                }
            }
        }, 1000);
    }

    //called if Android kills the job service
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i("TEST", "stopping job");
        Intent broadcastIntent = new Intent(RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                unregisterReceiver(restartSensorServiceReceiver);
            }
        }, 1000);
        return false;
    }

    //called when the tracker is stopped for whatever reason
    public static void stopJob(Context context) {
        Log.i("TEST", "stopJob");
        if (instance!=null && jobParameters!=null) {
            try{
                instance.unregisterReceiver(restartSensorServiceReceiver);
            } catch (Exception e) {

            }
            instance.jobFinished(jobParameters, true);
        }
    }
}