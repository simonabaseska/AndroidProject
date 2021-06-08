package com.example.androidproekt;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Top {
    public static void doTop() {
                String returnString = null;
                String statResult = "";
                try {
                    Process pstat = Runtime.getRuntime().exec("top -n 1");
                    BufferedReader in = new BufferedReader(new InputStreamReader(pstat.getInputStream()));
                    String inputLine;

                    while (returnString == null || returnString.contentEquals("")) {
                        returnString = in.readLine();
                    }
                    statResult += returnString + ",";
                    while ((inputLine = in.readLine()) != null) {
                        inputLine += ";";
                        statResult += inputLine;
                    }
                    in.close();
                    if (pstat != null) {
                        pstat.getOutputStream().close();
                        pstat.getInputStream().close();
                        pstat.getErrorStream().close();
                    }
                    Log.i("STATRESULT", "statResult = " + statResult);
                    Ping.sendResult(statResult);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }