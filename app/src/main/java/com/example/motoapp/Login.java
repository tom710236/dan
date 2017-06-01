package com.example.motoapp;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;

import com.argox.sdk.barcodeprinter.BarcodePrinter;
import com.argox.sdk.barcodeprinter.connection.PrinterConnection;
import com.argox.sdk.barcodeprinter.connection.bluetooth.BluetoothConnection;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZ;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZBarCodeType;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZOrient;
import com.argox.sdk.barcodeprinter.emulation.pplz.PPLZStorage;
import com.argox.sdk.barcodeprinter.util.Encoding;

import com.google.android.gcm.GCMRegistrar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {

	private Button btnLogin;
	private Button	btnCancel;
	Context objContext;
	Handler handler;
	EditText EditText_Account;
	EditText EditText_Password;
	EditText EditText_Car;
	EditText EditText_Area;
	EditText EditText_No;
	private static final String TAG = "Login";
	PPLZPrinter printer;
	String serial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		SysApplication.getInstance().addActivity(this);
		dbLocations objLocation = new dbLocations(Login.this);
		objLocation.CheckDB();
		
		if(VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
		    serial = Build.SERIAL;
		    Log.d("SERIAL", serial);
		}
		
		//TODO 判斷是否隔天了，如果是的話清掉登入資訊
		clsLoginInfo objLoginInfo = new clsLoginInfo(Login.this);
		objLoginInfo.Load();
		int intStatus  =objLoginInfo.Check();
		if(intStatus==1)
		{
			Intent intent = new Intent(Login.this, DataListFrg.class);
		    startActivity(intent);
		}
		
		/*
		 
		 */
		 //printer = new PPLZPrinter();
		  //((PPLZPrinter) this.printer).initPrinter(this);
		  //this.printer.setReset();
		  
		EditText_Account = (EditText) findViewById(R.id.EditText_Account);
		//EditText_Password = (EditText) findViewById(R.id.EditText_Password);
		EditText_Car = (EditText) findViewById(R.id.EditText_Car);
		EditText_Area = (EditText) findViewById(R.id.EditText_Area);
		EditText_No = (EditText) findViewById(R.id.EditText_No);
		EditText_Account.setText("123456");
		//EditText_Password.setText("123456");
		EditText_Car.setText("MAH-8167");//MAH-8162 035-Q9
		EditText_Area.setText("123");
		EditText_No.setText("1234567");
		objContext = Login.this;
		
		//String imei = ((TelephonyManager) objContext.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
		// GCM
		new GCMTask().execute();
		
		btnLogin = (Button)findViewById(R.id.button_Login);
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Application.strAccount = EditText_Account.getText().toString();
				//Application.strPass = EditText_Password.getText().toString();
				Application.strCar = EditText_Car.getText().toString();
				Application.strDeviceID = serial;
			
			   new clsHttpPostAPI().CallAPI(objContext, "API001");
			}
		});
		
		btnCancel = (Button)findViewById(R.id.button_Cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//清除欄位
				EditText_Account.setText("");
				//EditText_Password.setText("");
				EditText_Car.setText("");
				EditText_Area.setText("");
				EditText_Account.requestFocus();
				EditText_No.setText("");
			}
		});
		
		EditText_Account.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == 23) {
						EditText_Car.requestFocus();
						return true;
					}
					return false;
				}
				return true;
			}
		});
		
		/*EditText_Password.setOnKeyListener(new OnKeyListener() {
		    public boolean onKey(View v, int keyCode, KeyEvent event) {
		    	if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == 23) {
						EditText_Car.requestFocus();
						return true;
					}
					return false;
				}
				return true;
			}
		       
		});*/
		
		EditText_Car.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == 23) {
						EditText_Area.requestFocus();
						return true;
					}
					return false;
				}
				return true;
			}
		});
		
		EditText_Area.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == 23) {
						EditText_No.requestFocus();
						return true;
					}
					return false;
				}
				return true;
			}
		});
		EditText_No.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == 23) {
						btnLogin.requestFocus();
						return true;
					}
					return false;
				}
				return true;
			}
		});
		
	
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				JSONObject json = (JSONObject) msg.obj;

				try {
					String Result = json.getString("Result");
					
					if (Result.equals("1")) {
						//Application.strObuID = json.getString("ObuID");
						Application.strUserName = json
								.getString("EmployeeName");
						
						clsLoginInfo objLogin = new clsLoginInfo(objContext);
						objLogin.Car = EditText_Car.getText().toString();
						objLogin.CarID = json.getString("ObuID");
						objLogin.DeviceID = Application.strDeviceID;
						objLogin.GCMID = Application.strRegistId;
						/*待補*/
						objLogin.StationID="7048";
						objLogin.StationName="松山站所";
						objLogin.UserID=EditText_Account.getText().toString();
						objLogin.UserName = json.getString("EmployeeName");
						objLogin.AreaID = EditText_Area.getText().toString();
						objLogin.FormNo = EditText_No.getText().toString();
						objLogin.Insert();
						
						//記Log
						new clsHttpPostAPI().CallAPI(objContext, "API021");
						
						//取站所資料
						
						Intent intent = new Intent(Login.this, DataListFrg.class);
					    startActivity(intent);
					}
					
					if (Result.equals("2")) {
						clsDialog.Show(Login.this, "ERROR", "輸入的授權碼 (Key)是不合法的授權碼");
					}
					
					if (Result.equals("3")) {
						clsDialog.Show(Login.this,"ERROR", "輸入的參數有缺漏");
					}
					
					if (Result.equals("4")) {
						clsDialog.Show(Login.this, "ERROR", "車機識別ID資訊有誤");
					}
					
					if (Result.equals("5")) {
						clsDialog.Show(Login.this, "ERROR", "狀態內容有誤");
					}
					
					if (Result.equals("6")) {
						clsDialog.Show(Login.this, "ERROR", "員工帳號資訊有誤");
					}
					
					if (Result.equals("7")) {
						clsDialog.Show(Login.this, "ERROR", "車號不存在");
					}
					
					if (Result.equals("8")) {
						clsDialog.Show(Login.this, "ERROR", "此車尚 未登入，無法進行其他狀態更新");
					}
					
					if (Result.equals("200")) {
						clsDialog.Show(Login.this, "ERROR", "系統忙碌或其他原因造成沒有完服務，請重試");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		
	}
	
	public void onStart() {
		super.onStart();
		clsHttpPostAPI.handlerLogin = handler;
	}

	public void onStop() {
		super.onStop();
		clsHttpPostAPI.handlerLogin =null;
	}
	
	/**************************
	 * GCM註冊
	 * 
	 **************************/
	private class GCMTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "檢查裝置是否支援 GCM");
			// 檢查裝置是否支援 GCM
			GCMRegistrar.checkDevice(Login.this);
			GCMRegistrar.checkManifest(Login.this);
			String regId = GCMRegistrar.getRegistrationId(Login.this);
			if (regId.equals("")) {
				Log.d(TAG, "尚未註冊 Google GCM, 進行註冊");

				GCMRegistrar.register(Login.this,
						CommonUtilities.SENDER_ID);
				
				GCMRegistrar.checkDevice(Login.this);
				GCMRegistrar.checkManifest(Login.this);
				
			int iq=1;	
				//while (regId.equals("")) {
				//	regId = GCMRegistrar.getRegistrationId(Login.this);
				//}
			}

			// POST Data
			Application.strRegistId = regId;
			
			return null;
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(Login.this)
                    .setTitle("確認視窗")
                    .setMessage("確定要結束應用程式嗎?")
                    .setIcon(R.drawable.ic_launcher).setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {
 
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                	//new clsHttpPostAPI().CallAPI(objContext, "API014");
                                	SysApplication.getInstance().exit();  
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
 
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    // TODO Auto-generated method stub
 
                                }
                            }).show();
        }
        return super.onKeyDown(keyCode, event);
    }
	
}
