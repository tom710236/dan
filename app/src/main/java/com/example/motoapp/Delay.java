package com.example.motoapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by TOM on 2017/6/2.
 */

public class Delay extends Service implements LocationListener {
    static final int MIN_TIME = 5000; //間隔時間(5秒)
    static final float MIN_DIST = 1;  //間隔距離(1公尺)
    LocationManager mgr; //取得定位管理員
    Runnable runnable;
    Handler handler;
    String today,datatime;
    String IMEI;
    String GPSPeriod;
    Context context;

    public static String Employee, regID, lon, lat, UserID, GCMID;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // activity向service传值


        Employee = intent.getStringExtra("Employee");
        regID = intent.getStringExtra("regID");
        GPSPeriod = intent.getStringExtra("GPSPeriod");
        Log.e("GPSPeriod",GPSPeriod);
        handler = new Handler();
        runnable = new Runnable() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void run() {

                mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
                String best = mgr.getBestProvider(new Criteria(), true);
                if (best != null) {

                    if (ActivityCompat.checkSelfPermission(Delay.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(Delay.this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED&&
                            ActivityCompat.checkSelfPermission(Delay.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        return;
                    }
                    mgr.requestLocationUpdates(best,
                            MIN_TIME, MIN_DIST, Delay.this);
                    time();
                    TelephonyManager mTelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    IMEI = mTelManager.getDeviceId();
                    Get get = new Get();
                    get.start();
                    //Log.e("time",datatime);

                   // Application.datatime=datatime;
                } else {
                    Log.e("定位中", "定位中");
                }

                handler.postAtTime(this, android.os.SystemClock.uptimeMillis() + 10 * 1000);

            }

        };
        //每分鐘執行一次
        handler.postAtTime(runnable, android.os.SystemClock.uptimeMillis() + 10 * 1000);
        //return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        String str = "定位提供者:" + location.getProvider();
        str += String.format("\n緯度:%.5f\n經度:%.5f\n高度:%.2f公尺",
                location.getLatitude(),
                location.getLongitude(),
                location.getAltitude()
        );
        //Log.e("str", str);
        //Log.e("today", today);
        lon = String.valueOf(location.getLongitude());
        lat = String.valueOf(location.getLatitude());
        //Toast.makeText(Delay.this, str + today, Toast.LENGTH_SHORT).show();
        //Log.e("str",str);
        Toast.makeText(Delay.this, "已成功定位", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void time() {
        Calendar mCal = Calendar.getInstance();
        String dateformat = "yyyy/MM/dd HH:mm:ss";
        String datetime = "HHmm";
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        SimpleDateFormat df2 = new SimpleDateFormat(datetime);
        today = df.format(mCal.getTime());
        datatime = df2.format(mCal.getTime());
    }

    class Get extends Thread {
        @Override
        public void run() {
            okHttpGet();

        }

        private void okHttpGet() {
            clsLoginInfo objL = new clsLoginInfo(Delay.this);
            objL.Load();
            UserID = objL.UserID;
            GCMID = objL.GCMID;
            final String url1 =Application.ChtUrl+"Services/API/Motor_Dispatch/Send_GPSInfo.aspx?\n" +
                    //"http://efms.hinet.net/FMS_WSMotor/Services/API/Motor_Dispatch/Send_GPSInfo.aspx?\n" +
                    "Key=7092a3c1-8ad6-48b5-b354-577378c282a5\n" +
                    "&DeviceID=" + GCMID + "\n" +
                    "&EmployeeID=" + UserID + "\n" +
                    "&StatusTime=" + today + "\n" +
                    "&lon=" + lon + "\n" +
                    "&lat=" + lat + "";
            final OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url1)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    Log.e("URL",url1);
                    //Log.e("回傳",json);

                }
            });

        }

    }

    @Override
    public void onDestroy() {

        handler.removeCallbacks(runnable);
        if(mgr!=null){
            mgr.removeUpdates(this);
        }
        super.onDestroy();
        Log.e("STOP", "STOP");
    }

}
