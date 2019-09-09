package com.ihsankaraca.currencynotificationapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        yuzde=0.1;
        txt=(TextView) findViewById(R.id.txt_deger);
        txt1=(TextView) findViewById(R.id.textView5);
        txt2=(TextView) findViewById(R.id.textView);
        edt=(EditText) findViewById(R.id.yuzde);
        btn=(Button) findViewById(R.id.button);

        txt.setText("123");
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
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt.setText("1");
                

            }
        });

    }
    public void displayNotification() {

        NotificationCompat.Builder notificationBuilder= new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentTitle("Dolar Kuru Bilgilendirme");
        notificationBuilder.setContentText(txt1.getText());
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
    public void jsoup_veri_cekme(){
        String URL="https://www.bloomberght.com/doviz/dolar";
        try {
            String veri;
            Document doc= Jsoup.connect(URL).get();
            Element element=doc.select("div#KurWidget0").select("div.table-responsive").select("table").select("tbody").get(0);
            Elements rows=element.select("tr");
            Element row=rows.get(1);
            Elements cols=row.select("td");
            Element cols1=cols.get(2);

            String data=cols1.html();
            data=Jsoup.parse(data).text();
            txt.setText(data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void bildirim(){
        Intent BackGround_Service=new Intent(MainActivity.this,Background_Service.class);
        startService(BackGround_Service);
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
                            txt.setText(anlik_deger);
                            String edt_deger=edt.getText().toString();

                            Boolean b=edt_deger.isEmpty();
                            if (b==false){
                                yuzde=Double.parseDouble(String.valueOf(edt_deger));
                                Double anlik_deger_double=Double.parseDouble(anlik_deger);
                                Double hesaplı_deger_arti=(onceki_deger)+onceki_deger*yuzde/100;
                                Double hesaplı_deger_eksi=(onceki_deger)-onceki_deger*yuzde/100;
                                if(anlik_deger_double<=hesaplı_deger_eksi){
                                    txt1.setText("Dolar degeri belirlenen yüzde de azaldı");
                                    displayNotification();
                                }else if(anlik_deger_double>= hesaplı_deger_arti){
                                    txt1.setText("Dolar degeri belirlenen yüzde de arttı");
                                    displayNotification();
                                }else {
                                    txt1.setText("Dolar degeri degismedi");
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
                                onceki_deger=anlik_deger_double;
                                txt2.setText(onceki_deger.toString());
                            }
                            else{
                                txt2.setText("Bildirim yüzdesi değerini giriniz!");
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
    public void degerGetir(){
        StringRequest arrReq = new StringRequest(Request.Method.GET, baseUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        txt.setText("1");
                        if (response.length() > 0) {
                            // kaç veri varsa o kadar dönecek for döngüsünü oluşturuyoruz.
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                JSONObject jsonObj=jsonObject.getJSONObject("ABD DOLARI");
                                String abd_dolar_alis=jsonObj.getString("Alış");
                                txt.setText(abd_dolar_alis+"  TRY");
                                //yuzde=Double.parseDouble(edt.getText().toString());
                                yuzde=10.0;
                                Double dolar_deger=Double.parseDouble(abd_dolar_alis);
                                if (tempData>dolar_deger){
                                    txt1.setText("Dolar degeri belirlenen yüzde de azaldı");
                                    displayNotification();
                                }else if(tempData<dolar_deger){
                                    txt1.setText("Dolar degeri belirlenen yüzde de arttı.");
                                }else if (tempData==dolar_deger){
                                    txt1.setText("Dolar degeri aynı");
                                }
                                Double yuzdelik_deger=(dolar_deger)+(dolar_deger*yuzde/100.0);
                                /*if(tempData>yuzdelik_deger){
                                    txt1.setText("Dolar degeri belirlenen yüzde de azaldı");
                                    displayNotification();
                                }
                                else if (yuzdelik_deger>tempData){
                                    txt1.setText("Dolar degeri belirlenen yüzde de arttı.");
                                    displayNotification();
                                }else {
                                    txt1.setText("Dolarda belirlenen yüzde de bir değişiklik olmadı.");
                                }
*/

                                tempData=dolar_deger;

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("Volley", "JSON Nesnesi Bulunamadı.");
                            }

                        } else {
                            Log.e("Volley", "Veri Yok.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                    }
                }
        );
        requestQueue.add(arrReq);
    }
}
