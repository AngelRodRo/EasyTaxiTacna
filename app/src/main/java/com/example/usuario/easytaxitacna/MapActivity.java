package com.example.usuario.easytaxitacna;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MapActivity extends ActionBarActivity {


    Button button;
    AsyncTask<Void, Void, String> shareRegidTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        View view;
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                shareRegidTask = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String result = sendServer();
                        return result;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        shareRegidTask = null;
                        Toast.makeText(getApplicationContext(), result,
                                Toast.LENGTH_LONG).show();
                    }

                };
                shareRegidTask.execute(null, null, null);


            }
        });
    }

    //Enviar mensaje al cliente 'user2'
    public String sendServer(){
        String APP_SERVER_URL = "https://aqueous-escarpment-1930.herokuapp.com/send";

        JSONArray values = new JSONArray();
        values.put("user2");
        //JSON PRINCIPAL (MENSAJE)
        JSONObject princjs = new JSONObject();

        //Partes del JSON
        JSONObject androidjs = new JSONObject();
        JSONObject datajs = new JSONObject();
        JSONObject iosjs = new JSONObject();

        try {
            datajs.put("message", "Este es mi mensaje :D");

            androidjs.put("collapseKey", "optional");
            androidjs.put("data", datajs);

            iosjs.put("badge", 0);
            iosjs.put("alert", "Your message here");
            iosjs.put("sound", "soundName");


            princjs.put("users", values);
            princjs.put("android", androidjs);
            princjs.put("ios", iosjs);
        }
        catch (Exception e){

        }

        String result = "";
        //Map<String, String> paramsMap = new HashMap<String, String>();
        //paramsMap.put("regId", regId);
        try {
            URL serverUrl = null;
            try {
                serverUrl = new URL(APP_SERVER_URL);
            } catch (MalformedURLException e) {
                Log.e("AppUtil", "URL Connection Error: "
                        + APP_SERVER_URL, e);
                result = "Invalid URL: " + APP_SERVER_URL;
            }

            byte[] bytes =  princjs.toString().getBytes();
//			        byte[] bytes =  json.toString().getBytes("UTF-8");

            //byte[] bytes = body.getBytes();
            HttpURLConnection httpCon = null;
            try {
                httpCon = (HttpURLConnection) serverUrl.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setUseCaches(false);
                httpCon.setFixedLengthStreamingMode(bytes.length);
                httpCon.setRequestMethod("POST");
                httpCon.setRequestProperty("Content-Type","application/json");
                httpCon.connect();

                OutputStream out = httpCon.getOutputStream();
                out.write(bytes);
                out.close();

                int status = httpCon.getResponseCode();
                if (status == httpCon.HTTP_OK) {
                    result = "RegId shared with Application Server. RegId: "
                    ;
                } else {
                    result = "Post Failure." + " Status: " + status;
                }
            } finally {
                if (httpCon != null) {
                    httpCon.disconnect();
                }
            }

        } catch (IOException e) {
            result = "Post Failure. Error in sharing with App Server.";
            Log.e("AppUtil", "Error in sharing with App Server: " + e);
        }

        return result;
    }



}
