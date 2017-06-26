package com.example.motoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.motoapp.R.id.EditText_ENO1;
import static com.example.motoapp.R.id.EditText_SNO1;

public class InOutFrg extends Activity {
	MainActivity objActivity;
	ProgressDialog dialog;
	ListView listView;
	Context context;
	View view;
	clsLoginInfo objLoginInfo;
	int CARTYPE;
	int intType;
	EditText EditText_Val;
	Button button_DoList;
	Button button_IO;
	Button button_GT;
	Button button_DoneList;

	Handler handlerTask;
	String BasicUrl;
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
		ScrollView ScrollViewT = (ScrollView) findViewById(R.id.ScrollViewT);
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
				CARTYPE = 7;
				PostCondition post = new PostCondition();
				post.run();
				//Application.strCardNo = EditNo.getText().toString();
				((TextView) findViewById(R.id.TextView_SNO1)).setText("");
				((TextView) findViewById(R.id.TextView_ENO1)).setText("");

				ScrollView ScrollViewT = (ScrollView) findViewById(R.id.ScrollViewT);
				LinearLayout LinearLayout_doType = (LinearLayout) findViewById(R.id.LinearLayout_doType);
				LinearLayout LinearLayout_Search = (LinearLayout) findViewById(R.id.LinearLayout_Search);
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
				EditText EditForm = (EditText) findViewById(EditText_SNO1);
				EditForm.requestFocus();

				//LinearLayout_SAddress.setVisibility(View.GONE);

				TextView TextView_SOrderID2 = (TextView) findViewById(R.id.TextView_SOrderID2);
				TextView TextView_EOrderID2 = (TextView) findViewById(R.id.TextView_EOrderID2);
				TextView_SOrderID2.setText(objLoginInfo.FormNo);
				TextView_EOrderID2.setText(objLoginInfo.FormNo);

