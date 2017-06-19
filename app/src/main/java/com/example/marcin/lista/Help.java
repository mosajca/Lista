package com.example.marcin.lista;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


public class Help extends AppCompatActivity implements SensorEventListener {

    private Fragment fragment;
    private TextView textView;
    private SensorManager manager;
    private Sensor gravity;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout);

        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        getSupportFragmentManager().beginTransaction().hide(fragment).commit();
        textView = (TextView) fragment.getView().findViewById(R.id.gravity);

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gravity = manager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    @Override
    public void onResume() {
        super.onResume();
        manager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        manager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        super.onBackPressed();
    }

    public void gravity(View v) {
        ++counter;
        if (counter == 10) {
            getSupportFragmentManager().beginTransaction().show(fragment).commit();
        } else if (counter == 11) {
            getSupportFragmentManager().beginTransaction().hide(fragment).commit();
            counter = 0;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
            String g = "";
            for (float f : sensorEvent.values) {
                g += String.valueOf(f) + " ";
            }
            textView.setText(g);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void download(View v) {
        new Download().execute();
    }

    class Download extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            Toast.makeText(Help.this, "Proszę czekać.", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            InputStream is = null;
            HttpsURLConnection con = null;
            try {
                URL url = new URL("https://gist.githubusercontent.com/mosajca/49217d2f8707938316b00ab7594563d5/raw/8e6af267f1a6c1f05fb58127b41ea1c4b78cb0c9/example.json");
                con = (HttpsURLConnection) url.openConnection();
                if (con != null) {
                    con.setReadTimeout(5000);
                    con.setConnectTimeout(5000);
                    con.setRequestMethod("GET");
                    con.setDoInput(true);
                    con.connect();
                    if (con.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                        is = con.getInputStream();
                        if (is != null) {
                            Scanner scanner = new Scanner(is);
                            while (scanner.hasNext()) {
                                result += scanner.next();
                            }
                            is.close();
                        }
                    }
                    con.disconnect();
                }
            } catch (IOException e) {
                result = "";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent();
            if (result.equals("")) {
                Toast.makeText(Help.this, "Wystąpił błąd. Nie pobrano danych.", Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_CANCELED, intent);
            } else {
                intent.putExtra("result", result);
                setResult(Activity.RESULT_OK, intent);
            }
            finish();
        }
    }
}
