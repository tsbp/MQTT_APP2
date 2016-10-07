package com.voodoo.mqtt_app2;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import java.util.Locale;

public class MainActivity extends Activity {


    public final static String PARAM_TOPIC = "time";
    public final static String PARAM_DATA = "task";

    public final static String BROADCAST_ACTION = "com.voodoo.mqtt_app2.broadcast";

    Button btnStart, btnStop, btnPublish;
    TextView tv;
    RelativeLayout doorSTT;
    ImageView imgDoor, imgSecur, imgIntruder;

    BroadcastReceiver br;

//    MediaPlayer mp        = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doorSTT = (RelativeLayout) findViewById(R.id.doorBackground);
        tv = (TextView) findViewById(R.id.textView);
        imgDoor = (ImageView) findViewById(R.id.imgDoor);
        imgSecur = (ImageView) findViewById(R.id.imgAlarm);
        imgIntruder = (ImageView) findViewById(R.id.intruder);

        if(!isMyServiceRunning(MqttService.class)) MqttService.actionStart(this.getApplicationContext());

        // создаем BroadcastReceiver
        br = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
                String topic = intent.getStringExtra(PARAM_TOPIC);
                String data = intent.getStringExtra(PARAM_DATA);
                tv.setText("topic: " + topic + ", data: "+ data);

                char[] str = data.toCharArray();
                alr = true;
                if(str[0] == 'O')
                {
                        imgDoor.setImageResource(R.drawable.door_opened);
                        if(str[1] == 'A')
                            imgIntruder.setVisibility(View.VISIBLE);
                }
                else if(str[0] == 'C')
                        imgDoor.setImageResource(R.drawable.door_closed);

                if(str[1] == 'A') imgSecur.setImageResource(R.drawable.security_on);
                else if(str[1] == 'N')
                {
                        alr = false;
                        imgSecur.setImageResource(R.drawable.security_off);
                }


            }
        };

        IntentFilter intFilt = new IntentFilter(MainActivity.BROADCAST_ACTION);
        registerReceiver(br, intFilt);
    }
    //==============================================================================================
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    //==============================================================================================
    boolean alr = true;
    //==============================================================================================
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //MqttService.actionStop(this.getApplicationContext());
        unregisterReceiver(br);
        finish();
    }

    public void onClickEvent(View v)
    {
        switch (v.getId())        {

            case R.id.intruder:
                imgIntruder.setVisibility(View.INVISIBLE);
                break;

            case R.id.imgAlarm:

                if(alr == true) MqttService.MQTT_PUBLISH_MESSAGE = "ALR_OFF";
                else MqttService.MQTT_PUBLISH_MESSAGE = "ALR_ON";

                MqttService.actionPublish(this.getApplicationContext());
                break;
        }
    }
}
