package com.smart.medilation.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationSender {
    public static final String FCM_API_ENDPOINT = "https://fcm.googleapis.com/fcm/send";
    public static final String SERVER_KEY = "AAAA_-z71jg:APA91bF_t1U0ixTApBQrDlDq5_eVfZVQ5O2CFgKdoUUdctOrotNWnNBbH6-7DKP6Z29PRkQSya07HncupxysDg9GPa7-IqSCR0z4jvP2I1aPzLpUQvL9DAGmVoQY12DbnpzMiQ_TBJ7M";

    public void sendNotificationToTopic(Context context, String topic, String title, String message) {
        Log.d("TAG", "FcmNotificationSender :" + topic);
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            JSONObject json = new JSONObject();
            json.put("to", "/topics/" + topic);

            JSONObject data = new JSONObject();
            data.put("title", title);
            data.put("message", message);
            json.put("data", data);
            json.put("message", message);
            json.put("title", title);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, FCM_API_ENDPOINT, json,
                    response -> {
                        // Success
                        Log.d("FCMService", "Notification sent");
                    },
                    error -> {
                        // Error
                        Log.e("FCMService", "Error sending notification: " + error.getMessage());
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "key=" + SERVER_KEY);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            queue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void scheduleNotification(Context context, String topic, String title, String message, long delayInMillis) {
        // Calculate the scheduled time by adding the delay to the current time
        long scheduledTime = System.currentTimeMillis() + delayInMillis;

        // Create an intent for the broadcast receiver
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        intent.putExtra("topic", topic);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        // Create a pending intent with unique ID
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context, generateUniqueId(), intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, generateUniqueId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        // Get the alarm manager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Schedule the notification
        alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledTime, pendingIntent);
    }

    private static int generateUniqueId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

}
