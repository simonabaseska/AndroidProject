package com.example.androidproekt;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

public class Ping {

    public static SharedPreferences prefs;

    public static void doPing(JSONObject jsonObject, Context ctx) throws JSONException {

                String host = jsonObject.getString("host");
                int count = jsonObject.getInt("count");
                int packetSize = jsonObject.getInt("packetSize");
                String pingResult = "";
                try {
                    String pingCmd = "ping -s " + packetSize + " -c " + count + " " + host;
                    Runtime r = Runtime.getRuntime();
                    Process p = r.exec(pingCmd);
                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(p.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        Log.d("PINGTEST", inputLine);
                        pingResult += inputLine;
                    }
                    in.close();
                    Log.d("PINGTEST", pingResult);

                    prefs = ctx.getSharedPreferences("com.example.androidproekt", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    if (Service1.connectivity()) {
                        sendResult(pingResult);
                        if (prefs.getString("ping1", null) != null) {
                            sendResult(prefs.getString("ping1", null));
                            editor.putString("ping1", null);
                        }
                        if (prefs.getString("ping2", null) != null) {
                            sendResult(prefs.getString("ping2", null));
                            editor.putString("ping2", null);
                        }
                    } else {
                        if ((prefs.getString("ping1", null) != null && prefs.getString("ping2", null) != null)
                                || (prefs.getString("ping1", null) == null && prefs.getString("ping2", null) == null)) {
                            editor.putString("ping1", pingResult);
                        } else {
                            editor.putString("ping2", pingResult);
                        }
                        editor.apply();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


    public static void sendResult(String result) {
        try {
            URL url=null;
            if (NetworkUtils.isEmulator()) {
                url = new URL ("http://10.0.2.2:5000/postresults");
            }
            else {
                url=new URL("http://146.255.75.117:5000/postresults");
            }
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            String jsonInputString = "{ \"result\" : "+"\""+result+"\" }";
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            readResponse(con);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readResponse(HttpURLConnection connection) {
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            Log.d("RESPONSE", "response= "+response.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}