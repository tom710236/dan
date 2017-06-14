package com.example.motoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InOutFrg extends Activity {
	MainActivity objActivity;
	ProgressDialog dialog;
	ListView listView;
	Context context;
	View view;
	clsLoginInfo objLoginInfo;

	int intType;
	EditText EditText_Val;
	Button button_DoList;
	Button button_IO;
	Button button_GT;
	Button button_DoneList;

	Handler handlerTask;
	EditText EditNo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("=====>", "GoogleFragment onCreateView");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.frg_inout);

		context = InOutFrg.this;

		objLoginInfo = new clsLoginInfo(context);
		objLoginInfo.Load();

		java.util.Date now = new java.util.Date();
		String strDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(now);
		
		/*EditNo =(EditText) findViewById(R.id.EditText_OrderID1);
		
		EditNo.setText(objLoginInfo.FormNo);
		if(strDate.equals(Application.strDate))
		{
			EditNo.setText(Application.strCardNo);
		}else
		{
			Application.strDate = strDate;
		}*/

		SysApplication.getInstance().addActivity(this);
		listView = (ListView) findViewById(R.id.listView_Reason);
		//SetListView(1);
		ScrollView ScrollViewT = (ScrollView)findViewById(R.id.ScrollViewT);
		LinearLayout LinearLayout_doType = (LinearLayout) findViewById(R.id.LinearLayout_doType);

		LinearLayout LinearLayout_Start2 = (LinearLayout)
				findViewById(R.id.LinearLayout_Start2);
		LinearLayout LinearLayout_End2 = (LinearLayout)
				findViewById(R.id.LinearLayout_End2);
		LinearLayout LinearLayout_Search = (LinearLayout)
				findViewById(R.id.LinearLayout_Search);


		//LinearLayout LinearLayout_SAddress = (LinearLayout) 
		//		findViewById(R.id.LinearLayout_SAddress);
		LinearLayout LinearLayout_list = (LinearLayout)
				findViewById(R.id.LinearLayout_list);

		ScrollViewT.setVisibility(View.VISIBLE);
		LinearLayout_doType.setVisibility(View.VISIBLE);
		LinearLayout_Search.setVisibility(View.GONE);
		LinearLayout_Start2.setVisibility(View.GONE);
		LinearLayout_End2.setVisibility(View.GONE);

		//LinearLayout_SAddress.setVisibility(View.GONE);
		LinearLayout_list.setVisibility(View.GONE);
		
		/* 點選外層的配送 */
		Button button_doStart = (Button) findViewById(R.id.button_doStart);
		button_doStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Application.strCardNo = EditNo.getText().toString();
				((TextView) findViewById(R.id.TextView_SNO1)).setText("");
				((TextView) findViewById(R.id.TextView_ENO1)).setText("");

				ScrollView ScrollViewT = (ScrollView) findViewById(R.id.ScrollViewT);
				LinearLayout LinearLayout_doType = (LinearLayout) findViewById(R.id.LinearLayout_doType);
				LinearLayout LinearLayout_Search = (LinearLayout)findViewById(R.id.LinearLayout_Search);
				LinearLayout LinearLayout_Start2 = (LinearLayout) findViewById(R.id.LinearLayout_Start2);
				LinearLayout LinearLayout_End2 = (LinearLayout) findViewById(R.id.LinearLayout_End2);

				//LinearLayout LinearLayout_SAddress = (LinearLayout) findViewById(R.id.LinearLayout_SAddress);
				LinearLayout LinearLayout_list = (LinearLayout) findViewById(R.id.LinearLayout_list);

				ScrollViewT.setVisibility(View.VISIBLE);
				LinearLayout_doType.setVisibility(View.GONE);
				LinearLayout_Search.setVisibility(View.GONE);
				LinearLayout_list.setVisibility(View.GONE);
				intType = 0;

				LinearLayout_Start2.setVisibility(View.VISIBLE);
				EditText EditForm = (EditText) findViewById(R.id.EditText_SNO1);
				EditForm.requestFocus();

				//LinearLayout_SAddress.setVisibility(View.GONE);

				TextView TextView_SOrderID2 = (TextView) findViewById(R.id.TextView_SOrderID2);
				TextView TextView_EOrderID2 = (TextView) findViewById(R.id.TextView_EOrderID2);
				TextView_SOrderID2.setText(objLoginInfo.FormNo);
				TextView_EOrderID2.setText(objLoginInfo.FormNo);

				//TODO 顯示數量
				TextView TextView_SCount = (TextView)findViewById(R.id.TextView_SCount);
				TextView_SCount.setText(String.format("%04d", Integer.valueOf(objLoginInfo.In)));

			}
		});

		/* 配送-在運輸單號中點選Enter */
		EditText EditText_SNO1 = (EditText)findViewById(R.id.EditText_SNO1);
		EditText_SNO1.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == 23|| keyCode==66) {
						EditText EditText_SNO1 = (EditText)v;
						if(EditText_SNO1.getText().toString().length()<10)
						{
							clsDialog.Show(context, "提示", "請輸入10碼以上的託運單號！");
							return true;
						}
						/**
						 * 呼叫API
						 * */

						JSONObject json = new JSONObject();
						try {
							java.util.Date now = new java.util.Date();
							String strDate = new java.text.SimpleDateFormat("yyyyMMdd").format(now);
							String strMDate = String.valueOf(Integer.parseInt(strDate)-19110000);
							String strTime = new java.text.SimpleDateFormat("HHmmss").format(now);

							json.put("HT3101", ((Button)findViewById(R.id.EditText_SStatus1)).getText().toString());
							json.put("HT3102", ((EditText)findViewById(R.id.EditText_SNO1)).getText().toString());
							json.put("HT3103", objLoginInfo.FormNo);
							json.put("HT3113", strMDate);
							json.put("HT3114", strTime);
							json.put("HT3181", Application.TestCode);
							json.put("HT3182", objLoginInfo.AreaID);
							json.put("HT3183", objLoginInfo.UserID);
							json.put("HT3184", objLoginInfo.DeviceID);
							json.put("HT3185", "B");
							json.put("HT3186", "1");
							json.put("HT3191", strDate);
							json.put("HT3192", strTime);
						} catch (Exception e) {
							// TODO: handle exception
						}


						String strPOSTData =json.toString();
						new clsHttpPostAPI().CallAPI(context, "API016", strPOSTData);

						//TODO 記單號
						TextView TextView_SNo = (TextView)findViewById(R.id.TextView_SNO1);

						TextView_SNo.setText(EditText_SNO1.getText().toString());
						String sssss = TextView_SNo.getText().toString();
						//TODO 清掉欄位
						EditText_SNO1.setText("");

						//TODO 更新數量
						objLoginInfo.UpdateInOut("In");

						//TODO 顯示數量
						TextView TextView_SCount = (TextView)findViewById(R.id.TextView_SCount);
						TextView_SCount.setText(String.format("%04d", Integer.valueOf(objLoginInfo.In)));

						//TODO
						Button EditText_SStatus1 = (Button) findViewById(R.id.EditText_SStatus1);
						EditText_SStatus1.setText("73");

						TextView TextView_EStatusName1 = (TextView) findViewById(R.id.TextView_EStatusName1);
						TextView_EStatusName1.setText("配送");

						EditText_SNO1.requestFocus();
						return true;
					}
					return false;
				}
				return true;
			}
		});

		/* 配送-點查詢代碼 */
		Button EditText_SStatus1 = (Button) findViewById(R.id.EditText_SStatus1);
		EditText_SStatus1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SetListView(0);
			}
		});

		/* 配送2-點取消 */
		Button button_Scancel2 = (Button) findViewById(R.id.button_Scancel2);
		button_Scancel2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GoInit();
			}
		});


		/* 配送2-點取得地址 */

		Button button_SGetAddress = (Button) findViewById(R.id.button_SGetAddress);
		button_SGetAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//LinearLayout LinearLayout_SAddress = (LinearLayout) findViewById(R.id.LinearLayout_SAddress);
				//LinearLayout_SAddress.setVisibility(View.VISIBLE);

				/**
				 * 呼叫API查地址
				 * */
			}
		});


		/* 點選外層的配達 */
		Button button_doEnd = (Button) findViewById(R.id.button_doEnd);
		button_doEnd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Application.strCardNo = EditNo.getText().toString();
				((TextView) findViewById(R.id.TextView_SNO1)).setText("");
				((TextView) findViewById(R.id.TextView_ENO1)).setText("");

				ScrollView ScrollViewT = (ScrollView) findViewById(R.id.ScrollViewT);
				LinearLayout LinearLayout_doType = (LinearLayout) findViewById(R.id.LinearLayout_doType);
				LinearLayout LinearLayout_Search = (LinearLayout)findViewById(R.id.LinearLayout_Search);
				LinearLayout_Search.setVisibility(View.GONE);
				LinearLayout LinearLayout_Start2 = (LinearLayout) findViewById(R.id.LinearLayout_Start2);
				LinearLayout LinearLayout_End2 = (LinearLayout) findViewById(R.id.LinearLayout_End2);
				//LinearLayout LinearLayout_SAddress = (LinearLayout) findViewById(R.id.LinearLayout_SAddress);
				LinearLayout LinearLayout_list = (LinearLayout) findViewById(R.id.LinearLayout_list);

				ScrollViewT.setVisibility(View.VISIBLE);
				LinearLayout_doType.setVisibility(View.GONE);
				// LinearLayout_Start1.setVisibility(View.GONE);
				LinearLayout_list.setVisibility(View.GONE);
				intType = 1;

				LinearLayout_End2.setVisibility(View.VISIBLE);
				EditText EditForm = (EditText) findViewById(R.id.EditText_ENO1);
				EditForm.requestFocus();

				//LinearLayout_SAddress.setVisibility(View.GONE);

				TextView TextView_SOrderID2 = (TextView) findViewById(R.id.TextView_SOrderID2);
				TextView TextView_EOrderID2 = (TextView) findViewById(R.id.TextView_EOrderID2);
				TextView_SOrderID2.setText(objLoginInfo.FormNo);
				TextView_EOrderID2.setText(objLoginInfo.FormNo);

				//TODO 顯示數量
				TextView TextView_ECount = (TextView)findViewById(R.id.TextView_ECount);
				TextView_ECount.setText(String.format("%04d", Integer.valueOf(objLoginInfo.Out))+" / "+String.format("%04d", Integer.valueOf(objLoginInfo.In)));

			}
		});

		/* 配達-點取消 */
		Button button_Ecancel2 = (Button) findViewById(R.id.button_Ecancel2);
		button_Ecancel2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GoInit();
			}
		});

		/* 查詢-點返回 */
		Button button_Ecancel3 = (Button) findViewById(R.id.button_Ecancel3);
		button_Ecancel3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GoInit();
			}
		});

		/* 配達-點查詢代碼 */
		Button EditText_EStatus1 = (Button) findViewById(R.id.EditText_EStatus1);
		EditText_EStatus1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SetListView(1);
			}
		});

		/* 配達-在運輸單號中點選Enter */
		EditText EditText_ENO1 = (EditText)findViewById(R.id.EditText_ENO1);
		EditText_ENO1.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					EditText EditText_ENO1 = (EditText)v;
					if (keyCode == 23 || keyCode==66) {
						if(EditText_ENO1.getText().toString().length()<10)
						{
							clsDialog.Show(context, "提示", "請輸入10碼以上的託運單號！");
							return true;
						}

						/**
						 * 呼叫API
						 * */
						JSONObject json = new JSONObject();
						try {
							java.util.Date now = new java.util.Date();
							String strDate = new java.text.SimpleDateFormat("yyyyMMdd").format(now);
							String strMDate = String.valueOf(Integer.parseInt(strDate)-19110000);
							String strTime = new java.text.SimpleDateFormat("HHmmss").format(now);

							json.put("HT3101", ((Button)findViewById(R.id.EditText_EStatus1)).getText().toString());
							json.put("HT3102", ((EditText)findViewById(R.id.EditText_ENO1)).getText().toString());
							json.put("HT3103", objLoginInfo.FormNo);
							json.put("HT3113", strMDate);
							json.put("HT3114", strTime);
							json.put("HT3181", Application.TestCode);
							json.put("HT3182", objLoginInfo.AreaID);
							json.put("HT3183", objLoginInfo.UserID);
							json.put("HT3184", objLoginInfo.DeviceID);
							json.put("HT3185", "B");
							json.put("HT3186", "1");
							json.put("HT3191", strDate);
							json.put("HT3192", strTime);
						} catch (Exception e) {
							// TODO: handle exception
						}


						String strPOSTData =json.toString();
						new clsHttpPostAPI().CallAPI(context, "API023", strPOSTData);

						//TODO 記單號
						TextView TextView_ENo = (TextView)findViewById(R.id.TextView_ENO1);

						TextView_ENo.setText(EditText_ENO1.getText().toString());
						String sssss = TextView_ENo.getText().toString();
						//TODO 清掉欄位
						EditText_ENO1.setText("");

						//TODO 更新數量
						objLoginInfo.UpdateInOut("Out");

						//TODO 顯示數量
						TextView TextView_ECount = (TextView)findViewById(R.id.TextView_ECount);
						TextView_ECount.setText(String.format("%04d", Integer.valueOf(objLoginInfo.Out))+" / "+String.format("%04d", Integer.valueOf(objLoginInfo.In)));

						//TODO
						Button EditText_SStatus1 = (Button) findViewById(R.id.EditText_SStatus1);
						EditText_SStatus1.setText("02");

						TextView TextView_EStatusName1 = (TextView) findViewById(R.id.TextView_EStatusName1);
						TextView_EStatusName1.setText("配達");

						EditText_ENO1.requestFocus();
						return true;
					}
					return false;
				}
				return true;
			}
		});


		EditText_Val = (EditText)findViewById(R.id.EditText_Val);
		EditText_Val.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == 23) {
						String strText = "";

						for (int j = 0; j < listView.getAdapter().getCount(); j++) {
							ReasonItem item = (ReasonItem) listView
									.getAdapter().getItem(j);
							if (item.getNo().equals(
									EditText_Val.getText().toString())) {
								strText = item.getReason();
							}
						}

						if (strText.length() == 0) {
							clsDialog.Show(context, "提示", "代碼無效");
							EditText_Val.setText("");
							return true;
						}

						ScrollView ScrollViewT = (ScrollView) findViewById(R.id.ScrollViewT);
						LinearLayout LinearLayout_doType = (LinearLayout) findViewById(R.id.LinearLayout_doType);
						LinearLayout LinearLayout_Search = (LinearLayout)findViewById(R.id.LinearLayout_Search);
						LinearLayout_Search.setVisibility(View.GONE);
						LinearLayout LinearLayout_Start2 = (LinearLayout) findViewById(R.id.LinearLayout_Start2);
						LinearLayout LinearLayout_End2 = (LinearLayout) findViewById(R.id.LinearLayout_End2);
						//LinearLayout LinearLayout_SAddress = (LinearLayout) findViewById(R.id.LinearLayout_SAddress);
						LinearLayout LinearLayout_list = (LinearLayout) findViewById(R.id.LinearLayout_list);

						ScrollViewT.setVisibility(View.VISIBLE);
						LinearLayout_doType.setVisibility(View.GONE);
						// LinearLayout_Start1.setVisibility(View.GONE);

						if (intType == 0) {
							LinearLayout_Start2.setVisibility(View.VISIBLE);
							Button EditText_SStatus1 = (Button) findViewById(R.id.EditText_SStatus1);
							EditText_SStatus1.setText(EditText_Val.getText());

							TextView TextView_SStatusName1 = (TextView) findViewById(R.id.TextView_SStatusName1);

							TextView_SStatusName1.setText(strText);
							EditText EditForm = (EditText) findViewById(R.id.EditText_SNO1);
							EditForm.requestFocus();
						}
						if (intType == 1) {
							LinearLayout_End2.setVisibility(View.VISIBLE);
							Button EditText_EStatus1 = (Button) findViewById(R.id.EditText_EStatus1);
							EditText_EStatus1.setText(EditText_Val.getText());

							TextView TextView_EStatusName1 = (TextView) findViewById(R.id.TextView_EStatusName1);

							TextView_EStatusName1.setText(strText);
							EditText EditForm = (EditText) findViewById(R.id.EditText_ENO1);
							EditForm.requestFocus();
						}
						//LinearLayout_SAddress.setVisibility(View.GONE);
						LinearLayout_list.setVisibility(View.GONE);
						return true;
					}
					return false;
				}
				return true;
			}
		});

		Button button_Search = (Button)findViewById(R.id.button_Search);
		button_Search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SetSearch();
			}
		});

		button_DoList = (Button)findViewById(R.id.button_DoList);
		button_DoList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(InOutFrg.this, DataListFrg.class);
				startActivity(intent);
			}
		});

		button_IO = (Button)findViewById(R.id.button_IO);
		button_IO.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {


			}
		});

		button_GT = (Button)findViewById(R.id.button_GT);
		button_GT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(InOutFrg.this, GetTaskFrg.class);
				startActivity(intent);

			}
		});

		button_DoneList = (Button)findViewById(R.id.button_DoneList);
		button_DoneList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(InOutFrg.this, HistoryFragment.class);
				startActivity(intent);
			}
		});
		//登出 關閉SERVICE
		Button button_Logout = (Button)findViewById(R.id.Button_Logout);
		button_Logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(InOutFrg.this,Delay.class);
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

		button_IO.setBackgroundResource(R.drawable.menu02b);
		//return view;

		handlerTask = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				JSONObject json = (JSONObject) msg.obj;

				try {
					String Result = json.getString("Result");

					//if (Result.equals("1")) {
					TextView TextView_SAddress1 = (TextView)findViewById(R.id.TextView_SAddress1);
					TextView_SAddress1.setText(json.getString("Address"));
					//}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	public void onStart() {
		super.onStart();
		clsHttpPostAPI.handlerInOut = handlerTask;
	}

	public void onStop() {
		super.onStop();
		clsHttpPostAPI.handlerInOut =null;
	}

	private void SetSearch()
	{
		ScrollView ScrollViewT = (ScrollView) findViewById(R.id.ScrollViewT);
		ScrollViewT.setVisibility(View.VISIBLE);

		LinearLayout LinearLayout_doType = (LinearLayout) findViewById(R.id.LinearLayout_doType);
		LinearLayout LinearLayout_Start2 = (LinearLayout) findViewById(R.id.LinearLayout_Start2);
		LinearLayout LinearLayout_End2 = (LinearLayout) findViewById(R.id.LinearLayout_End2);
		LinearLayout LinearLayout_Search = (LinearLayout) findViewById(R.id.LinearLayout_Search);
		LinearLayout_doType.setVisibility(View.GONE);
		LinearLayout_Start2.setVisibility(View.GONE);
		LinearLayout_End2.setVisibility(View.GONE);
		LinearLayout_Search.setVisibility(View.VISIBLE);
	}

	private void SetListView(int pIntMode)
	{
		ScrollView ScrollViewT = (ScrollView) findViewById(R.id.ScrollViewT);
		LinearLayout LinearLayout_doType = (LinearLayout) findViewById(R.id.LinearLayout_doType);
		LinearLayout LinearLayout_Search = (LinearLayout)findViewById(R.id.LinearLayout_Search);
		LinearLayout_Search.setVisibility(View.GONE);
		LinearLayout LinearLayout_Start2 = (LinearLayout) findViewById(R.id.LinearLayout_Start2);
		LinearLayout LinearLayout_End2 = (LinearLayout) findViewById(R.id.LinearLayout_End2);
		//LinearLayout LinearLayout_SAddress = (LinearLayout) findViewById(R.id.LinearLayout_SAddress);
		LinearLayout LinearLayout_list = (LinearLayout) findViewById(R.id.LinearLayout_list);

		ScrollViewT.setVisibility(View.GONE);
		LinearLayout_doType.setVisibility(View.GONE);
		//LinearLayout_Start1.setVisibility(View.GONE);
		LinearLayout_Start2.setVisibility(View.GONE);
		LinearLayout_End2.setVisibility(View.GONE);
		//LinearLayout_SAddress.setVisibility(View.GONE);
		LinearLayout_list.setVisibility(View.VISIBLE);

		List rowitem = new ArrayList();
		String strType = "";

		if(pIntMode==0){
			rowitem.add(new ReasonItem(1, "37","外包轉出"));
			rowitem.add(new ReasonItem(2, "38","外包配達"));
			rowitem.add(new ReasonItem(3, "41","轉運積貨"));
			rowitem.add(new ReasonItem(4, "45","轉運卸貨"));
			rowitem.add(new ReasonItem(5, "52","空運積貨"));
			rowitem.add(new ReasonItem(6, "56","空運卸貨"));
			rowitem.add(new ReasonItem(7, "73","配送"));
			rowitem.add(new ReasonItem(8, "74","共配配送"));
			rowitem.add(new ReasonItem(9, "83","誤積誤訂轉出"));
			rowitem.add(new ReasonItem(10, "84","誤積誤訂轉入  "));
			rowitem.add(new ReasonItem(11, "92"," KTJ轉超峰    "));
			strType="配送";
		}

		if(pIntMode==1){
			rowitem.add(new ReasonItem(1, "00","客戶不在"));
			rowitem.add(new ReasonItem(2, "02","配達"));
			rowitem.add(new ReasonItem(3, "08","站止未領"));
			rowitem.add(new ReasonItem(4, "10","站戶拒收"));
			rowitem.add(new ReasonItem(5, "11","破損拒收"));
			rowitem.add(new ReasonItem(6, "13","客戶他遷"));
			rowitem.add(new ReasonItem(7, "16","欠採購文號"));
			rowitem.add(new ReasonItem(8, "19","另約時間配送"));
			rowitem.add(new ReasonItem(9, "22","指定收貨人不在"));

			rowitem.add(new ReasonItem(10, "24","客戶聯絡自領  "));
			rowitem.add(new ReasonItem(11, "25","節日休息節後送"));
			rowitem.add(new ReasonItem(12, "26","客戶要求改址 "));
			rowitem.add(new ReasonItem(13, "29","地址錯誤      "));
			rowitem.add(new ReasonItem(14, "40","無此收件人    "));
			rowitem.add(new ReasonItem(15, "42","公司已停業    "));
			rowitem.add(new ReasonItem(16, "46","電聯無人接聽  "));
			rowitem.add(new ReasonItem(17, "77","送回寄件人　　"));
			strType="配達";
		}

		EditText_Val.requestFocus();
		EditText_Val.setText("");
		TextView TextView_Type = (TextView)findViewById(R.id.TextView_Type);
		TextView_Type.setText(strType);

		ListViewReason adpater = new ListViewReason(InOutFrg.this, rowitem);

		listView.setAdapter(adpater);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View vi,
									int position, long id) {
				TextView TextViewNo = (TextView) vi
						.findViewById(R.id.TextViewNo);
				TextView TextViewReason = (TextView) vi
						.findViewById(R.id.TextViewReason);

				ScrollView ScrollViewT = (ScrollView) findViewById(R.id.ScrollViewT);
				LinearLayout LinearLayout_doType = (LinearLayout) findViewById(R.id.LinearLayout_doType);
				//LinearLayout LinearLayout_Start1 = (LinearLayout) findViewById(R.id.LinearLayout_Start1);
				LinearLayout LinearLayout_Start2 = (LinearLayout) findViewById(R.id.LinearLayout_Start2);
				LinearLayout LinearLayout_End2 = (LinearLayout) findViewById(R.id.LinearLayout_End2);
				//LinearLayout LinearLayout_SAddress = (LinearLayout) findViewById(R.id.LinearLayout_SAddress);
				LinearLayout LinearLayout_list = (LinearLayout) findViewById(R.id.LinearLayout_list);

				ScrollViewT.setVisibility(View.VISIBLE);
				LinearLayout_doType.setVisibility(View.GONE);
				//LinearLayout_Start1.setVisibility(View.GONE);
				if (intType == 0)
				{LinearLayout_Start2.setVisibility(View.VISIBLE);
					Button EditText_SStatus1 = (Button) findViewById(R.id.EditText_SStatus1);
					EditText_SStatus1.setText(TextViewNo.getText());

					TextView TextView_SStatusName1 = (TextView) findViewById(R.id.TextView_SStatusName1);
					TextView_SStatusName1.setText(TextViewReason.getText());
				}
				if (intType == 1)
				{LinearLayout_End2.setVisibility(View.VISIBLE);
					Button EditText_EStatus1 = (Button) findViewById(R.id.EditText_EStatus1);
					EditText_EStatus1.setText(TextViewNo.getText());

					TextView TextView_EStatusName1 = (TextView) findViewById(R.id.TextView_EStatusName1);
					TextView_EStatusName1.setText(TextViewReason.getText());
				}
				//LinearLayout_SAddress.setVisibility(View.GONE);
				LinearLayout_list.setVisibility(View.GONE);
			}
		});
	}

	private void GoInit() {
		ScrollView ScrollViewT = (ScrollView) findViewById(R.id.ScrollViewT);
		LinearLayout LinearLayout_doType = (LinearLayout) findViewById(R.id.LinearLayout_doType);
		LinearLayout LinearLayout_Search = (LinearLayout)findViewById(R.id.LinearLayout_Search);
		LinearLayout_Search.setVisibility(View.GONE);
		LinearLayout LinearLayout_Start2 = (LinearLayout) findViewById(R.id.LinearLayout_Start2);
		LinearLayout LinearLayout_End2 = (LinearLayout) findViewById(R.id.LinearLayout_End2);
		//LinearLayout LinearLayout_SAddress = (LinearLayout) findViewById(R.id.LinearLayout_SAddress);
		LinearLayout LinearLayout_list = (LinearLayout) findViewById(R.id.LinearLayout_list);

		ScrollViewT.setVisibility(View.VISIBLE);
		LinearLayout_doType.setVisibility(View.VISIBLE);
		//LinearLayout_Start1.setVisibility(View.GONE);
		LinearLayout_Start2.setVisibility(View.GONE);
		LinearLayout_End2.setVisibility(View.GONE);
		//LinearLayout_SAddress.setVisibility(View.GONE);
		LinearLayout_list.setVisibility(View.GONE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
			new AlertDialog.Builder(InOutFrg.this)
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
