package com.voodoo.mqtt_app2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {

    public final static String PARAM_TOPIC = "time";
    public final static String PARAM_DATA = "task";

    TextView tv;

    //ImageView imgDoor, imgSecur, imgIntruder;
    private static  String topic, data;

    BroadcastReceiver br;



    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
//        views.setTextViewText(R.id.textView, "topic: " + (topic ++) + " data: " + (data++));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    @Override
    public void onEnabled(Context context) {


    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    boolean alr = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);


        // Get the widget manager and ids for this widget provider, then call the shared clock update method.
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        String action = intent.getAction();
        if(MainActivity.BROADCAST_ACTION.equals(action))
        {
            topic = intent.getStringExtra(PARAM_TOPIC);
            data = intent.getStringExtra(PARAM_DATA);

            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID: ids)
            {
                AppWidgetProviderInfo appInfo = appWidgetManager.getAppWidgetInfo(appWidgetID);
                RemoteViews views = new RemoteViews(context.getPackageName(), appInfo.initialLayout);
                //views.setTextViewText(R.id.textView, "topic: " + topic + " data: " + data);

                //=====================================
                char[] str = data.toCharArray();
                alr = true;
                views.setViewVisibility(R.id.intruder, View.INVISIBLE);
                if(str[0] == 'O')
                {
                    views.setImageViewResource(R.id.imgDoor, R.drawable.door_opened);
                    //imgDoor.setImageResource(R.drawable.door_opened);
                    if(str[1] == 'A')
                           views.setViewVisibility(R.id.intruder, View.VISIBLE);
//                    else   views.setViewVisibility(R.id.intruder, View.INVISIBLE);
                }
                else if(str[0] == 'C')
                    views.setImageViewResource(R.id.imgDoor, R.drawable.door_closed);

                    //imgDoor.setImageResource(R.drawable.door_closed);

                if(str[1] == 'A') views.setImageViewResource(R.id.imgAlarm, R.drawable.security_on);//imgSecur.setImageResource(R.drawable.security_on);
                else if(str[1] == 'N')
                {
                    alr = false;
                    views.setImageViewResource(R.id.imgAlarm, R.drawable.security_off);
                    //imgSecur.setImageResource(R.drawable.security_off);
                }
                //=====================================

                Intent intent_ = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent_, 0);

                //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
                views.setOnClickPendingIntent(R.id.imgAlarm, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetID, views);
            }


        }
//        // Clock Update Event
//        if (CLOCK_UPDATE.equals(intent.getAction())) {
//            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
//            for (int appWidgetID : ids) {
//                updateClock(context, appWidgetManager, appWidgetID);
//            }
//        }
//
//        // Touch Event
//        if (SWITCH_COLORS_ACTION.equals(intent.getAction())) {
//            changeColor();
//        }

//        String action = intent.getAction();
//        if(MainActivity.BROADCAST_ACTION.equals(action))
//        {
//            topic = intent.getStringExtra(PARAM_TOPIC);
//            data = intent.getStringExtra(PARAM_DATA);
//        }


    }

}

