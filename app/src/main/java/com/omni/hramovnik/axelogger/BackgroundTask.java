package com.omni.hramovnik.axelogger;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackgroundTask extends Thread  {

    public Accelerometer accelerometer = new Accelerometer();
    public void stopTask(){
        working = false;
    }

    private volatile boolean working = false;
    private volatile File filePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"AxeLogger");
    public String getFullFilePath() {
        return fullFilePath;
    }
    private volatile String fullFilePath = "";

    @Override
    public void run() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdPath = Environment.getExternalStorageDirectory();
            filePath = new File(sdPath.getAbsolutePath(),"AxeLogger");
            if (!filePath.exists()) {
                if (!filePath.mkdirs()) {
                    Log.e("TAG", "Dir creating " + filePath.getAbsolutePath() + " failed");
                    fullFilePath = "Dir creating " + filePath.getAbsolutePath() + " failed";
                    return;
                }
            }
        }else{
            Log.e("TAG", "External storage not mounted");
            fullFilePath = "External storage not mounted";
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        File file = new File(filePath, currentDateandTime + ".csv");
        FileOutputStream stream = null;
        Log.d("TAG", "Before enter the task with path " + file.getAbsolutePath());
        fullFilePath = file.getAbsolutePath();
        long firstTime = System.currentTimeMillis();
        try{
            stream = new FileOutputStream(file);
            stream.write("t;x;y;z;\n".getBytes());
            Log.d("TAG", "Stream created");
            working = true;
            while (working){
                StringBuilder builder = new StringBuilder();
                builder.append(System.currentTimeMillis() - firstTime);
                builder.append(";");
                builder.append(String.valueOf(accelerometer.getX()));
                builder.append(";");
                builder.append(String.valueOf(accelerometer.getY()));
                builder.append(";");
                builder.append(String.valueOf(accelerometer.getZ()));
                builder.append(";\n");
                stream.write(builder.toString().getBytes());
                Log.d("TAG", builder.toString());
                Thread.sleep(5);
            }
        }catch (Exception e){
            working = false;
            Log.d("TAG", e.getMessage());
            fullFilePath = e.getMessage();
        }finally {
            try{
                stream.close();
            }catch (Exception e){
                Log.d("TAG", e.getMessage());
                fullFilePath = e.getMessage();
            }
        }
        Log.d("TAG", "Stopping");
    }


}
