package com.example.motoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.NoSuchAlgorithmException;

public class GCMActivity extends Activity {

	public static String strCaseID="";
	String strOrderID="";
	Button btnOpen;
	Button btnClose;
	Button btnClose1;
	LinearLayout LinearLayout_St0;
	LinearLayout LinearLayout_St1;
	TextView TextView_Msg;
	String strStatus;
	int checkInt = 0;
	Handler handlerGCM;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gcm);


		LinearLayout_St0 = (LinearLayout) findViewById(R.id.LinearLayout_St0);
		LinearLayout_St1 = (LinearLayout) findViewById(R.id.LinearLayout_St1);

		btnOpen = (Button) findViewById(R.id.Button_OpenGCM);
		btnClose = (Button) findViewById(R.id.Button_CloseGCM);
		btnClose1 = (Button) findViewById(R.id.Button_CloseGCM1);
		TextView_Msg = (TextView) findViewById(R.id.TextView_Msg);

		LinearLayout_St0.setVisibility(View.GONE);
		LinearLayout_St1.setVisibility(View.GONE);

		Intent intent = getIntent();
		final Bundle bundle = intent.getExtras();
		strStatus = bundle.getString("status");
		Log.e("strStatus GCM2",strStatus);

		// 假如Application.Company為空值 表示沒有登入
		if(!Application.Company.equals("")&&Application.Company!= null){
			if (strStatus.equals("0")) {
				dbLocations objDB = new dbLocations(GCMActivity.this);
				objDB.openDB();
				objDB.InsertTask(new Object[] { bundle.getString("caseID"),
						bundle.getString("orderID"),
						bundle.getString("customer_address"),
						bundle.getString("distance"), bundle.getString("size"),
						bundle.getString("item_count"),
						bundle.getString("request_time"), "0" });
				objDB.DBClose();

				strCaseID = bundle.getString("caseID");
				strOrderID =  bundle.getString("orderID");
				Application.ObuID = strOrderID;
				TextView_Msg.setText("收到一筆派遣任務("+strOrderID+")，請問要立刻開啟嗎?");
				LinearLayout_St0.setVisibility(View.VISIBLE);
				Log.e("strStatus",strStatus);
			}

			if (strStatus.equals("1")) {



				final dbLocations objDB = new dbLocations(GCMActivity.this);
				String customer_address2 = setEncryp(bundle.getString("customer_address"));
				String customer_name2 = setEncryp(bundle.getString("customer_name"));
				String customer_phoneNo2 = setEncryp(bundle.getString("customer_phoneNo"));
				String recipient_name2 = setEncryp(bundle.getString("recipient_name"));
				String recipient_address2 = setEncryp(bundle.getString("recipient_address"));
				String recipient_phoneNo2 = setEncryp(bundle.getString("recipient_phoneNo"));

				Log.e("GCM2","GCM2");
				Application.strCaseID = bundle.getString("caseID");
				Application.strObuID = bundle.getString("obuid");
				Application.objFormInfo = bundle;

				objDB.openDB();

				objDB.InsertTask(new Object[] {
						bundle.getString("caseID"),
						bundle.getString("orderID"),
						customer_address2,
						bundle.getString("distance"),
						bundle.getString("size"),
						bundle.getString("item_count"),
						bundle.getString("request_time"),
						"0" });
				//objDB.DBClose();

				//objDB.openDB();


				objDB.UpdateTask(
						customer_address2,
						customer_name2,
						customer_phoneNo2,
						recipient_name2,
						recipient_address2,
						recipient_phoneNo2,
						bundle.getString("pay_type"),
						bundle.getString("pay_amount"), "21",
						bundle.getString("caseID"),
						bundle.getString("orderID"),
						bundle.getString("cash_on_delivery"));


				objDB.DBClose();

				Intent intent1 = new Intent();
				intent1.setClass(GCMActivity.this, DataListFrg.class);

				Bundle obj = new Bundle();
				obj.putString("type", "21");
				obj.putString("GCM","1");
				//Application.objForm = bundle;
				intent1.putExtras(obj);
				//Application.strCaseID=strCaseID;
				startActivity(intent1);
				finish();

			}

			if (strStatus.equals("2")) {

				dbLocations objDB = new dbLocations(GCMActivity.this);
				objDB.openDB();
				objDB.UpdateTaskStatus("2", bundle.getString("caseID"));
				objDB.DBClose();
				LinearLayout_St1.setVisibility(View.VISIBLE);
				TextView_Msg.setText("很可惜，派遣任務("+bundle.getString("caseID")+")接單失敗！");
				checkInt = 1;

			}

			if (strStatus.equals("3")) {
				dbLocations objDB = new dbLocations(GCMActivity.this);
				objDB.openDB();
				objDB.UpdateTaskStatus("3", bundle.getString("caseID"));
				objDB.DBClose();
				LinearLayout_St1.setVisibility(View.VISIBLE);
				TextView_Msg.setText("派遣任務("+bundle.getString("caseID")+")逾時超過"+bundle.getString("timeout")+"分鐘未回應！");
			}
			if (strStatus.equals("4")) {
				//刪除已被其他司機轉單/指定派遣的既有派遣單
				//刪除掉原本有的案件
				LinearLayout_St1.setVisibility(View.VISIBLE);
				TextView_Msg.setText("取件改派，"+bundle.getString("orderID")+"派遣單");
				final String newUserID;
				final String strData = bundle.getString("orderID");

				if(bundle.getString("EmployeeID").length()<5){
					final int userID = Integer.parseInt(bundle.getString("EmployeeID"));
					newUserID= String.format("%05d", userID);
				}else {
					newUserID =bundle.getString("EmployeeID");
				}

					/*
					Log.e("newUserID",newUserID);
					Log.e("刪除",strData);
					dbLocations objDB;
					objDB = new dbLocations(GCMActivity.this);
					objDB.openDB();
					objDB.Delete("tblTask", "cOrderID='"+strData+"'");
					objDB.close();
					// 刷新listview
					*/


					Log.e("newUserID",newUserID);
					Log.e("刪除",strData);
					Application.strCaseID = bundle.getString("caseID");
					dbLocations objDB;
					objDB = new dbLocations(GCMActivity.this);
					objDB.openDB();
					objDB.UpdateTaskStatus("DD", bundle.getString("caseID"));
					objDB.DBClose();
					btnClose1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(GCMActivity.this, DataListFrg.class);
							startActivity(intent);
						}
					});

			}
			// 刷新listview
			btnClose.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(GCMActivity.this, DataListFrg.class);
					startActivity(intent);
					finish();
					Log.e("btnClose","btnClose");
				}
			});

			btnClose1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					checkInt = 1;
					Log.e("btnClose1","btnClose1");
					Intent intent = new Intent(GCMActivity.this, DataListFrg.class);
					startActivity(intent);
					finish();

				}
			});

			btnOpen.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(GCMActivity.this, DataListFrg.class);
					Bundle obj = new Bundle();
					obj.putString("type", "02");
					Application.strCaseID=strCaseID;
					intent.putExtras(obj);
					startActivity(intent);
					Log.e("btnOpen","btnOpen");
					finish();


				}
			});
		}else {
			//未登入時的處理
			LinearLayout_St1.setVisibility(View.VISIBLE);
			TextView_Msg.setText("收到一筆派遣任務，請先登入");
			// 刷新listview

			btnClose1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});

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
	//解密
	private String setDecrypt (String DecryptString){
		SetAES AES = new SetAES();
		EncrypMD5 encrypMD5 = new EncrypMD5();
		EncrypSHA encrypSHA = new EncrypSHA();
		Log.e("DecryptString",DecryptString);
		try {
			byte[] TextByte2 = AES.DecryptAES(encrypMD5.eccrypt(),encrypSHA.eccrypt(), Base64.decode(DecryptString.getBytes(),Base64.DEFAULT));
			DecryptString = new String(TextByte2);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		}
		return DecryptString;
	}

}
