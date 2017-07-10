package com.example.motoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class GetTaskFrg extends Activity {

	Context context;
	View view;
	Handler handlerTask;
	EditText objEdit;
	private dbLocations objDB;
	clsLoginInfo objLoginInfo;
	
	Button button_DoList;
	Button button_IO;
	Button button_GT;
	Button button_DoneList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("=====>", "FacebookFragment onCreateView");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.frg_gettask);
		//view = inflater.inflate(R.layout.frg_gettask, container, false);



		context = GetTaskFrg.this;

		objLoginInfo = new clsLoginInfo(context);
		objLoginInfo.Load();
		
		SysApplication.getInstance().addActivity(this);
		objEdit = (EditText)findViewById(R.id.TextView_OrderNo3);
		//objEdit.setText("40000200023");
		
		button_DoList = (Button)findViewById(R.id.button_DoList);
		button_DoList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GetTaskFrg.this, DataListFrg.class);
			    startActivity(intent);
			}
		});
		
		button_IO = (Button)findViewById(R.id.button_IO);
		button_IO.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GetTaskFrg.this, InOutFrg.class);
			    startActivity(intent);
			    
			}
		});
		
		button_GT = (Button)findViewById(R.id.button_GT);
		button_GT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		button_DoneList = (Button)findViewById(R.id.button_DoneList);
		button_DoneList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GetTaskFrg.this, HistoryFragment.class);
			    startActivity(intent);
			}
		});
		
		 button_GT.setBackgroundResource(R.drawable.menu03b);
		 
		
		Button button_Scran = (Button)findViewById(R.id.button_Scran);
		button_Scran.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(!objEdit.getText().toString().trim().equals(""))
				{
					/*
					 * 呼叫API 接單*/
					new clsHttpPostAPI().CallAPI(context,"API013",objEdit.getText().toString());
				}else
				{
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
					
					if (status.equals("1")) {
						//cCaseID,cOrderID,cCustAddress,cDistance,cSize,cItemCount,cRequestDate,cType
						objDB = new dbLocations(context);
						objDB.openDB();
						objDB.InsertTaskAllData(new Object[]{json.getString("caseID"),objEdit.getText().toString(),"","","",json.getString("item_count"),json.getString("request_time"),"1","","",json.getString("recipient_name"),json.getString("recipient_phoneNo"),json.getString("recipient_address"),json.getString("request_time"),json.getString("pay_type"),json.getString("pay_amount")});
						objDB.DBClose();
						
						clsDialog.Show(context, "提示", "取得案件資料！");
						
					}
					if (status.equals("2")) {
						clsDialog.Show(context, "錯誤訊息", "輸入的授權碼不合法！");
					}
					if (status.equals("4")) {
						clsDialog.Show(context, "提示訊息", "託運單號不存在！");
					}
					if (status.equals("200")) {
						clsDialog.Show(context, "提示訊息", "系統忙碌中，請重試！");
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
	}

	public void onStop() {
		super.onStop();
		clsHttpPostAPI.handlerGetTask = null;
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
	//掃描後的動作
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);


		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				// ZXing回傳的內容
				String contents = data.getStringExtra("SCAN_RESULT");
				final EditText editText = (EditText) findViewById(R.id.TextView_OrderNo3);
				editText.setText(contents);

			}

		}
	}
}
