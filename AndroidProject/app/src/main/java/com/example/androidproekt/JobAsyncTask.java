package com.example.androidproekt;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class JobAsyncTask extends AsyncTask<Void, Void, Void> {
    public Context ctx;
    public JobAsyncTask(Context context) {
        ctx=context;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Log.i("TEST", "doInBackground");
            Top.doTop();
            String data = NetworkUtils.getJobsInfo();
            Log.d("DATA", data);
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String jobType = jsonObject.getString("jobType");
                if (jobType.equals("PING")) {
                    int jobPeriod = jsonObject.getInt("jobPeriod");
                    int s = (int) (600 / jobPeriod) + 1;
                    Log.i("TEST", "vrti " + s + " pati");
                    for (int j = 0; j <= (int) (600 / jobPeriod); j++) { //10min(600s)
                        Ping.doPing(jsonObject, ctx);
                        try {
                            Thread.sleep(jobPeriod * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}