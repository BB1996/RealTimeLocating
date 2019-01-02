package com.tutorial.athina.pethood.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;

import com.tutorial.athina.pethood.R;

public class NotificationHelper extends ContextWrapper {

    public static final String CHANNEL_ID = "com.tutorial.athina.pethood.PETHOOD";
    public static final String CHANNEL_NAME = "PETHOOD Channel";
    private NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    private void createChannels() {
        NotificationChannel pethoodChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        pethoodChannel.enableLights(true);
        pethoodChannel.enableVibration(true);
        pethoodChannel.setLightColor(Color.RED);
        pethoodChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(pethoodChannel);
    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public Notification.Builder getPethoodChannelNotification(String title, String body) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentText(body)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true);
    }

}
