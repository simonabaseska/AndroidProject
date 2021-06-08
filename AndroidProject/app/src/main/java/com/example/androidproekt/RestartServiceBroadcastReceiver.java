package com.example.androidproekt;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.RequiresApi;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class RestartServiceBroadcastReceiver extends BroadcastReceiver {
    public static final String RESTART_INTENT = "com.example.androidproekt.RESTART_INTENT";
    private static JobScheduler jobScheduler;
    private RestartServiceBroadcastReceiver restartSensorServiceReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TEST", "onReceive");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            scheduleJob(context);
        } else {
            registerRestarterReceiver(context);
            StarterClass starterClass = new StarterClass();
            starterClass.launchService(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(Context context) {
        Log.i("TEST", "Schedule job");
        if (jobScheduler == null) {
            jobScheduler = (JobScheduler) context
                    .getSystemService(JOB_SCHEDULER_SERVICE);
        }
        ComponentName componentName = new ComponentName(context,
                JobService1.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setOverrideDeadline(0)
                .setPersisted(true).build();
        jobScheduler.schedule(jobInfo);
    }

    private void registerRestarterReceiver(final Context context) {
        Log.i("TEST", "register receiver vo receiver");
        if (restartSensorServiceReceiver == null)
            restartSensorServiceReceiver = new RestartServiceBroadcastReceiver();
        else try{
            context.unregisterReceiver(restartSensorServiceReceiver);
        } catch (Exception e) {

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                IntentFilter filter = new IntentFilter(RESTART_INTENT);
                try {
                    context.registerReceiver(restartSensorServiceReceiver, filter);
                } catch (Exception e) {
                    try {
                        context.getApplicationContext().registerReceiver(restartSensorServiceReceiver, filter);
                    } catch (Exception ex) {

                    }
                }
            }
        }, 1000);
    }

    public static void reStartTracker(Context context) {
       Intent broadcastIntent = new Intent(RESTART_INTENT);
        context.sendBroadcast(broadcastIntent);
    }
}