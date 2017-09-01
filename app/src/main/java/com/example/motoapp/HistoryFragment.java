package com.example.motoapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Activity implements GestureDetector.OnGestureListener{

	private String value = "";
	ListView listView;
	Context context;
	clsLoginInfo objLoginInfo;

	Button button_DoList;
	Button button_IO;
	Button button_GT;
	Button button_DoneList;
	GestureDetector detector;
	Handler handlerGCM;
	Handler handlerTask;
	Handler handlerListView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.frg_history);
		context = HistoryFragment.this;

		objLoginInfo = new clsLoginInfo(context);
		objLoginInfo.Load();

		detector = new GestureDetector(this,this);
		detector.setIsLongpressEnabled(true);

		LinearLayout linearLayout = (LinearLayout)this.findViewById(R.id.linear);
		linearLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});

		dbLocations objDB = new dbLocations(HistoryFragment.this);
		objDB.openDB();
		SysApplication.getInstance().addActivity(this);
		//清單位置
		Cursor cursor= objDB.Load1("tblTask", "cStatus='71' or cStatus='81' or cStatus='2' or cStatus='3' or cStatus='09' or cStatus='00'or cStatus='CC'or cStatus='AA'", "cRequestDate desc", "");
		List rowitem = new ArrayList();
		//設定手勢滑動
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try{
					return detector.onTouchEvent(event);
				}catch (Exception e){
					return false;
				}

			}
		});

		ScrollView scrollView = (ScrollView)findViewById(R.id.ScrollView_H1);
		scrollView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});
		//員工卡號姓名設定
		clsLoginInfo objL = new clsLoginInfo(context);
		objL.Load();
		TextView tID = (TextView)findViewById(R.id.TextID);
		TextView tName = (TextView)findViewById(R.id.TextName);
		tID.setText(objL.UserID);
		tName.setText(objL.UserName);

		if(cursor!=null && cursor.getCount() > 0)
		{
			while(true)
			{
				String strOrderID = cursor.getString(cursor.getColumnIndex("cOrderID"));
				String strCaseID = cursor.getString(cursor.getColumnIndex("cCaseID"));
				String strStatus = cursor.getString(cursor.getColumnIndex("cStatus"));
				String strDate = cursor.getString(cursor.getColumnIndex("cLastDate"));

				//Log.e("oldStrDate",strDate);
				String oldStrDate = cursor.getString(cursor.getColumnIndex("cRequestDate"));

				String oldStrDate2 = oldStrDate.substring(11,oldStrDate.length()-3);
				Log.e("oldStrDate2",oldStrDate2);

				if(strDate != null && !strDate.equals("")){
						rowitem.add(new HistoryItem(strOrderID,clsTask.GetStatus(strStatus),cursor.getString(cursor.getColumnIndex("cLastDate")))); //時間
				}else{
					rowitem.add(new HistoryItem(strOrderID,clsTask.GetStatus(strStatus),oldStrDate2)); //時間
					Log.e("oldStrDate2",oldStrDate2);
				}

				if(cursor.isLast())
					break;

				cursor.moveToNext();
			}
			cursor.close();
		}
		objDB.DBClose();

		ListViewAdpaterHistory adpater = new ListViewAdpaterHistory(HistoryFragment.this, rowitem);
		listView.setAdapter(adpater);

		/*List rowitem = new ArrayList();

		listView = (ListView) findViewById(R.id.listView);
		rowitem.add(new HistoryItem("1234567", "循車中", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "循車中", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "已結案", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "已結案", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "已結案", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "已結案", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "循車中", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "已結案", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "循車中", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "循車中", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "循車中", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "已結案", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "已結案", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "已結案", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "已結案", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "循車中", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "已結案", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "循車中", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "已結案", "2015/06/25"));
		rowitem.add(new HistoryItem("1234567", "已結案", "2015/06/25"));
		ListViewAdpaterHistory adpater = new ListViewAdpaterHistory(
				HistoryFragment.this, rowitem);

		listView.setAdapter(adpater);*/

		LinearLayout LinearLayout_list = (LinearLayout)findViewById(R.id.LinearLayout_list);
		ScrollView ScrollView_H1 = (ScrollView)findViewById(R.id.ScrollView_H1);

		LinearLayout_list.setVisibility(View.VISIBLE);
		ScrollView_H1.setVisibility(View.GONE);




		/* 取出資料 */
		/*
		objDB.openDB();
		clsTask objT = objDB.LoadTask(caseID);

		objDB.DBClose();
		((TextView) findViewById(R.id.TextView_CarNo))
				.setText(Application.strCar);
		((TextView) findViewById(R.id.TextView_DateTime))
				.setText(objT.RequestDate);
		((TextView) findViewById(R.id.TextView_CaseID))
				.setText(objT.OrderID);
		((TextView) findViewById(R.id.editText_Address))
				.setText(objT.RecAddress);
		((TextView) findViewById(R.id.EditText_Size))
				.setText(objT.RecName);
		((TextView) findViewById(R.id.editText_Distant))
				.setText(objT.Distance);
		((TextView) findViewById(R.id.EditText_Count))
				.setText(objT.ItemCount);
		*/

		//返回
		Button button_Back = (Button)findViewById(R.id.button_Back);
		button_Back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ScrollView sv = (ScrollView)findViewById(R.id.ScrollView_H1);
				LinearLayout LinearLayout_list = (LinearLayout)findViewById(R.id.LinearLayout_list);
				sv.setVisibility(View.GONE);
				LinearLayout_list.setVisibility(View.VISIBLE);

			}
		});

		//上排按鈕
		button_DoList = (Button)findViewById(R.id.button_DoList);
		button_DoList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HistoryFragment.this, DataListFrg.class);
				startActivity(intent);
				HistoryFragment.this.finish();
			}
		});

		button_IO = (Button)findViewById(R.id.button_IO);
		button_IO.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Intent intent2 = new Intent(HistoryFragment.this, InOutFrg.class);
				//startActivity(intent2);
				Intent intent = new Intent(HistoryFragment.this, InOutFrg.class);
				startActivity(intent);
				HistoryFragment.this.finish();


			}
		});

		button_GT = (Button)findViewById(R.id.button_GT);
		button_GT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Intent intent2 = new Intent(HistoryFragment.this, InOutFrg.class);
				//startActivity(intent2);
				Intent intent = new Intent(HistoryFragment.this, GetTaskFrg.class);
				startActivity(intent);
				HistoryFragment.this.finish();
			}
		});

		button_DoneList = (Button)findViewById(R.id.button_DoneList);
		button_DoneList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Intent intent2 = new Intent(HistoryFragment.this, InOutFrg.class);
				//startActivity(intent2);
				Intent intent = new Intent(HistoryFragment.this, HistoryFragment.class);
				startActivity(intent);
				HistoryFragment.this.finish();
			}
		});

		Button button_Logout = (Button)findViewById(R.id.Button_Logout);
		button_Logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(HistoryFragment.this,Delay.class);
				stopService(it);
				new clsHttpPostAPI().CallAPI(context, "API014");
				HistoryFragment.this.finish();
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

		button_DoneList.setBackgroundResource(R.drawable.menu04b);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
			new AlertDialog.Builder(HistoryFragment.this)
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
		if(e2!=null && !e2.equals("") && e2.getX()!=0 ){
			float distance = e2.getX()-e1.getX();
			if(distance>100){
				Log.e("方向1","右邊");
				Intent intent = new Intent(HistoryFragment.this, HistoryFragment.class);
				startActivity(intent);
				HistoryFragment.this.finish();
			}else if(distance<-100){
				Intent intent = new Intent(HistoryFragment.this, GetTaskFrg.class);
				startActivity(intent);
				HistoryFragment.this.finish();
				Log.e("方向1","左邊");
			}else {
				return false;
			}
			return false;
		}*/
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		float distance = e2.getX()-e1.getX();
		if(distance>100){
			Log.e("方向2","右邊");
			Intent intent = new Intent(HistoryFragment.this, DataListFrg.class);
			startActivity(intent);
			HistoryFragment.this.finish();
		}else if(distance<-100){
			Intent intent = new Intent(HistoryFragment.this, GetTaskFrg.class);
			startActivity(intent);
			HistoryFragment.this.finish();
			Log.e("方向2","左邊");
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
}
