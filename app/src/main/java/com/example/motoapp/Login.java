package com.example.motoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    public static String regId ;
	Context context;
	Button button;
	ProgressDialog myDialog;
	int textInt = 0 ;
	String Updata = "V1.00";
	dbLocations objDB;
	String datetime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		// 版本更新
		TextView textView = (TextView)findViewById(R.id.textView9);
		textView.setText(Updata);

		Button button = (Button)findViewById(R.id.button2);
		button.setEnabled(false);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});


		new GCMTask().execute();

		SysApplication.getInstance().addActivity(this);
		dbLocations objLocation = new dbLocations(Login.this);
		objLocation.CheckDB();
        openGps();
		// GCM
		Intent it = new Intent(Login.this,Delay.class);
		stopService(it);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
		    serial = Build.SERIAL;

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
		EditText_Account.setText(setting.getString("Account", ""));
		EditText_Car.setText(setting.getString("Car", ""));
		EditText_No.setText(setting.getString("NO", ""));
		//EditText_Area.setText(setting.getString("Area", "123"));

		//時間到清除欄位
		/* 取出資料 */
		/*
		objDB.openDB();
		clsTask objT = objDB.LoadTask(Application.strCaseID);
		objDB.DBClose();
		if(objT.LoginTime!=datetime){
			//清除欄位
			Log.e("YES","YES");
			EditText_Account.setText("");
			//EditText_Password.setText("");
			EditText_Car.setText("");
			//EditText_Area.setText("");
			EditText_Account.requestFocus();
			EditText_No.setText("");
		}
			*/

		//TODO 判斷是否隔天了，如果是的話清掉登入資訊
		clsLoginInfo objLoginInfo = new clsLoginInfo(Login.this);
		objLoginInfo.Load();
		int intStatus  = objLoginInfo.Check();
		Log.e("intStatus", String.valueOf(intStatus));
		if(intStatus==1) {

			//Intent intent = new Intent(Login.this, DataListFrg.class);
			//startActivity(intent);
			//清除欄位

		}else if (intStatus==0){
			EditText_Account.setText("");
			//EditText_Password.setText("");
			EditText_Car.setText("");
			//EditText_Area.setText("");
			EditText_Account.requestFocus();
			EditText_No.setText("");

		}

		if(Application.datatime.equals("0100")){
			EditText_Account.setText("");
			//EditText_Password.setText("");
			EditText_Car.setText("");
			//EditText_Area.setText("");
			EditText_Account.requestFocus();
			EditText_No.setText("");
		}
		btnLogin = (Button)findViewById(R.id.button_Login);
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

					new GCMTask().execute();
					Post post = new Post();
					post.run();
					//myDialog = ProgressDialog.show(Login.this, "登入中", "登入資訊檢查中，請稍後！", false);
					setDialog();

					Application.strAccount = EditText_Account.getText().toString();//員工單號
					Application.strPass = EditText_No.getText().toString();//運輸單號
					Application.strCar = EditText_Car.getText().toString();//路瑪里程
					Application.strDeviceID = serial;
					Account = EditText_Account.getText().toString();//員工單號
					carID = EditText_Car.getText().toString();//路碼里程
					NO = EditText_No.getText().toString();//運輸單號




				//AREA = EditText_Area.getText().toString();
				//Log.e("regId",regId);
				/*
				if(regId!=null){
					new clsHttpPostAPI().CallAPI(objContext, "API001");
					myDialog = ProgressDialog.show(Login.this, "登入中", "登入資訊檢查中，請稍後！", false);

					new Thread(new Runnable(){
						@Override
						public void run() {
							try{
								Thread.sleep(10000);
							}
							catch(Exception e){
								e.printStackTrace();
							}
							finally{
								//myDialog.dismiss();
							}
						}
					}).start();

				}else {
					clsDialog.Show(Login.this, "", "GCMID收尋中");
				}
				*/
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

		/*
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				JSONObject json = (JSONObject) msg.obj;
				//Log.e("LoginJson", String.valueOf(json));
				//myDialog.dismiss();
				try {
					Result = json.getString("Result");
					//myDialog.dismiss();
					Log.e("Resultand",Result);
						if (Result.equals("1")) {
							//myDialog.dismiss();
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
							String GPSPeriod = json.getString("GPSPeriod");
							//new clsHttpPostAPI().CallAPI(objContext, "API021");



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
							it.putExtra("GPSPeriod",GPSPeriod);
							startService(it);
							Intent intent = new Intent(Login.this, DataListFrg.class);
							startActivity(intent);

						}
					if (Result.equals("2")) {
						//myDialog.dismiss();
						clsDialog.Show(Login.this, "ERROR", "輸入的授權碼 (Key)是不合法的授權碼");
					}

					if (Result.equals("3")) {
						//myDialog.dismiss();
						clsDialog.Show(Login.this,"ERROR", "輸入的參數有缺漏");
					}

					if (Result.equals("4")) {
						//myDialog.dismiss();
						clsDialog.Show(Login.this, "ERROR", "車機識別ID資訊有誤");
					}

					if (Result.equals("5")) {
						//myDialog.dismiss();
						clsDialog.Show(Login.this, "ERROR", "狀態內容有誤");
					}

					if (Result.equals("6")) {
						//myDialog.dismiss();
						clsDialog.Show(Login.this, "ERROR", "員工帳號資訊有誤");
					}

					if (Result.equals("7")) {
						//myDialog.dismiss();
						clsDialog.Show(Login.this, "ERROR", "車號不存在");
					}

					if (Result.equals("8")) {
						//myDialog.dismiss();
						clsDialog.Show(Login.this, "ERROR", "此車尚 未登入，無法進行其他狀態更新");
					}

					if (Result.equals("200")) {
						//myDialog.dismiss();
						clsDialog.Show(Login.this, "ERROR", "系統忙碌或其他原因造成沒有完服務，請重試");
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		*/
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


	class Post extends Thread{
		@Override
		public void run() {
				PostUserInfo();
		}

		private void PostUserInfo() {
			final String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DeviceInfo.aspx?" +
					"DeviceID="+ regId +
					"&Status=1" +
					"&EmployeeID="+EditText_Account.getText().toString()+
					"&Odometer="+EditText_No.getText().toString()+
					"&TransportID="+EditText_Car.getText().toString()+
					"&key="+Application.strKey;
			OkHttpClient client = new OkHttpClient();

			//處理 java.net.SocketTimeoutException in okhttp
			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder.connectTimeout(30, TimeUnit.SECONDS);
			builder.readTimeout(30, TimeUnit.SECONDS);
			builder.writeTimeout(30, TimeUnit.SECONDS);
			client = builder.build();
			//要上傳的內容(JSON)--帳號登入
			Request request = new Request.Builder()
					.url(strUrl)
					.build();
			Call call = client.newCall(request);
			call.enqueue(new Callback(){

				@Override
				public void onFailure(Call call, final IOException e) {
					Log.e("LOGIN", String.valueOf(e));
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							clsDialog.Show(Login.this, "ERROR", String.valueOf(e));
							myDialog.dismiss();
						}
					});


				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					//取得回傳資料json 還是JSON檔
					Log.e("strUrl",strUrl);
					String json = response.body().string();
					Log.e("json",json);
					try {
						String Result = new JSONObject(json).getString("Result");
						Log.e("Result",Result);
							if (Result.equals("1")) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									myDialog.dismiss();
								}
							});
							//Application.strObuID = json.getString("ObuID");
							Application.strUserName = new JSONObject(json)
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
							objLogin.UserName = new JSONObject(json).getString("EmployeeName");
							objLogin.AreaID = EditText_Area.getText().toString();
							objLogin.FormNo = EditText_No.getText().toString();
							objLogin.Insert();
							//Log.e("GCMID",regId);
							//Log.e("UserID",EditText_Account.getText().toString());
							//記Log
							String GPSPeriod = new JSONObject(json).getString("GPSPeriod");
							//new clsHttpPostAPI().CallAPI(objContext, "API021");



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
							it.putExtra("GPSPeriod",GPSPeriod);
							startService(it);
							Intent intent = new Intent(Login.this, DataListFrg.class);
							startActivity(intent);

						}if (Result.equals("2")) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									myDialog.dismiss();
									clsDialog.Show(Login.this, "ERROR", "輸入的授權碼 (Key)是不合法的授權碼");
								}
							});

						}

						if (Result.equals("3")) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									myDialog.dismiss();
									//clsDialog.Show(Login.this,"ERROR", "輸入的參數有缺漏");
									clsDialog.Show(Login.this,"ERROR", "GCM收尋中");
								}
							});

						}

						if (Result.equals("4")) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									myDialog.dismiss();
									clsDialog.Show(Login.this, "ERROR", "車機識別ID資訊有誤");
								}
							});

						}

						if (Result.equals("5")) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									myDialog.dismiss();
									clsDialog.Show(Login.this, "ERROR", "狀態內容有誤");
								}
							});

						}

						if (Result.equals("6")) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									myDialog.dismiss();
									clsDialog.Show(Login.this, "ERROR", "員工帳號資訊有誤");
								}
							});

						}

						if (Result.equals("7")) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									myDialog.dismiss();
									clsDialog.Show(Login.this, "ERROR", "車號不存在");
								}
							});

						}

						if (Result.equals("8")) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									myDialog.dismiss();
									clsDialog.Show(Login.this, "ERROR", "此車尚 未登入，無法進行其他狀態更新");
								}
							});

						}

						if (Result.equals("200")) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									myDialog.dismiss();
									clsDialog.Show(Login.this, "ERROR", "系統忙碌或其他原因造成沒有完服務，請重試");
								}
							});

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}


				}
			});
		}
	}
	private void time() {
		Calendar mCal = Calendar.getInstance();
		String datetime = "yyyyHHmm";
		SimpleDateFormat df2 = new SimpleDateFormat(datetime);
		datetime = df2.format(mCal.getTime());
	}
	private void setDialog(){
		myDialog = new ProgressDialog(Login.this);
		myDialog.setTitle("登入中");
		myDialog.setMessage("登入資訊檢查中，請稍後！");
		myDialog.setButton("關閉", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				myDialog.dismiss();
			}

		});
		myDialog.setCancelable(false);
		myDialog.show();
	}



}
