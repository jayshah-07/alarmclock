package com.example.matthew.alarmclock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service to start with the AlarmManager
 * Allows us to launch a new activity after an alarm has gone off
 *
 * Created by Matthew on 2015/07/14.
 */
public class AlarmService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmManagerHelper.setAlarms(this);
        return super.onStartCommand(intent, flags, startId);
    }
}
