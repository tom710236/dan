package com.example.motoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetTaskFrg extends Activity implements GestureDetector.OnGestureListener{

	Context context;
	View view;
	Handler handlerTask;
	Handler handlerListView;
	EditText objEdit;
	private dbLocations objDB;
	clsLoginInfo objLoginInfo;
	Handler handlerGCM;
	Button button_DoList;
	Button button_IO;
	Button button_GT;
	Button button_DoneList;
	GestureDetector detector;
	ArrayList Value;
	String ObuID;
    SQLiteDatabase db ;

	private final int REQUEST_CODE = 0xa1;
	private boolean isSingleSacn = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("=====>", "FacebookFragment onCreateView");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.frg_gettask);
		//view = inflater.inflate(R.layout.frg_gettask, container, false);



		detector = new GestureDetector(this,this);
		detector.setIsLongpressEnabled(true);


		ScrollView ScrollViewT = (ScrollView) findViewById(R.id.ScrollViewT);
		//滑動設定
		ScrollViewT.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try{
					return detector.onTouchEvent(event);
				}catch (Exception e){
					return false;
				}
			}
		});

		context = GetTaskFrg.this;

		objLoginInfo = new clsLoginInfo(context);
		objLoginInfo.Load();
		
		SysApplication.getInstance().addActivity(this);
		objEdit = (EditText)findViewById(R.id.TextView_OrderNo3);
		//objEdit.setText("40000200023");
		objEdit.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				/*
				if (event.getAction() == event.ACTION_DOWN) {

					if(objEdit.getText().toString().length() == 8 || objEdit.getText().toString().length() ==11){
						new clsHttpPostAPI().CallAPI(context,"API013",objEdit.getText().toString());
					}else {
						clsDialog.Show(context, "提示", "請輸入正確託運編號格式！");
					}
					return false;

				}*/
				if (event.getAction() == event.ACTION_DOWN) {
					EditText objEdit = (EditText) v;
					if (keyCode == 23 || keyCode == 66) {
						if (objEdit.getText().toString().length()==10 || objEdit.getText().toString().length()==7) {
							clsDialog.Show(context, "提示", "請輸入正確託運單號格式！");
							return true;
						}

						/**
						 * 呼叫API
						 * */
						if(checkOdb(objEdit.getText().toString())==true || checkOdb2(objEdit.getText().toString())==true){
							new clsHttpPostAPI().CallAPI(context,"API013",objEdit.getText().toString());
						}else {
							Toast.makeText(GetTaskFrg.this, "託運單號"+objEdit.getText().toString()+"已在清單中", Toast.LENGTH_SHORT).show();
						}

					}
					return false;

				}

				if(!objEdit.getText().toString().equals("") && objEdit.getText().toString()!=null){
					final TextView textView = (TextView)findViewById(R.id.textView14);
					textView.setText(objEdit.getText().toString());

				}else {
					final TextView textView = (TextView)findViewById(R.id.textView14);
					textView.setText(String.valueOf(CaptureActivity.num));

				}

				CaptureActivity.num = null;
				return true;
			}
		});


		//上排按鈕
		button_DoList = (Button)findViewById(R.id.button_DoList);
		button_DoList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Intent intent2 = new Intent(GetTaskFrg.this, HistoryFragment.class);
				//startActivity(intent2);
                //GetTaskFrg.this.finish();
				Intent intent = new Intent(GetTaskFrg.this, DataListFrg.class);
			    startActivity(intent);
				GetTaskFrg.this.finish();

			}
		});
		
		button_IO = (Button)findViewById(R.id.button_IO);
		button_IO.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Intent intent2 = new Intent(GetTaskFrg.this, HistoryFragment.class);
				//startActivity(intent2);
				Intent intent = new Intent(GetTaskFrg.this, InOutFrg.class);
			    startActivity(intent);
				GetTaskFrg.this.finish();

			    
			}
		});
		
		button_GT = (Button)findViewById(R.id.button_GT);
		button_GT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Intent intent2 = new Intent(GetTaskFrg.this, HistoryFragment.class);
				//startActivity(intent2);
				Intent intent = new Intent(GetTaskFrg.this, GetTaskFrg.class);
				startActivity(intent);
				GetTaskFrg.this.finish();

			}
		});
		
		button_DoneList = (Button)findViewById(R.id.button_DoneList);
		button_DoneList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(GetTaskFrg.this, HistoryFragment.class);
			    startActivity(intent);
				//GetTaskFrg.this.finish();
				finish();
			}
		});
		
		 button_GT.setBackgroundResource(R.drawable.menu03b);
		 
		
		Button button_Scran = (Button)findViewById(R.id.button_Scran);
		button_Scran.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(!objEdit.getText().toString().trim().equals("")) {
					/*
					 * 呼叫API 接單
					 * */

					if(checkOdb(objEdit.getText().toString())==true || checkOdb2(objEdit.getText().toString())==true){
						new clsHttpPostAPI().CallAPI(context,"API013",objEdit.getText().toString());
					}else {
						Toast.makeText(GetTaskFrg.this, "託運單號"+objEdit.getText().toString()+"已在清單中", Toast.LENGTH_SHORT).show();
					}

				}else {
					clsDialog.Show(context, "提示", "請輸入託運編號！");

				}

			}
		});
		// 登出 關閉SERVICE
		Button button_Logout = (Button)findViewById(R.id.Button_Logout);
		button_Logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//objLoginInfo.Update("03");
				Intent it = new Intent(GetTaskFrg.this,Delay.class);
				stopService(it);
				new clsHttpPostAPI().CallAPI(context, "API014");
				GetTaskFrg.this.finish();
			}
		});
		
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
		//員工卡號姓名設定
		clsLoginInfo objL = new clsLoginInfo(context);
		objL.Load();
		TextView tID = (TextView)findViewById(R.id.TextID);
		TextView tName = (TextView)findViewById(R.id.TextName);
		tID.setText(objL.UserID);
		tName.setText(objL.UserName);



		handlerTask = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				JSONObject json = (JSONObject) msg.obj;

				try {
					String status = json.getString("Result");
					Log.e("轉單status",status);
					if (status.equals("1")) {
						//cCaseID,cOrderID,cCustAddress,cDistance,cSize,cItemCount,cRequestDate,cType
						ObuID = objEdit.getText().toString();
						objDB = new dbLocations(context);
						objDB.openDB();
						objDB.Delete("tblTask", "cOrderID='"+ObuID+"'");
						String customer_name  = setEncryp (json.getString("customer_name"));
						String recipient_name = setEncryp(json.getString("recipient_name")) ;
						String recipient_phoneNo = setEncryp(json.getString("recipient_phoneNo"));
						String recipient_address = setEncryp(json.getString("recipient_address")) ;
						objDB.InsertTaskAllData(new Object[]{json.getString("caseID"), ObuID ,"","",json.getString("size"),json.getString("item_count"),json.getString("status_time"),"1",customer_name,"",recipient_name,recipient_phoneNo,recipient_address,json.getString("request_time"),json.getString("pay_type_MD"),json.getString("pay_amount_MD"),json.getString("cash_on_delivery")});
						//objDB.InsertTaskAllData(new Object[]{json.getString("caseID"),contents,"","",json.getString("size"),json.getString("item_count"),json.getString("status_time"),"1",customer_name,"",recipient_name,recipient_phoneNo,recipient_address,json.getString("request_time"),json.getString("pay_type_MD"),json.getString("pay_amount_MD"),json.getString("cash_on_delivery")});
						objDB.DBClose();
						Toast.makeText(GetTaskFrg.this,"取得"+ObuID+"資料！",Toast.LENGTH_SHORT).show();
						//clsDialog.Show(context, "提示", "取得案件資料！");
						Log.e("Array", String.valueOf(CaptureActivity.num));

						//若託運單號有更改而且資料庫裡是舊的託運單號時
						objDB = new dbLocations(context);
						objDB.openDB();
						clsTask objT = objDB.LoadTask(json.getString("caseID"));
						if(!objT.equals("") && objT != null){
							//objDB = new dbLocations(context);
							objDB.openDB();
							objDB.Delete("tblTask", "cOrderID='"+objT.OrderID+"'");
							Log.e("刪除2",ObuID);
							customer_name  = setEncryp (json.getString("customer_name"));
							recipient_name = setEncryp(json.getString("recipient_name")) ;
							recipient_phoneNo = setEncryp(json.getString("recipient_phoneNo"));
							recipient_address = setEncryp(json.getString("recipient_address")) ;
							objDB.InsertTaskAllData(new Object[]{json.getString("caseID"), ObuID ,"","",json.getString("size"),json.getString("item_count"),json.getString("status_time"),"1",customer_name,"",recipient_name,recipient_phoneNo,recipient_address,json.getString("request_time"),json.getString("pay_type_MD"),json.getString("pay_amount_MD"),json.getString("cash_on_delivery")});
							//objDB.InsertTaskAllData(new Object[]{json.getString("caseID"),contents,"","",json.getString("size"),json.getString("item_count"),json.getString("status_time"),"1",customer_name,"",recipient_name,recipient_phoneNo,recipient_address,json.getString("request_time"),json.getString("pay_type_MD"),json.getString("pay_amount_MD"),json.getString("cash_on_delivery")});
							objDB.DBClose();
							Toast.makeText(GetTaskFrg.this,"取得"+ObuID+"資料！",Toast.LENGTH_SHORT).show();
							//clsDialog.Show(context, "提示", "取得案件資料！");
							Log.e("Array", String.valueOf(CaptureActivity.num));
						}

					}
					if (status.equals("2")) {
						//clsDialog.Show(context, "錯誤訊息", "輸入的授權碼不合法！");
						Toast.makeText(GetTaskFrg.this,"輸入的授權碼不合法！",Toast.LENGTH_SHORT).show();
					}
					if (status.equals("4")) {

						Toast.makeText(GetTaskFrg.this,"託運單號不存在！",Toast.LENGTH_SHORT).show();
					}
					if (status.equals("200")) {
						//clsDialog.Show(context, "提示訊息", "系統忙碌中，請重試！");
						Toast.makeText(GetTaskFrg.this,"系統忙碌中，請重試！",Toast.LENGTH_SHORT).show();
					}

					objEdit.setText("");


				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		//return view;
	}

	public void onStart() {
		super.onStart();
		clsHttpPostAPI.handlerGetTask = handlerTask;
		GCMIntentService.handlerGCM = handlerGCM;
		ListViewAdpater.handler = handlerListView;
	}

	public void onStop() {
		super.onStop();
		//clsHttpPostAPI.handlerGetTask = null;
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(GetTaskFrg.this)
                    .setTitle("確認視窗")
                    .setMessage("確定要結束應用程式嗎?")
                    .setIcon(R.drawable.ic_launcher).setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {
 
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                	new clsHttpPostAPI().CallAPI(context, "API014");
                                	SysApplication.getInstance().exit();
									finish();
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

	//取件完成前 掃描
	public void onScan (View v){

		isSingleSacn = false;
		Intent intent = new Intent(GetTaskFrg.this, CaptureActivity.class);
		intent.setAction(Intents.Scan.ACTION); //啟動掃描動作，一定要設定
		intent.putExtra(Intents.Scan.WIDTH, 1200); //調整掃描視窗寬度(Optional)
		intent.putExtra(Intents.Scan.HEIGHT, 675); //調整掃描視窗高度(Optional)
		intent.putExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, 100L); //設定掃描成功地顯示時間(Optional)
		intent.putExtra(Intents.Scan.PROMPT_MESSAGE, "請將條碼置於鏡頭範圍進行掃描"); //客製化掃描視窗的提示文字(Optional)
		//intent.putExtra(Scan.MODE, Scan.ONE_D_MODE);  //限制只能掃一維條碼(預設為全部條碼都支援)
        //intent.putExtra(CaptureActivity.SACN_MODE_NAME, CaptureActivity.SCAN_SIGLE_MODE);
		intent.putExtra(CaptureActivity.SACN_MODE_NAME, CaptureActivity.SCAN_BATCH_MODE);

		startActivityForResult(intent, 1);


		/*
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
			intent.putExtra(CaptureActivity.SACN_MODE_NAME, CaptureActivity.SCAN_BATCH_MODE);
			isSingleSacn = false;
			// 呼叫ZXing Scanner，完成動作後回傳 1 給 onActivityResult 的 requestCode 參數
			startActivityForResult(intent, 1);
		}
		*/

	}
	//掃描後的動作
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		//取得掃描後的值 arraylist
		ArrayList num = CaptureActivity.num;

			for (int i = 0 ; i<num.size() ; i++) {
				final String contents;
				contents = String.valueOf(num.get(i));
				if (contents.length() == 11 || contents.length() == 8) {
					//objEdit.setText(contents);
					//new clsHttpPostAPI().CallAPI(context,"API013",contents);
					// 先判斷轉單有無在清單中
					if(checkOdb(contents) == true || checkOdb2(contents) == true){
						final String strUrl = Application.ChtUrl + "Services/API/Motor_Dispatch/Get_DispatchInfo.aspx?OrderID=" + contents + "&key=" + Application.strKey + "&EmployeeID="+Application.strAccount+ "&TransportID="+Application.strCar+"&Company="+Application.Company;
						OkHttpClient client = new OkHttpClient();
						Request request = new Request.Builder()
								.url(strUrl)
								.build();
						Call call = client.newCall(request);
						call.enqueue(new Callback() {
							@Override public void onFailure(Call call, IOException e) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										//TODO 顯示數量
										Toast.makeText(GetTaskFrg.this,"請確認網路是否連線",Toast.LENGTH_SHORT).show();

									}
								});
							}

							@Override
							public void onResponse(Call call, Response response) throws IOException {
								String json = response.body().string();
								Log.e("轉單URL", strUrl);
								Log.e("轉單", json);
								try {
									JSONObject j = new JSONObject(json);
									String status = j.getString("Result");
									if (status.equals("1")) {
										// 刪除掉原本有的案件
										dbLocations objDB;
										objDB = new dbLocations(context);
										objDB.openDB();
										objDB.Delete("tblTask", "cOrderID='"+contents+"'");
										//objDB.Delete("tblTask", "cOrderID='"+objT.OrderID+"'");
										//加入轉單後的案件
										String customer_name = setEncryp(j.getString("customer_name"));
										String recipient_name = setEncryp(j.getString("recipient_name"));
										String recipient_phoneNo = setEncryp(j.getString("recipient_phoneNo"));
										String recipient_address = setEncryp(j.getString("recipient_address"));
										Log.e("轉單contents2", contents);
										objDB.InsertTaskAllData(new Object[]{j.getString("caseID"), contents, "", "", j.getString("size"), j.getString("item_count"), j.getString("status_time"), "1", customer_name, "", recipient_name, recipient_phoneNo, recipient_address, j.getString("request_time"), j.getString("pay_type_MD"), j.getString("pay_amount_MD"), j.getString("cash_on_delivery")});
										objDB.close();

										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												Toast.makeText(GetTaskFrg.this,"取得"+contents+"資料！",Toast.LENGTH_SHORT).show();
											}
										});
										//clsDialog.Show(context, "提示", "取得案件資料！");
										//Log.e("Array", String.valueOf(CaptureActivity.num));

										//若託運單號有更改而且資料庫裡是舊的託運單號時
										objDB = new dbLocations(context);
										objDB.openDB();
										clsTask objT = objDB.LoadTask(j.getString("caseID"));
										if(!objT.equals("") && objT != null){
											//objDB = new dbLocations(context);
											objDB.openDB();
											objDB.Delete("tblTask", "cOrderID='"+objT.OrderID+"'");

											customer_name  = setEncryp (j.getString("customer_name"));
											recipient_name = setEncryp(j.getString("recipient_name")) ;
											recipient_phoneNo = setEncryp(j.getString("recipient_phoneNo"));
											recipient_address = setEncryp(j.getString("recipient_address")) ;
											objDB.InsertTaskAllData(new Object[]{j.getString("caseID"), contents ,"","",j.getString("size"),j.getString("item_count"),j.getString("status_time"),"1",customer_name,"",recipient_name,recipient_phoneNo,recipient_address,j.getString("request_time"),j.getString("pay_type_MD"),j.getString("pay_amount_MD"),j.getString("cash_on_delivery")});
											//objDB.InsertTaskAllData(new Object[]{json.getString("caseID"),contents,"","",json.getString("size"),json.getString("item_count"),json.getString("status_time"),"1",customer_name,"",recipient_name,recipient_phoneNo,recipient_address,json.getString("request_time"),json.getString("pay_type_MD"),json.getString("pay_amount_MD"),json.getString("cash_on_delivery")});
											objDB.DBClose();
											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													Toast.makeText(GetTaskFrg.this,"取得"+contents+"資料！",Toast.LENGTH_SHORT).show();
												}
											});

											//clsDialog.Show(context, "提示", "取得案件資料！");
											Log.e("Array", String.valueOf(CaptureActivity.num));
										}

									}
									if (status.equals("2")) {
										//clsDialog.Show(context, "錯誤訊息", "輸入的授權碼不合法！");
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												Toast.makeText(GetTaskFrg.this, "輸入的授權碼不合法！", Toast.LENGTH_SHORT).show();
											}
										});
									}
									if (status.equals("4")) {
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												Toast.makeText(GetTaskFrg.this, "託運單號不存在！", Toast.LENGTH_SHORT).show();
											}
										});

									}
									if (status.equals("200")) {
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												Toast.makeText(GetTaskFrg.this, "系統忙碌中，請重試！", Toast.LENGTH_SHORT).show();
											}
										});

									}
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											objEdit.setText("");

										}
									});

								} catch (JSONException e) {
									e.printStackTrace();
								}

							}
						});
					}else {
						Toast.makeText(this, "託運單號"+contents+"已在清單中", Toast.LENGTH_SHORT).show();
					}

				} else {

					Toast.makeText(this, "託運編號格式不符", Toast.LENGTH_SHORT).show();
				}
			}


		//Log.e("Barcode_Value2", String.valueOf(num));
		//Application.getTask = String.valueOf(num);
		/*
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {

				CaptureActivity.Number_Order = 1;
				CaptureActivity.Barcode_Type = data.getStringExtra(Intents.Scan.RESULT_FORMAT);
				CaptureActivity.Barcode_Value = data.getStringExtra(Intents.Scan.RESULT);
				String contents =data.getStringExtra(Intents.Scan.RESULT);
				// ZXing回傳的內容
				//String contents = data.getStringExtra("SCAN_RESULT");
				final EditText editText = (EditText) findViewById(R.id.TextView_OrderNo3);

				editText.setText(contents);
				Application.getTask=contents;


				if(contents.length()==11 || contents.length() ==8){

					if(!objEdit.getText().toString().trim().equals("")) {
						//Application.getTask=contents;
						new clsHttpPostAPI().CallAPI(context,"API013",objEdit.getText().toString());

					}else {

						Toast.makeText(this,"無此託運單號",Toast.LENGTH_SHORT).show();

					}
				}else{

					Toast.makeText(this,"託運編號格式不符",Toast.LENGTH_SHORT).show();

				}

				//startActivityForResult(data, 1);//連續-重複掃描的動作

			}

		}
		*/
		TextView textView = (TextView)findViewById(R.id.textView14);
		textView.setText(String.valueOf(CaptureActivity.num));
	}

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
		/*
		if(e2!=null && !e2.equals("")&& e2.getX()!=0 ){
			float distance = e2.getX()-e1.getX();
			if(distance>50){
				Log.e("方向","右邊");
				Intent intent = new Intent(GetTaskFrg.this, HistoryFragment.class);
				startActivity(intent);
				GetTaskFrg.this.finish();
			}else if(distance<-50){
				Intent intent = new Intent(GetTaskFrg.this, InOutFrg.class);
				startActivity(intent);
				Log.e("方向","左邊");
				GetTaskFrg.this.finish();
			}
			return false;
		}
		*/
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		float distance = e2.getX()-e1.getX();
		Log.e("distance", String.valueOf(distance));
		if(distance>50){
			Log.e("方向","右邊");
			Intent intent = new Intent(GetTaskFrg.this, HistoryFragment.class);
			startActivity(intent);
			GetTaskFrg.this.finish();
		}else if(distance<-50){
			Intent intent = new Intent(GetTaskFrg.this, InOutFrg.class);
			startActivity(intent);
			GetTaskFrg.this.finish();
			Log.e("方向","左邊");
		}else {
			return false;
		}
		return false;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			return detector.onTouchEvent(event);
		}catch (Exception e){
			return false;
		}
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
	private boolean checkOdb (String contents){
		//要比對資料庫裡面是否已有要轉的單子
		dbLocations objDB;
		objDB = new dbLocations(context);
		objDB.openDB();
		db = objDB.getWritableDatabase();
		Cursor c = db.query("tblTask",
				null,
				"cOrderID = ? ",
				new String[]{contents},
				null,
				null,
				null);
		while (c.moveToNext()){
			Log.e("轉單資料庫",c.getString(c.getColumnIndex("cOrderID")));
		}
		Log.e("轉單資料庫數字", String.valueOf(c.getCount()));

		if (c.getCount() == 0){
			return  true;
		}

		objDB.close();
		return false;
	}
	private boolean checkOdb2 (String contents){
		//要比對資料庫裡面是否已有要轉的單子
		dbLocations objDB;
		objDB = new dbLocations(context);
		objDB.openDB();
		db = objDB.getWritableDatabase();
		Cursor c = db.query("tblTask",
				null,
				"cOrderID = ? and cStatus = ?",
				new String[]{contents,"2"},
				null,
				null,
				null);
		while (c.moveToNext()){
			Log.e("轉單資料2",c.getString(c.getColumnIndex("cOrderID")));
		}
		Log.e("轉單資料庫數字2", String.valueOf(c.getCount()));

		if (c.getCount() == 1){
			return  true;
		}

		objDB.close();
		return false;
	}
}
