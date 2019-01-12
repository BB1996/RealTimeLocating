package com.tutorial.athina.pethood.Notifications;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.tutorial.athina.pethood.NotificationService;


public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();
    private static final long DEFAULT_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    Handler mHandler;
    Runnable runnable;

    @Override
    public void onReceive(final Context context, Intent intent) {


        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        long interval = Long.parseLong(prefs.getString("interval",
                Long.toString(DEFAULT_INTERVAL)));

        PendingIntent operation = PendingIntent.getService(context, -1,
                new Intent(context, NotificationService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        if (interval == 0) {
            if(mHandler != null){
                mHandler.removeCallbacks(runnable);
            }
            alarmManager.cancel(operation);
            Log.d(TAG, "cancelling repeat operation");
        } else if (interval == 10000) {
            mHandler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {

                    mHandler.postDelayed(this, 1000);
                    notifyAlways(context);

                }
            };

            mHandler.post(runnable);

        } else {
            if(mHandler != null){
                mHandler.removeCallbacks(runnable);
            }

            alarmManager.setInexactRepeating(AlarmManager.RTC,
                    System.currentTimeMillis(), interval, operation);
            Log.d(TAG, "setting repeat operation for: " + interval);
        }
        Log.d(TAG, "onReceived");
    }

    void notifyAlways(Context context) {
        context.startService(new Intent(context, NotificationService.class));
        Log.d(TAG, "halooo");
    }
}