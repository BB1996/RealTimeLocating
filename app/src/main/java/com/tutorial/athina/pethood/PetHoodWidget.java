package com.tutorial.athina.pethood;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.RemoteViews;

public class PetHoodWidget extends AppWidgetProvider {

    public static final String TAG = PetHoodWidget.class.getSimpleName();

    public static Bitmap buildUpdate(String sender, int size, Context context) {
        Paint paint = new Paint();
        paint.setTextSize(size);
        Typeface ourCustomTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Italic.ttf");
        paint.setTypeface(ourCustomTypeface);
        paint.setColor(Color.argb(255, 255, 255, 255));
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setSubpixelText(true);
        paint.setAntiAlias(true);
        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(sender) + 0.5f);
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(image);
        canvas.drawText(sender, 0, baseline, paint);
        return image;

    }


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
                new Intent(context, MissingDogsActivity.class),

                PendingIntent.FLAG_UPDATE_CURRENT);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget);

            view.setImageViewBitmap(R.id.list_item_text_user, buildUpdate(message, 100, context));
            view.setImageViewBitmap(R.id.list_item_text_message, buildUpdate("MISSING DOG OF " + user, 70, context));

            view.setOnClickPendingIntent(R.id.list_item_text_user, operation);
            view.setOnClickPendingIntent(R.id.list_item_text_message, operation);


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