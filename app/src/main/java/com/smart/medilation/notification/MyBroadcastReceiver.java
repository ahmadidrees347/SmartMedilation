package com.smart.medilation.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("onReceive", "MyBroadcastReceiver");
        // Retrieve the message from the intent
        String topic = intent.getStringExtra("topic");
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        new FcmNotificationSender().sendNotificationToTopic(context, topic, title, message);
    }
}

