package com.example.motoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.motoapp.R.id.EditText_ENO1;
import static com.example.motoapp.R.id.EditText_SNO1;

public class InOutFrg extends Activity implements GestureDetector.OnGestureListener{
	MainActivity objActivity;
	ProgressDialog dialog;
	ListView listView;
	Context context;
	View view;
	clsLoginInfo objLoginInfo;
	int CARTYPE; //7配送 8配達
	int intType;
	EditText EditText_Val;
	Button button_DoList;
	Button button_IO;
	Button button_GT;
	Button button_DoneList;
	Handler handlerGCM;
	Handler handlerTask;
	Handler handlerListView;
	String BasicUrl;
	String type7="73",type8="02",typeNUM;
	String onClickNum;
	String BrushDate,BrushTime,UP_DATE,UP_TIME,today,today2;
	String ADDRESS,CASH,COD_AMT;
	private dbLocations objDB;
	ProgressDialog myDialog;
	Handler handler;
	GestureDetector detector;
	int numType = 0 ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("=====>", "GoogleFragment onCreateView");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.frg_inout);


		detector = new GestureDetector(this,this);
		//detector.setIsLongpressEnabled(true);

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


		//員工卡號姓名設定
		clsLoginInfo objL = new clsLoginInfo(context);
		objL.Load();
		TextView tID = (TextView)findViewById(R.id.TextID);
		TextView tName = (TextView)findViewById(R.id.TextName);
		tID.setText(objL.UserID);
		tName.setText(objL.UserName);

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
		//配送鍵
		Button button_doStart = (Button) findViewById(R.id.button_doStart);
		button_doStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				CARTYPE = 7;

				TextView TextView_SAddress1 = (TextView) findViewById(R.id.TextView_SAddress1);
				TextView_SAddress1.setText("");
				TextView TextView_SMoney = (TextView)findViewById(R.id.TextView_SMoney);
				TextView_SMoney.setText("");
				TextView TextView_SMoney2 = (TextView)findViewById(R.id.TextView_SMoney2);
				TextView_SMoney2.setText("");
				//
				PostCondition post = new PostCondition();
				post.run();
				//Application.strCardNo = EditNo.getText().toString();
				((TextView) findViewById(R.id.TextView_SNO1)).setText("");
				((TextView) findViewById(R.id.TextView_ENO1)).setText("");
				((TextView) findViewById(R.id.TextView_ENO2)).setText("");

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
				TextView_SOrderID2.setText(Application.strCar);
				TextView_EOrderID2.setText(Application.strCar);

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
						if (EditText_SNO1.getText().toString().length() ==10 || EditText_SNO1.getText().toString().length() ==7 ) {
							clsDialog.Show(context, "提示", "請輸入正確託運單號！");
							return true;
						}
						onClickNum =((EditText) findViewById(R.id.EditText_SNO1)).getText().toString();
						final TextView textview = (TextView) findViewById(R.id.TextView_SNO1);
						textview.setText(onClickNum);
						/**
						 * 呼叫API
						 * */
						if(!onClickNum.equals("") && onClickNum !=null && onClickNum.length()==8 || onClickNum.length()==11){

							BasicUrl = "https://ga.kerrytj.com/Cht_Motor/api/GetBasic/Get?" +
									"ID="+Application.strAccount+
									"&CAR_NO="+Application.strCar+
									"&Company="+Application.Company+
									"&BOL_NO="+((EditText) findViewById(R.id.EditText_SNO1)).getText().toString();
							//取得資訊API
							PostBasic post = new PostBasic();
							post.run();
							setDialog();
							//資訊更新API
							getBrushDate();
							//getUPDate();
							PostCondition_UP post2 = new PostCondition_UP();
							post2.run();
						}else {
							EditText_SNO1.setText("");
							clsDialog.Show(context, "提示", "請輸入正確託運單號！");

						}


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
		//配達鍵
		Button button_doEnd = (Button) findViewById(R.id.button_doEnd);
		button_doEnd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				CARTYPE = 8;

				typeNUM = null;
				onClickNum = null;

				TextView TextView_EAddress1 = (TextView)findViewById(R.id.TextView_EAddress1);
				TextView_EAddress1.setText("");
				TextView TextView_EMoney = (TextView)findViewById(R.id.TextView_EMoney);
				TextView_EMoney.setText("");
				TextView TextView_EMoney2 = (TextView)findViewById(R.id.TextView_EMoney2);
				TextView_EMoney2.setText("");

				PostCondition post = new PostCondition();
				post.run();
				//Application.strCardNo = EditNo.getText().toString();
				((TextView) findViewById(R.id.TextView_SNO1)).setText("");
				((TextView) findViewById(R.id.TextView_ENO1)).setText("");
				((TextView) findViewById(R.id.TextView_ENO2)).setText("");

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
				TextView_SOrderID2.setText(Application.strCar);
				TextView_EOrderID2.setText(Application.strCar);

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

		/* 查詢-點確認 */
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

				TextView TextView_AD = (TextView)findViewById(R.id.TextView_AD);
				TextView_AD.setText("");
				TextView TextView_ADMoney = (TextView)findViewById(R.id.TextView_ADMoney);
				TextView_ADMoney.setText("");
				TextView TextView_ADMoney2 = (TextView)findViewById(R.id.TextView_ADMoney2);
				TextView_ADMoney2.setText("");
				/**
				 * 呼叫API
				 * */
				EditText EditText_SearchVal = (EditText)findViewById(R.id.EditText_SearchVal);

				if(EditText_SearchVal.getText().toString().length()==11||EditText_SearchVal.getText().toString().length()==8){
					EditText editText = (EditText) findViewById(R.id.EditText_SearchVal);
					JSONObject json = new JSONObject();
					TextView textView = (TextView) findViewById(R.id.TextView_ENO2);
					textView.setText(editText.getText().toString());
					Log.e("查詢前", String.valueOf(json));
					onClickNum = editText.getText().toString();
					BasicUrl = "https://ga.kerrytj.com/Cht_Motor/api/GetBasic/Get?" +
							"ID="+Application.strAccount+
							"&CAR_NO="+Application.strCar+
							"&Company="+Application.Company+
							"&BOL_NO="+(editText.getText().toString());
					PostBasic post = new PostBasic();
					post.run();
					EditText editText2 = (EditText)findViewById(R.id.EditText_SearchVal);
					editText2.setText("");
					setDialog();
				}else {
					clsDialog.Show(context, "提示", "請輸入正確格式的託運單號！");
				}

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
						if (EditText_ENO1.getText().toString().length()==10 || EditText_ENO1.getText().toString().length()==7) {
							clsDialog.Show(context, "提示", "請輸入10碼以上的託運單號！");
							return true;
						}

						/**
						 * 呼叫API
						 * */
						//取得資訊API
						onClickNum =((EditText) findViewById(R.id.EditText_ENO1)).getText().toString();
						final TextView textview = (TextView) findViewById(R.id.TextView_ENO1);
						textview.setText(onClickNum);
						if(!onClickNum.equals("") && onClickNum !=null && onClickNum.length()==8 || onClickNum.length()==11){
							BasicUrl = "https://ga.kerrytj.com/Cht_Motor/api/GetBasic/Get?" +
									"ID="+Application.strAccount+
									"&CAR_NO="+Application.strCar+
									"&Company="+Application.Company+
									"&BOL_NO="+((EditText) findViewById(R.id.EditText_ENO1)).getText().toString();
							PostBasic post = new PostBasic();
							post.run();
							setDialog();
						}else {
							EditText_ENO1.setText("");
							clsDialog.Show(context, "提示", "請輸入正確託運單號！");
						}


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
		//查詢鍵
		Button button_Search = (Button) findViewById(R.id.button_Search);
		button_Search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				TextView TextView_AD = (TextView)findViewById(R.id.TextView_AD);
				TextView_AD.setText("");
				TextView TextView_ADMoney = (TextView)findViewById(R.id.TextView_ADMoney);
				TextView_ADMoney.setText("");
				TextView TextView_ADMoney2 = (TextView)findViewById(R.id.TextView_ADMoney2);
				TextView_ADMoney2.setText("");

				((TextView) findViewById(R.id.TextView_ENO2)).setText("");

				SetSearch();
			}
		});

		//上排按鈕設定
		button_DoList = (Button) findViewById(R.id.button_DoList);
		button_DoList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(InOutFrg.this, DataListFrg.class);
				startActivity(intent);
				InOutFrg.this.finish();
			}
		});

		button_IO = (Button) findViewById(R.id.button_IO);
		button_IO.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(InOutFrg.this,InOutFrg.class);
				startActivity(intent);
				InOutFrg.this.finish();
			}
		});

		button_GT = (Button) findViewById(R.id.button_GT);
		button_GT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(InOutFrg.this, GetTaskFrg.class);
				startActivity(intent);
				InOutFrg.this.finish();
			}
		});

		button_DoneList = (Button) findViewById(R.id.button_DoneList);
		button_DoneList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(InOutFrg.this, HistoryFragment.class);
				startActivity(intent);
				InOutFrg.this.finish();
			}
		});

		//手勢 滑動設定

		ScrollView ScrollViewT2 = (ScrollView) findViewById(R.id.ScrollViewT);
		ScrollViewT2.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try{
					return detector.onTouchEvent(event);
				}catch (Exception e){
					return false;
				}
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
				InOutFrg.this.finish();
			}
		});

		Button Button_Status = (Button) findViewById(R.id.Button_Status);
		Button_Status.setText(objLoginInfo.GetStatus());
		if(Application.GPS!=null && !Application.GPS.equals("")){
			Button_Status.setTextColor(Color.GREEN);
		}
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
	public void onStart() {
		super.onStart();
		GCMIntentService.handlerGCM = handlerGCM;
		clsHttpPostAPI.handlerTask = handlerTask;
		ListViewAdpater.handler = handlerListView;
	}

	public void onStop() {
		super.onStop();
		//clsHttpPostAPI.handlerInOut = null;
		//GCMIntentService.handlerGCM = null;
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

	//配送 - 按掃描
	public void onScran(View v) {


			Intent intent = new Intent(InOutFrg.this, CaptureActivity.class);
			intent.setAction(Intents.Scan.ACTION); //啟動掃描動作，一定要設定
			intent.putExtra(Intents.Scan.WIDTH, 1200); //調整掃描視窗寬度(Optional)
			intent.putExtra(Intents.Scan.HEIGHT, 675); //調整掃描視窗高度(Optional)
			intent.putExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, 100L); //設定掃描成功地顯示時間(Optional)
			intent.putExtra(Intents.Scan.PROMPT_MESSAGE, "請將條碼置於鏡頭範圍進行掃描"); //客製化掃描視窗的提示文字(Optional)
			//intent.putExtra(Scan.MODE, Scan.ONE_D_MODE);  //限制只能掃一維條碼(預設為全部條碼都支援)
			//intent.putExtra(CaptureActivity.SACN_MODE_NAME, CaptureActivity.SCAN_SIGLE_MODE);
			intent.putExtra(CaptureActivity.SACN_MODE_NAME, CaptureActivity.SCAN_BATCH_MODE);
			startActivityForResult(intent, 1);


	}
	//配達 按掃描
	public void onScran2(View v) {
		Intent intent = new Intent(InOutFrg.this, CaptureActivity.class);
		intent.setAction(Intents.Scan.ACTION); //啟動掃描動作，一定要設定
		intent.putExtra(Intents.Scan.WIDTH, 1200); //調整掃描視窗寬度(Optional)
		intent.putExtra(Intents.Scan.HEIGHT, 675); //調整掃描視窗高度(Optional)
		intent.putExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, 100L); //設定掃描成功地顯示時間(Optional)
		intent.putExtra(Intents.Scan.PROMPT_MESSAGE, "請將條碼置於鏡頭範圍進行掃描"); //客製化掃描視窗的提示文字(Optional)
		//intent.putExtra(Scan.MODE, Scan.ONE_D_MODE);  //限制只能掃一維條碼(預設為全部條碼都支援)
		//intent.putExtra(CaptureActivity.SACN_MODE_NAME, CaptureActivity.SCAN_SIGLE_MODE);
		intent.putExtra(CaptureActivity.SACN_MODE_NAME, CaptureActivity.SCAN_SIGLE_MODE);
		startActivityForResult(intent, 2);

	}
	//查詢 - 按掃描
	public void onScran3(View v) {
		Intent intent = new Intent(InOutFrg.this, CaptureActivity.class);
		intent.setAction(Intents.Scan.ACTION); //啟動掃描動作，一定要設定
		intent.putExtra(Intents.Scan.WIDTH, 1200); //調整掃描視窗寬度(Optional)
		intent.putExtra(Intents.Scan.HEIGHT, 675); //調整掃描視窗高度(Optional)
		intent.putExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, 100L); //設定掃描成功地顯示時間(Optional)
		intent.putExtra(Intents.Scan.PROMPT_MESSAGE, "請將條碼置於鏡頭範圍進行掃描"); //客製化掃描視窗的提示文字(Optional)
		//intent.putExtra(Scan.MODE, Scan.ONE_D_MODE);  //限制只能掃一維條碼(預設為全部條碼都支援)
		//intent.putExtra(CaptureActivity.SACN_MODE_NAME, CaptureActivity.SCAN_SIGLE_MODE);
		intent.putExtra(CaptureActivity.SACN_MODE_NAME, CaptureActivity.SCAN_SIGLE_MODE);
		startActivityForResult(intent, 3);

	}

	// 接收 ZXing 掃描後回傳來的結果
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//配送
		if (requestCode == 1) {
				// ZXing回傳的內容
				//取得掃描後的值 arraylist
				ArrayList num = CaptureActivity.num;
				Log.e("配送", String.valueOf(num));
				setDialog();
			if(num.size()!=0){
				for (int i = 0 ; i<num.size() ; i++) {
					final String contents;
					contents = String.valueOf(num.get(i));
					final EditText editText = (EditText) findViewById(EditText_SNO1);
					editText.setText(contents);
					TextView textView = (TextView) findViewById(R.id.TextView_SNO1);
					textView.setText(contents);
					BasicUrl = "https://ga.kerrytj.com/Cht_Motor/api/GetBasic/Get?" +
							"ID="+Application.strAccount+
							"&CAR_NO="+Application.strCar+
							"&Company="+Application.Company+
							"&BOL_NO="+contents;
					//onClickNum = contents;
					if (contents.length() == 11 || contents.length() == 8 ) {

						/**
						 * 呼叫API託運單資訊
						 * */
						//
						//PostBasic post = new PostBasic();
						//post.run();
						/**
						 *
						 */
						final OkHttpClient client = new OkHttpClient()
								.newBuilder()
								.connectTimeout(30, TimeUnit.SECONDS)
								.readTimeout(30, TimeUnit.SECONDS)
								.writeTimeout(30, TimeUnit.SECONDS)
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
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(InOutFrg.this, "請確認網路是否連線",Toast.LENGTH_SHORT).show();
										myDialog.dismiss();
									}
								});
							}

							@Override
							public void onResponse(Call call, Response response) throws IOException {
								myDialog.dismiss();
								String json = response.body().string();
								Log.e("託運單資訊回傳", json);
								parseJson(json);

							}

							private void parseJson(String json) {
								try {

									JSONArray array = new JSONArray(json);
									for(int i = 0 ; i<array.length() ; i++) {
										final JSONObject obj = array.getJSONObject(i);
										ADDRESS = String.valueOf(obj.get("ADDRES"));
										CASH = String.valueOf(obj.get("CASH"));
										COD_AMT = String.valueOf(obj.get("COD_AMT"));

										Log.e("託運單地址",ADDRESS);

									}

									if(ADDRESS.equals("null")){
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												myDialog.dismiss();
												Log.e("無此單號","無此單號");
												Toast.makeText(InOutFrg.this, "無此單號",Toast.LENGTH_SHORT).show();
											}
										});

									}else{
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												//配送資訊存進資料庫
													/*
													String Add = setEncryp(ADDRESS);
													final dbLocations objDB = new dbLocations(InOutFrg.this);
													objDB.openDB();
													objDB.InsertTask(new Object[] {
															contents,
															contents,
															Add,
															CASH,
															COD_AMT,
															null,
															today,
															"0" });
													objDB.UpdateTaskStatus("BB", contents);
													objDB.DBClose();
													*/


												//配送
												myDialog.dismiss();
												TextView TextView_SAddress1 = (TextView) findViewById(R.id.TextView_SAddress1);
												TextView_SAddress1.setText(ADDRESS);
												TextView TextView_SMoney = (TextView)findViewById(R.id.TextView_SMoney);
												TextView_SMoney.setText(CASH);
												TextView TextView_SMoney2 = (TextView)findViewById(R.id.TextView_SMoney2);
												TextView_SMoney2.setText(COD_AMT);

												//配達
												myDialog.dismiss();
												TextView TextView_EAddress1 = (TextView)findViewById(R.id.TextView_EAddress1);
												TextView_EAddress1.setText(ADDRESS);
												TextView TextView_EMoney = (TextView)findViewById(R.id.TextView_EMoney);
												TextView_EMoney.setText(CASH);
												TextView TextView_EMoney2 = (TextView)findViewById(R.id.TextView_EMoney2);
												TextView_EMoney2.setText(COD_AMT);



												//查詢
												myDialog.dismiss();
												TextView TextView_AD = (TextView)findViewById(R.id.TextView_AD);
												TextView_AD.setText(ADDRESS);
												TextView TextView_ADMoney = (TextView)findViewById(R.id.TextView_ADMoney);
												TextView_ADMoney.setText(CASH);
												TextView TextView_ADMoney2 = (TextView)findViewById(R.id.TextView_ADMoney2);
												TextView_ADMoney2.setText(COD_AMT);

											}
										});
									}

								} catch (JSONException e) {
									e.printStackTrace();
									myDialog.dismiss();
								}

							}

						});

						getBrushDate();
						//getUPDate();
						//託運單上傳
						//資訊更新API
						//PostCondition_UP post2 = new PostCondition_UP();
						//post2.run();
						/**
						 *
						 */
						//延遲上傳時間

						try {
							Thread.sleep(200); //1000為1秒
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

							final String url = "https://ga.kerrytj.com/Cht_Motor/api/Condition_UP/GET?" +
									"NUM=" + typeNUM +
									"&BOL_NO=" + contents +
									"&CAR_NO=" + Application.strCar +
									"&BrushDate=" +BrushDate+
									"&BrushTime=" +BrushTime+
									"&BrushDept=" +"0078"+
									"&Area=" +"777"+
									"&ID=" +objLoginInfo.UserID+
									"&HTnumber=" +"ABCD"+
									"&DataResource=" +"B"+
									"&BusinessID=" +"1"+
									"&UP_DATE=" + null +
									"&Company=" + Application.Company +
									"&UP_TIME="+ null ;
							final OkHttpClient client2 = new OkHttpClient()
									.newBuilder()
									.connectTimeout(15, TimeUnit.SECONDS)
									.readTimeout(15, TimeUnit.SECONDS)
									.writeTimeout(15, TimeUnit.SECONDS)
									//.addInterceptor(new LogInterceptor())
									//.addInterceptor(new TokenInterceptor())
									.sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
									.hostnameVerifier(SSLSocketClient.getHostnameVerifier())
									.build();
							final MediaType JSON
									= MediaType.parse("application/json; charset=utf-8");
							RequestBody body = RequestBody.create(JSON,url);
							final Request request2 = new Request.Builder()
									.url(url)
									.post(body)
									.build();
							Call call2 = client2.newCall(request2);
							call2.enqueue(new Callback() {
								@Override
								public void onFailure(Call call, IOException e) {
									Log.e("GetCondition_UP e", String.valueOf(e));
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											//TODO 顯示數量
											Toast.makeText(InOutFrg.this,"請確認網路是否連線",Toast.LENGTH_SHORT).show();
											myDialog.dismiss();
										}
									});
								}

								@Override
								public void onResponse(Call call, Response response) throws IOException {
									final String json = response.body().string();
									Log.e("託運單資訊更新",url);
									Log.e("託運單資訊更新回傳", json);
									Log.e("CARTYPE", String.valueOf(CARTYPE));

									if(json.equals("[{\"MESSAGE\":\"TRUE\"}]")){
										//TODO 更新數量
										objLoginInfo.UpdateInOut("In");
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												//TODO 顯示數量
												Toast.makeText(InOutFrg.this,json,Toast.LENGTH_SHORT).show();
												TextView TextView_SCount = (TextView) findViewById(R.id.TextView_SCount);
												TextView_SCount.setText(String.format("%04d", Integer.valueOf(objLoginInfo.In)));

											}
										});

									}else{
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												Toast.makeText(InOutFrg.this,json,Toast.LENGTH_SHORT).show();
											}
										});
									}

									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											TextView EditText_ENO1 = (TextView)findViewById(R.id.EditText_ENO1);
											EditText_ENO1.setText("");
											TextView EditText_SNO1 = (TextView)findViewById(R.id.EditText_SNO1);
											EditText_SNO1.setText("");
										}
									});
								}
							});



					}else{
						Toast.makeText(InOutFrg.this,"條碼格式不符",Toast.LENGTH_SHORT).show();
						myDialog.dismiss();
					}

				}
			}else {
				myDialog.dismiss();
			}


		//配達
		} else if (requestCode == 2) {
			if (resultCode == RESULT_OK) {
				// ZXing回傳的內容
				String contents = intent.getStringExtra("SCAN_RESULT");
				final EditText editText = (EditText) findViewById(R.id.EditText_ENO1);
				editText.setText(contents);
				TextView textView = (TextView) findViewById(R.id.TextView_ENO1);
				textView.setText(contents);
				onClickNum = contents;
				BasicUrl = "https://ga.kerrytj.com/Cht_Motor/api/GetBasic/Get?" +
						"ID="+Application.strAccount+
						"&CAR_NO="+Application.strCar+
						"&Company="+Application.Company+
						"&BOL_NO="+contents;
				if (editText.length() == 11 || editText.length() == 8) {

					/**
					 * 呼叫API
					 * */
					PostBasic post = new PostBasic();
					post.run();
					setDialog();

				}else {
					startActivityForResult(intent, 2);
					Toast.makeText(InOutFrg.this,"條碼格式不符",Toast.LENGTH_SHORT).show();
				}
			}
		//查詢
		} else if (requestCode == 3) {
			if (resultCode == RESULT_OK) {
				// ZXing回傳的內容
				String contents = intent.getStringExtra("SCAN_RESULT");
				final EditText editText = (EditText) findViewById(R.id.EditText_SearchVal);
				editText.setText(contents);
				TextView textView = (TextView) findViewById(R.id.TextView_ENO2);
				textView.setText(contents);
				BasicUrl = "https://ga.kerrytj.com/Cht_Motor/api/GetBasic/Get?" +
						"ID="+Application.strAccount+
						"&CAR_NO="+Application.strCar+
						"&Company="+Application.Company+
						"&BOL_NO="+contents;
				if (editText.length() == 11 || editText.length() == 8) {
					/**
					 * 呼叫API
					 * */

				}
				else {
					startActivityForResult(intent, 3);
					Toast.makeText(InOutFrg.this,"條碼格式不符",Toast.LENGTH_SHORT).show();
				}
			}

		}
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
				//Intent intent = new Intent(InOutFrg.this, GetTaskFrg.class); 這樣設定才可以點明細 原因不明
				Log.e("方向1","右邊");
				Intent intent = new Intent(InOutFrg.this, GetTaskFrg.class);
				startActivity(intent);
				this.finish();
			}else if(distance<-50){
				Intent intent = new Intent(InOutFrg.this,GetTaskFrg.class);
				startActivity(intent);
				this.finish();
				Log.e("方向1","左邊");
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
		if(distance>50){
			Log.e("方向2","右邊");
			Intent intent = new Intent(InOutFrg.this, GetTaskFrg.class);
			startActivity(intent);
			this.finish();
		}else if(distance<-50){
			Intent intent = new Intent(InOutFrg.this, DataListFrg.class);
			startActivity(intent);
			this.finish();
			Log.e("方向2","左邊");
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

	//貨況 7-配送 8-配達
	class PostCondition extends Thread {
		@Override
		public void run() {
			GetConditionInfo();
			setDialog();
		}

		private void GetConditionInfo() {


			//String url = "https://ga.kerrytj.com/Cht_Motor/api/GetEmployee/GetCondition?CARTYPE=" + CARTYPE;
			String url = "https://ga.kerrytj.com/Cht_Motor/api/GetCondition/GET?CARTYPE=" + CARTYPE+"&Company="+Application.Company;
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
				public void onFailure(Call call, final IOException e) {
					Log.e("e", String.valueOf(e));
					myDialog.dismiss();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(InOutFrg.this, "請確認網路是否連線",Toast.LENGTH_SHORT).show();
							myDialog.dismiss();
						}
					});

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
			int where73 = 0;
			int where02 = 0;
			try {
				JSONArray array = new JSONArray(json);
				final ArrayList NUMArray = new ArrayList<>();
				//預設貨況
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					String NUM = String.valueOf(obj.get("NUM"));
					String DES = String.valueOf(obj.get("DES"));
					NUMArray.add(NUM + " " + DES);
					Log.e("NUMArray", String.valueOf(NUMArray));
					Log.e("NUM", NUM);
					Log.e("DES", DES);
					where73 = NUMArray.indexOf("73 配送");
					where02 = NUMArray.indexOf("00 配達");

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

				final int finalWhere7 = where73;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						spinner.setAdapter(list);
						spinner.setSelection(finalWhere7);
						list.notifyDataSetChanged();
						myDialog.dismiss();
					}
				});
				//spinner 點擊事件
				spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						//所點擊的索引值
						int index ;
						index = spinner.getSelectedItemPosition();
						//所點擊的內容文字
						String Sname;
						Sname = spinner.getSelectedItem().toString();
						type7 = Sname.substring(0,2);
						typeNUM = type7;
						Log.e("index", String.valueOf(index));
						Log.e("type7", type7);

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// sometimes you need nothing here
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
				final int finalWhere01 = where02;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						spinner2.setAdapter(list2);
						spinner2.setSelection(finalWhere01);
						list.notifyDataSetChanged();
						myDialog.dismiss();
					}
				});
				//spinner 點擊事件
				spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						//所點擊的索引值
						int index ;
						index = spinner2.getSelectedItemPosition();
						//所點擊的內容文字
						String Sname;
						Sname = spinner2.getSelectedItem().toString();
						type8 = Sname.substring(0,2);
						typeNUM = type8;
						Log.e("index", String.valueOf(index));
						Log.e("type8", type8);
						Log.e("index", String.valueOf(index));
						Log.e("name", Sname);

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// sometimes you need nothing here
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
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
					.connectTimeout(30, TimeUnit.SECONDS)
					.readTimeout(30, TimeUnit.SECONDS)
					.writeTimeout(30, TimeUnit.SECONDS)
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
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(InOutFrg.this, "請確認網路是否連線", Toast.LENGTH_SHORT).show();
							myDialog.dismiss();
						}
					});
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {

					String json = response.body().string();
					Log.e("託運單資訊回傳", json);
					Log.e("託運單資訊回傳",BasicUrl);
					parseJson(json);

				}

				private void parseJson(String json) {
					try {
						JSONArray array = new JSONArray(json);
						for(int i = 0 ; i<array.length() ; i++) {
							JSONObject obj = array.getJSONObject(i);
							ADDRESS = String.valueOf(obj.get("ADDRES"));
							final String CASH = String.valueOf(obj.get("CASH"));
							final String COD_AMT = String.valueOf(obj.get("COD_AMT"));

							if(ADDRESS.equals("null")){
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										myDialog.dismiss();
										Log.e("無此單號","無此單號");
										Toast.makeText(InOutFrg.this, "無此單號",Toast.LENGTH_SHORT).show();
									}
								});

							}else{
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										//配送資訊存進資料庫
										/*
										final dbLocations objDB = new dbLocations(InOutFrg.this);
										String Add = setEncryp(ADDRESS);
										objDB.openDB();
										objDB.InsertTask(new Object[] {
												onClickNum,
												onClickNum,
												Add,
												CASH,
												COD_AMT,
												null,
												today,
												"0" });
										objDB.UpdateTaskStatus("BB", onClickNum);
										objDB.DBClose();
										*/


										//配送
										myDialog.dismiss();
										TextView TextView_SAddress1 = (TextView) findViewById(R.id.TextView_SAddress1);
										TextView_SAddress1.setText(ADDRESS);
										TextView TextView_SMoney = (TextView)findViewById(R.id.TextView_SMoney);
										TextView_SMoney.setText(CASH);
										TextView TextView_SMoney2 = (TextView)findViewById(R.id.TextView_SMoney2);
										TextView_SMoney2.setText(COD_AMT);

										//配達
										myDialog.dismiss();
										TextView TextView_EAddress1 = (TextView)findViewById(R.id.TextView_EAddress1);
										TextView_EAddress1.setText(ADDRESS);
										TextView TextView_EMoney = (TextView)findViewById(R.id.TextView_EMoney);
										TextView_EMoney.setText(CASH);
										TextView TextView_EMoney2 = (TextView)findViewById(R.id.TextView_EMoney2);
										TextView_EMoney2.setText(COD_AMT);



										//查詢
										myDialog.dismiss();
										TextView TextView_AD = (TextView)findViewById(R.id.TextView_AD);
										TextView_AD.setText(ADDRESS);
										TextView TextView_ADMoney = (TextView)findViewById(R.id.TextView_ADMoney);
										TextView_ADMoney.setText(CASH);
										TextView TextView_ADMoney2 = (TextView)findViewById(R.id.TextView_ADMoney2);
										TextView_ADMoney2.setText(COD_AMT);

									}
								});
							}


						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

			});
		}



	}
	//配達 確認鍵
	public void onClick (View v){
		EditText EditText_ENO1 = (EditText)findViewById(R.id.EditText_ENO1);
		if(EditText_ENO1.getText().toString().length()==11 || EditText_ENO1.getText().toString().length()==8){
			getBrushDate();
			//getUPDate();
			PostCondition_UP post = new PostCondition_UP();
			post.run();

			TextView TextView_EAddress1 = (TextView)findViewById(R.id.TextView_EAddress1);
			TextView_EAddress1.setText("");
			TextView TextView_EMoney = (TextView)findViewById(R.id.TextView_EMoney);
			TextView_EMoney.setText("");
			TextView TextView_EMoney2 = (TextView)findViewById(R.id.TextView_EMoney2);
			TextView_EMoney2.setText("");
		}else {
			clsDialog.Show(context, "提示", "請輸入正確格式託運單號！");
		}


	}
	//貨況 7-配送 8-配達
	class PostCondition_UP extends Thread {
		@Override
		public void run() {
			GetCondition_UPInfo();
		}

		private void GetCondition_UPInfo() {
			/**
			 * KTJ
			 */
			/*
			final String url = "https://ga.kerrytj.com/Cht_Motor/api/GetEmployee/Condition_UP?" +
					"NUM=" + typeNUM +
					"&BOL_NO=" + onClickNum +
					"&CAR_NO=" + Application.strCar +
					"&BrushDate=" +BrushDate+
					"&BrushTime=" +BrushTime+
					"&BrushDept=" +"0078"+
					"&Area=" +"777"+
					"&ID=" +objLoginInfo.UserID+
					"&HTnumber=" +"ABCD"+
					"&DataResource=" +"B"+
					"&BusinessID=" +"1"+
					"&UP_DATE=" + UP_DATE +
					"&UP_TIME="+ UP_TIME ;
				*/
			/**
			 * KE
			 */
			final String url = "https://ga.kerrytj.com/Cht_Motor/api/Condition_UP/GET?" +
					"NUM=" + typeNUM +
					"&BOL_NO=" + onClickNum +
					"&CAR_NO=" + Application.strCar +
					"&BrushDate=" +BrushDate+
					"&BrushTime=" +BrushTime+
					"&BrushDept=" +"0078"+
					"&Area=" +"777"+
					"&ID=" +objLoginInfo.UserID+
					"&HTnumber=" +"ABCD"+
					"&DataResource=" +"B"+
					"&BusinessID=" +"1"+
					"&UP_DATE=" + null +
					"&Company=" + Application.Company +
					"&UP_TIME="+ null ;
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
			final MediaType JSON
					= MediaType.parse("application/json; charset=utf-8");
			RequestBody body = RequestBody.create(JSON,url);
			final Request request = new Request.Builder()
					.url(url)
					.post(body)
					.build();
			Call call = client.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(InOutFrg.this, "請確認網路是否連線",Toast.LENGTH_SHORT).show();
							myDialog.dismiss();
						}
					});
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					final String json = response.body().string();
					Log.e("託運單資訊更新",url);
					Log.e("託運單資訊更新回傳", json);
					Log.e("CARTYPE", String.valueOf(CARTYPE));

					if(json.equals("[{\"MESSAGE\":\"TRUE\"}]")){
						if(CARTYPE == 8 ){
							//TODO 更新數量
							objLoginInfo.UpdateInOut("Out");
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									//TODO 顯示數量
									Toast.makeText(InOutFrg.this,json,Toast.LENGTH_SHORT).show();
									TextView TextView_ECount = (TextView) findViewById(R.id.TextView_ECount);
									TextView_ECount.setText(String.format("%04d", Integer.valueOf(objLoginInfo.Out)) + " / " + String.format("%04d", Integer.valueOf(objLoginInfo.In)));
									/*
									final dbLocations objDB = new dbLocations(InOutFrg.this);
									objDB.openDB();
									objDB.UpdateDate(today2,onClickNum);
									objDB.UpdateTaskStatus("CC",onClickNum);
									objDB.DBClose();
									*/

								}
							});
						}

						if(CARTYPE == 7 ){
							//TODO 更新數量
							objLoginInfo.UpdateInOut("In");
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									//TODO 顯示數量
									Toast.makeText(InOutFrg.this,json,Toast.LENGTH_SHORT).show();
									TextView TextView_SCount = (TextView) findViewById(R.id.TextView_SCount);
									TextView_SCount.setText(String.format("%04d", Integer.valueOf(objLoginInfo.In)));


								}
							});
						}
					}else{
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(InOutFrg.this,json,Toast.LENGTH_SHORT).show();
							}
						});
					}

					/*
					if(CARTYPE == 8 && json.equals("\"True\"") ){
						//TODO 更新數量
						objLoginInfo.UpdateInOut("Out");
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//TODO 顯示數量
								TextView TextView_ECount = (TextView) findViewById(R.id.TextView_ECount);
								TextView_ECount.setText(String.format("%04d", Integer.valueOf(objLoginInfo.Out)) + " / " + String.format("%04d", Integer.valueOf(objLoginInfo.In)));
							}
						});
					}
					if(CARTYPE == 7 && json.equals("\"True\"")){
						//TODO 更新數量
						objLoginInfo.UpdateInOut("In");
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//TODO 顯示數量
								TextView TextView_SCount = (TextView) findViewById(R.id.TextView_SCount);
								TextView_SCount.setText(String.format("%04d", Integer.valueOf(objLoginInfo.In)));
							}
						});
					}
					*/
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								TextView EditText_ENO1 = (TextView)findViewById(R.id.EditText_ENO1);
								EditText_ENO1.setText("");
								TextView EditText_SNO1 = (TextView)findViewById(R.id.EditText_SNO1);
								EditText_SNO1.setText("");
							}
						});

					}


			});
		}
	}

	private void getBrushDate() {
		Calendar mCal = Calendar.getInstance();
		String dateformat = "yyyyMMdd";
		SimpleDateFormat df = new SimpleDateFormat(dateformat);
		BrushDate = df.format(mCal.getTime());
		String dateformat2 = "HHmmss";
		df = new SimpleDateFormat(dateformat2);
		BrushTime = df.format(mCal.getTime());
		Log.e("DATE",BrushDate + BrushTime);
	}
	private void getUPDate() {
		Calendar mCal = Calendar.getInstance();

		String dateformat = "yyyyMMdd";
		SimpleDateFormat df = new SimpleDateFormat(dateformat);
		UP_DATE = df.format(mCal.getTime());
		String dateformat2 = "HHmmss";
		df = new SimpleDateFormat(dateformat2);
		UP_TIME = df.format(mCal.getTime());
		Log.e("DATE",BrushDate + BrushTime);
		String dateformat3 = "yyyy/MM/dd HH:mm:ss";
		df = new SimpleDateFormat(dateformat3);
		today = df.format(mCal.getTime());

		String dateformat4 = "HH:mm";
		df = new SimpleDateFormat(dateformat4);
		today2 = df.format(mCal.getTime());


	}
	private void setDialog(){
		myDialog = new ProgressDialog(InOutFrg.this);
		myDialog.setTitle("載入中");
		myDialog.setMessage("載入資訊中，請稍後！");
		myDialog.setButton("關閉", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				myDialog.dismiss();
			}

		});
		myDialog.setCancelable(false);
		myDialog.show();
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

}