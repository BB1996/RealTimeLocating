package com.tutorial.athina.pethood;

import android.app.IntentService;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.Models.Chat;
import com.tutorial.athina.pethood.Models.MissingDog;
import com.tutorial.athina.pethood.Notifications.BootReceiver;
import com.tutorial.athina.pethood.Notifications.NotificationHelper;

import java.util.Random;

public class NotificationService extends IntentService {

    public static final String TAG = "NotifService";
    DatabaseReference chatsRef, reportRef;
    String myUser;
    BroadcastReceiver broadcastReceiver;
    NotificationHelper helper;
    String lastPost1;
    String lastPost2;
    Query lastReportQuery, lastMessageQuery;
    ValueEventListener lastReportQueryListener, lastMessageQueryListener;

    public NotificationService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        lastPost1 = "";
        lastPost2 = "";
        helper = new NotificationHelper(this);
        broadcastReceiver = new BootReceiver();

        chatsRef = FirebaseDatabase.getInstance().getReference();
        reportRef = FirebaseDatabase.getInstance().getReference();
        myUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        lastReportQuery = reportRef.child("reports").orderByKey().limitToLast(1);
        lastMessageQuery = chatsRef.child("chats").orderByKey().limitToLast(1);

        IntentFilter intentFilter = new IntentFilter("com.tutorial.athina.pethood.action.REFRESH_INTERVAL");
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(broadcastReceiver, intentFilter);

        Log.d(TAG, "onCreated Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        lastReportQueryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    MissingDog missingDog = data.getValue(MissingDog.class);
                    if (!lastPost1.equals(missingDog.getMessage())) {

                        lastPost1 = missingDog.getMessage();
                        Notification.Builder builder = helper.getPethoodChannelNotification("Missing dog Alert from " + missingDog.getSender(), missingDog.getMessage());
                        helper.getManager().notify(new Random().nextInt(), builder.build());
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        };
        lastReportQuery.addListenerForSingleValueEvent(lastReportQueryListener);
        lastMessageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Chat chat = data.getValue(Chat.class);
                    if (chat.getReceiver().equals(myUser)) {
                        if (!lastPost2.equals(chat.getMessage())) {

                            lastPost2 = chat.getMessage();
                            Notification.Builder builder = helper.getPethoodChannelNotification("New message from " + chat.getSender(), chat.getMessage());
                            helper.getManager().notify(new Random().nextInt(), builder.build());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendBroadcast();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        lastReportQuery.removeEventListener(lastReportQueryListener);
        Log.d(TAG, "onDestroyed Service");
    }

    public void sendBroadcast() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setAction("com.tutorial.athina.pethood.action.REFRESH_INTERVAL");
        sendBroadcast(intent);
    }

}
