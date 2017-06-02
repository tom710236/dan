package com.example.motoapp;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by TOM on 2017/6/2.
 */

public class Delay extends Service implements LocationListener {
    static final int MIN_TIME = 5000; //間隔時間(5秒)
    static final float MIN_DIST = 1;  //間隔距離(1公尺)
    LocationManager mgr; //取得定位管理員
    Runnable runnable;
    Handler handler;
    String today, IMEI;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        runnable = new Runnable() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void run() {
                Log.e("A", "A");
                mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
                String best = mgr.getBestProvider(new Criteria(), true);
                if (best != null) {


                    if (ActivityCompat.checkSelfPermission(Delay.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Delay.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    TelephonyManager mTelManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    IMEI = mTelManager.getDeviceId();
                }else{
                    Log.e("定位中","定位中");
                }

                handler.postAtTime(this,android.os.SystemClock.uptimeMillis()+10*1000);
            }

        };
        //每分鐘執行一次
        handler.postAtTime(runnable,android.os.SystemClock.uptimeMillis()+10*1000);
        //return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        String str = "定位提供者:"+location.getProvider();
        str+=String.format("\n緯度:%.5f\n經度:%.5f\n高度:%.2f公尺",
                location.getLatitude(),
                location.getLongitude(),
                location.getAltitude()
        );
        Log.e("str",str);
        Log.e("location.getLatitude()", String.valueOf(location.getLatitude()));
        Log.e("today",today);
        Toast.makeText(Delay.this,str+today+IMEI,Toast.LENGTH_SHORT).show();
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
        String dateformat = "yyyy/MM/dd/ HH:mm:ss";
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        today = df.format(mCal.getTime());
    }
}


