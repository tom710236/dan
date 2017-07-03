package com.example.motoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
		Bundle bundle = intent.getExtras();
		strStatus = bundle.getString("status");

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
			TextView_Msg.setText("收到一筆派遣任務("+strOrderID+")，請問要立刻開啟嗎?");
			LinearLayout_St0.setVisibility(View.VISIBLE);
			Log.e("strStatus",strStatus);
		}

		if (strStatus.equals("1")) {
			dbLocations objDB = new dbLocations(GCMActivity.this);
			objDB.openDB();
			objDB.UpdateTask(bundle.getString("customer_address"),
					bundle.getString("customer_name"),
					bundle.getString("customer_phoneNo"),
					bundle.getString("recipient_name"),
					bundle.getString("recipient_address"),
					bundle.getString("recipient_phoneNo"),
					bundle.getString("pay_type"),
					bundle.getString("pay_amount"), "21",
					bundle.getString("caseID"));
			objDB.DBClose();
			Intent intent1 = new Intent();
			intent1.setClass(GCMActivity.this, DataListFrg.class);
			Bundle obj = new Bundle();
			obj.putString("type", "21");

			intent1.putExtras(obj);
			startActivity(intent);
			finish();
		}

		if (strStatus.equals("2")) {
			dbLocations objDB = new dbLocations(GCMActivity.this);
			objDB.openDB();
			objDB.UpdateTaskStatus("2", bundle.getString("caseID"));
			objDB.DBClose();
			LinearLayout_St1.setVisibility(View.VISIBLE);
			TextView_Msg.setText("很可惜，派遣任務("+bundle.getString("caseID")+")接單失敗！");
			//checkInt = 1;
		}

		if (strStatus.equals("3")) {
			dbLocations objDB = new dbLocations(GCMActivity.this);
			objDB.openDB();
			objDB.UpdateTaskStatus("3", bundle.getString("caseID"));
			objDB.DBClose();
			LinearLayout_St1.setVisibility(View.VISIBLE);
			TextView_Msg.setText("派遣任務("+bundle.getString("caseID")+")逾時超過"+bundle.getString("timeout")+"分鐘未回應！");
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
				//checkInt = 1;
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
	}
}
