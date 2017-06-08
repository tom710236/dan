package com.example.motoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DataListFrg extends Activity implements SurfaceHolder.Callback {

	MainActivity objActivity;
	ListView listView;
	View rootView;
	Context context;
	Handler handlerGCM;
	Handler handlerTask;
	Handler handlerListView;
	Handler handlerThread;
	clsLoginInfo objLoginInfo;
	
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

	/*
	 * 01列表 02接單 03前往取件 04取件完成，拍照上傳 05回站 06直送 07已送達，拍照上傳 08送達失敗，失敗原因
	 */
	public static String type = "01";
	private dbLocations objDB;
	SurfaceHolder surfaceHolder;
	SurfaceView surfaceView1;
	ImageView imageView1;

	Camera camera;
	ProgressDialog myDialog;
	PPLZPrinter printer;

	GCMActivity gcm = new GCMActivity();
	public String caseID = gcm.strCaseID;
	public static String regID,Account,carID;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("=====>", "GoogleFragment onCreateView");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.frg_waiting);
		SysApplication.getInstance().addActivity(this);
		context = DataListFrg.this;

		objLoginInfo = new clsLoginInfo(context);
		objLoginInfo.Load();
		
		objDB = new dbLocations(context);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle!=null)
		{
			type=bundle.getString("type");
			display();
		}else
		{
			
			setListView();
		}
		/*printer = new PPLZPrinter();
		((PPLZPrinter) this.printer).initPrinter(this);*/

		

		/* 失敗原因 */
		setDropDownListReason();

		/*鍵盤事件*/
		setKeyListener();
		
		// 0代表橫向、1代表縱向
		//this.setRequestedOrientation(1);
		// 設為横向顯示。因為攝影頭會自動翻轉90度，所以如果不横向顯示，看到的畫面就是翻轉的。

		surfaceView1 = (SurfaceView) findViewById(R.id.surfaceView1);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		surfaceHolder = surfaceView1.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);

		/* 接單 */
		Button Button_Get = (Button) findViewById(R.id.button_Get);
		Button_Get.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				/*
				 * 呼叫API 接單
				 */
				// 顯示Progress對話方塊
				myDialog = ProgressDialog.show(context, "載入中", "資料讀取中，請稍後！", false);
				new clsHttpPostAPI().CallAPI(context, "API002");

			}
		});
		/* 拒絕 */
		Button Button_Reject = (Button) findViewById(R.id.button_Reject);
		Button_Reject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new clsHttpPostAPI().CallAPI(context, "API003");
				finish();
			}
		});

		/* 前往取件 */
		Button button_Go = (Button) findViewById(R.id.button_Go);
		button_Go.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				/* 更新預計時間欄位 */
				objDB.openDB();
				objDB.UpdateTaskRecTime(
						((TextView) findViewById(R.id.EditText_Receive))
								.getText().toString(), Application.strCaseID);

				objDB.DBClose();

				/*
				 * 呼叫API 前往取件
				 */
				new clsHttpPostAPI().CallAPI(context, "API004");
				EditText_OrderID1.requestFocus();
				
			}
		});

		/* 取件完成 */
		Button button_Sucess = (Button) findViewById(R.id.button_Sucess);
		button_Sucess.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new clsHttpPostAPI().CallAPI(context, "API005");
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
		
		/* 下一步 */
		Button button_Next1 = (Button) findViewById(R.id.button_Next1);
		button_Next1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				type="040";
				display();
			}
		});

		/* 拍照-託運單 */
		Button button_takePic1 = (Button) findViewById(R.id.button_takePic);
		button_takePic1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 自動對焦
				camera.autoFocus(afcb);
			}
		});

		/* 託運單拍照後送出 */
		Button button_Send = (Button) findViewById(R.id.button_Send);
		button_Send.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {
				Post post = new Post();
				post.start();


				/*
				 * 上傳照片及是否協助建檔


				if (type.equals("070")) {
					// 簽收單
					new clsHttpPostAPI().CallAPI(context, "API011");
				} else {
					Application.IsCreateData = ((CheckBox) findViewById(R.id.chkCreateData))
							.isChecked();
					objDB.openDB();

					new clsHttpPostAPI().CallAPI(context, "API006");
					imageView1.setImageDrawable(null);
					// imageView1.setImageResource(0);
				}
				 */
			}

		});

		/* 直送 */
		Button button_Online = (Button) findViewById(R.id.button_Online);
		button_Online.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new clsHttpPostAPI().CallAPI(context, "API007");
			}
		});

		/* 回站 */
		Button button_BackStation = (Button) findViewById(R.id.button_BackStation);
		button_BackStation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new clsHttpPostAPI().CallAPI(context, "API012");

				// 取得站所資料
			}
		});

		/* 選擇站所 */
		Button button_SetGoods2 = (Button) findViewById(R.id.button_SetGoods);
		button_SetGoods2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				/* 顯示結果須等post回來的資訊決定，測試先寫 */
				Spinner Spinner_SetGoods = (Spinner) findViewById(R.id.Spinner_SetGoods);
				objDB.openDB();
				objDB.UpdateTaskStationID(((ClsDropDownStation) Spinner_SetGoods
						.getSelectedItem()).GetID(), Application.strCaseID);
				objDB.DBClose();

				new clsHttpPostAPI().CallAPI(context, "API010",((ClsDropDownStation) Spinner_SetGoods
						.getSelectedItem()).GetStationType());

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
		
		/* 下一步 */
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
				new clsHttpPostAPI().CallAPI(context, "API008");
			}
		});

		/* 送達失敗 */
		Button button_NG = (Button) findViewById(R.id.button_NG);
		button_NG.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				type = "08";
				display();
			}
		});

		/* 送達失敗，原因 */
		Button button_Save2 = (Button) findViewById(R.id.button_Save);
		button_Save2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Spinner Reason = (Spinner) findViewById(R.id.Spinner_Reasion);

				objDB.openDB();
				objDB.UpdateTaskFailReasonID(
						((ClsDropDownItem) Reason.getSelectedItem()).GetID(),
						Application.strCaseID);
				objDB.UpdateTaskStatus("81", Application.strCaseID);
				objDB.DBClose();

				new clsHttpPostAPI().CallAPI(context, "API009");
			}
		});

		/* 續配 */
		Button button_Resend = (Button) findViewById(R.id.button_Resend);
		button_Resend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				new clsHttpPostAPI().CallAPI(context, "API017");
			}
		});
		
		/* 卸集 */
		Button button_Discharge = (Button) findViewById(R.id.button_Discharge);
		button_Discharge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				new clsHttpPostAPI().CallAPI(context, "API018");
			}
		});

		handlerGCM = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bundle json = (Bundle) msg.obj;
				Log.e("json", String.valueOf(json));
				try {
					String status = json.getString("status");
					Log.e("status",status);
					myDialog.dismiss();
					if (status.equals("0")) {
						Application.objForm = json;
						// cCaseID,cOrderID,cCustAddress,cDistance,cSize,cItemCount,cRequestDate,cType
						objDB.openDB();
						objDB.InsertTask(new Object[] {
								json.getString("caseID"),
								json.getString("orderID"),
								json.getString("customer_address"),
								json.getString("distance"),
								json.getString("size"),
								json.getString("item_count"),
								json.getString("request_time"), "0" });
						objDB.DBClose();
						Application.strCaseID = json.getString("caseID");
						Application.strObuID = json.getString("obuid");
						Log.e("strCaseID",Application.strCaseID);
						Log.e("strCaseID",json.getString("caseID"));
						type = "02";
						display();
					}
					if (status.equals("1")) {
						Application.strCaseID = json.getString("caseID");
						Application.strObuID = json.getString("obuid");
						Application.objFormInfo = json;
						Log.e("strCaseID2",Application.strCaseID);
						Log.e("strCaseID2",json.getString("caseID"));
						type = "21";
						objDB.openDB();
						objDB.UpdateTask(json.getString("customer_address"),
								json.getString("customer_name"),
								json.getString("customer_phoneNo"),
								json.getString("recipient_name"),
								json.getString("recipient_address"),
								json.getString("recipient_phoneNo"),
								json.getString("pay_type"),
								json.getString("pay_amount"), "21",
								json.getString("caseID"));

						objDB.DBClose();
						display();
					}
					if (status.equals("2")) {
						// type = 1;
						// changeTab(0);
						clsDialog.Show(context, "提示訊息", "接單失敗");
						// dialog = ProgressDialog.show(MainActivity.this,
						// "接單失敗", "等待詢車中...", true);
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

				try {
					String Result = json.getString("Result");

					if (Result.equals("1")) {
						String strType = "";
						switch (json.getString("Type")) {
						case "1":
							strType = "派遣接單回覆";
							type = "03";
							//display();
							break;
						case "2":
							strType = "派遣拒絕回覆";
							type = "01";
							break;
						case "3":
							strType = "前往取件回覆";
							/* 顯示結果須等post回來的資訊決定，測試先寫 */
							objDB.openDB();
							objDB.UpdateTaskStatus("03", Application.strCaseID);
							objDB.DBClose();
							type = "03";
							display();
							break;
						case "4":
							strType = "取件完成回覆";
							objDB.openDB();
							objDB.UpdateTask("", "", "", EditText_CustomName.getText().toString(), editText_Address1.getText().toString(), editText_Phone.getText().toString(), ((ClsDropDownItem)Spinner_PayType.getSelectedItem()).GetID(), EditText_Money.getText().toString(), "04", Application.strCaseID);
							//呼叫API
							clsTask.postToAS400(context, EditText_OrderID1.getText().toString(), "01");

							objDB.DBClose();

							type = "04";
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
							type = "06";
							display();
							break;
						case "7":
							strType = "已送達";
							objDB.openDB();
							objDB.UpdateTaskStatus("07", Application.strCaseID);
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
							type = "07";
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
							
							Spinner Reason = (Spinner) findViewById(R.id.Spinner_Reasion);
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
								objList.add(new ClsDropDownStation(jsonItem
										.getString("StationID"), jsonItem
										.getString("StationName"), jsonItem
										.getString("StationType")));
							}

							ArrayAdapter<ClsDropDownStation> Adapter = new ArrayAdapter<ClsDropDownStation>(
									context, R.layout.myspinner, objList);

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
							type = "51";
							display();
							break;

						case "12":
							strType = "取得失敗原因";
							/* 顯示結果須等post回來的資訊決定，測試先寫 */
							Spinner Spinner_Reasion = (Spinner) findViewById(R.id.Spinner_Reasion);
							List<ClsDropDownItem> objList1 = new ArrayList<ClsDropDownItem>();
							JSONArray objArray1 = json
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
							Spinner_Reasion.setAdapter(Adapter1);

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

		button_DoList = (Button) findViewById(R.id.button_DoList);
		button_DoList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		button_IO = (Button) findViewById(R.id.button_IO);
		button_IO.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DataListFrg.this, InOutFrg.class);
				startActivity(intent);

			}
		});

		button_GT = (Button) findViewById(R.id.button_GT);
		button_GT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DataListFrg.this, GetTaskFrg.class);
				startActivity(intent);

			}
		});

		button_DoneList = (Button) findViewById(R.id.button_DoneList);
		button_DoneList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DataListFrg.this,
						HistoryFragment.class);
				startActivity(intent);
			}
		});

		Button button_Logout = (Button) findViewById(R.id.Button_Logout);
		button_Logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new clsHttpPostAPI().CallAPI(context, "API014");
				//Intent intent = new Intent(DataListFrg.this, Login.class);
			 	//startActivity(intent);
				Intent it = new Intent(DataListFrg.this,Delay.class);
				stopService(it);
			}
		});
		
		Button Button_Status = (Button)findViewById(R.id.Button_Status);
		Button_Status.setText(objLoginInfo.GetStatus());
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

							rowitem.add(new NewItem(strOrderID, strCaseID,
									clsTask.GetStatus(strStatus)));

							if (cursor.isLast())
								break;

							cursor.moveToNext();
						}
						cursor.close();
					}
					objDB.DBClose();

					ListViewAdpater adpater = new ListViewAdpater(context,
							rowitem);
					listView.setAdapter(adpater);
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
			((TextView) findViewById(R.id.EditText_Size)).setText(objT.Size);

			objDB.DBClose();
			objDB.close();

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
			((TextView) findViewById(R.id.TextView_CarNo))
					.setText(Application.strCar);
			((TextView) findViewById(R.id.TextView_DateTime))
					.setText(objT.RequestDate);
			((TextView) findViewById(R.id.TextView_OrderID))
					.setText(objT.OrderID);
			((TextView) findViewById(R.id.editText_Address))
					.setText(objT.CustAddress);
			((TextView) findViewById(R.id.EditText_Size)).setText(objT.Size);
			((TextView) findViewById(R.id.EditText_Receive)).setText("");

			((TextView) findViewById(R.id.EditText_Receive)).requestFocus();
			
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
			
			for (int j = 0; j < Spinner_PayType.getAdapter().getCount(); j++) {
				if(((ClsDropDownItem)Spinner_PayType.getAdapter().getItem(j)).GetID().equals(objT.PayType))
				{
					Spinner_PayType.setSelection(j);
				}
			}
			
			((EditText) findViewById(R.id.EditText_Money))
					.setText(objT.PayAmount);
			
			((EditText) findViewById(R.id.EditText_OrderID1)).requestFocus();

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
			
			for (int j = 0; j < Spinner_PayType.getAdapter().getCount(); j++) {
				if(((ClsDropDownItem)Spinner_PayType.getAdapter().getItem(j)).GetID().equals(objT.PayType))
				{
					Spinner_PayType.setSelection(j);
				}
			}
			
			((EditText) findViewById(R.id.EditText_Money))
					.setText(objT.PayAmount);
			
			((EditText) findViewById(R.id.EditText_OrderID1)).requestFocus();

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

		}

		if (type.equals("41"))// 直送或回站畫面
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
		}

		if (type.equals("05"))// 回集貨站
		{
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
		}
	}

	public void onStart() {
		super.onStart();
		GCMIntentService.handlerGCM = handlerGCM;
		clsHttpPostAPI.handlerTask = handlerTask;
		ListViewAdpater.handler = handlerListView;
	}

	public void onStop() {
		super.onStop();
		GCMIntentService.handlerGCM = null;
		clsHttpPostAPI.handlerTask = null;
		ListViewAdpater.handler = null;
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

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {

		//camera = Camera.open();
		try {
			/*
			 * Camera.Parameters params = camera.getParameters();
			 * params.setPreviewSize(300, 400); params.setPreviewFrameRate(4);
			 * params.setPictureFormat(PixelFormat.JPEG);
			 * params.set("jpeg-quality", 85); params.setPictureSize(300, 400);
			 */
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

	public void surfaceDestroyed(SurfaceHolder holder) {

		System.out.println("surfaceDestroyed");
//	    camera.stopPreview();
		// 關閉預覽
		// 	camera.release();
		//
	}

	// 自動對焦監聽式
	AutoFocusCallback afcb = new AutoFocusCallback() {

		public void onAutoFocus(boolean success, Camera camera) {

			if (success) {
				// 對焦成功才拍照
				camera.takePicture(null, null, jpeg);
			}
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
			new AlertDialog.Builder(DataListFrg.this)
					.setTitle("確認視窗")
					.setMessage("確定要結束應用程式嗎?")
					.setIcon(R.drawable.ic_launcher)
					.setPositiveButton("確定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									new clsHttpPostAPI().CallAPI(context,
											"API014");
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
	class Post extends Thread{
		@Override
		public void run() {
			String url=Application.ChtUrl+"/Services/API/Motor_Dispatch/Upload_ForwardOrder.aspx";
			String file2 = "DCIM/100MEDIA/IMAG0111.jpg";
			String data = null;
			File file = new File("DCIM/100MEDIA/IMAG0111.jpg");
			if (file != null){
				Log.e("Yes", String.valueOf(file));
			}else{
				Log.e("NO","NO");
			}
			//upload(url,file);
			//upload2(url,file);
			//upload3(url,file);
			//postAsynFile(url,file2);
			UploadService uploadService = new UploadService();
			try {
				uploadService.uploadImage(file,data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void upload(final String url, File file) throws IOException {
		Log.e("caseid",caseID);
		OkHttpClient client = new OkHttpClient();
		RequestBody formBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("data", file.getName(),
						RequestBody.create(MediaType.parse("jpg/git"), file))
				.addFormDataPart("other_field", "other_field_value")
				.build();

		RequestBody body = new FormBody.Builder()
				.add("key", Application.strKey)
				.add("caseID",caseID)
				.add("Type","1")
				.add("FileType","jpg")
				.add("KeyinFile","1")
				.build();
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				//.post(formBody)
				.build();
		/*
		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) {
			throw new IOException("Unexpected code " + response);
		}
		*/

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String json = response.body().string();
				Log.e("caseid",caseID);
				Log.e("OkHttp", response.toString());
				Log.e("OkHttp2", json);
			}
		});

	}
	public void upload2(String url, File file) throws IOException {
		OkHttpClient client = new OkHttpClient();
		final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
		RequestBody formBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("file", "date", RequestBody.create(MEDIA_TYPE_PNG, file))

				.build();
		Request request = new Request.Builder()
				.url(url)
				.post(formBody)
				.build();
		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) {
			throw new IOException("Unexpected code " + response);
		}

	}
	public void upload3(String url, File file) throws IOException {
		OkHttpClient client = new OkHttpClient();
		final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
		RequestBody formBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("file", "file", RequestBody.create(MEDIA_TYPE_PNG, file))
				.build();
		RequestBody body = new FormBody.Builder()
				.add("key", Application.strKey)
				.add("caseID",caseID)
				.add("Type","1")
				.add("FileType","jpg")
				.add("KeyinFile","1")
				.build();
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.post(formBody)
				.build();
		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) {
			throw new IOException("Unexpected code " + response);
		}

	}
	private void postAsynFile(String url,String file2) {
		final MediaType MEDIA_TYPE_MARKDOWN
				= MediaType.parse("jpg/x-markdown; charset=utf-8");

		OkHttpClient client = new OkHttpClient();
		File file = new File(file2);
		Request request = new Request.Builder()
				.url(url)
				.post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				Log.i("wangshu",response.body().string());
				String json = response.body().string();
				Log.e("caseid",caseID);
				Log.e("OkHttp", response.toString());
				Log.e("OkHttp2", json);
			}
		});
	}
	public class UploadService {

		final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");

		public void uploadImage(File image, String imageName) throws IOException {

			OkHttpClient client = new OkHttpClient();

			RequestBody requestBody = new MultipartBody
					.Builder()
					.setType(MultipartBody.FORM)
					.addFormDataPart("file", imageName, RequestBody.create(MEDIA_TYPE_PNG, image))
					.addFormDataPart("key", Application.strKey)
					.addFormDataPart("caseID",caseID)
					.addFormDataPart("Type","1")
					.addFormDataPart("FileType","jpg")
					.addFormDataPart("KeyinFile","1")
					.build();

			Request request = new Request.Builder()
					.header("Authorization", "Client-ID " + "...")
					.url(Application.ChtUrl+"/Services/API/Motor_Dispatch/Upload_ForwardOrder.aspx")
					.post(requestBody)
					.build();

			client.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {

				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					String json = response.body().string();
					Log.e("caseid",caseID);
					Log.e("OkHttp", response.toString());
					Log.e("OkHttp2", json);
				}
			});


		}

	}


}
