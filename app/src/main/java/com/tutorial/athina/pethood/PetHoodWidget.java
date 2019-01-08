package com.tutorial.athina.pethood;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tutorial.athina.pethood.Models.MissingDog;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class PetHoodWidget extends AppWidgetProvider {

    public static final String TAG = PetHoodWidget.class.getSimpleName();




    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");



        Cursor cursor = context.getContentResolver().query(
                StatusContract.CONTENT_URI, null,
                null, null, StatusContract.DEFAULT_SORT);
        if (!cursor.moveToFirst())
            return;

        String user = cursor.getString(cursor
                .getColumnIndex(StatusContract.Column.USER));
        String message = cursor.getString(cursor
                .getColumnIndex(StatusContract.Column.MESSAGE));


        PendingIntent operation = PendingIntent.getActivity(context, -1,
                new Intent(context, ListOnline.class),

                PendingIntent.FLAG_UPDATE_CURRENT);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget);
            view.setInt(R.id.list_item_content, "setBackgroundColor", Color.WHITE);
           
            view.setTextViewText(R.id.list_item_text_user, user);
            view.setTextColor(R.id.list_item_text_user,Color.argb(0,128,0,128));
            view.setTextViewText(R.id.list_item_text_message, message);
            view.setTextColor(R.id.list_item_text_message,Color.argb(0,128,0,128));
            view.setTextViewText(R.id.list_item_text_created_at,"ALERT");
            view.setTextColor(R.id.list_item_text_created_at,Color.RED);

            view.setOnClickPendingIntent(R.id.list_item_text_user, operation);
            view.setOnClickPendingIntent(R.id.list_item_text_message, operation);
            view.setOnClickPendingIntent(R.id.list_item_text_created_at, operation);

            appWidgetManager.updateAppWidget(appWidgetId, view);


        }
    }




    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        this.onUpdate(context, appWidgetManager, appWidgetManager
                .getAppWidgetIds(new ComponentName(context, PetHoodWidget.class)));
    }
}