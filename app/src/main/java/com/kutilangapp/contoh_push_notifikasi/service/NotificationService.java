package com.kutilangapp.contoh_push_notifikasi.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.kutilangapp.contoh_push_notifikasi.MainActivity;
import com.kutilangapp.contoh_push_notifikasi.R;
import com.kutilangapp.contoh_push_notifikasi.broadcast.MyBroadcastListener;
import com.kutilangapp.contoh_push_notifikasi.broadcast.MyBroadcastReceiver;
import com.kutilangapp.contoh_push_notifikasi.push_notifikasi.DSS_NotificationListener;
import com.kutilangapp.contoh_push_notifikasi.push_notifikasi.DSS_WebSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service
        implements DSS_NotificationListener, MyBroadcastListener {
    public int counter = 0;

    Thread thread_ws;
    boolean is_disconnect = true;
String FROM_ACTIVITY;
    DSS_WebSocket ws;
    MyBroadcastReceiver receiver;
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
        Log.wtf("SERVICE", "start");
    /*    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else{
            Intent notificationIntent = new Intent(this, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("My Awesome App")
                    .setContentText("Doing some work...")
                    .setContentIntent(pendingIntent).build();

            startForeground(1, notification);
        }
*/
        receiver =new MyBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kutilangapp.SUBCRIBE");
        registerReceiver(receiver, filter);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(1010, notification);

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager) {
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //startTimer();
        if(ws == null) {
            ws = new DSS_WebSocket(this, "ws://103.105.80.18:7002");
            ws.req("subcribe").channel("notification").email("ini@gmail.com").commit();
            Log.wtf("SERVICE", "WS CONNECT");/**/
        }else{
//            ws.close(100,"Goodbye");
        }

        if (thread_ws == null) {
            thread_ws = new Thread() {

                @Override
                public void run() {
                    try {
                        while (!isInterrupted()) {
                            Thread.sleep(1000);
                            if (is_disconnect == true) {
                                Log.wtf("TREAD", "OK " + counter+" "+FROM_ACTIVITY);
                                counter++;
                                //location = getLocation();
                            } else {
                                // Thread.currentThread().interrupt();
                                thread_ws.interrupt();
                                thread_ws = null;
                            }

                        }
                    } catch (InterruptedException e) {
                    }
                }
            };
            thread_ws.start();
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //stoptimertask();
        if (thread_ws != null) {
            thread_ws.interrupt();
            thread_ws = null;
        }
        ws.close(100, "");
/*
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);*/
    }

    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.i("Count", "=========  " + (counter++));
            }
        };
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        /* run forever*/
        /*if(ws != null){
            ws.close(100, "Goodbye !");
            ws = null;
        }*/

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);

    }

    @Override
    public void pushNotification(String msg) {
        String _msg = "";
        try {
            JSONObject jsonObject = new JSONObject(msg);
            _msg = jsonObject.getString("msg");

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
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                        .setContentTitle("Simple Notification")   //Set the title of Notification
                        .setContentText(_msg)    //Set the text for notification
                        .setSmallIcon(android.R.drawable.ic_menu_view)   //Set the icon
                        .setContentIntent(contentIntent)
                        .build();
                mNotificationManager.notify(1, notification);
            } else {
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle("Simple Notification")   //Set the title of Notification
                        .setContentText(_msg)    //Set the text for notification
                        .setSmallIcon(android.R.drawable.ic_menu_view)   //Set the icon
                        .setContentIntent(contentIntent)
                        .build();



                mNotificationManager.notify(1, notification);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void doSomething(String value) {
        FROM_ACTIVITY = value;
        ws.req("initID").commit();
    }
}
