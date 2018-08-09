package com.omni.hramovnik.axelogger;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager = null;
    private Button buttonStartStop = null;
    private TextView textResult = null;
    protected boolean working = false;
    private BackgroundTask task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textResult = findViewById(R.id.textResult);
        try {
            buttonStartStop = findViewById(R.id.buttonStartStop);
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            buttonStartStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    working = !working;
                    if (working) {
                        startTask();
                    } else {
                        stopTask();
                    }
                }
            });
        }catch (Exception e){
            buttonStartStop.setEnabled(false);
            textResult.setText(e.getMessage());
        }
        verifyStoragePermissions(this);
    }

    public void startTask(){
        if (task != null) {
            mSensorManager.unregisterListener(task.accelerometer);
        }
        task = new BackgroundTask();
        mSensorManager.registerListener(task.accelerometer,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        task.start();
        buttonStartStop.setText("Stop");
    }

    public void stopTask(){
        textResult.setText(task.getFullFilePath());
        task.stopTask();
        buttonStartStop.setText("Start");
    }

    @Override
    protected void onDestroy() {
        task.stopTask();
        mSensorManager.unregisterListener(task.accelerometer);
        super.onDestroy();
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}
