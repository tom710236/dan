package com.example.motoapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

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
    static final int MIN_TIME = Application.GPSPeriod; //間隔時間(5秒)
    static final float MIN_DIST = 10;  //間隔距離(1公尺)
    LocationManager mgr; //取得定位管理員
    Runnable runnable;
    Handler handler;
    String today, datatime;
    String IMEI;
    String GPSPeriod;
    Context context;
    int CheckGPS;
    PowerManager.WakeLock mWakeLock;
    int upTime = 10;
    public static String Employee, regID, lon, lat, UserID, GCMID;
    private final static int GOHNSON_ID = 1000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        // 讓CPU一直運行 7.0以上好像沒效果
        acquireWakeLock();
        // activity向service传值
        Employee = intent.getStringExtra("Employee");
        regID = intent.getStringExtra("regID");
        GPSPeriod = intent.getStringExtra("GPSPeriod");
        Log.e("GPSPeriod", GPSPeriod);

        //灰色喚醒
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GOHNSON_ID, new Notification());
        } else {
            Intent innerIntent = new Intent(this, GohnsonInnerService.class);
            startService(innerIntent);
            startForeground(GOHNSON_ID, new Notification());
        }




        handler = new Handler();
        runnable = new Runnable() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void run() {
                //定位設定
                mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(Delay.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Delay.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                //network定位
                mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME, MIN_DIST, Delay.this);
                time();
                //IMEI 設定
                TelephonyManager mTelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                IMEI = mTelManager.getDeviceId();
                //上傳GPS到平台
                Get get = new Get();
                get.start();

                //時間到跳到Login 清楚帳號用
                Application.datatime=datatime;
                if(datatime.equals(Application.timeClear)){
                    //new clsHttpPostAPI().CallAPI(context, "API014");
                    Intent intent1 = new Intent(Delay.this,Login.class);
                    // 錯誤代碼 Calling startActivity() from outside of an Activity context requires the , FLAG_ACTIVITY_NEW_TASK , Is this really what you want
                    //使用 intent1.addFlags(intent1.FLAG_ACTIVITY_NEW_TASK);
                    intent1.addFlags(intent1.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }

                //GPS第一次啟動後 跳到DataListFrg
                if(Application.GPS!=null && !Application.GPS.equals("") && CheckGPS == 0){
                    Intent intent1 = new Intent(Delay.this,DataListFrg.class);
                    // 錯誤代碼 Calling startActivity() from outside of an Activity context requires the , FLAG_ACTIVITY_NEW_TASK , Is this really what you want
                    //使用 intent1.addFlags(intent1.FLAG_ACTIVITY_NEW_TASK);
                    intent1.addFlags(intent1.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                    vibrator();
                    CheckGPS = 1;
                }
                handler.postAtTime(this, android.os.SystemClock.uptimeMillis() + upTime * 1000);

            }

        };
        //每分鐘執行一次
        handler.postAtTime(runnable, android.os.SystemClock.uptimeMillis() + upTime * 1000);
        return super.onStartCommand(intent, flags, startId);
        //return START_NOT_STICKY;

    }
    // 定位成功後 執行
    @Override
    public void onLocationChanged(Location location) {
        String str = "定位提供者:" + location.getProvider();
        str += String.format("\n緯度:%.5f\n經度:%.5f\n高度:%.2f公尺",
                location.getLatitude(),
                location.getLongitude(),
                location.getAltitude()
        );

        Log.e("today", today);
        lon = String.valueOf(location.getLongitude());
        lat = String.valueOf(location.getLatitude());

        //Log.e("定位",str);
        //Toast.makeText(Delay.this, str, Toast.LENGTH_SHORT).show();
        Application.GPS = str;
        //在定位前每十秒執行一次 定位後 依GPSPeriod的時間執行
        upTime = Application.GPSPeriod;

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
    //得到現在時間
    private void time() {
        Calendar mCal = Calendar.getInstance();
        String dateformat = "yyyy/MM/dd HH:mm:ss";
        String datetime = "HHmm";
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        SimpleDateFormat df2 = new SimpleDateFormat(datetime);
        today = df.format(mCal.getTime());
        datatime = df2.format(mCal.getTime());
    }
    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        /*
        PowerManager pm;
        PowerManager.WakeLock wakeLock;
        //创建PowerManager对象
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //保持cpu一直运行，不管屏幕是否黑屏
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
        wakeLock.acquire();
        */
    }

    //上傳GPS到平台
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

                    //Log.e("URL",url1);
                    Log.e("回傳",json);

                }
            });

        }

    }

    //停止service
    @Override
    public void onDestroy() {

        releaseWakeLock();
        handler.removeCallbacks(runnable);
        if(mgr!=null){
            mgr.removeUpdates(this);
        }
        super.onDestroy();
        Log.e("STOP", "STOP");
        Application.GPS = null;

    }
    //震動
    private void vibrator (){
        Vibrator vb = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(1500);

    }


    //申請設備電源鎖
    private void acquireWakeLock() {
        Log.e("MyGPS","正在申請電源鎖");
        if (null == mWakeLock) {
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |PowerManager.ON_AFTER_RELEASE, "");
        if (null != mWakeLock) {
            mWakeLock.acquire();
            Log.e("MyGPS申請", String.valueOf(mWakeLock));
            }
        }
    }
    //釋放設備電源鎖
    private void releaseWakeLock() {
        Log.e("MyGPS","正在釋放電源鎖");
        if (null != mWakeLock) {
            mWakeLock.release();
            mWakeLock = null;
            Log.e("MyGPS釋放", String.valueOf(mWakeLock));
        }
    }
    public static class GohnsonInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GOHNSON_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
