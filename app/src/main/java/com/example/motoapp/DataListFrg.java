package com.example.motoapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.motoapp.R.id.Spinner_Reasion;
import static com.example.motoapp.R.id.button_Save;

public class DataListFrg extends Activity implements GestureDetector.OnGestureListener , SurfaceHolder.Callback {


	MainActivity objActivity;
	ListView listView;
	View rootView;
	Context context;
	Handler handlerGCM;
	Handler handlerTask;
	Handler handlerListView;
	Handler handlerThread;
    Handler handlerGet;
	clsLoginInfo objLoginInfo;
	ListViewAdpater adpater;
	Button button_DoList;
	Button button_IO;
	Button button_GT;
	Button button_DoneList;
	
	EditText EditText_OrderID1;
	EditText EditText_CustomName;
	EditText editText_Address1;
	EditText editText_Phone;
	EditText EditText_Count1;
	EditText EditText_PayType;
	EditText EditText_Money;
	//Button Button_Print1;
	Spinner Spinner_PayType;
	String today;
	Message msg;
	int CheckNet = 0 ;
	int CheckGPS = 0 ;
	/*
	 * 01列表 02接單 03前往取件 04取件完成，拍照上傳 05回站 06直送 07已送達，拍照上傳 08送達失敗，失敗原因
	 */
	public static String type = "01";
	private dbLocations objDB;
	SurfaceHolder surfaceHolder;
	SurfaceView surfaceView1;
	ImageView imageView1;
	int KeyinFile = 0;
	Camera camera;
	ProgressDialog myDialog;
	ProgressDialog myDialog2;
	PPLZPrinter printer;

	GCMActivity gcm = new GCMActivity();
	Uri imgUri;    //用來參照拍照存檔的 Uri 物件
	ImageView imv; //用來參照 ImageView 物件
	Bitmap bmp;
	int chickInt = 0;
    GestureDetector detector;
	float upX,upY,downX,downY ;
	JSONArray objArray1;
	List<ClsDropDownItem> objList1;
	//覆寫掉沒有用的onTouchEvent


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("=====>", "GoogleFragment onCreateView");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.frg_waiting);


		PostFail post = new PostFail();
		post.start();

		PostStation post2 = new PostStation();
		post2.start();
        //手勢滑動
        detector = new GestureDetector(DataListFrg.this,this);
		detector.setIsLongpressEnabled(true);


		SysApplication.getInstance().addActivity(this);
		context = DataListFrg.this;

		objLoginInfo = new clsLoginInfo(context);
		objLoginInfo.Load();

		objDB = new dbLocations(context);

		Intent intent = getIntent();
		Bundle bag = intent.getExtras();
		//type = bundle.getString("type",null);
		Log.e("bundle", String.valueOf(bag));
		if(bag!=null) {
			Log.e("bundle1", String.valueOf(bag));
			type=bag.getString("type",null);
			display();
		}else {
			Log.e("bundle2", String.valueOf(bag));
			setListView();
			Log.e("type2",type);
		}
		/*printer = new PPLZPrinter();
		((PPLZPrinter) this.printer).initPrinter(this);*/



		

		/* 失敗原因 */
		setDropDownListReason();

		/*鍵盤事件*///
		setKeyListener();



		//員工卡號姓名設定
		final clsLoginInfo objL = new clsLoginInfo(context);
		objL.Load();
		TextView tID = (TextView)findViewById(R.id.TextID);
		TextView tName = (TextView)findViewById(R.id.TextName);
		tID.setText(objL.UserID);
		tName.setText(objL.UserName);
		//TODO 判斷是否隔天了，如果是的話清掉登入資訊
		int intStatus  = objL.Check();
		Log.e("intStatus", String.valueOf(intStatus));
		if(intStatus==1) {

			//Intent intent = new Intent(Login.this, DataListFrg.class);
			//startActivity(intent);
			//清除欄位

		}else if (intStatus==0){
			Intent intent1 = new Intent(DataListFrg.this,Login.class);
			startActivity(intent1);
		}


		// 0代表橫向、1代表縱向
		//this.setRequestedOrientation(1);
		// 設為横向顯示。因為攝影頭會自動翻轉90度，所以如果不横向顯示，看到的畫面就是翻轉的。

		//surfaceView1 = (SurfaceView) findViewById(R.id.surfaceView1);
		imv = (ImageView) findViewById(R.id.imageView1);
		//surfaceHolder = surfaceView1.getHolder();