				//TODO 顯示數量
				TextView TextView_SCount = (TextView) findViewById(R.id.TextView_SCount);
				TextView_SCount.setText(String.format("%04d", Integer.valueOf(objLoginInfo.In)));

			}
		});


		/* 配送-在運輸單號中點選Enter */
		final EditText EditText_SNO1 = (EditText) findViewById(R.id.EditText_SNO1);
		EditText_SNO1.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					if (keyCode == 23 || keyCode == 66) {
						EditText EditText_SNO1 = (EditText) v;
						if (EditText_SNO1.getText().toString().length() < 10) {
							clsDialog.Show(context, "提示", "請輸入10碼以上的託運單號！");
							return true;
						}
						/**
						 * 呼叫API
						 * */
						//取得資訊API
						BasicUrl = "https://ga.kerrytj.com/Cht_Motor/api/GetEmployee/GetBasic?" +
								"ID="+Application.strAccount+
								"&CAR_NO="+Application.strCar+
								"&BOL_NO="+((EditText) findViewById(R.id.EditText_SNO1)).getText().toString();
						PostBasic post = new PostBasic();
						post.run();
						//上傳AS400API
						JSONObject json = new JSONObject();
						Log.e("配送前", String.valueOf(json));
						try {
							java.util.Date now = new java.util.Date();
							String strDate = new java.text.SimpleDateFormat("yyyyMMdd").format(now);
							String strMDate = String.valueOf(Integer.parseInt(strDate) - 19110000);
							String strTime = new java.text.SimpleDateFormat("HHmmss").format(now);

							json.put("HT3101", ((Button) findViewById(R.id.EditText_SStatus1)).getText().toString());
							//json.put("HT3101", ("查詢"));
							json.put("HT3102", ((EditText) findViewById(R.id.EditText_SNO1)).getText().toString());
							json.put("HT3103", objLoginInfo.FormNo);
							json.put("HT3113", strMDate);
							json.put("HT3114", strTime);
							json.put("HT3181", Application.TestCode);
							json.put("HT3182", objLoginInfo.AreaID);
							json.put("HT3183", objLoginInfo.UserID);
							json.put("HT3184", "ABCD");
							json.put("HT3185", "B");
							json.put("HT3186", "1");
							json.put("HT3191", strDate);
							json.put("HT3192", strTime);
							Log.e("配送", String.valueOf(json));
						} catch (Exception e) {
							// TODO: handle exception
						}


						String strPOSTData = json.toString();
						new clsHttpPostAPI().CallAPI(context, "API016", strPOSTData);
						Log.e("strPOSTData", strPOSTData);
						//TODO 記單號
						TextView TextView_SNo = (TextView) findViewById(R.id.TextView_SNO1);

						TextView_SNo.setText(EditText_SNO1.getText().toString());
						String sssss = TextView_SNo.getText().toString();
						//TODO 清掉欄位
						EditText_SNO1.setText("");

						//TODO 更新數量
						objLoginInfo.UpdateInOut("In");

						//TODO 顯示數量
						TextView TextView_SCount = (TextView) findViewById(R.id.TextView_SCount);
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

				CARTYPE = 8;
				PostCondition post = new PostCondition();
				post.run();
				//Application.strCardNo = EditNo.getText().toString();
				((TextView) findViewById(R.id.TextView_SNO1)).setText("");
				((TextView) findViewById(R.id.TextView_ENO1)).setText("");

				ScrollView ScrollViewT = (ScrollView) findViewById(R.id.ScrollViewT);
				LinearLayout LinearLayout_doType = (LinearLayout) findViewById(R.id.LinearLayout_doType);
				LinearLayout LinearLayout_Search = (LinearLayout) findViewById(R.id.LinearLayout_Search);
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
				EditText EditForm = (EditText) findViewById(EditText_ENO1);
				EditForm.requestFocus();

				//LinearLayout_SAddress.setVisibility(View.GONE);

				TextView TextView_SOrderID2 = (TextView) findViewById(R.id.TextView_SOrderID2);
				TextView TextView_EOrderID2 = (TextView) findViewById(R.id.TextView_EOrderID2);
				TextView_SOrderID2.setText(objLoginInfo.FormNo);
				TextView_EOrderID2.setText(objLoginInfo.FormNo);

				//TODO 顯示數量
				TextView TextView_ECount = (TextView) findViewById(R.id.TextView_ECount);
				TextView_ECount.setText(String.format("%04d", Integer.valueOf(objLoginInfo.Out)) + " / " + String.format("%04d", Integer.valueOf(objLoginInfo.In)));

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
		});/* 查詢-點查詢 */
		Button button_Search2 = (Button) findViewById(R.id.button_Search2);
		button_Search2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/**
				 * 呼叫API
				 * */


				EditText editText = (EditText) findViewById(R.id.EditText_SearchVal);
				JSONObject json = new JSONObject();
				TextView textView = (TextView) findViewById(R.id.TextView_ENO2);
				textView.setText(editText.getText().toString());
				Log.e("查詢前", String.valueOf(json));

				BasicUrl = "https://ga.kerrytj.com/Cht_Motor/api/GetEmployee/GetBasic?" +
						"ID="+Application.strAccount+
						"&CAR_NO="+Application.strCar+
						"&BOL_NO="+(editText.getText().toString());
				PostBasic post = new PostBasic();
				post.run();

				try {
					java.util.Date now = new java.util.Date();
					String strDate = new java.text.SimpleDateFormat("yyyyMMdd").format(now);
					String strMDate = String.valueOf(Integer.parseInt(strDate) - 19110000);
					String strTime = new java.text.SimpleDateFormat("HHmmss").format(now);

					//json.put("HT3101", ((Button)findViewById(R.id.EditText_SStatus1)).getText().toString());
					//json.put("HT3101", ("查詢"));
					json.put("HT3102", (editText.getText().toString()));
					json.put("HT3184", "ABCD");
					/*
					json.put("HT3103", objLoginInfo.FormNo);
					json.put("HT3113", strMDate);
					json.put("HT3114", strTime);
					json.put("HT3181", Application.TestCode);
					json.put("HT3182", objLoginInfo.AreaID);
					json.put("HT3183", objLoginInfo.UserID);

					json.put("HT3185", "B");
					json.put("HT3186", "1");
					json.put("HT3191", strDate);
					json.put("HT3192", strTime);
					*/
					Log.e("查詢", String.valueOf(json));
				} catch (Exception e) {
					// TODO: handle exception
				}


				String strPOSTData = json.toString();
				new clsHttpPostAPI().CallAPI(context, "API024", strPOSTData);
				Log.e("strPOSTData", strPOSTData);


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
		EditText EditText_ENO1 = (EditText) findViewById(R.id.EditText_ENO1);
		EditText_ENO1.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == event.ACTION_DOWN) {
					EditText EditText_ENO1 = (EditText) v;
					if (keyCode == 23 || keyCode == 66) {
						if (EditText_ENO1.getText().toString().length() < 10) {
							clsDialog.Show(context, "提示", "請輸入10碼以上的託運單號！");
							return true;
						}

						/**
						 * 呼叫API
						 * */
						//取得資訊API
						BasicUrl = "https://ga.kerrytj.com/Cht_Motor/api/GetEmployee/GetBasic?" +
								"ID="+Application.strAccount+
								"&CAR_NO="+Application.strCar+
								"&BOL_NO="+((EditText) findViewById(R.id.EditText_ENO1)).getText().toString();
						PostBasic post = new PostBasic();
						post.run();
						//上傳AS400API
						JSONObject json = new JSONObject();
						try {
							java.util.Date now = new java.util.Date();
							String strDate = new java.text.SimpleDateFormat("yyyyMMdd").format(now);
							String strMDate = String.valueOf(Integer.parseInt(strDate) - 19110000);
							String strTime = new java.text.SimpleDateFormat("HHmmss").format(now);

							json.put("HT3101", ((Button) findViewById(R.id.EditText_EStatus1)).getText().toString());
							json.put("HT3102", ((EditText) findViewById(R.id.EditText_ENO1)).getText().toString());
							json.put("HT3103", objLoginInfo.FormNo);
							json.put("HT3113", strMDate);
							json.put("HT3114", strTime);
							json.put("HT3181", Application.TestCode);
							json.put("HT3182", objLoginInfo.AreaID);
							json.put("HT3183", objLoginInfo.UserID);
							json.put("HT3184", "ABCD");
							json.put("HT3185", "B");
							json.put("HT3186", "1");
							json.put("HT3191", strDate);
							json.put("HT3192", strTime);
						} catch (Exception e) {
							// TODO: handle exception
						}


						String strPOSTData = json.toString();
						new clsHttpPostAPI().CallAPI(context, "API023", strPOSTData);

						//TODO 記單號
						TextView TextView_ENo = (TextView) findViewById(R.id.TextView_ENO1);

						TextView_ENo.setText(EditText_ENO1.getText().toString());
						String sssss = TextView_ENo.getText().toString();
						//TODO 清掉欄位
						EditText_ENO1.setText("");

						//TODO 更新數量
						objLoginInfo.UpdateInOut("Out");

						//TODO 顯示數量
						TextView TextView_ECount = (TextView) findViewById(R.id.TextView_ECount);
						TextView_ECount.setText(String.format("%04d", Integer.valueOf(objLoginInfo.Out)) + " / " + String.format("%04d", Integer.valueOf(objLoginInfo.In)));

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


		EditText_Val = (EditText) findViewById(R.id.EditText_Val);
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
						LinearLayout LinearLayout_Search = (LinearLayout) findViewById(R.id.LinearLayout_Search);
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

		Button button_Search = (Button) findViewById(R.id.button_Search);
		button_Search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SetSearch();
			}
		});

		button_DoList = (Button) findViewById(R.id.button_DoList);
		button_DoList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(InOutFrg.this, DataListFrg.class);
				startActivity(intent);
			}
		});

		button_IO = (Button) findViewById(R.id.button_IO);
		button_IO.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {


			}
		});

		button_GT = (Button) findViewById(R.id.button_GT);
		button_GT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(InOutFrg.this, GetTaskFrg.class);
				startActivity(intent);

			}
		});

		button_DoneList = (Button) findViewById(R.id.button_DoneList);
		button_DoneList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(InOutFrg.this, HistoryFragment.class);
				startActivity(intent);
			}
		});
		//登出 關閉SERVICE
		Button button_Logout = (Button) findViewById(R.id.Button_Logout);
		button_Logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(InOutFrg.this, Delay.class);
				stopService(it);
				new clsHttpPostAPI().CallAPI(context, "API014");
			}
		});

		Button Button_Status = (Button) findViewById(R.id.Button_Status);
		Button_Status.setText(objLoginInfo.GetStatus());
		Button_Status.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (objLoginInfo.Status.equals("02"))//休息中
				{
					objLoginInfo.Update("01");
					((Button) v).setText(objLoginInfo.GetStatus());
					//呼叫API
					new clsHttpPostAPI().CallAPI(context, "API020");
				} else if (objLoginInfo.Status.equals("01"))//接單中
				{
					objLoginInfo.Update("02");
					((Button) v).setText(objLoginInfo.GetStatus());
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
					TextView TextView_SAddress1 = (TextView) findViewById(R.id.TextView_SAddress1);
					TextView_SAddress1.setText(json.getString("Address"));
					TextView textViewAD = (TextView) findViewById(R.id.TextView_AD);
					textViewAD.setText(json.getString("Address"));
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
		clsHttpPostAPI.handlerInOut = null;
	}

	private void SetSearch() {
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

	private void SetListView(int pIntMode) {
		ScrollView ScrollViewT = (ScrollView) findViewById(R.id.ScrollViewT);
		LinearLayout LinearLayout_doType = (LinearLayout) findViewById(R.id.LinearLayout_doType);
		LinearLayout LinearLayout_Search = (LinearLayout) findViewById(R.id.LinearLayout_Search);
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

		if (pIntMode == 0) {
			rowitem.add(new ReasonItem(1, "37", "外包轉出"));
			rowitem.add(new ReasonItem(2, "38", "外包配達"));
			rowitem.add(new ReasonItem(3, "41", "轉運積貨"));
			rowitem.add(new ReasonItem(4, "45", "轉運卸貨"));
			rowitem.add(new ReasonItem(5, "52", "空運積貨"));
			rowitem.add(new ReasonItem(6, "56", "空運卸貨"));
			rowitem.add(new ReasonItem(7, "73", "配送"));
			rowitem.add(new ReasonItem(8, "74", "共配配送"));
			rowitem.add(new ReasonItem(9, "83", "誤積誤訂轉出"));
			rowitem.add(new ReasonItem(10, "84", "誤積誤訂轉入  "));
			rowitem.add(new ReasonItem(11, "92", " KTJ轉超峰    "));
			strType = "配送";
		}

		if (pIntMode == 1) {
			rowitem.add(new ReasonItem(1, "00", "客戶不在"));
			rowitem.add(new ReasonItem(2, "02", "配達"));
			rowitem.add(new ReasonItem(3, "08", "站止未領"));
			rowitem.add(new ReasonItem(4, "10", "站戶拒收"));
			rowitem.add(new ReasonItem(5, "11", "破損拒收"));
			rowitem.add(new ReasonItem(6, "13", "客戶他遷"));
			rowitem.add(new ReasonItem(7, "16", "欠採購文號"));
			rowitem.add(new ReasonItem(8, "19", "另約時間配送"));
			rowitem.add(new ReasonItem(9, "22", "指定收貨人不在"));

			rowitem.add(new ReasonItem(10, "24", "客戶聯絡自領  "));
			rowitem.add(new ReasonItem(11, "25", "節日休息節後送"));
			rowitem.add(new ReasonItem(12, "26", "客戶要求改址 "));
			rowitem.add(new ReasonItem(13, "29", "地址錯誤      "));
			rowitem.add(new ReasonItem(14, "40", "無此收件人    "));
			rowitem.add(new ReasonItem(15, "42", "公司已停業    "));
			rowitem.add(new ReasonItem(16, "46", "電聯無人接聽  "));
			rowitem.add(new ReasonItem(17, "77", "送回寄件人　　"));
			strType = "配達";
		}

		EditText_Val.requestFocus();
		EditText_Val.setText("");
		TextView TextView_Type = (TextView) findViewById(R.id.TextView_Type);
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
				if (intType == 0) {
					LinearLayout_Start2.setVisibility(View.VISIBLE);
					Button EditText_SStatus1 = (Button) findViewById(R.id.EditText_SStatus1);
					EditText_SStatus1.setText(TextViewNo.getText());

					TextView TextView_SStatusName1 = (TextView) findViewById(R.id.TextView_SStatusName1);
					TextView_SStatusName1.setText(TextViewReason.getText());
				}
				if (intType == 1) {
					LinearLayout_End2.setVisibility(View.VISIBLE);
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
		LinearLayout LinearLayout_Search = (LinearLayout) findViewById(R.id.LinearLayout_Search);
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

	//配送 - 按掃描
	public void onScran(View v) {
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

	public void onScran2(View v) {
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
			startActivityForResult(intent, 2);
		}
	}

	public void onScran3(View v) {
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
			startActivityForResult(intent, 3);
		}
	}

	// 接收 ZXing 掃描後回傳來的結果
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				// ZXing回傳的內容
				String contents = intent.getStringExtra("SCAN_RESULT");
				final EditText editText = (EditText) findViewById(EditText_SNO1);
				editText.setText(contents);
				TextView textView = (TextView) findViewById(R.id.TextView_SNO1);
				textView.setText(contents);
				BasicUrl = "https://ga.kerrytj.com/Cht_Motor/api/GetEmployee/GetBasic?" +
						"ID="+Application.strAccount+
						"&CAR_NO="+Application.strCar+
						"&BOL_NO="+contents;
				if (editText.length() == 11) {
					/**
					 * 呼叫API
					 * */
					PostBasic post = new PostBasic();
					post.run();
					/*
					JSONObject json = new JSONObject();
					try {
						java.util.Date now = new java.util.Date();
						String strDate = new java.text.SimpleDateFormat("yyyyMMdd").format(now);
						String strMDate = String.valueOf(Integer.parseInt(strDate) - 19110000);
						String strTime = new java.text.SimpleDateFormat("HHmmss").format(now);

						json.put("HT3101", ((Button) findViewById(R.id.EditText_EStatus1)).getText().toString());
						//json.put("HT3102", ((EditText) findViewById(EditText_ENO1)).getText().toString());
						json.put("HT3102", contents);
						json.put("HT3103", objLoginInfo.FormNo);
						json.put("HT3113", strMDate);
						json.put("HT3114", strTime);
						json.put("HT3181", Application.TestCode);
						json.put("HT3182", objLoginInfo.AreaID);
						json.put("HT3183", objLoginInfo.UserID);
						json.put("HT3184", "ABCD");
						json.put("HT3185", "B");
						json.put("HT3186", "1");
						json.put("HT3191", strDate);
						json.put("HT3192", strTime);

					} catch (Exception e) {
						// TODO: handle exception
					}


					String strPOSTData = json.toString();
					new clsHttpPostAPI().CallAPI(context, "API016", strPOSTData);
					Log.e("strPOSTData", strPOSTData);
					//TODO 記單號
					TextView TextView_SNo = (TextView) findViewById(R.id.TextView_SNO1);

					//TextView_SNo.setText(EditText_SNO1.getText().toString());
					String sssss = TextView_SNo.getText().toString();
					//TODO 清掉欄位
					//EditText_SNO1.setText("");

					//TODO 更新數量
					objLoginInfo.UpdateInOut("In");

					//TODO 顯示數量
					TextView TextView_SCount = (TextView) findViewById(R.id.TextView_SCount);
					TextView_SCount.setText(String.format("%04d", Integer.valueOf(objLoginInfo.In)));
					//TODO
					Button EditText_SStatus1 = (Button) findViewById(R.id.EditText_SStatus1);
					EditText_SStatus1.setText("73");

					TextView TextView_EStatusName1 = (TextView) findViewById(R.id.TextView_EStatusName1);
					TextView_EStatusName1.setText("配送");

					editText.setText("");
					*/
				}

			}
		} else if (requestCode == 2) {
			if (resultCode == RESULT_OK) {
				// ZXing回傳的內容
				String contents = intent.getStringExtra("SCAN_RESULT");
				final EditText editText = (EditText) findViewById(R.id.EditText_ENO1);
				editText.setText(contents);
				TextView textView = (TextView) findViewById(R.id.TextView_ENO1);
				textView.setText(contents);
				BasicUrl = "https://ga.kerrytj.com/Cht_Motor/api/GetEmployee/GetBasic?" +
						"ID="+Application.strAccount+
						"&CAR_NO="+Application.strCar+
						"&BOL_NO="+contents;
				if (editText.length() == 11) {

					/**
					 * 呼叫API
					 * */
					PostBasic post = new PostBasic();
					post.run();
					/*
					JSONObject json = new JSONObject();
					try {
						java.util.Date now = new java.util.Date();
						String strDate = new java.text.SimpleDateFormat("yyyyMMdd").format(now);
						String strMDate = String.valueOf(Integer.parseInt(strDate) - 19110000);
						String strTime = new java.text.SimpleDateFormat("HHmmss").format(now);

						json.put("HT3101", ((Button) findViewById(R.id.EditText_EStatus1)).getText().toString());
						//json.put("HT3102", ((EditText) findViewById(R.id.EditText_ENO1)).getText().toString());
						json.put("HT3102", (contents));
						json.put("HT3103", objLoginInfo.FormNo);
						json.put("HT3113", strMDate);
						json.put("HT3114", strTime);
						json.put("HT3181", Application.TestCode);
						json.put("HT3182", objLoginInfo.AreaID);
						json.put("HT3183", objLoginInfo.UserID);
						json.put("HT3184", "ABCD");
						json.put("HT3185", "B");
						json.put("HT3186", "1");
						json.put("HT3191", strDate);
						json.put("HT3192", strTime);
					} catch (Exception e) {
						// TODO: handle exception
					}


					String strPOSTData = json.toString();
					new clsHttpPostAPI().CallAPI(context, "API023", strPOSTData);

					//TODO 記單號
					TextView TextView_ENo = (TextView) findViewById(R.id.TextView_ENO1);

					//TextView_ENo.setText(EditText_ENO1.getText().toString());
					String sssss = TextView_ENo.getText().toString();
					//TODO 清掉欄位
					//EditText_ENO1.setText("");

					//TODO 更新數量
					objLoginInfo.UpdateInOut("Out");

					//TODO 顯示數量
					TextView TextView_ECount = (TextView) findViewById(R.id.TextView_ECount);
					TextView_ECount.setText(String.format("%04d", Integer.valueOf(objLoginInfo.Out)) + " / " + String.format("%04d", Integer.valueOf(objLoginInfo.In)));

					//TODO
					Button EditText_SStatus1 = (Button) findViewById(R.id.EditText_SStatus1);
					EditText_SStatus1.setText("02");

					TextView TextView_EStatusName1 = (TextView) findViewById(R.id.TextView_EStatusName1);
					TextView_EStatusName1.setText("配達");

					//EditText_ENO1.requestFocus();
					editText.setText("");
					*/
				}
			}

		} else if (requestCode == 3) {
			if (resultCode == RESULT_OK) {
				// ZXing回傳的內容
				String contents = intent.getStringExtra("SCAN_RESULT");
				final EditText editText = (EditText) findViewById(R.id.EditText_SearchVal);
				editText.setText(contents);
				TextView textView = (TextView) findViewById(R.id.TextView_ENO2);
				textView.setText(contents);
				BasicUrl = "https://ga.kerrytj.com/Cht_Motor/api/GetEmployee/GetBasic?" +
						"ID="+Application.strAccount+
						"&CAR_NO="+Application.strCar+
						"&BOL_NO="+contents;
				if (editText.length() == 11) {
					/**
					 * 呼叫API
					 * */

					PostBasic post = new PostBasic();
					post.run();
					//EditText editText = (EditText) findViewById(R.id.EditText_SearchVal);
					/*
					JSONObject json = new JSONObject();
					Log.e("查詢前", String.valueOf(json));
					try {
						java.util.Date now = new java.util.Date();
						String strDate = new java.text.SimpleDateFormat("yyyyMMdd").format(now);
						String strMDate = String.valueOf(Integer.parseInt(strDate) - 19110000);
						String strTime = new java.text.SimpleDateFormat("HHmmss").format(now);

						//json.put("HT3101", ((Button)findViewById(R.id.EditText_SStatus1)).getText().toString());
						//json.put("HT3101", ("查詢"));
						//json.put("HT3102", (editText.getText().toString()));
						json.put("HT3102", (contents));
						json.put("HT3184", "ABCD");
					/*
					json.put("HT3103", objLoginInfo.FormNo);
					json.put("HT3113", strMDate);
					json.put("HT3114", strTime);
					json.put("HT3181", Application.TestCode);
					json.put("HT3182", objLoginInfo.AreaID);
					json.put("HT3183", objLoginInfo.UserID);

					json.put("HT3185", "B");
					json.put("HT3186", "1");
					json.put("HT3191", strDate);
					json.put("HT3192", strTime);

						Log.e("查詢", String.valueOf(json));
					} catch (Exception e) {
						// TODO: handle exception
					}


					String strPOSTData = json.toString();
					new clsHttpPostAPI().CallAPI(context, "API024", strPOSTData);
					Log.e("strPOSTData", strPOSTData);
					editText.setText("");
				*/
				}
			}

		}
	}

	//貨況 7-配送 8-配達
	class PostCondition extends Thread {
		@Override
		public void run() {
			GetConditionInfo();
		}

		private void GetConditionInfo() {


			String url = "https://ga.kerrytj.com/Cht_Motor/api/GetEmployee/GetCondition?CARTYPE=" + CARTYPE;

			final OkHttpClient client = new OkHttpClient()
					.newBuilder()
					.connectTimeout(15, TimeUnit.SECONDS)
					.readTimeout(15, TimeUnit.SECONDS)
					.writeTimeout(15, TimeUnit.SECONDS)
					//.addInterceptor(new LogInterceptor())
					//.addInterceptor(new TokenInterceptor())
					.sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
					.hostnameVerifier(SSLSocketClient.getHostnameVerifier())
					.build();
			final Request request = new Request.Builder()
					.url(url)
					.build();
			Call call = client.newCall(request);

			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					Log.e("e", String.valueOf(e));
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					String json = response.body().string();
					Log.e("貨況回傳", json);
					parseJson(json);
				}
			});

		}

		private void parseJson(String json) {
			try {
				JSONArray array = new JSONArray(json);
				final ArrayList NUMArray = new ArrayList<>();
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					String NUM = String.valueOf(obj.get("NUM"));
					String DES = String.valueOf(obj.get("DES"));
					NUMArray.add(NUM + " " + DES);
					Log.e("NUMArray", String.valueOf(NUMArray));
					Log.e("NUM", NUM);
					Log.e("DES", DES);
				}
				//配送
				//宣告並取得Spinner
				final Spinner spinner = (Spinner) findViewById(R.id.spinner);
				//設定Spinner
				final ArrayAdapter list = new ArrayAdapter<>(
						InOutFrg.this,
						android.R.layout.simple_list_item_1,
						NUMArray);

				//顯示Spinner 非主執行緒的UI 需用runOnUiThread
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						spinner.setAdapter(list);
						spinner.setSelection(1);
						list.notifyDataSetChanged();
					}
				});
				//配達
				final Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
				//設定Spinner
				final ArrayAdapter list2 = new ArrayAdapter<>(
						InOutFrg.this,
						android.R.layout.simple_list_item_1,
						NUMArray);

				//顯示Spinner 非主執行緒的UI 需用runOnUiThread
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						spinner2.setAdapter(list2);
						spinner2.setSelection(1);
						list.notifyDataSetChanged();
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Https 憑證不安全
	 * 略過憑證方法
	 */
	public static class SSLSocketClient {

		//获取这个SSLSocketFactory
		public static SSLSocketFactory getSSLSocketFactory() {
			try {
				SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, getTrustManager(), new SecureRandom());
				return sslContext.getSocketFactory();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		//获取TrustManager
		private static TrustManager[] getTrustManager() {
			TrustManager[] trustAllCerts = new TrustManager[]{
					new X509TrustManager() {
						@Override
						public void checkClientTrusted(X509Certificate[] chain, String authType) {
						}

						@Override
						public void checkServerTrusted(X509Certificate[] chain, String authType) {
						}

						@Override
						public X509Certificate[] getAcceptedIssuers() {
							return new X509Certificate[]{};
						}
					}
			};
			return trustAllCerts;
		}

		//获取HostnameVerifier
		public static HostnameVerifier getHostnameVerifier() {
			HostnameVerifier hostnameVerifier = new HostnameVerifier() {
				@Override
				public boolean verify(String s, SSLSession sslSession) {
					return true;
				}
			};
			return hostnameVerifier;
		}
	}

	//貨況 7-配送 8-配達
	class PostBasic extends Thread {
		@Override
		public void run() {
			PostBasicInfo();
		}

		private void PostBasicInfo() {

			final OkHttpClient client = new OkHttpClient()
					.newBuilder()
					.connectTimeout(15, TimeUnit.SECONDS)
					.readTimeout(15, TimeUnit.SECONDS)
					.writeTimeout(15, TimeUnit.SECONDS)
					//.addInterceptor(new LogInterceptor())
					//.addInterceptor(new TokenInterceptor())
					.sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
					.hostnameVerifier(SSLSocketClient.getHostnameVerifier())
					.build();
			final Request request = new Request.Builder()
					.url(BasicUrl)
					.build();
			Call call = client.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					Log.e("basic e", String.valueOf(e));
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					String json = response.body().string();
					Log.e("託運單資訊回傳", json);
				}
			});
		}

	}
}