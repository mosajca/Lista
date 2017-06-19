package com.example.marcin.lista;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveToFileService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String text = intent.getExtras().getString("data");
        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                File sdcard = Environment.getExternalStorageDirectory();
                File dir = new File(sdcard.getAbsolutePath() + "/Lista_dane/");
                boolean saved = true;
                boolean dirExists = true;
                if (!dir.exists()) {
                    dirExists = dir.mkdir();
                }
                if (dirExists) {
                    String dateTime = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    File file = new File(dir, "lista_" + dateTime + "_.txt");
                    try {
                        FileOutputStream os = new FileOutputStream(file);
                        os.write(text.getBytes());
                        os.close();
                    } catch (IOException e) {
                        saved = false;
                    }
                } else {
                    saved = false;
                }
                if(saved) {
                    Toast.makeText(getApplicationContext(), "Zapisano do pliku.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Wystąpił błąd podczas zapisu do pliku.", Toast.LENGTH_LONG).show();
                }
                stopSelf();
            }
        });
        return Service.START_STICKY;
    }
}
