package com.ihsankaraca.currencynotificationapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

/**
 * Created by ihsan.karaca on 9/9/2019.
 */

public class Background_Service extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onCreate(){
        super.onCreate();
        displayNotification();
    }
    public void displayNotification() {

        NotificationCompat.Builder notificationBuilder= new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentTitle("CurrencyNotificationApp");
        notificationBuilder.setContentText("Dolar Kuru Bilgilendirme");
        notificationBuilder.setTicker("Yeni bildiriminiz var !");

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder TSB = TaskStackBuilder.create(this);
        TSB.addParentStack(MainActivity.class);
        TSB.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =TSB.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =(NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(11221, notificationBuilder.build());

    }
}