//		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//		surfaceHolder.addCallback(this);

		/* 接單 */
		final Button Button_Get = (Button) findViewById(R.id.button_Get);
		Button_Get.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isConnected()){
					chickInt=1;
				/*
				 * 呼叫API 接單
				 */
					// 顯示Progress對話方塊
					//new clsHttpPostAPI().CallAPI(context, "API002");
					//Button_Get.setEnabled(false);
					clsHttpPostAPI clsHttpPostAPI = new clsHttpPostAPI();
					clsHttpPostAPI.CallAPI(context, "API002");
					//myDialog2 = ProgressDialog.show(context, "載入中", "資料讀取中，請稍後！", false);
					if(chickInt==1){
						myDialog2 = new ProgressDialog(DataListFrg.this);
						myDialog2.setTitle("接單中");
						myDialog2.setMessage("接單資訊檢查中，請稍後！");
						myDialog2.setButton("關閉", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								myDialog2.dismiss();
							}

						});
						myDialog2.setCancelable(false);
						myDialog2.show();
						new Thread(new Runnable(){
							@Override
							public void run() {
								try{
									Thread.sleep(60000);
								}
								catch(Exception e){
									e.printStackTrace();
								}
								finally{
									myDialog2.dismiss();
								}
							}
						}).start();
						int i = clsHttpPostAPI.from_get_json;
						Log.e("clsHttpPostAPI", String.valueOf(i));

						display();
					}else {
						clsDialog.Show(context, "提示訊息", "請確認網路是否正常！");
					}


				}

			}
		});
		/* 拒絕 */
		Button Button_Reject = (Button) findViewById(R.id.button_Reject);
		Button_Reject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new clsHttpPostAPI().CallAPI(context, "API003");
				setDialog();
				objDB.openDB();
				objDB.UpdateTaskStatus("00", Application.strCaseID);
				objDB.DBClose();
				type="71";
				display();

			}
		});

		/* 前往取件 */
		Button button_Go = (Button) findViewById(R.id.button_Go);
		button_Go.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isConnected()){
					EditText editText = (EditText)findViewById(R.id.EditText_Receive);
					if(editText.length()!=0){
					/* 更新預計時間欄位 */
						Application.IsCreateData =false;
						Log.e("cash_on_delivery GCM",Application.cash_on_delivery);
						Log.e("strCaseID GCM",Application.strCaseID);
						objDB.openDB();
						objDB.UpdateTaskRecTime(
								((TextView) findViewById(R.id.EditText_Receive))
										.getText().toString(), Application.strCaseID);

						clsTask objT = objDB.LoadTask(Application.strCaseID);
						Log.e("time",objT.RecTime);
						objDB.DBClose();
				/*
				 * 呼叫API 前往取件
				 */
						new clsHttpPostAPI().CallAPI(context, "API004");
						setDialog();
						EditText_OrderID1.requestFocus();


					}else{
						Toast.makeText(DataListFrg.this, "請輸入時間", Toast.LENGTH_SHORT).show();
					}
				}else {
					clsDialog.Show(context, "提示訊息", "請確認網路是否正常！");
				}

			}
		});

		/* 取件完成 */
		Button button_Sucess = (Button) findViewById(R.id.button_Sucess);
		button_Sucess.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isConnected()){
					//運輸單號更新
					EditText editText = (EditText)findViewById(R.id.EditText_OrderID1);
					Application.newstrObuID = editText.getText().toString();

					//objDB.openDB();
					//objDB.UpdateTaskOrdID(Application.newstrObuID,Application.strCardNo);
					//objDB.close();

					//付款金額更新
					EditText editText1 = (EditText)findViewById(R.id.EditText_Money);
					Application.newPay = editText1.getText().toString();
					//objDB.openDB();
					//objDB.UpdateTaskPayAmount(Application.newPay,Application.strCardNo);
					//objDB.close();

					//代收貨款更新
					Application.cash_on_delivery = ((TextView) findViewById(R.id.textView_Cash)).getText().toString();
					Log.e("cash_on_delivery",Application.cash_on_delivery);

					//付款方式更新
				/*
				final int[] indexSpinner = new int[1];

				Spinner_PayType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						indexSpinner[0] = Spinner_PayType.getSelectedItemPosition();
						Log.e("indexSpinner", String.valueOf(indexSpinner[0]));
						Application.newPayType = String.valueOf(indexSpinner[0]);
						//objDB.openDB();
						//objDB.UpdateTaskPayTypeID(String.valueOf(indexSpinner[0]),Application.strCardNo);
						//objDB.close();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
					*/
					//照片清除
					bmp = null;
					ImageView imv;
					imv = (ImageView) findViewById(R.id.imageView);
					imv.setImageBitmap(bmp);
					//協助修改 清除
					CheckBox c1 = (CheckBox)findViewById(R.id.chkCreateData);
					c1.setChecked(false);//checkbox狀態

					// 資料變更後資料庫更新
					objDB.openDB();
					objDB.UpdateTask("", "", "", EditText_CustomName.getText().toString(), editText_Address1.getText().toString(), editText_Phone.getText().toString(), ((ClsDropDownItem)Spinner_PayType.getSelectedItem()).GetID(), EditText_Money.getText().toString(), "03", Application.strCaseID,Application.newstrObuID,Application.cash_on_delivery);
					objDB.close();
					new clsHttpPostAPI().CallAPI(context, "API005");
					setDialog();
				}else{
					clsDialog.Show(context, "提示訊息", "請確認網路是否正常！");
				}

			}
		});
		
		/* 列印託運單 */
		Button button_Print1 = (Button) findViewById(R.id.button_Print1);
		button_Print1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO 呼叫列印
				Intent intent = new Intent(DataListFrg.this, BluetoothChat.class);
				objDB.openDB();
				clsTask objTask = objDB.LoadTask(Application.strCaseID);
				objDB.close();

                intent.putExtra("keys", new String[]
                    { "@", "託運單號", "寄件人", "電話", "地址", "收件人", "電話", "地址", "貨件數" });
                intent.putExtra("values", new String[]
                    { objTask.OrderID, objTask.OrderID, objTask.CustName, objTask.CustPhone, objTask.CustAddress, objTask.RecName, objTask.RecPhone, objTask.RecAddress, objTask.ItemCount });
                startActivity(intent);
			}
		});
		
		/* 第一個下一步 */
		Button button_Next1 = (Button) findViewById(R.id.button_Next1);
		button_Next1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("下一步","下一步");
				new clsHttpPostAPI().CallAPI(context, "API030");

				type="040";
				display();
			}
		});

		/* 拍照-託運單 */
		Button button_takePic1 = (Button) findViewById(R.id.button_takePic);
		button_takePic1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String dir = Environment.getExternalStoragePublicDirectory(  //取得系統的公用圖檔路徑
						Environment.DIRECTORY_PICTURES).toString();
				String fname = "p" + System.currentTimeMillis() + ".jpg";  //利用目前時間組合出一個不會重複的檔名
				imgUri = Uri.parse("file://" + dir + "/" + fname);    //依前面的路徑及檔名建立 Uri 物件
				//android 版本> android N 拍照會有問題 需另外加判斷(strictmode)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
					StrictMode.setVmPolicy(builder.build());
					Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
					it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
					startActivityForResult(it, 100);
					Log.e("imgUri", String.valueOf(imgUri));
					//把照片路徑存入DB
					objDB.openDB();
					objDB.UpdateRecPicture(
							(String.valueOf(imgUri)) , Application.strCaseID);
					clsTask objT = objDB.LoadTask(Application.strCaseID);
					Log.e("imgUri2",objT.RecPicture);
					objDB.close();

					Button button_Send = (Button) findViewById(R.id.button_Send);
					button_Send.setText("上傳");
				}else {
					Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
					it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);    //將 uri 加到拍照 Intent 的額外資料中
					startActivityForResult(it, 100);
					Log.e("imgUri", String.valueOf(imgUri));
					//把照片路徑存入DB
					objDB.openDB();
					objDB.UpdateRecPicture(
							(String.valueOf(imgUri)) , Application.strCaseID);
					clsTask objT = objDB.LoadTask(Application.strCaseID);
					Log.e("imgUri2",objT.RecPicture);
					objDB.close();
					Button button_Send = (Button) findViewById(R.id.button_Send);
					button_Send.setText("上傳");
				}


			}
		});



		/* 託運單拍照後傳送 */ //上傳
		Button button_Send = (Button) findViewById(R.id.button_Send);
		button_Send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String check;
                if (isConnected()) {
                    Log.e("網路狀態","正常");
					Post post = new Post();
					post.run();
					//協助修改 清除
					Application.IsCreateData = ((CheckBox) findViewById(R.id.chkCreateData))
							.isChecked();
					CheckBox c1 = (CheckBox)findViewById(R.id.chkCreateData);
					c1.setChecked(false);//checkbox狀態
                }else{
					CheckNet = 1;
					//網路不穩時 協助更新存入DB在直送或回站時上傳
					if(bmp!=null){
						Application.IsCreateData = ((CheckBox) findViewById(R.id.chkCreateData))
								.isChecked();
						if(Application.IsCreateData==true){
							check="1";
						}else {
							check="0";
						}
						objDB.openDB(); //更新狀態 和是否需協助更新
						clsTask objT = objDB.LoadTask(Application.strCaseID);
						objDB.UpdateTaskIsCreateData(
								check , Application.strCaseID);
						objDB.UpdateTaskStatus("41", objT.CaseID);
						objDB.DBClose();
                        type = "41";
                        display();
						//刪除照片
						bmp = null;
						ImageView imv;
						imv = (ImageView) findViewById(R.id.imageView);
						imv.setImageBitmap(bmp);

						//協助修改 清除
						CheckBox c1 = (CheckBox)findViewById(R.id.chkCreateData);
						c1.setChecked(false);//checkbox狀態

					}else {
						Toast.makeText(DataListFrg.this, "請確認是否有拍照", Toast.LENGTH_SHORT).show();
					}

                }

			}

		});



		/* 直送 */
		Button button_Online = (Button) findViewById(R.id.button_Online);
		button_Online.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isConnected()){
					new clsHttpPostAPI().CallAPI(context, "API007");
					setDialog();
					type="06";
					display();
					if(CheckNet == 1){
						setDialog();
						Post2 post = new Post2();
						post.run();
						type="06";
						display();
					}


				}else {
					clsDialog.Show(context, "提示訊息", "請確認網路是否正常！");
				}

			}
		});

		/* 回站 */
		Button button_BackStation = (Button) findViewById(R.id.button_BackStation);
		button_BackStation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isConnected()){
					new clsHttpPostAPI().CallAPI(context, "API012");
					setDialog();
					type="05";
					display();
					if(CheckNet == 1){

						setDialog();
						Post2 post = new Post2();
						post.run();
						type="05";
						display();
					}

					// 取得站所資料
				}else {
					clsDialog.Show(context, "提示訊息", "請確認網路是否正常！");
				}

			}
		});

		/* 選擇站所 */
		Button button_SetGoods2 = (Button) findViewById(R.id.button_SetGoods);
		button_SetGoods2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isConnected()){
					/* 顯示結果須等post回來的資訊決定，測試先寫 */
					Spinner Spinner_SetGoods = (Spinner) findViewById(R.id.Spinner_SetGoods);
					objDB.openDB();
					objDB.UpdateTaskStationID(((ClsDropDownStation) Spinner_SetGoods
							.getSelectedItem()).GetID(), Application.strCaseID);
					objDB.UpdateTaskStationName(((ClsDropDownStation) Spinner_SetGoods
							.getSelectedItem()).GetValue(), Application.strCaseID);
					objDB.DBClose();
					Log.e("11",((ClsDropDownStation) Spinner_SetGoods
							.getSelectedItem()).GetValue());
					new clsHttpPostAPI().CallAPI(context, "API010",((ClsDropDownStation) Spinner_SetGoods
							.getSelectedItem()).GetStationType());
					setDialog();
				}else {
					clsDialog.Show(context, "提示訊息", "請確認網路是否正常！");
				}

			}
		});

		/* 列簽收單 */
		Button button_Print2 = (Button) findViewById(R.id.button_Print2);
		button_Print2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO 呼叫列印
				Intent intent = new Intent(DataListFrg.this, BluetoothChat.class);
				objDB.openDB();
				clsTask objTask = objDB.LoadTask(Application.strCaseID);
				objDB.close();
				
                intent.putExtra("keys", new String[]
                    { "@", "託運單號", "寄件人", "收件人", "收件人電話", "收件人地址", "貨件數", "", "簽收" });
                intent.putExtra("values", new String[]
                    { objTask.OrderID, objTask.OrderID, objTask.CustName, objTask.RecName, objTask.RecPhone, objTask.RecAddress,objTask.ItemCount, "", "_____________" });
                startActivity(intent);
                
			}
		});
		
		/* 第二個下一步 */
		Button button_Next2 = (Button) findViewById(R.id.button_Next2);
		button_Next2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				type="070";
				display();
			}
		});
		
		/* 已送達 */
		Button button_OK = (Button) findViewById(R.id.button_OK);
		button_OK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isConnected()){
					//照片清除
					bmp = null;
					ImageView imv;
					imv = (ImageView) findViewById(R.id.imageView);
					imv.setImageBitmap(bmp);
					new clsHttpPostAPI().CallAPI(context, "API008");
					setDialog();
					time();
					Log.e("today",today);
					objDB.openDB();
					objDB.UpdateDate(today, Application.strCaseID);
					objDB.DBClose();
					//type="070";
					//display();
				}else {
					clsDialog.Show(context, "提示訊息", "請確認網路是否正常！");
				}

			}
		});

		/* 送達失敗 */
		Button button_NG = (Button) findViewById(R.id.button_NG);
		button_NG.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isConnected()){
					new clsHttpPostAPI().CallAPI(context, "API015");
					type = "08";
					display();
				}else {
					clsDialog.Show(context, "提示訊息", "請確認網路是否正常！");
				}

			}
		});

		/* 送達失敗，原因 */
		Button button_Save2 = (Button) findViewById(button_Save);
		button_Save2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isConnected()){
					Spinner Reason = (Spinner) findViewById(Spinner_Reasion);
					Log.e("getSelectedItem()", String.valueOf(Reason.getSelectedItem()));
					if(!String.valueOf(Reason.getSelectedItem()).equals(null)&&String.valueOf(Reason.getSelectedItem())!=null&&Reason.getSelectedItem()!=null){
						objDB.openDB();
						objDB.UpdateTaskFailReasonID(
								((ClsDropDownItem) Reason.getSelectedItem()).GetID(),
								Application.strCaseID);
                        objDB.UpdateTaskFailReasonName(
                                (String.valueOf(Reason.getSelectedItem())),
                                Application.strCaseID);
						objDB.UpdateTaskStatus("81", Application.strCaseID);
						objDB.DBClose();
						new clsHttpPostAPI().CallAPI(context, "API009");

						setDialog();
					}else {
						Toast.makeText(DataListFrg.this, "請返回", Toast.LENGTH_SHORT).show();
					}
				}else {
					clsDialog.Show(context, "提示訊息", "請確認網路是否正常！");
				}




			}
		});
		//送達失敗，返回
		Button button_back = (Button) findViewById(R.id.button_Back);
		button_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isConnected()){
					new clsHttpPostAPI().CallAPI(context, "API007");
					setDialog();
				}else {
					clsDialog.Show(context, "提示訊息", "請確認網路是否正常！");
				}

			}
		});

		/* 續配 */
		Button button_Resend = (Button) findViewById(R.id.button_Resend);
		button_Resend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isConnected()){
					new clsHttpPostAPI().CallAPI(context, "API017");
					setDialog();
				}else {
					clsDialog.Show(context, "提示訊息", "請確認網路是否正常！");
				}

			}
		});
		
		/* 卸集 */
		Button button_Discharge = (Button) findViewById(R.id.button_Discharge);
		button_Discharge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isConnected()){
					time();
					Log.e("today",today);
					objDB.openDB();
					objDB.UpdateDate(today, Application.strCaseID);
					objDB.DBClose();
					new clsHttpPostAPI().CallAPI(context, "API018");
					setDialog();
				}else {
					clsDialog.Show(context, "提示訊息", "請確認網路是否正常！");
				}

			}
		});

		handlerGCM = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bundle json = (Bundle) msg.obj;
				if(chickInt==1){
					myDialog2.dismiss();
					chickInt=0;
				}
				Log.e("GCM資料", String.valueOf(json));
				try {
					String status = json.getString("status");
					Log.e("GCM status",status);
					String cash_on_delivery = json.getString("cash_on_delivery");
					Log.e("GCM cash_on_delivery",cash_on_delivery);
					Application.cash_on_delivery = cash_on_delivery;

					if (status.equals("0")) {

						Application.objForm = json;
						// cCaseID,cOrderID,cCustAddress,cDistance,cSize,cItemCount,cRequestDate,cType

						String customer_address = setEncryp(json.getString("customer_address"));
						objDB.openDB();
						objDB.InsertTask(new Object[] {
								json.getString("caseID"),
								json.getString("orderID"),
								customer_address,
								json.getString("distance"),
								json.getString("size"),
								json.getString("item_count"),
								json.getString("request_time"), "0" });
						objDB.DBClose();
						Log.e("GCM_Application",Application.strCaseID);
						Log.e("GCM_strCaseID", String.valueOf(json.getInt("caseID")));

						type = "02";
						display();

					}
					if (status.equals("1")) {

						Log.e("GCM2","GCM2");
						String orderID = setEncryp(json.getString("orderID"));
						String customer_address = setEncryp(json.getString("customer_address"));
						customer_address = setEncryp(customer_address);
						Application.strCaseID = json.getString("caseID");
						Application.strObuID = json.getString("obuid");
						Application.objFormInfo = json;

						objDB.openDB();
						objDB.InsertTask(new Object[] {
								json.getString("caseID"),
								orderID,
								customer_address,
								json.getString("distance"),
								json.getString("size"),
								json.getString("item_count"),
								json.getString("request_time"), "0" });
						objDB.DBClose();
						objDB.openDB();
						String customer_address2 = setEncryp(json.getString("customer_address"));
						String customer_name2 = setEncryp(json.getString("customer_name"));
						String customer_phoneNo2 = setEncryp(json.getString("customer_phoneNo"));
						String recipient_name2 = setEncryp(json.getString("recipient_name"));
						String recipient_address2 = setEncryp(json.getString("recipient_address"));
						String recipient_phoneNo2 = setEncryp(json.getString("recipient_phoneNo"));
						objDB.UpdateTask(
								customer_address2,
								customer_name2,
								customer_phoneNo2,
								recipient_name2,
								recipient_address2,
								recipient_phoneNo2,
								json.getString("pay_type"),
								json.getString("pay_amount"), "21",
								json.getString("caseID"),
                                json.getString("orderID"),
								json.getString("cash_on_delivery"));

						objDB.DBClose();
						type = "21";
						display();
					}
					if (status.equals("2")) {
						// type = 1;
						// changeTab(0);
						// clsDialog.Show(context, "提示訊息", "接單失敗");
						// dialog = ProgressDialog.show(MainActivity.this,
						// "接單失敗", "等待詢車中...", true);
						Log.e("接單status",status);
						type = "71";
						display();
					}
					if (status.equals("3")) {

						// type = 1;
						// changeTab(0);
						// clsDialog.Show(MainActivity.this, "提示訊息", "接單逾時");
						// dialog = ProgressDialog.show(MainActivity.this,
						// "逾時接單", "等待詢車中...", true);
					}


				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		handlerTask = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				JSONObject json = (JSONObject) msg.obj;
				Log.e("handlerTask JSON3", String.valueOf(json));
				try {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							myDialog.dismiss();
						}
					});
					String Result = json.getString("Result");
					Log.e("Result", String.valueOf(Result));

					if (Result.equals("1")) {
						Log.e("handlerTask JSON2", String.valueOf(json));
						Log.e("json.getString",json.getString("Type"));
						String strType = "";
						switch (json.getString("Type")) {
						case "1":
							strType = "派遣接單回覆";
							type = "03";
							//display();
							break;
						case "2":
							strType = "派遣拒絕回覆";

							objDB.openDB();
							objDB.UpdateTaskStatus("00", Application.strCaseID);
							objDB.DBClose();
							type = "01";
							break;
						case "3":
							strType = "前往取件回覆";
							/* 顯示結果須等post回來的資訊決定，測試先寫 */
							objDB.openDB();
							objDB.UpdateTaskStatus("03", Application.strCaseID);
							objDB.DBClose();
							type = "71";//
							display();
							break;
						case "4":
							strType = "取件完成回覆";
							objDB.openDB();

							String CustomName = setEncryp(EditText_CustomName.getText().toString());
							String Address1 = setEncryp(editText_Address1.getText().toString());
							String Phone = setEncryp(editText_Phone.getText().toString());
							objDB.UpdateTask("", "", "", CustomName, Address1, Phone, ((ClsDropDownItem)Spinner_PayType.getSelectedItem()).GetID(), EditText_Money.getText().toString(), "03", Application.strCaseID,Application.newstrObuID,Application.cash_on_delivery);
							//呼叫API
							clsTask.postToAS400(context, EditText_OrderID1.getText().toString(), "01");
							objDB.UpdateTaskStatus("040", Application.strCaseID);
							objDB.DBClose();

							//type = "04";
							type = "040";
							display();

							break;
						case "5":
							strType = "回站回覆";
							/* 顯示結果須等post回來的資訊決定，測試先寫 */
							objDB.openDB();
							objDB.UpdateTaskStatus("41", Application.strCaseID);
							objDB.DBClose();
							type = "41";
							display();
							break;
						case "6":
							strType = "前往配送";
							/* 顯示結果須等post回來的資訊決定，測試先寫 */
							objDB.openDB();
							objDB.UpdateTaskStatus("06", Application.strCaseID);
							objDB.DBClose();
							type = "71";
							display();
							break;
						case "7":
							strType = "已送達";
							objDB.openDB();
							objDB.UpdateTaskStatus("070", Application.strCaseID);//06
							clsTask objTask1 = objDB
									.LoadTask(Application.strCaseID);
							/*DataListFrg.this.printer.clearData();
							DataListFrg.this.printer.addDataPair("　", "　");
							DataListFrg.this.printer.addDataPair("　", "　");
							DataListFrg.this.printer.addDataPair("@",
									objTask1.OrderID);
							DataListFrg.this.printer.addDataPair("託運單號",
									objTask1.OrderID);
							DataListFrg.this.printer.addDataPair("寄件人",
									objTask1.CustName);
							DataListFrg.this.printer.addDataPair("寄件人電話",
									objTask1.CustPhone);
							DataListFrg.this.printer.addDataPair("寄件人地址",
									objTask1.CustAddress);
							DataListFrg.this.printer.addDataPair("收件人",
									objTask1.RecName);
							DataListFrg.this.printer.addDataPair("收件人電話",
									objTask1.RecPhone);
							DataListFrg.this.printer.addDataPair("收件人地址",
									objTask1.RecAddress);
							DataListFrg.this.printer.addDataPair("貨件數",
									objTask1.ItemCount);
							DataListFrg.this.printer.addDataPair("貨品大小",
									objTask1.Size);*/
							objDB.DBClose();

							//DataListFrg.this.printer.printF(DataListFrg.this);
							//type = "07";

							type = "070";
							display();

							break;
						case "8":
							strType = "已送達回報";
							/* 顯示結果須等post回來的資訊決定，測試先寫 */
							objDB.openDB();
							objDB.UpdateTaskStatus("71", Application.strCaseID);
							objDB.DBClose();
							type = "71";
							//呼叫API
							clsTask.postToAS400(context, EditText_OrderID1.getText().toString(), "02");
							display();
							break;
						case "9":
							strType = "送達失敗";
							/* 顯示結果須等post回來的資訊決定，測試先寫 */
							objDB.openDB();
							objDB.UpdateTaskStatus("81", Application.strCaseID);
							objDB.DBClose();
							
							Spinner Reason = (Spinner) findViewById(Spinner_Reasion);
							String strProStatus = String.format("%02d", Integer.valueOf(((ClsDropDownItem) Reason.getSelectedItem()).GetID()));
							//呼叫API
							clsTask.postToAS400(context, EditText_OrderID1.getText().toString(), strProStatus);
							new clsHttpPostAPI().CallAPI(context, "API012");
							break;
						case "10":
							strType = "取得站所資訊";
							/* 集貨 */

							Spinner Spinner_SetGoods = (Spinner) findViewById(R.id.Spinner_SetGoods);
							List<ClsDropDownStation> objList = new ArrayList<ClsDropDownStation>();
							JSONArray objArray = json
									.getJSONArray("DataContents");
							JSONObject jsonItem = null;
							for (int i = 0; i < objArray.length(); i++) {
								jsonItem = objArray.getJSONObject(i);
								objList.add(new ClsDropDownStation(
										jsonItem.getString("StationID"),
										jsonItem.getString("StationName"),
										"1"));
							}

							ArrayAdapter<ClsDropDownStation> Adapter = new ArrayAdapter<ClsDropDownStation>(
									context, R.layout.myspinner, objList);
							objDB.openDB();
							objDB.UpdateTaskStatus("05", Application.strCaseID);
							objDB.DBClose();
							Spinner_SetGoods.setAdapter(Adapter);
							type = "05";
							display();

							break;
						case "11":
							strType = "回站完成";
							/* 顯示結果須等post回來的資訊決定，測試先寫 */
							objDB.openDB();
							objDB.UpdateTaskStatus("51", Application.strCaseID);
							objDB.DBClose();
							//type = "51";
							type = "71";
							display();
							break;

						case "12":
							strType = "取得失敗原因";
							/* 顯示結果須等post回來的資訊決定，測試先寫 */
							/*
							Spinner Spinner_Reasion = (Spinner) findViewById(R.id.Spinner_Reasion);
							objList1 = new ArrayList<ClsDropDownItem>();
							objArray1 = json
									.getJSONArray("DataContents");
							JSONObject jsonItem1 = null;
							for (int i = 0; i < objArray1.length(); i++) {
								jsonItem1 = objArray1.getJSONObject(i);
								objList1.add(new ClsDropDownItem(jsonItem1
										.getString("FailReasonID"), jsonItem1
										.getString("Reason")));
							}

							ArrayAdapter<ClsDropDownItem> Adapter1 = new ArrayAdapter<ClsDropDownItem>(
									context, R.layout.myspinner, objList1);
							for(int i =0; i < objList1.size() ;i++){
                                Log.e("objList1", String.valueOf(objList1.get(i)));
                            }
                            Spinner_Reasion.setAdapter(Adapter1);
							Log.e("Adapter", String.valueOf(Adapter1));
							*/

							break;

						case "13":
							strType = "續配";
							objDB.openDB();
							objDB.UpdateTaskStatus("06", Application.strCaseID);
							objDB.DBClose();
							type = "06";
							display();
							break;
						case "14":
							strType = "卸貨";
							objDB.openDB();
							objDB.UpdateTaskStatus("09", Application.strCaseID);
							objDB.DBClose();
							
							//呼叫API
							clsTask.postToAS400(context, EditText_OrderID1.getText().toString(), "49");
							
							
							type = "09";
							display();
							break;
						}

						// objDB.openDB();
						// objDB.UpdateTaskStatus(json.getString("Type"),
						// Application.strCaseID);
						// objDB.DBClose();
						// clsDialog.Show(context, "提示訊息", strType+"OK");
					}

					if (Result.equals("2")) {
						clsDialog
								.Show(context, "ERROR", "輸入的授權碼 (Key)是不合法的授權碼");
					}

					if (Result.equals("3")) {
						clsDialog.Show(context, "ERROR", "輸入的參數有缺漏");
					}

					if (Result.equals("4")) {
						clsDialog.Show(context, "ERROR", "案件編號不存在");
                        objDB.openDB();
                        objDB.UpdateTaskStatus("AA", Application.strCaseID);
                        objDB.DBClose();
                        myDialog.dismiss();
                        type="71";
                        display();

					}

					if (Result.equals("5")) {
						clsDialog.Show(context, "ERROR", "obuID不存在");
					}

					if (Result.equals("6")) {
						clsDialog.Show(context, "ERROR", "託運單號格式有誤");
					}

					if (Result.equals("7")) {
						clsDialog.Show(context, "ERROR", "集貨站ID不存在");
					}
					if (Result.equals("200")) {
						clsDialog.Show(context, "ERROR", "系統忙碌或其他原因造成沒有完成服務，請重試");
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			}
		};

		handlerListView = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				type = (String) msg.obj;
				Log.e("listview", String.valueOf(msg));
				display();

			}
		};

		handlerThread = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String strType = (String) msg.obj;
				switch (strType) {
				case "ListView":
					//myDialog.dismiss();
					break;
				}
			}
		};
		//手勢滑動設定
		ListView listView = (ListView)findViewById(R.id.listView);
		listView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});

		/*
		ScrollView scrollView = (ScrollView)findViewById(R.id.ScrollView_Step1);
		scrollView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});
		ScrollView scrollView2 = (ScrollView)findViewById(R.id.ScrollView_Step2);
		scrollView2.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});
		ScrollView scrollView3 = (ScrollView)findViewById(R.id.ScrollView_Delivery);
		scrollView3.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});
			*/
		//上排按鈕設定
		button_DoList = (Button) findViewById(R.id.button_DoList);
		button_DoList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				type = "71";
				display();

			}
		});

		button_IO = (Button) findViewById(R.id.button_IO);
		button_IO.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(DataListFrg.this, InOutFrg.class);
				startActivity(intent);
				DataListFrg.this.finish();
			}
		});

		button_GT = (Button) findViewById(R.id.button_GT);
		button_GT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(DataListFrg.this, GetTaskFrg.class);
				startActivity(intent);
				DataListFrg.this.finish();
			}
		});

		button_DoneList = (Button) findViewById(R.id.button_DoneList);
		button_DoneList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DataListFrg.this, HistoryFragment.class);
				startActivity(intent);
				DataListFrg.this.finish();
			}
		});
		//登出 關閉service
		Button button_Logout = (Button) findViewById(R.id.Button_Logout);
		button_Logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				//Intent intent = new Intent(DataListFrg.this, Login.class);
			 	//startActivity(intent);
				Intent it = new Intent(DataListFrg.this,Delay.class);
				stopService(it);
				new clsHttpPostAPI().CallAPI(context, "API014");
			}
		});
		// 休息中 接單中
		Button Button_Status = (Button)findViewById(R.id.Button_Status);
		Button_Status.setText(objLoginInfo.GetStatus());
		if(Application.GPS!=null && !Application.GPS.equals("")){
			Button_Status.setTextColor(Color.GREEN);
		}

		Button_Status.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(objLoginInfo.Status.equals("02"))//休息中
				{
					objLoginInfo.Update("01");
					((Button)v).setText(objLoginInfo.GetStatus());
					//呼叫API
					new clsHttpPostAPI().CallAPI(context, "API020");
				}
				else
				if(objLoginInfo.Status.equals("01"))//接單中
				{
					objLoginInfo.Update("02");
					((Button)v).setText(objLoginInfo.GetStatus());
					//呼叫API
					new clsHttpPostAPI().CallAPI(context, "API019");
				}
			}
		});

		button_DoList.setBackgroundResource(R.drawable.menu01b);



	}



	/**
	 * Bind ListView Data
	 * */
	private void setListView() {

		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					objDB.openDB();
					Cursor cursor = objDB
							.Load1("tblTask",
									"cStatus<>'71' and cStatus<>'81' and cStatus<>'2' and cStatus<>'3' and cStatus<>'09'",
									"cRequestDate desc", "");
					List rowitem = new ArrayList();
					listView = (ListView) findViewById(R.id.listView);

					if (cursor != null && cursor.getCount() > 0) {
						while (true) {
							String strOrderID = cursor.getString(cursor
									.getColumnIndex("cOrderID"));
							String strCaseID = cursor.getString(cursor
									.getColumnIndex("cCaseID"));
							String strStatus = cursor.getString(cursor
									.getColumnIndex("cStatus"));
							Log.e("狀態",strStatus);
							rowitem.add(new NewItem(strOrderID, strCaseID,
									clsTask.GetStatus(strStatus)));

							if (cursor.isLast())
								break;

							cursor.moveToNext();
						}
						cursor.close();
					}
					objDB.DBClose();

					adpater = new ListViewAdpater(context,
							rowitem);
					listView.setAdapter(adpater);
					//adpater.notifyDataSetChanged();
				} catch (Exception e) {
					Log.i("Error", e.getMessage());
					try {

					} catch (Exception e2) {
						// TODO: handle exception
					}

				} finally {
					//myDialog.dismiss();
				}
			}
		}).start();


		/*
		 * Message msg = new Message(); msg.obj = "ListView";
		 * handlerThread.sendMessage(msg);
		 */
	}

	private void setDropDownListReason() {
		new clsHttpPostAPI().CallAPI(context, "API015");
	}

	/**
	 * 
	 * */
	private void setKeyListener()
	{
		EditText EditText_Receive=((EditText) findViewById(R.id.EditText_Receive));
		
		 Spinner_PayType=((Spinner) findViewById(R.id.Spinner_PayType));

			List<ClsDropDownItem> objList = new ArrayList<ClsDropDownItem>();

				objList.add(new ClsDropDownItem("0", "月結"));
				objList.add(new ClsDropDownItem("1", "現金"));
				objList.add(new ClsDropDownItem("2", "到付"));

			ArrayAdapter<ClsDropDownItem> Adapter = new ArrayAdapter<ClsDropDownItem>(
					context, R.layout.myspinner, objList);
			Spinner_PayType.setAdapter(Adapter);
		 
		 EditText_OrderID1=((EditText) findViewById(R.id.EditText_OrderID1));
		 EditText_CustomName=((EditText) findViewById(R.id.EditText_CustomName));
		 editText_Address1=	((EditText) findViewById(R.id.editText_Address1));
		 editText_Phone=((EditText) findViewById(R.id.editText_Phone));
		 EditText_Count1=((EditText) findViewById(R.id.EditText_Count1));
		 //EditText_PayType=((EditText) findViewById(R.id.EditText_PayType));
		 EditText_Money=((EditText) findViewById(R.id.EditText_Money));
		 //Button_Print1 = (Button)findViewById(R.id.button_Print);
		
		 EditText_OrderID1.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (event.getAction() == event.ACTION_DOWN) {
						if (keyCode == 23) {
							EditText_CustomName.requestFocus();
							return true;
						}
						return false;
					}
					return true;
				}
			});
		 
		EditText_CustomName.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == 23) {
						editText_Address1.requestFocus();
						return true;
					}
					return false;
				}
				return true;
			}
		});
		
		editText_Address1.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == 23) {
						editText_Phone.requestFocus();
						return true;
					}
					return false;
				}
				return true;
			}
		});
		
		editText_Phone.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == 23) {
						EditText_Count1.requestFocus();
						return true;
					}
					return false;
				}
				return true;
			}
		});
		
		EditText_Count1.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == 23) {
						Spinner_PayType.requestFocus();
						return true;
					}
					return false;
				}
				return true;
			}
		});
		
		Spinner_PayType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView adapterView, View view, int position, long id){
            	EditText_Money.requestFocus();
            }
            public void onNothingSelected(AdapterView arg0) {
            }
        });
		
		EditText_Money.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == 23) {
						EditText_OrderID1.requestFocus();
						return true;
					}
					return false;
				}
				return true;
			}
		});
		EditText_Receive.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == 23) {
						Button btnGo = (Button)findViewById(R.id.button_Go);
						btnGo.requestFocus();
						return true;
					}
					return false;
				}
				return true;
			}
		});
		
	}




	public void onStart() {
		super.onStart();
		GCMIntentService.handlerGCM = handlerGCM;
		clsHttpPostAPI.handlerTask = handlerTask;
		ListViewAdpater.handler = handlerListView;

	}



	//手勢滑動設定 implements GestureDetector.OnGestureListener 產生以下方法
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if(e2!=null){
			float distance = e2.getX()-e1.getX();
			if(distance>100){
				Log.e("方向2","右邊");
				Intent intent = new Intent(DataListFrg.this, HistoryFragment.class);
				startActivity(intent);
			}else if(distance<-100){
				Log.e("方向2","左邊");
			}
			return false;
		}
		return false;

	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		float distance = e2.getX()-e1.getX();

		if(distance>100){

			Log.e("方向","右邊");
			Intent intent = new Intent(DataListFrg.this, InOutFrg.class);
			startActivity(intent);
		}else if(distance<-100){
			Log.e("方向","左邊");

			Intent intent = new Intent(DataListFrg.this, HistoryFragment.class);
			startActivity(intent);
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(detector!=null){
			return detector.onTouchEvent(event);
		}
		return false;
	}
	//畫面處理 點選明細後畫面新增
	public void display() {
		if (type.equals("02"))// 接單畫面
		{
			LinearLayout LinearLayout_List = (LinearLayout) findViewById(R.id.LinearLayout_list);
			LinearLayout_List.setVisibility(View.GONE);

			ScrollView ScrollView_Delivery = (ScrollView) findViewById(R.id.ScrollView_Delivery);
			ScrollView_Delivery.setVisibility(View.GONE);

			ScrollView ScrollView_Step1 = (ScrollView) findViewById(R.id.ScrollView_Step1);
			ScrollView_Step1.setVisibility(View.VISIBLE);

			ScrollView ScrollView_Step2 = (ScrollView) findViewById(R.id.ScrollView_Step2);
			ScrollView_Step2.setVisibility(View.GONE);

			LinearLayout LinearLayout_TextViewReceive = (LinearLayout) findViewById(R.id.LinearLayout_TextViewReceive);
			LinearLayout LinearLayout_ButtonGo = (LinearLayout) findViewById(R.id.LinearLayout_ButtonGo);
			LinearLayout LinearLayout_ButtonGet = (LinearLayout) findViewById(R.id.LinearLayout_ButtonGet);

			LinearLayout_ButtonGo.setVisibility(View.GONE);
			LinearLayout_TextViewReceive.setVisibility(View.GONE);
			LinearLayout_ButtonGet.setVisibility(View.VISIBLE);

			/* 取出資料 */
			objDB.openDB();
			clsTask objT = objDB.LoadTask(Application.strCaseID);

			((TextView) findViewById(R.id.TextView_CarNo))
					.setText(Application.strCar);
			((TextView) findViewById(R.id.TextView_DateTime))
					.setText(objT.RequestDate);
			((TextView) findViewById(R.id.TextView_OrderID))
					.setText(objT.OrderID);
			((TextView) findViewById(R.id.editText_Address))
					.setText(objT.CustAddress);
			((TextView) findViewById(R.id.EditText_Count))
					.setText(objT.ItemCount);
			((TextView) findViewById(R.id.editText_Distant))
					.setText(objT.Distance);
			((TextView) findViewById(R.id.EditText_Size))
					.setText(objT.Size);

			objDB.DBClose();
			objDB.close();
			Log.e("type",type);

		}

		if (type.equals("21"))// 點選接單後，設定預計到達時間畫面
		{
			/* Panel的顯示隱藏 */
			LinearLayout LinearLayout_List = (LinearLayout) findViewById(R.id.LinearLayout_list);
			LinearLayout_List.setVisibility(View.GONE);

			ScrollView ScrollView_Delivery = (ScrollView) findViewById(R.id.ScrollView_Delivery);
			ScrollView_Delivery.setVisibility(View.GONE);

			ScrollView ScrollView_Step1 = (ScrollView) findViewById(R.id.ScrollView_Step1);
			ScrollView_Step1.setVisibility(View.VISIBLE);

			ScrollView ScrollView_Step2 = (ScrollView) findViewById(R.id.ScrollView_Step2);
			ScrollView_Step2.setVisibility(View.GONE);

			LinearLayout LinearLayout_ButtonGet = (LinearLayout) findViewById(R.id.LinearLayout_ButtonGet);
			LinearLayout LinearLayout_ButtonGo = (LinearLayout) findViewById(R.id.LinearLayout_ButtonGo);
			LinearLayout LinearLayout_TextViewReceive = (LinearLayout) findViewById(R.id.LinearLayout_TextViewReceive);

			LinearLayout_ButtonGet.setVisibility(View.GONE);
			LinearLayout_ButtonGo.setVisibility(View.GONE);
			LinearLayout_TextViewReceive.setVisibility(View.GONE);

			LinearLayout_ButtonGo.setVisibility(View.VISIBLE);
			LinearLayout_TextViewReceive.setVisibility(View.VISIBLE);

			/* 取出資料 */
			objDB.openDB();
			clsTask objT = objDB.LoadTask(Application.strCaseID);
			objDB.DBClose();
			//測試
			String CustAddress = setDecrypt(objT.CustAddress);

			((TextView) findViewById(R.id.TextView_CarNo))
					.setText(Application.strCar);
			((TextView) findViewById(R.id.TextView_DateTime))
					.setText(objT.RequestDate);
			((TextView) findViewById(R.id.TextView_OrderID))
					.setText(objT.OrderID);
			((TextView) findViewById(R.id.editText_Address))
					.setText(CustAddress);
			((TextView) findViewById(R.id.EditText_Size))
					.setText(objT.Size);
			((TextView) findViewById(R.id.EditText_Count))
					.setText(objT.ItemCount);
			((TextView) findViewById(R.id.editText_Distant))
					.setText(objT.Distance);
			((TextView) findViewById(R.id.EditText_Receive)).setText("");

			((TextView) findViewById(R.id.EditText_Receive)).requestFocus();
			Log.e("type",type);
		}

		if (type.equals("03"))// 取件完成畫面
		{
			/* Panel的顯示隱藏 */
			LinearLayout LinearLayout_List = (LinearLayout) findViewById(R.id.LinearLayout_list);
			LinearLayout_List.setVisibility(View.GONE);
			ScrollView ScrollView_Step1 = (ScrollView) findViewById(R.id.ScrollView_Step1);
			ScrollView ScrollView_Step2 = (ScrollView) findViewById(R.id.ScrollView_Step2);
			LinearLayout LinearLayout_Pickup = (LinearLayout) findViewById(R.id.LinearLayout_Pickup);
			LinearLayout LinearLayout_pic = (LinearLayout) findViewById(R.id.LinearLayout_pic);

			ScrollView_Step1.setVisibility(View.GONE);
			ScrollView_Step2.setVisibility(View.GONE);
			LinearLayout_Pickup.setVisibility(View.GONE);
			LinearLayout_pic.setVisibility(View.GONE);

			ScrollView_Step2.setVisibility(View.VISIBLE);
			LinearLayout_Pickup.setVisibility(View.VISIBLE);

			LinearLayout LinearLayout_ButtonSend = (LinearLayout) findViewById(R.id.LinearLayout_ButtonSend);
			LinearLayout LinearLayout_ButtonPrint1 = (LinearLayout) findViewById(R.id.LinearLayout_ButtonPrint1);

			LinearLayout_ButtonSend.setVisibility(View.VISIBLE);
			LinearLayout_ButtonPrint1.setVisibility(View.GONE);


			/* 取出資料 */
			objDB.openDB();
			clsTask objT = objDB.LoadTask(Application.strCaseID);
			String RecAddress2 = setDecrypt(objT.RecAddress);
			String RecName2 = setDecrypt(objT.RecName);
			String RecPhone2 = setDecrypt(objT.RecPhone);
			objDB.DBClose();
			((TextView) findViewById(R.id.TextView_CarNo1))
					.setText(Application.strCar);
			((TextView) findViewById(R.id.TextView_DateTime1))
					.setText(objT.RequestDate);
			((EditText) findViewById(R.id.EditText_OrderID1))
					.setText(objT.OrderID);
			((EditText) findViewById(R.id.editText_Address1))
					.setText(RecAddress2);
			((EditText) findViewById(R.id.EditText_CustomName))
					.setText(RecName2);
			((EditText) findViewById(R.id.editText_Phone))
					.setText(RecPhone2);
			((EditText) findViewById(R.id.EditText_Count1))
					.setText(objT.ItemCount);
			String PayType = null;
			if(objT.PayType.equals("0")){
				PayType = "月結";
			}else if (objT.PayType.equals("1")){
				PayType = "現金";
			}else if (objT.PayType.equals("2")){
				PayType = "到付";
			}
			((TextView) findViewById(R.id.textView13))
					.setText(PayType);
			for (int j = 0; j < Spinner_PayType.getAdapter().getCount(); j++) {
				if(((ClsDropDownItem)Spinner_PayType.getAdapter().getItem(j)).GetID().equals(objT.PayType))
				{
					Spinner_PayType.setSelection(j);

				}
			}

			((EditText) findViewById(R.id.EditText_Money))
					.setText(objT.PayAmount);
			((TextView) findViewById(R.id.textView_Cash))
					.setText(objT.Cash);
			((EditText) findViewById(R.id.EditText_OrderID1)).requestFocus();

			Log.e("type",type);

		}

		if (type.equals("04"))// 列印
		{
			/* Panel的顯示隱藏 */
			LinearLayout LinearLayout_List = (LinearLayout) findViewById(R.id.LinearLayout_list);
			LinearLayout_List.setVisibility(View.GONE);
			ScrollView ScrollView_Step1 = (ScrollView) findViewById(R.id.ScrollView_Step1);
			ScrollView ScrollView_Step2 = (ScrollView) findViewById(R.id.ScrollView_Step2);
			LinearLayout LinearLayout_Pickup = (LinearLayout) findViewById(R.id.LinearLayout_Pickup);
			LinearLayout LinearLayout_pic = (LinearLayout) findViewById(R.id.LinearLayout_pic);

			ScrollView_Step1.setVisibility(View.GONE);
			ScrollView_Step2.setVisibility(View.GONE);
			LinearLayout_Pickup.setVisibility(View.GONE);
			LinearLayout_pic.setVisibility(View.GONE);

			ScrollView_Step2.setVisibility(View.VISIBLE);
			LinearLayout_Pickup.setVisibility(View.VISIBLE);

			LinearLayout LinearLayout_ButtonSend = (LinearLayout) findViewById(R.id.LinearLayout_ButtonSend);
			LinearLayout LinearLayout_ButtonPrint1 = (LinearLayout) findViewById(R.id.LinearLayout_ButtonPrint1);

			LinearLayout_ButtonSend.setVisibility(View.GONE);
			LinearLayout_ButtonPrint1.setVisibility(View.VISIBLE);

			/* 取出資料 */
			objDB.openDB();
			clsTask objT = objDB.LoadTask(Application.strCaseID);

			objDB.DBClose();
			((TextView) findViewById(R.id.TextView_CarNo1))
					.setText(Application.strCar);
			((TextView) findViewById(R.id.TextView_DateTime1))
					.setText(objT.RequestDate);
			((EditText) findViewById(R.id.EditText_OrderID1))
					.setText(objT.OrderID);
			((EditText) findViewById(R.id.editText_Address1))
					.setText(objT.RecAddress);
			((EditText) findViewById(R.id.EditText_CustomName))
					.setText(objT.RecName);
			((EditText) findViewById(R.id.editText_Phone))
					.setText(objT.RecPhone);
			((EditText) findViewById(R.id.EditText_Count1))
					.setText(objT.ItemCount);
			/*
			for (int j = 0; j < Spinner_PayType.getAdapter().getCount(); j++) {
				if(((ClsDropDownItem)Spinner_PayType.getAdapter().getItem(j)).GetID().equals(objT.PayType))
				{
					Spinner_PayType.setSelection(j);
					Log.e("付款方式", String.valueOf(j));
					Application.strPayType= j;
				}
			}
				*/
			EditText editText = (EditText)findViewById(R.id.EditText_Money);
			String payAmount = editText.getText().toString();
			Log.e("PayAmount1",payAmount);
			Application.strPayAmounts = payAmount;

			((EditText) findViewById(R.id.EditText_OrderID1)).requestFocus();
			Log.e("type",type);
		}

		if (type.equals("040"))// 拍託運單
		{
			/* Panel的顯示隱藏 */
			LinearLayout LinearLayout_List = (LinearLayout) findViewById(R.id.LinearLayout_list);
			LinearLayout_List.setVisibility(View.GONE);
			ScrollView ScrollView_Step1 = (ScrollView) findViewById(R.id.ScrollView_Step1);
			ScrollView ScrollView_Step2 = (ScrollView) findViewById(R.id.ScrollView_Step2);
			LinearLayout LinearLayout_Pickup = (LinearLayout) findViewById(R.id.LinearLayout_Pickup);
			LinearLayout LinearLayout_pic = (LinearLayout) findViewById(R.id.LinearLayout_pic);

			ScrollView_Step1.setVisibility(View.GONE);
			ScrollView_Step2.setVisibility(View.GONE);
			LinearLayout_Pickup.setVisibility(View.GONE);
			LinearLayout_pic.setVisibility(View.GONE);

			ScrollView_Step2.setVisibility(View.VISIBLE);
			LinearLayout_Pickup.setVisibility(View.GONE);
			LinearLayout_pic.setVisibility(View.VISIBLE);

			/* 取出資料 */
			objDB.openDB();
			clsTask objT = objDB.LoadTask(Application.strCaseID);

			objDB.DBClose();
			((TextView) findViewById(R.id.TextView_CarNo1))
					.setText(Application.strCar);
			((TextView) findViewById(R.id.TextView_DateTime1))
					.setText(objT.RequestDate);
			((TextView) findViewById(R.id.EditText_OrderID1))
					.setText(objT.OrderID);
			((TextView) findViewById(R.id.editText_Address1))
					.setText(objT.CustAddress);
			((TextView) findViewById(R.id.EditText_CustomName))
					.setText(objT.CustName);
			((TextView) findViewById(R.id.editText_Phone))
					.setText(objT.CustPhone);
			((TextView) findViewById(R.id.EditText_Count1))
					.setText(objT.ItemCount);

			CheckBox chk = (CheckBox) findViewById(R.id.chkCreateData);
			chk.setVisibility(View.VISIBLE);
			for (int j = 0; j < Spinner_PayType.getAdapter().getCount(); j++) {
				if(((ClsDropDownItem)Spinner_PayType.getAdapter().getItem(j)).GetID().equals(objT.PayType))
				{
					Spinner_PayType.setSelection(j);
				}
			}
			/*
			((TextView) findViewById(R.id.EditText_PayType)).setText(clsTask
					.GetPayType(objT.PayType));*/
			((TextView) findViewById(R.id.EditText_Money))
					.setText(objT.PayAmount);
            Button button_Send = (Button) findViewById(R.id.button_Send);
            button_Send.setText("上傳");
			Log.e("type",type);
		}

		if (type.equals("41"))// 直送或回站畫面 //轉單後
		{
			/* 設定主框 */

			LinearLayout LinearLayout_List = (LinearLayout) findViewById(R.id.LinearLayout_list);
			LinearLayout_List.setVisibility(View.GONE);
			ScrollView ScrollView_Step1 = (ScrollView) findViewById(R.id.ScrollView_Step1);

			ScrollView ScrollView_Step2 = (ScrollView) findViewById(R.id.ScrollView_Step2);

			ScrollView ScrollView_Delivery = (ScrollView) findViewById(R.id.ScrollView_Delivery);

			ScrollView_Step1.setVisibility(View.GONE);
			ScrollView_Step2.setVisibility(View.GONE);
			ScrollView_Delivery.setVisibility(View.VISIBLE);

			/* 設定第二層 */
			LinearLayout LinearLayout_Arrivals = (LinearLayout) findViewById(R.id.LinearLayout_Arrivals);
			LinearLayout LinearLayout_pic2 = (LinearLayout) findViewById(R.id.LinearLayout_pic2);
			LinearLayout LinearLayout_Reasion = (LinearLayout) findViewById(R.id.LinearLayout_Reasion);
			LinearLayout LinearLayout_SG = (LinearLayout) findViewById(R.id.LinearLayout_SG);

			LinearLayout_Arrivals.setVisibility(View.VISIBLE);
			LinearLayout_pic2.setVisibility(View.GONE);
			LinearLayout_Reasion.setVisibility(View.GONE);
			LinearLayout_SG.setVisibility(View.GONE);

			/* 設定第三層 */
			LinearLayout LinearLayout_Type = (LinearLayout) findViewById(R.id.LinearLayout_Type);
			LinearLayout LinearLayout_OKNG = (LinearLayout) findViewById(R.id.LinearLayout_OKNG);
			LinearLayout LinearLayout_SetGoods = (LinearLayout) findViewById(R.id.LinearLayout_SetGoods);
			LinearLayout LinearLayout_ButtonPrint2 = (LinearLayout) findViewById(R.id.LinearLayout_ButtonPrint2);

			LinearLayout_Type.setVisibility(View.VISIBLE);
			LinearLayout_OKNG.setVisibility(View.GONE);
			LinearLayout_SetGoods.setVisibility(View.GONE);
			LinearLayout_ButtonPrint2.setVisibility(View.GONE);

			/* 取出資料 */
			objDB.openDB();
			clsTask objT = objDB.LoadTask(Application.strCaseID);
			if(Application.strCaseID!=null){
				Log.e("直送",Application.strCaseID);
			}else {
				Log.e("直送","null");
			}
			objDB.DBClose();
			String RecAddress2 ;
			String RecName2 ;
			String RecPhone2 ;
			String CustName2 ;

			RecAddress2 = setDecrypt(objT.RecAddress);
			RecName2 = setDecrypt(objT.RecName);
			RecPhone2 = setDecrypt(objT.RecPhone);
			CustName2 = setDecrypt(objT.CustName);

			((TextView) findViewById(R.id.TextView_CarNo2))
					.setText(Application.strCar);
			((TextView) findViewById(R.id.TextView_DateTime2))
					.setText(objT.RequestDate);
			((TextView) findViewById(R.id.TextView_OrderID2))
					.setText(objT.OrderID);
			((TextView) findViewById(R.id.editText_Address2))
					.setText(RecAddress2);
			((TextView) findViewById(R.id.EditText_CustomName2))
					.setText(RecName2);
			((TextView) findViewById(R.id.editText_Phone2))
					.setText(RecPhone2);
			((TextView) findViewById(R.id.EditText_Count2))
					.setText(objT.ItemCount);
			((TextView) findViewById(R.id.EditText_PayType2)).setText(clsTask
					.GetPayType(objT.PayType));
			((TextView) findViewById(R.id.EditText_Money2))
					.setText(objT.PayAmount);
			((TextView) findViewById(R.id.editText_SendMan))
					.setText(CustName2);
			((TextView) findViewById(R.id.EditText_Size2))
					.setText(objT.Size);
			((TextView) findViewById(R.id.EditText_Cash2))
					.setText(objT.Cash);
			Log.e("type",type);
		}

		if (type.equals("06"))// 選直送
		{
			/* 設定主框 */

			LinearLayout LinearLayout_List = (LinearLayout) findViewById(R.id.LinearLayout_list);
			LinearLayout_List.setVisibility(View.GONE);
			ScrollView ScrollView_Step1 = (ScrollView) findViewById(R.id.ScrollView_Step1);

			ScrollView ScrollView_Step2 = (ScrollView) findViewById(R.id.ScrollView_Step2);

			ScrollView ScrollView_Delivery = (ScrollView) findViewById(R.id.ScrollView_Delivery);

			ScrollView_Step1.setVisibility(View.GONE);
			ScrollView_Step2.setVisibility(View.GONE);
			ScrollView_Delivery.setVisibility(View.VISIBLE);

			/* 設定第二層 */
			LinearLayout LinearLayout_Arrivals = (LinearLayout) findViewById(R.id.LinearLayout_Arrivals);
			LinearLayout LinearLayout_pic2 = (LinearLayout) findViewById(R.id.LinearLayout_pic2);
			LinearLayout LinearLayout_Reasion = (LinearLayout) findViewById(R.id.LinearLayout_Reasion);

			LinearLayout_Arrivals.setVisibility(View.VISIBLE);
			LinearLayout_pic2.setVisibility(View.GONE);
			LinearLayout_Reasion.setVisibility(View.GONE);

			/* 設定第三層 */
			LinearLayout LinearLayout_Type = (LinearLayout) findViewById(R.id.LinearLayout_Type);
			LinearLayout LinearLayout_OKNG = (LinearLayout) findViewById(R.id.LinearLayout_OKNG);
			LinearLayout LinearLayout_SetGoods = (LinearLayout) findViewById(R.id.LinearLayout_SetGoods);
			LinearLayout LinearLayout_SG = (LinearLayout) findViewById(R.id.LinearLayout_SG);
			LinearLayout LinearLayout_ButtonPrint2 = (LinearLayout) findViewById(R.id.LinearLayout_ButtonPrint2);

			LinearLayout_Type.setVisibility(View.GONE);
			LinearLayout_OKNG.setVisibility(View.VISIBLE);
			LinearLayout_SetGoods.setVisibility(View.GONE);
			LinearLayout_SG.setVisibility(View.GONE);
			LinearLayout_ButtonPrint2.setVisibility(View.GONE);

			objDB.openDB();
			clsTask objT = objDB.LoadTask(Application.strCaseID);
			if(Application.strCaseID!=null){
				Log.e("直送",Application.strCaseID);
			}else {
				Log.e("直送","null");
			}
			objDB.DBClose();
			String RecAddress = setDecrypt(objT.RecAddress);
			String RecName = setDecrypt(objT.RecName);
			String RecPhone = setDecrypt(objT.RecPhone);
			String CustName = setDecrypt(objT.CustName);

			((TextView) findViewById(R.id.TextView_CarNo2))
					.setText(Application.strCar);
			((TextView) findViewById(R.id.TextView_DateTime2))
					.setText(objT.RequestDate);
			((TextView) findViewById(R.id.TextView_OrderID2))
					.setText(objT.OrderID);
			((TextView) findViewById(R.id.editText_Address2))
					.setText(RecAddress);
			((TextView) findViewById(R.id.EditText_CustomName2))
					.setText(RecName);
			((TextView) findViewById(R.id.editText_Phone2))
					.setText(RecPhone);
			((TextView) findViewById(R.id.EditText_Count2))
					.setText(objT.ItemCount);
			((TextView) findViewById(R.id.EditText_PayType2)).setText(clsTask
					.GetPayType(objT.PayType));
			((TextView) findViewById(R.id.EditText_Money2))
					.setText(objT.PayAmount);
			((TextView) findViewById(R.id.editText_SendMan))
					.setText(CustName);
			((TextView) findViewById(R.id.EditText_Size2))
					.setText(objT.Size);
			((TextView) findViewById(R.id.EditText_Cash2))
					.setText(objT.Cash);
			Log.e("type",type);
		}

		if (type.equals("07"))// 列印簽收單
		{
			/* 設定主框 */
			LinearLayout LinearLayout_List = (LinearLayout) findViewById(R.id.LinearLayout_list);
			LinearLayout_List.setVisibility(View.GONE);
			ScrollView ScrollView_Step1 = (ScrollView) findViewById(R.id.ScrollView_Step1);

			ScrollView ScrollView_Step2 = (ScrollView) findViewById(R.id.ScrollView_Step2);

			ScrollView ScrollView_Delivery = (ScrollView) findViewById(R.id.ScrollView_Delivery);

			ScrollView_Step1.setVisibility(View.GONE);
			ScrollView_Step2.setVisibility(View.GONE);
			ScrollView_Delivery.setVisibility(View.VISIBLE);

			/* 設定第二層 */
			LinearLayout LinearLayout_Arrivals = (LinearLayout) findViewById(R.id.LinearLayout_Arrivals);
			LinearLayout LinearLayout_pic2 = (LinearLayout) findViewById(R.id.LinearLayout_pic2);
			LinearLayout LinearLayout_Reasion = (LinearLayout) findViewById(R.id.LinearLayout_Reasion);

			LinearLayout_Arrivals.setVisibility(View.VISIBLE);
			LinearLayout_pic2.setVisibility(View.GONE);
			LinearLayout_Reasion.setVisibility(View.GONE);

			/* 設定第三層 */
			LinearLayout LinearLayout_Type = (LinearLayout) findViewById(R.id.LinearLayout_Type);
			LinearLayout LinearLayout_OKNG = (LinearLayout) findViewById(R.id.LinearLayout_OKNG);
			LinearLayout LinearLayout_SetGoods = (LinearLayout) findViewById(R.id.LinearLayout_SetGoods);
			LinearLayout LinearLayout_SG = (LinearLayout) findViewById(R.id.LinearLayout_SG);
			LinearLayout LinearLayout_ButtonPrint2 = (LinearLayout) findViewById(R.id.LinearLayout_ButtonPrint2);

			LinearLayout_Type.setVisibility(View.GONE);
			LinearLayout_OKNG.setVisibility(View.GONE);
			LinearLayout_SetGoods.setVisibility(View.GONE);
			LinearLayout_SG.setVisibility(View.GONE);
			LinearLayout_ButtonPrint2.setVisibility(View.VISIBLE);
			Log.e("type",type);

			/* 取出資料 */
			objDB.openDB();
			clsTask objT = objDB.LoadTask(Application.strCaseID);
			if(Application.strCaseID!=null){
				Log.e("直送",Application.strCaseID);
			}else {
				Log.e("直送","null");
			}
			objDB.DBClose();
			((TextView) findViewById(R.id.TextView_CarNo2))
					.setText(Application.strCar);
			((TextView) findViewById(R.id.TextView_DateTime2))
					.setText(objT.RequestDate);
			((TextView) findViewById(R.id.TextView_OrderID2))
					.setText(objT.OrderID);
			((TextView) findViewById(R.id.editText_Address2))
					.setText(objT.RecAddress);
			((TextView) findViewById(R.id.EditText_CustomName2))
					.setText(objT.RecName);
			((TextView) findViewById(R.id.editText_Phone2))
					.setText(objT.RecPhone);
			((TextView) findViewById(R.id.EditText_Count2))
					.setText(objT.ItemCount);
			((TextView) findViewById(R.id.EditText_PayType2)).setText(clsTask
					.GetPayType(objT.PayType));
			((TextView) findViewById(R.id.EditText_Money2))
					.setText(objT.PayAmount);
			Log.e("type",type);

		}

		if (type.equals("070"))// 拍簽收單
		{
			/* Panel的顯示隱藏 */
			LinearLayout LinearLayout_List = (LinearLayout) findViewById(R.id.LinearLayout_list);
			LinearLayout_List.setVisibility(View.GONE);
			ScrollView ScrollView_Step1 = (ScrollView) findViewById(R.id.ScrollView_Step1);
			ScrollView ScrollView_Step2 = (ScrollView) findViewById(R.id.ScrollView_Step2);
			LinearLayout LinearLayout_Pickup = (LinearLayout) findViewById(R.id.LinearLayout_Pickup);
			LinearLayout LinearLayout_pic = (LinearLayout) findViewById(R.id.LinearLayout_pic);

			ScrollView_Step1.setVisibility(View.GONE);
			ScrollView_Step2.setVisibility(View.GONE);
			LinearLayout_Pickup.setVisibility(View.GONE);
			LinearLayout_pic.setVisibility(View.GONE);

			ScrollView_Step2.setVisibility(View.VISIBLE);
			LinearLayout_Pickup.setVisibility(View.VISIBLE);

			LinearLayout_Pickup.setVisibility(View.GONE);
			LinearLayout_pic.setVisibility(View.GONE);

			CheckBox chk = (CheckBox) findViewById(R.id.chkCreateData);
			chk.setVisibility(View.GONE);
			LinearLayout_pic.setVisibility(View.VISIBLE);

			Button button_Send = (Button) findViewById(R.id.button_Send);
			button_Send.setText("略過");
			Log.e("type",type);
		}

		if (type.equals("08"))// 送達失敗
		{
			LinearLayout LinearLayout_List = (LinearLayout) findViewById(R.id.LinearLayout_list);
			LinearLayout_List.setVisibility(View.GONE);

			/* 設定主框 */
			ScrollView ScrollView_Step1 = (ScrollView) findViewById(R.id.ScrollView_Step1);

			ScrollView ScrollView_Step2 = (ScrollView) findViewById(R.id.ScrollView_Step2);

			ScrollView ScrollView_Delivery = (ScrollView) findViewById(R.id.ScrollView_Delivery);

			ScrollView_Step1.setVisibility(View.GONE);
			ScrollView_Step2.setVisibility(View.GONE);
			ScrollView_Delivery.setVisibility(View.VISIBLE);

			/* 設定第二層 */
			LinearLayout LinearLayout_Arrivals = (LinearLayout) findViewById(R.id.LinearLayout_Arrivals);
			LinearLayout LinearLayout_pic2 = (LinearLayout) findViewById(R.id.LinearLayout_pic2);
			LinearLayout LinearLayout_Reasion = (LinearLayout) findViewById(R.id.LinearLayout_Reasion);

			LinearLayout_Arrivals.setVisibility(View.GONE);
			LinearLayout_pic2.setVisibility(View.GONE);
			LinearLayout_Reasion.setVisibility(View.VISIBLE);

			/* 設定第三層 */
			LinearLayout LinearLayout_Type = (LinearLayout) findViewById(R.id.LinearLayout_Type);
			LinearLayout LinearLayout_OKNG = (LinearLayout) findViewById(R.id.LinearLayout_OKNG);
			LinearLayout LinearLayout_SetGoods = (LinearLayout) findViewById(R.id.LinearLayout_SetGoods);
			LinearLayout LinearLayout_SG = (LinearLayout) findViewById(R.id.LinearLayout_SG);

			LinearLayout_Type.setVisibility(View.GONE);
			LinearLayout_OKNG.setVisibility(View.GONE);
			LinearLayout_SetGoods.setVisibility(View.GONE);
			LinearLayout_SG.setVisibility(View.GONE);
			/* 取出資料 */
			objDB.openDB();
			clsTask objT = objDB.LoadTask(Application.strCaseID);

			if(Application.strCaseID!=null){
				Log.e("直送",Application.strCaseID);
			}else {
				Log.e("直送","null");
			}
			objDB.DBClose();

			((TextView) findViewById(R.id.EditText_CaseID3))
					.setText(objT.OrderID);

			Log.e("type",type);
		}

		if (type.equals("05"))// 回集貨站
		{

			LinearLayout LinearLayout_List = (LinearLayout) findViewById(R.id.LinearLayout_list);
			LinearLayout_List.setVisibility(View.GONE);
			/* 設定主框 */
			ScrollView ScrollView_Step1 = (ScrollView) findViewById(R.id.ScrollView_Step1);

			ScrollView ScrollView_Step2 = (ScrollView) findViewById(R.id.ScrollView_Step2);

			ScrollView ScrollView_Delivery = (ScrollView) findViewById(R.id.ScrollView_Delivery);

			ScrollView_Step1.setVisibility(View.GONE);
			ScrollView_Step2.setVisibility(View.GONE);
			ScrollView_Delivery.setVisibility(View.VISIBLE);

			/* 設定第二層 */
			LinearLayout LinearLayout_Arrivals = (LinearLayout) findViewById(R.id.LinearLayout_Arrivals);
			LinearLayout LinearLayout_pic2 = (LinearLayout) findViewById(R.id.LinearLayout_pic2);
			LinearLayout LinearLayout_Reasion = (LinearLayout) findViewById(R.id.LinearLayout_Reasion);

			LinearLayout_Arrivals.setVisibility(View.VISIBLE);
			LinearLayout_pic2.setVisibility(View.GONE);
			LinearLayout_Reasion.setVisibility(View.GONE);

			/* 設定第三層 */
			LinearLayout LinearLayout_Type = (LinearLayout) findViewById(R.id.LinearLayout_Type);
			LinearLayout LinearLayout_OKNG = (LinearLayout) findViewById(R.id.LinearLayout_OKNG);
			LinearLayout LinearLayout_SetGoods = (LinearLayout) findViewById(R.id.LinearLayout_SetGoods);
			LinearLayout LinearLayout_SG = (LinearLayout) findViewById(R.id.LinearLayout_SG);

			LinearLayout_Type.setVisibility(View.GONE);
			LinearLayout_OKNG.setVisibility(View.GONE);
			LinearLayout_SetGoods.setVisibility(View.VISIBLE);
			LinearLayout_SG.setVisibility(View.GONE);
			Log.e("type5",type);

			/* 取出資料 */
			objDB.openDB();
			clsTask objT = objDB.LoadTask(Application.strCaseID);
			objDB.DBClose();
			String RecAddress = setDecrypt(objT.RecAddress);
			String RecName = setDecrypt(objT.RecName);
			String RecPhone = setDecrypt(objT.RecPhone);
			String CustName = setDecrypt(objT.CustName);

			((TextView) findViewById(R.id.TextView_CarNo2))
					.setText(Application.strCar);
			((TextView) findViewById(R.id.TextView_DateTime2))
					.setText(objT.RequestDate);
			((TextView) findViewById(R.id.TextView_OrderID2))
					.setText(objT.OrderID);
			((TextView) findViewById(R.id.editText_Address2))
					.setText(RecAddress);
			((TextView) findViewById(R.id.EditText_CustomName2))
					.setText(RecName);
			((TextView) findViewById(R.id.editText_Phone2))
					.setText(RecPhone);
			((TextView) findViewById(R.id.EditText_Count2))
					.setText(objT.ItemCount);
			((TextView) findViewById(R.id.EditText_PayType2)).setText(clsTask
					.GetPayType(objT.PayType));
			((TextView) findViewById(R.id.EditText_Money2))
					.setText(objT.PayAmount);
			((TextView) findViewById(R.id.editText_SendMan))
					.setText(CustName);
			((TextView) findViewById(R.id.EditText_Size2))
					.setText(objT.Size);
			((TextView) findViewById(R.id.EditText_Cash2))
					.setText(objT.Cash);

		}

		if (type.equals("51"))// 續配 & 卸集貨
		{
			LinearLayout LinearLayout_List = (LinearLayout) findViewById(R.id.LinearLayout_list);
			LinearLayout_List.setVisibility(View.GONE);

			/* 設定主框 */
			ScrollView ScrollView_Step1 = (ScrollView) findViewById(R.id.ScrollView_Step1);

			ScrollView ScrollView_Step2 = (ScrollView) findViewById(R.id.ScrollView_Step2);

			ScrollView ScrollView_Delivery = (ScrollView) findViewById(R.id.ScrollView_Delivery);

			ScrollView_Step1.setVisibility(View.GONE);
			ScrollView_Step2.setVisibility(View.GONE);
			ScrollView_Delivery.setVisibility(View.VISIBLE);

			/* 設定第二層 */
			LinearLayout LinearLayout_Arrivals = (LinearLayout) findViewById(R.id.LinearLayout_Arrivals);
			LinearLayout LinearLayout_pic2 = (LinearLayout) findViewById(R.id.LinearLayout_pic2);
			LinearLayout LinearLayout_Reasion = (LinearLayout) findViewById(R.id.LinearLayout_Reasion);

			LinearLayout_Arrivals.setVisibility(View.VISIBLE);
			LinearLayout_pic2.setVisibility(View.GONE);
			LinearLayout_Reasion.setVisibility(View.GONE);

			/* 設定第三層 */
			LinearLayout LinearLayout_Type = (LinearLayout) findViewById(R.id.LinearLayout_Type);
			LinearLayout LinearLayout_OKNG = (LinearLayout) findViewById(R.id.LinearLayout_OKNG);
			LinearLayout LinearLayout_SetGoods = (LinearLayout) findViewById(R.id.LinearLayout_SetGoods);
			LinearLayout LinearLayout_SG = (LinearLayout) findViewById(R.id.LinearLayout_SG);

			LinearLayout_Type.setVisibility(View.GONE);
			LinearLayout_OKNG.setVisibility(View.GONE);
			LinearLayout_SetGoods.setVisibility(View.GONE);
			LinearLayout_SG.setVisibility(View.VISIBLE);
			Button button = (Button)findViewById(R.id.button_Next2);
			button.setVisibility(View.GONE);

			objDB.openDB();
			clsTask objT = objDB.LoadTask(Application.strCaseID);
			if(Application.strCaseID!=null){
				Log.e("直送",Application.strCaseID);
			}else {
				Log.e("直送","null");
			}
			String RecAddress = setDecrypt(objT.RecAddress);
			String RecName = setDecrypt(objT.RecName);
			String RecPhone = setDecrypt(objT.RecPhone);
			String CustName = setDecrypt(objT.CustName);
			objDB.DBClose();
			((TextView) findViewById(R.id.TextView_CarNo2))
					.setText(Application.strCar);
			((TextView) findViewById(R.id.TextView_DateTime2))
					.setText(objT.RequestDate);
			((TextView) findViewById(R.id.TextView_OrderID2))
					.setText(objT.OrderID);
			((TextView) findViewById(R.id.editText_Address2))
					.setText(RecAddress);
			((TextView) findViewById(R.id.EditText_CustomName2))
					.setText(RecName);
			((TextView) findViewById(R.id.editText_Phone2))
					.setText(RecPhone);
			((TextView) findViewById(R.id.EditText_Count2))
					.setText(objT.ItemCount);
			((TextView) findViewById(R.id.EditText_PayType2)).setText(clsTask
					.GetPayType(objT.PayType));
			((TextView) findViewById(R.id.EditText_Money2))
					.setText(objT.PayAmount);
			((TextView) findViewById(R.id.editText_SendMan))
					.setText(CustName);
			((TextView) findViewById(R.id.EditText_Size2))
					.setText(objT.Size);
			((TextView) findViewById(R.id.EditText_Cash2))
					.setText(objT.Cash);
			Log.e("type",type);
		}

		/* 回列表 */
		if (type.equals("71") || type.equals("81") || type.equals("09")) {
			LinearLayout LinearLayout_List = (LinearLayout) findViewById(R.id.LinearLayout_list);
			LinearLayout_List.setVisibility(View.VISIBLE);

			ScrollView ScrollView_Delivery = (ScrollView) findViewById(R.id.ScrollView_Delivery);
			ScrollView_Delivery.setVisibility(View.GONE);

			ScrollView ScrollView_Step1 = (ScrollView) findViewById(R.id.ScrollView_Step1);
			ScrollView_Step1.setVisibility(View.GONE);

			ScrollView ScrollView_Step2 = (ScrollView) findViewById(R.id.ScrollView_Step2);
			ScrollView_Step2.setVisibility(View.GONE);

			objDB.openDB();

			Cursor cursor = objDB.Load1("tblTask",
					"cStatus<>'71' and cStatus<>'81' and cStatus<>'2' and cStatus<>'3' and cStatus<>'09'", "cRequestDate desc", "");
			List rowitem = new ArrayList();
			listView = (ListView) findViewById(R.id.listView);

			if (cursor != null && cursor.getCount() > 0) {
				while (true) {
					String strOrderID = cursor.getString(cursor
							.getColumnIndex("cOrderID"));
					String strCaseID = cursor.getString(cursor
							.getColumnIndex("cCaseID"));
					String strStatus = cursor.getString(cursor
							.getColumnIndex("cStatus"));

					rowitem.add(new NewItem(strOrderID, strCaseID, clsTask
							.GetStatus(strStatus)));

					if (cursor.isLast())
						break;

					cursor.moveToNext();
				}
				cursor.close();
			}
			objDB.DBClose();

			ListViewAdpater adpater = new ListViewAdpater(context, rowitem);
			listView.setAdapter(adpater);
			Log.e("type",type);
		}

	}
	PictureCallback jpeg = new PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {

			Bitmap bmp1 = BitmapFactory.decodeByteArray(data, 0, data.length);

			Matrix m = new Matrix();
			m.setRotate(90, (float) bmp1.getWidth() / 2,
					(float) bmp1.getHeight() / 2);
			final Bitmap bmp = Bitmap.createBitmap(bmp1, 0, 0, bmp1.getWidth(),
					bmp1.getHeight(), m, true);
			// byte數组轉換成Bitmap
			imageView1.setImageBitmap(bmp);
			// imageView2.setImageBitmap(bmp);
			// 拍下圖片顯示在下面的ImageView裡
			FileOutputStream fop;
			try {
				File sdPath = null;
				if (Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED)) {
					sdPath = Environment.getExternalStorageDirectory();
				}
				fop = new FileOutputStream("/sdcard/dd.jpg");
				// 實例化FileOutputStream，參數是生成路徑
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, fop);
				// 壓缩bitmap寫進outputStream 參數：輸出格式 輸出質量 目標OutputStream
				// 格式可以為jpg,png,jpg不能存儲透明
				fop.close();
				System.out.println("拍照成功");
				// 關閉流
			} catch (FileNotFoundException e) {

				e.printStackTrace();
				System.out.println("FileNotFoundException");

			} catch (IOException e) {

				e.printStackTrace();
				System.out.println("IOException");
			}
			camera.startPreview();
			// 需要手動重新startPreview，否則停在拍下的瞬間
		}

	};

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {

	}
	/*
	public void surfaceCreated(SurfaceHolder holder) {

		//camera = Camera.open();
		try {

			 Camera.Parameters params = camera.getParameters();
			 params.setPreviewSize(300, 400); params.setPreviewFrameRate(4);
			 params.setPictureFormat(PixelFormat.JPEG);
			 params.set("jpeg-quality", 85); params.setPictureSize(300, 400);

			// camera.setParameters(params);
			// camera.setPreviewDisplay(surfaceHolder);
			// camera.setDisplayOrientation(90);
			// camera.setPreviewDisplay(sHolder);
			// camera.startPreview();
			try {
				camera.autoFocus(null);

			} catch (Exception e) {
			}

			Camera.Parameters parameters = camera.getParameters();
			parameters.setPictureFormat(PixelFormat.JPEG);
			parameters.setPreviewSize(600, 800);
			parameters.setPreviewFrameRate(4);
			parameters.set("jpeg-quality", 90);
			parameters.setPictureSize(600, 800);
			camera.setParameters(parameters);
			// 設置參數
			camera.setPreviewDisplay(surfaceHolder);
			// 鏡頭的方向和手機相差90度，所以要轉向
			camera.setDisplayOrientation(90);
			// camera.setParameters(parameters);

			// 鏡頭的方向和手機相差90度，所以要轉向
			// camera.setDisplayOrientation(90);
			// 攝影頭畫面顯示在Surface上
			camera.startPreview();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	*/

	public void surfaceDestroyed(SurfaceHolder holder) {

		System.out.println("surfaceDestroyed");
		// camera.stopPreview();
		// 關閉預覽
		// camera.release();
		//
	}

	// 自動對焦監聽式
	AutoFocusCallback afcb = new AutoFocusCallback() {

		public void onAutoFocus(boolean success, Camera camera) {

			if (success) {
				// 對焦成功才拍照
				//camera.takePicture(null, null, jpeg);
			}
		}

	};

	//拍照後的預覽畫面設定
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // ZXing回傳的內容
                String contents = data.getStringExtra("SCAN_RESULT");
				if(contents.length()== 11 || contents.length() ==8){
					final EditText editText = (EditText) findViewById(R.id.EditText_OrderID1);
					editText.setText(contents);
					objDB.openDB();
					objDB.UpdateTaskOrdID(contents,Application.strCaseID);
					objDB.close();
					Application.newstrObuID = contents;
				}else {
					Toast.makeText(this,"託運單號格式不符",Toast.LENGTH_SHORT).show();
					startActivityForResult(data, 1);//連續掃描
				}


            }
        }else if(requestCode==100){
            if(resultCode == Activity.RESULT_OK){
                showImg();
            }else {
                Toast.makeText(this, "沒有拍到照片", Toast.LENGTH_LONG).show();
                display();
            }
        }

	}
	//判斷網路有無訊號
	private boolean isConnected(){
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	void showImg() {
		int iw, ih, vw, vh;
		Log.e("showing", String.valueOf(imgUri));
		if(imgUri!=null){
			BitmapFactory.Options option = new BitmapFactory.Options(); //建立選項物件
			option.inJustDecodeBounds = true;      //設定選項：只讀取圖檔資訊而不載入圖檔
			BitmapFactory.decodeFile(imgUri.getPath(), option);  //讀取圖檔資訊存入 Option 中
			iw = option.outWidth;   //由 option 中讀出圖檔寬度
			ih = option.outHeight;  //由 option 中讀出圖檔高度
			vw = imv.getWidth();    //取得 ImageView 的寬度
			vh = imv.getHeight();   //取得 ImageView 的高度

			//int scaleFactor = 2 ;//Math.min(iw/vw, ih/vh); // 計算縮小比率
			ImageView imv;
			imv = (ImageView) findViewById(R.id.imageView);
			option.inJustDecodeBounds = false;  //關閉只載入圖檔資訊的選項
			option.inSampleSize = 2;  //設定縮小比例, 例如 2 則長寬都將縮小為原來的 1/2
			//取出DB的照片路徑
			objDB.openDB();
			clsTask objT = objDB.LoadTask(Application.strCaseID);
			Log.e("imgUri3",objT.RecPicture);
			objDB.close();
			bmp = BitmapFactory.decodeFile(Uri.parse(objT.RecPicture).getPath(), option); //載入圖檔
			imv.setImageBitmap(bmp);
		}else{
			//Toast.makeText(this, "沒有拍到照片", Toast.LENGTH_LONG).show();
			display();
		}

	}


    // 執行緒 - 執行PostUserInfo()方法
	class Post extends Thread {
		@Override
		public void run() {
			//上傳 照片
			try {
				Postfile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	private void Postfile() throws IOException {
		int typeInt = 0;
		if (type.equals("070")) {
			typeInt = 2;
		} else {
			typeInt = 1;
		}
		Application.IsCreateData = ((CheckBox) findViewById(R.id.chkCreateData))
				.isChecked();

		if (Application.IsCreateData == true) {
			KeyinFile = 1;
			Application.IsCreateData =false;
		} else {
			KeyinFile = 0;
			Application.IsCreateData =false;
		}

		Log.e("IsCreateData", String.valueOf(KeyinFile));
		objDB = new dbLocations(context);
		objDB.openDB();
		final clsTask objT = objDB.LoadTask(Application.strCaseID);
		objDB.DBClose();
		clsLoginInfo objL = new clsLoginInfo(context);
		objL.Load();


		String url = Application.ChtUrl + "Services/API/Motor_Dispatch/Upload_For\n" +
				"wardOrder.aspx";
		final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
		OkHttpClient client = new OkHttpClient();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		if (bmp != null) {
			setDialog();
			bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
			Log.e("bmp", String.valueOf(bmp));
			RequestBody requestBody = new MultipartBody.Builder()
					.setType(MultipartBody.FORM)
					.addFormDataPart("file", "test", RequestBody.create(MEDIA_TYPE_PNG, bos.toByteArray()))
					.addFormDataPart("Key", "7092a3c1-8ad6-48b5-b354-577378c282a5")
					.addFormDataPart("caseID", objT.CaseID)
					.addFormDataPart("KeyinFile", String.valueOf(KeyinFile))
					.addFormDataPart("FileType", "jpg")
					.addFormDataPart("Type", String.valueOf(typeInt))
					.build();

			final Request request = new Request.Builder().url(url)
					.post(requestBody).build();

			client.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {

				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					String string = response.body().string();
					Log.e("request", String.valueOf(request));
					Log.e("回傳訊息", string);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							myDialog.dismiss();
						}
					});
					//new clsHttpPostAPI().CallAPI(context, "API006");
					if (type.equals("070")) {
						// 簽收單
						objDB.openDB(); //狀態
						objDB.UpdateTaskStatus("71", objT.CaseID);
						objDB.DBClose();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								bmp = null;
								ImageView imv;
								imv = (ImageView) findViewById(R.id.imageView);
								imv.setImageBitmap(bmp);
								myDialog.dismiss();
								type = "71";
								display();
							}
						});
					} else {

						//託運單
						//new clsHttpPostAPI().CallAPI(context, "API006");
						objDB.openDB(); //狀態
						objDB.UpdateTaskStatus("41", objT.CaseID);
						objDB.DBClose();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								bmp = null;
								ImageView imv;
								imv = (ImageView) findViewById(R.id.imageView);
								imv.setImageBitmap(bmp);
								myDialog.dismiss();
								type = "41";
								display();

							}
						});
					}

				}
			});


		} else {
			if(type.equals("070")){
				objDB.openDB(); //狀態
				objDB.UpdateTaskStatus("71", objT.CaseID);
				objDB.DBClose();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						bmp = null;
						ImageView imv;
						imv = (ImageView) findViewById(R.id.imageView);
						imv.setImageBitmap(bmp);
						type = "71";
						display();
					}
				});
			} else {
				Toast.makeText(DataListFrg.this, "請確認是否有拍照", Toast.LENGTH_SHORT).show();
			}


		}
	}
	}

	class Post2 extends Thread{
		@Override
		public void run() {
			//上傳 照片
			Postfile2();
		}

		private void Postfile2() {
			int typeInt = 0;
			if (type.equals("070")) {
				typeInt = 2;
			} else {
				typeInt = 1;
			}




			objDB = new dbLocations(context);
			objDB.openDB();
			final clsTask objT = objDB.LoadTask(Application.strCaseID);
			//Log.e("IsCreateData",objT.IsCreateData);

			objDB.DBClose();
			clsLoginInfo objL = new clsLoginInfo(context);
			objL.Load();


			String url = Application.ChtUrl + "Services/API/Motor_Dispatch/Upload_For\n" +
					"wardOrder.aspx";
			final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
			OkHttpClient client = new OkHttpClient();
			Log.e("bmp", String.valueOf(bmp));
			//把照片路徑轉成bmp
			showImg();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			if (bmp != null) {

				bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
				RequestBody requestBody = new MultipartBody.Builder()
						.setType(MultipartBody.FORM)
						.addFormDataPart("file", "test", RequestBody.create(MEDIA_TYPE_PNG, bos.toByteArray()))
						.addFormDataPart("Key", "7092a3c1-8ad6-48b5-b354-577378c282a5")
						.addFormDataPart("caseID", objT.CaseID)
						.addFormDataPart("KeyinFile", String.valueOf(objT.IsCreateData))
						.addFormDataPart("FileType", "jpg")
						.addFormDataPart("Type", String.valueOf(typeInt))
						.build();

				final Request request = new Request.Builder().url(url)
						.post(requestBody).build();

				client.newCall(request).enqueue(new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {

					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						String json = response.body().string();
						Log.e("照片回傳", json);
						//刪除照片
						bmp = null;
						ImageView imv;
						imv = (ImageView) findViewById(R.id.imageView);
						imv.setImageBitmap(bmp);

						//協助修改 清除
						CheckBox c1 = (CheckBox)findViewById(R.id.chkCreateData);
						c1.setChecked(false);//checkbox狀態
					}
				});
			}
		}
	}

	//取件完成前 掃描
	public void onScan (View v){
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() == 0) {
			// 未安裝
			Toast.makeText(this, "請至 Play 商店安裝 ZXing 條碼掃描器", Toast.LENGTH_LONG).show();
		} else {
			// SCAN_MODE, 可判別所有支援的條碼
			// QR_CODE_MODE, 只判別 QRCode
			// PRODUCT_MODE, UPC and EAN 碼
			// ONE_D_MODE, 1 維條碼
			intent.putExtra("SCAN_MODE", "SCAN_MODE");

			// 呼叫ZXing Scanner，完成動作後回傳 1 給 onActivityResult 的 requestCode 參數
			startActivityForResult(intent, 1);
		}

	}
	private void time() {
		Calendar mCal = Calendar.getInstance();
		String dateformat = "HH:mm";
		SimpleDateFormat df = new SimpleDateFormat(dateformat);
		today = df.format(mCal.getTime());

	}
	private void setDialog(){
		myDialog = new ProgressDialog(DataListFrg.this);
		myDialog.setTitle("載入中");
		myDialog.setMessage("載入資訊中，請稍後！");
		myDialog.setButton("關閉", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				myDialog.dismiss();
			}

		});
		myDialog.setCancelable(false);
		myDialog.show();
	}

	//加密
	private String setEncryp (String EncrypString){

		SetAES AES = new SetAES();
		EncrypMD5 encrypMD5 = new EncrypMD5();
		EncrypSHA encrypSHA = new EncrypSHA();
		try {
			byte[] TextByte = AES.EncryptAES(encrypMD5.eccrypt(),encrypSHA.eccrypt(),EncrypString.getBytes());
			EncrypString = Base64.encodeToString(TextByte,Base64.DEFAULT);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return EncrypString;
	}
	//解密
	private String setDecrypt (String DecryptString){
		//Log.e("DecryptString",DecryptString);
        if(DecryptString!=null && !DecryptString.equals("")){
            SetAES AES = new SetAES();
            EncrypMD5 encrypMD5 = new EncrypMD5();
            EncrypSHA encrypSHA = new EncrypSHA();
            try {
                byte[] TextByte2 = AES.DecryptAES(encrypMD5.eccrypt(),encrypSHA.eccrypt(), Base64.decode(DecryptString.getBytes(),Base64.DEFAULT));
				//Log.e("DecryptString2",DecryptString);
				DecryptString = new String(TextByte2);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();

            }
            return DecryptString;
        }
        return DecryptString;
	}

	public void onStop() {
		super.onStop();
		//GCMIntentService.handlerGCM = null;
		//clsHttpPostAPI.handlerTask = null;
		//ListViewAdpater.handler = null;
	}
	class PostFail extends Thread{
		@Override
		public void run() {
			PostFailInfo();
		}

		private void PostFailInfo() {
			final OkHttpClient client = new OkHttpClient();
			String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Get_DispatchFailReason.aspx?key="+Application.strKey+"&Company="+Application.Company;
			Request request = new Request.Builder()
					.url(strUrl)
					.build();
			Call call = client.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {

				}

				@RequiresApi(api = Build.VERSION_CODES.KITKAT)
				@Override
				public void onResponse(Call call, Response response) throws IOException {
					String json = response.body().string();
					//Log.e("回傳", json);
					JSONObject json2 = null;
					List<ClsDropDownItem> objList = new ArrayList<>();
					ArrayAdapter<ClsDropDownItem> Adapter2;
					try {
						json2 = new JSONObject(json);
						JSONArray objArray = json2
								.getJSONArray("DataContents");

						//Log.e("json2", String.valueOf(objArray));
						JSONObject jsonItem1 = null;
						for(int i = 0 ; i<objArray.length();i++){
							jsonItem1 = objArray.getJSONObject(i);
							objList.add(new ClsDropDownItem(jsonItem1
									.getString("FailReasonID"), jsonItem1
									.getString("Reason")));
							//Log.e("objArray", String.valueOf(objArray.getJSONObject(i)));

							Adapter2 = new ArrayAdapter<ClsDropDownItem>(
									context, R.layout.myspinner, objList);
							final ArrayAdapter<ClsDropDownItem> finalAdapter = Adapter2;
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Spinner Spinner_Reasion = (Spinner) findViewById(R.id.Spinner_Reasion);
									Spinner_Reasion.setAdapter(finalAdapter);

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
	class PostStation extends Thread{
		@Override
		public void run() {
			PostFailInfo();
		}

		private void PostFailInfo() {
			final OkHttpClient client = new OkHttpClient();
			final String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Get_StationList.aspx?Status=5&obuID="+"&key="+Application.strKey+"&Company="+Application.Company;
			Request request = new Request.Builder()
					.url(strUrl)
					.build();
			Call call = client.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {

				}

				@RequiresApi(api = Build.VERSION_CODES.KITKAT)
				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Log.e("URL站",strUrl);
					String json = response.body().string();
					Log.e("回傳站", json);
					JSONObject json2 = null;
					final Spinner Spinner_SetGoods = (Spinner) findViewById(R.id.Spinner_SetGoods);
					final List<ClsDropDownStation> objList = new ArrayList<ClsDropDownStation>();
					try {
						json2 = new JSONObject(json);
						JSONArray objArray = json2
								.getJSONArray("DataContents");
						JSONObject jsonItem2 = null;

						for(int i = 0 ; i<objArray.length();i++){
							jsonItem2 = objArray.getJSONObject(i);
							objList.add(new ClsDropDownStation(
									jsonItem2.getString("StationID"),
									jsonItem2.getString("StationName"),
									"1"));//集貨站TYPE寫死
						}


						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								final ArrayAdapter<ClsDropDownStation> Adapter = new ArrayAdapter<ClsDropDownStation>(
										context, R.layout.myspinner, objList);
								Spinner Spinner_SetGoods = (Spinner) findViewById(R.id.Spinner_SetGoods);
								Spinner_SetGoods.setAdapter(Adapter);
							}
						});


					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			});
		}

	}

}
