package com.kutilangapp.contoh_push_notifikasi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kutilangapp.contoh_push_notifikasi.push_notifikasi.DSS_NotificationListener;
import com.kutilangapp.contoh_push_notifikasi.push_notifikasi.DSS_WebSocket;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements DSS_NotificationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DSS_WebSocket listener = new DSS_WebSocket(this, "ws://192.168.43.8:33333");
    }

    @Override
    public void pushNotification(String msg) {
        String _msg = "";
        try {
            JSONObject jsonObject = new JSONObject(msg);
            _msg = jsonObject.getString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String DEFAULT_CHANNEL_ID = "default_channel";
        String DEFAULT_CHANNEL_NAME = "Default";
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNotificationManager.getNotificationChannel(DEFAULT_CHANNEL_ID) == null) {
                mNotificationManager.createNotificationChannel(new NotificationChannel(
                        DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                ));
            }
            Notification notification = new NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                    .setContentTitle("Simple Notification")   //Set the title of Notification
                    .setContentText(_msg)    //Set the text for notification
                    .setSmallIcon(android.R.drawable.ic_menu_view)   //Set the icon
                    .build();
            mNotificationManager.notify(1, notification);
        } else {
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Simple Notification")   //Set the title of Notification
                    .setContentText(_msg)    //Set the text for notification
                    .setSmallIcon(android.R.drawable.ic_menu_view)   //Set the icon
                    .build();
            mNotificationManager.notify(1, notification);
        }
    }
}
