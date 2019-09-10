package com.ihsankaraca.currencynotificationapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ihsan.karaca on 9/9/2019.
 */

public class Background_Service extends Service {
    String baseUrl = "https://finans.truncgil.com/today.json";// döviz değerlerini çektiğimiz api
    String baseUrl1 ="http://bigpara.hurriyet.com.tr/api/v1/hisse/list";
    String freeforexurl="https://www.freeforexapi.com/api/live?pairs=USDTRY";
    TextView txt,txt1,txt2;
    EditText edt;
    Double yuzde=0.0;
    Double onceki_deger=0.0;
    Double tempData=5.8222;
    Button btn;
    RequestQueue requestQueue;
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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        requestQueue = Volley.newRequestQueue(this);
        yuzde=0.1;

        final Handler handler = new Handler();
        Timer timer;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //jsoup_veri_cekme();
                        freeforexapi();
                    }
                });
            }
        };

        timer = new Timer();

        timer.schedule(timerTask,1000,7000);

        displayNotification();
    }
    public void displayNotification() {

        NotificationCompat.Builder notificationBuilder= new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentTitle("CurrencyNotificationApp");
        notificationBuilder.setContentText("Dolar Kuru Bilgilendirme");
        notificationBuilder.setTicker("Yeni bildiriminiz var !");
        notificationBuilder.setVibrate(new long[]{1000,1000,1000,1000,1000});
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
    public void freeforexapi(){

        StringRequest strReq=new StringRequest(Request.Method.GET, freeforexurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONObject jsonObject1=jsonObject.getJSONObject("rates").getJSONObject("USDTRY");
                            final String anlik_deger=jsonObject1.getString("rate");

                            String edt_deger=edt.getText().toString();

                            Boolean b=edt_deger.isEmpty();
                            if (b==false){
                                yuzde=Double.parseDouble(String.valueOf(edt_deger));
                                Double anlik_deger_double=Double.parseDouble(anlik_deger);
                                Double hesaplı_deger_arti=(onceki_deger)+onceki_deger*yuzde/100;
                                Double hesaplı_deger_eksi=(onceki_deger)-onceki_deger*yuzde/100;
                                if(anlik_deger_double<=hesaplı_deger_eksi){

                                    displayNotification();
                                }else if(anlik_deger_double>= hesaplı_deger_arti){

                                    displayNotification();
                                }else {

                                }
                                /*if (onceki_deger<hesaplı_deger){
                                    txt1.setText("Dolar belirlenen yüzdede arttı");
                                    displayNotification();
                                }
                                else if(onceki_deger>hesaplı_deger){
                                    txt1.setText("Dolar belirlenen yüzdede azaldı");
                                    displayNotification();
                                }
                                else {
                                    txt1.setText("degerler eşit");
                                }*/

                            }
                            else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                    }
                });
        requestQueue.add(strReq);

    }
}
