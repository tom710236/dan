package com.example.motoapp;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import static com.example.motoapp.CommonUtilities.SENDER_ID;
import static com.example.motoapp.CommonUtilities.displayMessage;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	public enum EnumCmd
	{
		API001,
		API002,
		API003,
		API004,
		API005,
		API006,

		API013,
		API014,
		API015,

		API999,
		GCM,
	}

	public static Handler handler = null;
	public static Handler handlerGCM = null;
	public static Handler handlerReadGCM = null;
	public static Handler handlerForceLogoutGCM = null;
	public static Handler handlerContactGCM = null;
	static String strContent = "";
	static String ID = "";
	static String Account = "";
	static String UserName = "";

	@SuppressWarnings("hiding")
	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(SENDER_ID);

	}

	@Override
	protected void onRegistered(Context context, String registrationId) {

		clsLogger.i("onRegistered", "Device registered: regId = " + registrationId);
		Log.e("onRegistered GCM", "Device registered: regId = " + registrationId);
		displayMessage(context, getString(R.string.gcm_registered));
		ServerUtilities.register(context, registrationId);



	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		clsLogger.i("onUnregistered", "Device unregistered");
		Log.e("onUnregistered GCM", "Device unregistered");
		displayMessage(context, getString(R.string.gcm_unregistered));
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			ServerUtilities.unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			clsLogger.i("onUnregistered2", "Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent)
	{
		Log.i("onMessage", "Received message");
		Log.e("onMessage GCM", "Received message");
		// 接收 GCM server 傳來的訊息
		Bundle bData = intent.getExtras();

		PowerManager pm=(PowerManager) getSystemService(Context.POWER_SERVICE);
		//获取电源管理器对象
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
		//获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
		wl.acquire();
		//点亮屏幕
		wl.release();
		//释放
		// 處理 bData 內含的訊息
		// 在本例中, 我的 server 端程式 gcm_send.php 傳來了 message, campaigndate, title, description 四項資料

		KeyguardManager km= (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		//得到鍵盤鎖管理器對象
		KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
		//參數是LogCat裡用的Tag
		kl.disableKeyguard();
		//解鎖

		try {

			clsLogger.i(TAG, "Received message");
			String message = getString(R.string.gcm_message);
			displayMessage(context, message);
			generateNotification(context, intent);
			Log.e("gcm訊息", String.valueOf(bData));


		} catch (Exception e1) {
			clsLogger.i(TAG, e1.getMessage());
		}

	    /*
	    // 通知 user
	    generateNotification(context, bData);
	    
	    clsLogger.i(TAG, "Received message");
		String message = getString(R.string.gcm_message);
		displayMessage(context, message);
		generateNotification(context, intent);*/

	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		clsLogger.i("onDeletedMessages GCM", "Received deleted messages notification");
		Log.e("onDeletedMessages GCM", "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		displayMessage(context, message);
		// notifies user
		// generateNotification(context, message);
	}

	@Override
	public void onError(Context context, String errorId) {
		clsLogger.i("onError", "Received error: " + errorId);
		Log.e("onError GCM", "Received error: " + errorId);
		displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		clsLogger.i("onRecoverableError", "Received recoverable error: " + errorId);
		Log.e("onRecoverableError GCM", "Received recoverable error: " + errorId);
		displayMessage(context,
				getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}


	private static void generateNotification(Context context, Intent intent_GCM) throws JSONException {

		try {
			clsLogger.i(" generateNotification", new String(intent_GCM.getExtras().toString()
					.getBytes(), "UTF-8"));
			Log.e(" generate GCM", new String(intent_GCM.getExtras().toString()
					.getBytes(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			clsLogger.i(TAG, e.getMessage());
			e.printStackTrace();
		}
		Bundle bundle=null;
		try {

			bundle = intent_GCM.getExtras();

			MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.windows_8_notify);
			mPlayer.start();

			String strStatus = bundle.getString("status");
			Log.e("strStatus GCM",strStatus);

			if (strStatus.equals("1")) {
				if (handlerGCM != null) {
					Log.e("handlerGCM", String.valueOf(handlerGCM));
					Message objMessage = new Message();
					objMessage.obj = bundle;
					handlerGCM.sendMessage(objMessage);

				}else{
					Log.e("GCM bundle", String.valueOf(bundle));
					Message objMessage = new Message();
					objMessage.obj = bundle;


					Intent intent = new Intent("com.example.motoapp.MAIN");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			} else {

				Intent intent = new Intent("com.example.motoapp.MAIN");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		//JSONObject json = new JSONObject(strMsg);
		//int status = json.getInt("status");
		
	/*	*/

	}

}