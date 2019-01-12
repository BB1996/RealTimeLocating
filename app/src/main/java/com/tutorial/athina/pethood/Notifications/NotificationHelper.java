package com.tutorial.athina.pethood.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;

import com.tutorial.athina.pethood.ListOnline;
import com.tutorial.athina.pethood.MessageActivity;
import com.tutorial.athina.pethood.MissingDogsActivity;
import com.tutorial.athina.pethood.PetHoodWidget;
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
        PendingIntent operation = PendingIntent.getActivity(getApplicationContext(), -1,
                new Intent(getApplicationContext(), ListOnline.class), PendingIntent.FLAG_ONE_SHOT);;
        if (title.contains("Missing dog Alert from ")){
             operation = PendingIntent.getActivity(getApplicationContext(), -1,
                    new Intent(getApplicationContext(), MissingDogsActivity.class), PendingIntent.FLAG_ONE_SHOT);
        }
        else if(title.contains("New message from ")){
            Intent notifChat = new Intent(getApplicationContext(), MessageActivity.class);
            notifChat.putExtra("dogOwner",title.substring(title.lastIndexOf(" ") + 1));
            operation = PendingIntent.getActivity(getApplicationContext(), -1,
                    notifChat, PendingIntent.FLAG_ONE_SHOT);
        }

        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentText(body)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_pet_notification)
                .setContentIntent(operation)
                .setAutoCancel(true);
    }

}
