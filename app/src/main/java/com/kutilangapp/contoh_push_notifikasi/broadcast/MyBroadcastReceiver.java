package com.kutilangapp.contoh_push_notifikasi.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    MyBroadcastListener listener;

    public MyBroadcastReceiver(MyBroadcastListener listener){
        this.listener = listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.wtf("RECEIVEE",intent.getAction());
        listener.doSomething(intent.getAction());
       /* ////::BROADCAST:: 5.HANDLING ON RECEIVE __
        CharSequence iData = intent.getCharSequenceExtra("msg");
        //Toast.makeText(context,"Tutlane Received Message: "+iData,Toast.LENGTH_LONG).show();
        if(intent.getAction().equals("com.kutilangapp.DO_SOMETHING")){
            ////::BROADCAST:: 6.SEND TO LISTENER
            listener.doSomething("DO_SOMETHING: "+iData);
        }else if(intent.getAction().equals("com.kutilangapp.GET_GPS")){
            String gps = intent.getStringExtra("gps");
            listener.doSomething("GET_GPS: "+gps);

        }*/


    }

}
