package com.example.matthew.alarmclock;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by Matthew on 2015/07/14.
 */
public class AlarmManagerHelper extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarms(context);
    }

    public static void setAlarms(Context context){

    }

    public static void cancelAlarms(Context context){

    }

    /*private static PendingIntent createPendingIntent(Context context, AlarmModel model){
       return null;
    }*/
}
