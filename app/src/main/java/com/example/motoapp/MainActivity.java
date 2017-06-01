package com.example.motoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	Context context;

	Handler handlerSu;

	public static int intTabIndex=0;
	ProgressDialog dialog;
	@SuppressWarnings("hiding")
	private static final String TAG = "MainActivity";
	FragmentTabHost tabHost;
	private DataListFrg fragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		context = this.context;

		//type=2;
		tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		//設定Tab頁面的顯示區域，帶入Context、FragmentManager、Container ID
		tabHost.setup(this, getSupportFragmentManager(), R.id.container);

		//建立一個Tab，這個Tab的Tag設定為one，
		//並設定Tab上顯示的文字為第一堂課與icon圖片，Tab連結切換至

		tabHost.addTab(tabHost.newTabSpec("派遣列表").setIndicator("派遣列表",getResources().getDrawable(R.drawable.box)),
				DataListFrg.class, null);
		// 2
		tabHost.addTab(tabHost.newTabSpec("攜出銷卡").setIndicator("攜出銷卡",getResources().getDrawable(R.drawable.camera)),
				InOutFrg.class, null);
		// 3
		tabHost.addTab(tabHost.newTabSpec("配送").setIndicator("配送",getResources().getDrawable(R.drawable.compass)),
				GetTaskFrg.class, null);
		// 4
		tabHost.addTab(tabHost.newTabSpec("結案列表").setIndicator("結案列表",getResources().getDrawable(R.drawable.documents)),
				HistoryFragment.class, null);

		changeTab(2);

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {


				tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.menu01a);
				tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.menu02a);
				tabHost.getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.menu02a);
				tabHost.getTabWidget().getChildAt(3).setBackgroundResource(R.drawable.menu04a);

				if ("派遣列表".equals(tabId)) {
					tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.menu01b);

					// destroy earth
				}
				if ("攜出銷卡".equals(tabId)) {
					tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.menu02b);
					// destroy mars
				}
				if ("配送".equals(tabId)) {
					tabHost.getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.menu02b);
					// destroy earth
				}
				if ("結案列表".equals(tabId)) {
					tabHost.getTabWidget().getChildAt(3).setBackgroundResource(R.drawable.menu04b);
					// destroy mars
				}
			}
		});

		handlerSu = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String status = (String) msg.obj;

				try {
					//type = "04";
					changeTab(2);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

	}

	Fragment fg;
	@Override
	public void onAttachFragment(Fragment fragment) {
		// TODO Auto-generated method stub

		try {
			fg= fragment;
		} catch (Exception e) {
			// TODO: handle exception
		}

		super.onAttachFragment(fragment);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d("ActionBar", "OnKey事件");
		/*if (fg instanceof DataListFrg) {
			DataListFrg.onKeyDown(keyCode, event);
		}*/
		return true;//super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d("ActionBar", "OnKey事件");
		/*if (fg instanceof DataListFrg) {
			DataListFrg.onKeyDown(keyCode, event);
		}*/
		return true;//super.onKeyDown(keyCode, event);
	}


	public void onStart() {
		super.onStart();

	}

	public void onStop() {
		super.onStop();

	}

	public void Button_Reject(View view) {
		new clsHttpPostAPI().CallAPI(context, "API003");

		clsDialog.Show(MainActivity.this, "提示", "您拒絕這筆派遣單！");
		changeTab(0);
		//type = "01";
		dialog = ProgressDialog.show(MainActivity.this, "Loading", "等待詢車中...",
				true);
	}


	/**************************
	 *
	 * 給子頁籤呼叫用
	 *
	 **************************/
	public void changeTab(int pIntMode) {

		tabHost.setCurrentTab(pIntMode);

		tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.menu01a);
		tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.menu02a);
		tabHost.getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.menu02a);
		tabHost.getTabWidget().getChildAt(3).setBackgroundResource(R.drawable.menu04a);

		TextView tv =null;
		for (int i = 0; i < 4; i++) {
			tv = (TextView) tabHost.getTabWidget().getChildAt(i)
					.findViewById(android.R.id.title);
			if (tv != null) {
				tv.setTextColor(android.graphics.Color.WHITE);
				tv.setTextSize(16);
			}
		}


		if (pIntMode==0) {
			tabHost.getTabWidget().getChildAt(pIntMode).setBackgroundResource(R.drawable.menu01b);
		}
		if (pIntMode==1) {
			tabHost.getTabWidget().getChildAt(pIntMode).setBackgroundResource(R.drawable.menu02b);
		}
		if (pIntMode==2) {
			tabHost.getTabWidget().getChildAt(pIntMode).setBackgroundResource(R.drawable.menu02b);
		}
		if (pIntMode==3) {
			tabHost.getTabWidget().getChildAt(pIntMode).setBackgroundResource(R.drawable.menu04b);
		}

	}

	public void setSuccess() {
		//type = "04";
		changeTab(2);
	}



}
