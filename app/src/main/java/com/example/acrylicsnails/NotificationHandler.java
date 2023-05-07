package com.example.acrylicsnails;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHandler extends BroadcastReceiver {

    private static final String CHANNEL_ID = "acrylicsnails_notification_channel";
    private final int NOTIFICATION_ID = 1;
    private NotificationManager mManager;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("Értesítés")
                .setContentText("Ez egy értesítés.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

        scheduleNotification(context);
    }


    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Értesítések";
            String channelDescription = "Ez a csatorna értesítéseket küld.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void scheduleNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(context, NotificationHandler.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long triggerTime = System.currentTimeMillis() + 60 * 1000;

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }

    public NotificationHandler(Context context) {
        this.mContext = context;
        this.mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return;
        }

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Acrylicsnails Notification", NotificationManager.IMPORTANCE_HIGH);

        channel.enableVibration(true);
        channel.setDescription("Book an appointment today! Visit our app!");

        this.mManager.createNotificationChannel(channel);
    }

    public void send(String message) {
        Intent intent = new Intent(mContext, HomeScreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle("Booking")
                .setContentText(message)
                //.setSmallIcon(R.drawable.ic_shopping_bag)
                .setContentIntent(pendingIntent);

        this.mManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void cancel(){
        this.mManager.cancel(NOTIFICATION_ID);
    }







}
