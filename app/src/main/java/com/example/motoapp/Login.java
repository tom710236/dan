package com.example.motoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gcm.GCMRegistrar;

import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Login extends Activity {


    private static final int REQUEST_CONTACTS = 1;
    private Button btnLogin;
	private Button	btnCancel;
	Context objContext;
	Handler handler;
	EditText EditText_Account;
	EditText EditText_Password;
	EditText EditText_Car;
	EditText EditText_Area;
	EditText EditText_No;
	public static String carID;
	public static String Account,NO,AREA;
	private static final String TAG = "Login";
	PPLZPrinter printer;
	String serial;
    public static String regId;
	Context context;
	Button button;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		new GCMTask().execute();
		if(regId!=null){
			button = (Button)findViewById(R.id.GCM);
			button.setVisibility(View.GONE);
		}
		SysApplication.getInstance().addActivity(this);
		dbLocations objLocation = new dbLocations(Login.this);
		objLocation.CheckDB();
        openGps();
		// GCM


		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
		    serial = Build.SERIAL;

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

		//
		EditText_Account = (EditText) findViewById(R.id.EditText_Account);
		//EditText_Password = (EditText) findViewById(R.id.EditText_Password);
		EditText_Car = (EditText) findViewById(R.id.EditText_Car);
		EditText_Area = (EditText) findViewById(R.id.EditText_Area);
		EditText_No = (EditText) findViewById(R.id.EditText_No);
		EditText_Account.setText("123456");//員工卡號
		//EditText_Password.setText("123456");
		EditText_Car.setText("32");//路碼里程
		//EditText_Area.setText("123");
		EditText_No.setText("1234567");//運輸單號
		objContext = Login.this;
		
		//String imei = ((TelephonyManager) objContext.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
		//帳號若輸入正確 記住登入帳號

		SharedPreferences setting =
				getSharedPreferences("Login", MODE_PRIVATE);
		EditText_Account.setText(setting.getString("Account", "123456"));
		EditText_Car.setText(setting.getString("Car", "1234567"));
		EditText_No.setText(setting.getString("NO", "32"));
		//EditText_Area.setText(setting.getString("Area", "123"));


		btnLogin = (Button)findViewById(R.id.button_Login);
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new GCMTask().execute();
				Application.strAccount = EditText_Account.getText().toString();//員工單號
				Application.strPass = EditText_No.getText().toString();//運輸單號
				Application.strCar = EditText_Car.getText().toString();//路瑪里程
				Application.strDeviceID = serial;
				Account = EditText_Account.getText().toString();//員工單號
				carID = EditText_Car.getText().toString();//路碼里程
				NO = EditText_No.getText().toString();//運輸單號
				//AREA = EditText_Area.getText().toString();
				//Log.e("regId",regId);
				if(regId!=null){
					new clsHttpPostAPI().CallAPI(objContext, "API001");
				}else {
					clsDialog.Show(Login.this, "", "GCMID收尋中");
				}
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
				//EditText_Area.setText("");
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
				//Log.e("json", String.valueOf(json));
				try {
					String Result = json.getString("Result");
					//Log.e("Resultand",Result);
						if (Result.equals("1")) {
							//Application.strObuID = json.getString("ObuID");
							Application.strUserName = json
									.getString("EmployeeName");

							clsLoginInfo objLogin = new clsLoginInfo(objContext);
							objLogin.Car = EditText_Car.getText().toString();
							//objLogin.CarID = json.getString("ObuID");
							objLogin.DeviceID = Application.strDeviceID;
							//objLogin.GCMID = Application.strRegistId;
							objLogin.GCMID=regId;
							objLogin.StationID="7048";
							objLogin.StationName="松山站所";
							objLogin.UserID=EditText_Account.getText().toString();
							objLogin.UserName = json.getString("EmployeeName");
							objLogin.AreaID = EditText_Area.getText().toString();
							objLogin.FormNo = EditText_No.getText().toString();
							objLogin.Insert();
							//Log.e("GCMID",regId);
							//Log.e("UserID",EditText_Account.getText().toString());
							//記Log
							new clsHttpPostAPI().CallAPI(objContext, "API021");



							//記住帳號
							SharedPreferences setting =
									getSharedPreferences("Login", MODE_PRIVATE);
							setting.edit()
									.putString("Account", Account)
									.putString("Car",carID)
									.putString("NO",NO)
									.commit();

							//String EmployeeName =  objLogin.UserName;
							//String Employee;
							//Employee =  EmployeeName.substring(0, 3);
							Intent it = new Intent(Login.this,Delay.class);
							//Log.e("Account",Account);
							it.putExtra("Employee",Account);
							it.putExtra("regID",regId);
							startService(it);
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
							Intent intent = new Intent(Login.this, DataListFrg.class);
							startActivity(intent);
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
			regId = GCMRegistrar.getRegistrationId(Login.this);
			Log.e("REGID",regId);
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

    //取得 權限
    private void openGps() {
        int permission = ActivityCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION);
        int permission2 = ActivityCompat.checkSelfPermission(this,
                ACCESS_COARSE_LOCATION);
        int permission3 = ActivityCompat.checkSelfPermission(this,
                READ_PHONE_STATE);
		int permission4 = ActivityCompat.checkSelfPermission(this,
				WRITE_EXTERNAL_STORAGE);
		int permission5 = ActivityCompat.checkSelfPermission(this,
				READ_EXTERNAL_STORAGE);
		int permission6 = ActivityCompat.checkSelfPermission(this,
				CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED|| permission3 != PackageManager.PERMISSION_GRANTED|| permission4 != PackageManager.PERMISSION_GRANTED|| permission5 != PackageManager.PERMISSION_GRANTED|| permission6 != PackageManager.PERMISSION_GRANTED) {
            //若尚未取得權限，則向使用者要求允許聯絡人讀取與寫入的權限，REQUEST_CONTACTS常數未宣告則請按下Alt+Enter自動定義常數值。
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,READ_PHONE_STATE,WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,CAMERA},
                    REQUEST_CONTACTS);
        }
    }
    public void onGCM (View v){
		new GCMTask().execute();

		if (regId!=null){
			button = (Button)findViewById(R.id.GCM);
			button.setVisibility(View.GONE);
		}
	}
}
