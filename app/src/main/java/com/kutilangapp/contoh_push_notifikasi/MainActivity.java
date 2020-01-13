package com.kutilangapp.contoh_push_notifikasi;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kutilangapp.contoh_push_notifikasi.broadcast.MyBroadcastReceiver;
import com.kutilangapp.contoh_push_notifikasi.push_notifikasi.DSS_NotificationListener;
import com.kutilangapp.contoh_push_notifikasi.push_notifikasi.DSS_WebSocket;
import com.kutilangapp.contoh_push_notifikasi.service.NotificationService;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {//implements DSS_NotificationListener {//HAPUS IMPLEMENT DSS
    DSS_WebSocket ws;
    TextView edTextResp;
    EditText edSubCh, edSubEm, edPushCh, edPushEm, edPushMs;
    //MyBroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edTextResp = findViewById(R.id.edTextResp);
        edSubCh = findViewById(R.id.edSubCh);
        edSubEm = findViewById(R.id.edSubEm);
        edPushCh = findViewById(R.id.edPushCh);
        edPushEm = findViewById(R.id.edPushEm);
        edPushMs = findViewById(R.id.edPushMs);
       // ws = new DSS_WebSocket(this, "ws://192.168.43.8:33333");
        //ws = new DSS_WebSocket(this, "ws://103.105.80.18:7002");
       // receiver = new MyBroadcastReceiver(this);   // This is how you initialise receiver
    }

    public void Subcribe(View v) {
        //ws.req("subcribe").channel("notification").email("ini@gmail.com").commit();
        ws.req("subcribe")
                .channel(edSubCh.getText().toString())
                .email(edSubEm.getText().toString()).commit();
        Toast.makeText(MainActivity.this, "Subcribe ke channel notifikasi dengan email ini@gmail.com", Toast.LENGTH_LONG).show();
    }

    public void Push(View v) {
    /*
        Intent intent = new Intent("subcribe");
        intent.putExtra("msg", "Service Started");
        intent.setAction("com.kutilangapp.SUBCRIBE");
        sendBroadcast(intent);*/

        Intent checkBroadCastState = new Intent();
        checkBroadCastState.setAction("com.kutilangapp.SUBCRIBE");
        checkBroadCastState.putExtra("msg", "Hello");
        sendBroadcast(checkBroadCastState);
        //ws.req("sendToChannel").channel("notification").email("ini@gmail.com").msg("hello bro").commit();
        /*
        ws.req("sendToChannel")
                .channel(edPushCh.getText().toString())
                .email(edPushEm.getText().toString())
                .msg(edPushMs.getText().toString()).commit();
        Toast.makeText(MainActivity.this, "Push pesan ke channel notifikasi untuk email ini@gmail.com", Toast.LENGTH_LONG).show();
   */
    }

    public void Clear(View v) {
        edTextResp.setText("");
        Intent mServiceIntent = new Intent(this, NotificationService.class);
        startService(mServiceIntent);
        //START SERVICE ON RUNNING
       /* NotificationService notificationService = new NotificationService();
        Intent mServiceIntent = new Intent(this, NotificationService.class);
        if (!isMyServiceRunning(NotificationService.class)) {
            startService(mServiceIntent);
        }*/
    }
    @SuppressWarnings("deprecation")
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    //@Override
    public void pushNotification(String msg) {//LISTENER DARI DSS WEB SOCKET
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String str = msg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                edTextResp.append("FROM WS SOCKET :: " + str + "\n");

               /* new AlertDialog.Builder(WebSocketSimple.this)
                        .setTitle("Your Alert")
                        .setMessage("Your Message")
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();*/
            }
        });
    }

}
