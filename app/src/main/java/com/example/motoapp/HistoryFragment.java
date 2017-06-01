package com.example.motoapp;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Activity {

	private String value = "";
	ListView listView;
	Context context;
	clsLoginInfo objLoginInfo;

	Button button_DoList;
	Button button_IO;
	Button button_GT;
	Button button_DoneList;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.frg_history);
		context = HistoryFragment.this;

		objLoginInfo = new clsLoginInfo(context);
		objLoginInfo.Load();

		dbLocations objDB = new dbLocations(HistoryFragment.this);
		objDB.openDB();
		SysApplication.getInstance().addActivity(this);
		Cursor cursor= objDB.Load1("tblTask", "cStatus='71' or cStatus='81' or cStatus='2' or cStatus='3' or cStatus='09'", "cRequestDate desc", "");
		List rowitem = new ArrayList();
		listView = (ListView) findViewById(R.id.listView);

		if(cursor!=null && cursor.getCount() > 0)
		{
			while(true)
			{
				String strOrderID = cursor.getString(cursor.getColumnIndex("cOrderID"));
				String strCaseID = cursor.getString(cursor.getColumnIndex("cCaseID"));
				String strStatus = cursor.getString(cursor.getColumnIndex("cStatus"));

				rowitem.add(new HistoryItem(strOrderID,clsTask.GetStatus(strStatus),cursor.getString(cursor.getColumnIndex("cRequestDate"))));

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

		button_DoList = (Button)findViewById(R.id.button_DoList);
		button_DoList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HistoryFragment.this, DataListFrg.class);
				startActivity(intent);
			}
		});

		button_IO = (Button)findViewById(R.id.button_IO);
		button_IO.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HistoryFragment.this, InOutFrg.class);
				startActivity(intent);

			}
		});

		button_GT = (Button)findViewById(R.id.button_GT);
		button_GT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HistoryFragment.this, GetTaskFrg.class);
				startActivity(intent);

			}
		});

		button_DoneList = (Button)findViewById(R.id.button_DoneList);
		button_DoneList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HistoryFragment.this, HistoryFragment.class);
				startActivity(intent);
			}
		});

		Button button_Logout = (Button)findViewById(R.id.Button_Logout);
		button_Logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new clsHttpPostAPI().CallAPI(context, "API014");
				//Intent intent = new Intent(HistoryFragment.this, Login.class);
				//startActivity(intent);
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
