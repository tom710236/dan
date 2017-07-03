package com.example.motoapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;


public class clsHttpPostAPI extends Activity {

	public static Handler handlerLogin = null;
	public static Handler handlerTask = null;
	public static Handler handlerGetTask = null;
	public static Handler handlerInOut = null;
	private String strData;
	ProgressDialog progressDialog;
	Context context;
	DataListFrg dataListFrg = new DataListFrg();
	Login login = new Login();
    String regId = login.regId;
    String Account = login.Account;
	String carID = login.carID;
	GCMActivity gcm = new GCMActivity();
	String caseID = gcm.strCaseID;
	Delay delay = new Delay();
	String lon = delay.lon;
	String lan = delay.lat;
	public int from_get_json;
	public static String form_get_Result;
	ProgressDialog myDialog;
	private dbLocations objDB;

	/**
	 * API001=登入
	 * API002=接單
	 * API003=拒絕
	 * API004=前往取件
	 * API005=取件完成
	 * API006=上傳拖運單
	 * API007=前往配送
	 * API008=已送達
	 * API009=送達失敗
	 * API010=回站
	 * API011=上傳簽收單
	 * */
	public void CallAPI(Context pContext, String pStrAPI) {

		//objDB = new dbLocations(pContext);

		context = pContext;
		switch (pStrAPI) {
			case "API001":
				new Thread(Login).start(); // 登入
				break;
			case "API002":
				new Thread(form_get).start(); // 接單
				break;
			case "API003":
				new Thread(form_reject).start(); // 拒絕
				break;
			case "API004":
				objDB = new dbLocations(pContext);
				objDB.openDB();
				clsTask objT = objDB.LoadTask(Application.strCaseID);
				objDB.DBClose();
				form_Go obj = new form_Go(objT.RecTime);
				obj.start();
				//new Thread(form_Go).start(); // 前往取件
				break;
			case "API005":
				new Thread(form_success).start(); // 完成
				break;
			case "API006":
				new Thread(form_upload).start(); // 上傳拖運單
				break;
			case "API007":
				new Thread(form_Send).start(); // 前往配送 直送
				break;
			case "API008":
				new Thread(form_SendOK).start(); // 已送達
				break;
			case "API009":
				new Thread(form_SendNG).start(); // 送達失敗
				break;
			case "API010":
				new Thread(form_Back).start(); // 回站
				break;
			case "API011":
				new Thread(form_upload1).start(); // 上傳簽收單
				break;
			case "API012":
				new Thread(form_GetStation).start(); // 取得站所 配送站
				break;
			case "API013":
				new Thread(form_GetTaskData).start(); // 取得任務
				break;
			case "API014":
				new Thread(Logout).start(); // 登出
				break;
			case "API015":
				new Thread(FailReason).start(); // 取得失敗原因
				break;
			case "API016":
				new Thread(CardPost).start(); // 攜出消卡
				break;
			case "API017":
				new Thread(Resend).start(); // 續配
				break;
			case "API018":
				new Thread(Form_close).start(); // 卸集
				break;
			case "API019":
				new Thread(Form_Reset).start(); // 休息中
				break;
			case "API020":
				new Thread(Form_Get).start(); // 接單中
				break;
			case "API021":
				new Thread(LoginLog).start(); // Login Log
				break;
			case "API022":
				new Thread(LogoutLog).start(); // Logout Log
			case "API030":
				new Thread(form_next).start(); // Logout Log
				break;

		}
	}

	public void CallAPI(Context pContext, String pStrAPI,String pStrData)
	{
		strData = pStrData;
		context = pContext;
		switch (pStrAPI) {
			case "API010":
				new Thread(form_Back).start(); // 配送站
				break;
			case "API013":
				new Thread(form_GetTaskData).start(); // 取得任務
				break;
			case "API016":
				new Thread(CardPost).start(); // 攜出消卡
				break;
			case "API023":
				new Thread(CardPost_1).start(); // 攜出消卡不帶地址
				break;
			case "API024":
				new Thread(CardPost_2).start(); // 查詢
				break;
		}
	}

	Runnable Login = new Runnable() {

		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";

			try {

				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DeviceInfo.aspx?" +
						"DeviceID="+ regId +
						"&Status=1" +
						"&EmployeeID="+Application.strAccount+
						"&Odometer="+Application.strPass+
						"&TransportID="+Application.strCar+
						"&key="+Application.strKey;
						//"&lon=121.48225"+ "&lat=25.02479";
				//Log.e("strUrl",strUrl);
				clsLogger.i("Login", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");

				/*
				JSONObject json = new JSONObject(strRequestJSON);
				if(handlerLogin!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerLogin.sendMessage(objMessage);
				}
				*/

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Runnable form_get = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				clsLoginInfo objL = new clsLoginInfo(context);
				objL.Load();

				String strUrl =Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ Application.strCaseID + "&Status=1&EmployeeID=" + objL.UserID+"&key="+Application.strKey;
				clsLogger.i("form_get", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");

				JSONObject json = new JSONObject(strRequestJSON);
				from_get_json = json.getInt("Result");
				Log.e("from_get_json", String.valueOf(from_get_json));
				json.put("Type", "1");
				if(handlerTask!=null)
				Log.e("json1", String.valueOf(json));
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					form_get_Result = json.getString("Result");
					handlerTask.sendMessage(objMessage);

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

	};


	Runnable form_reject = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//
			clsLoginInfo objL = new clsLoginInfo(context);
			objL.Load();

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				strRequestJSON = objHttppost
						.Invoke(Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ Application.strCaseID+ "&Status=2&EmployeeID=" + objL.UserID+"&key="+Application.strKey, "");
				JSONObject json = new JSONObject(strRequestJSON);
				json.put("Type", "00");
				Log.e("拒絕", String.valueOf(json));
				if(handlerTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (strRequestJSON.contains("<Result>1</Result>")){
				Application.intS = 1;
				Log.e("strRequestJSON",strRequestJSON);
			}


		}

	};


	private class form_Go extends Thread{
		String strTime;
		public form_Go(String pStrTime)
		{
			strTime = pStrTime;
		}

		@Override
		public void run() {
			// your stuff
			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				/*objDB = new dbLocations(context);
				objDB.openDB();
				clsTask objT = objDB.LoadTask(Application.strCaseID);
				objDB.DBClose();*/
				clsLoginInfo objL = new clsLoginInfo(context);
				objL.Load();

				//String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ Application.strCaseID + "&Status=3&obuID=" + carID+"&key="+Application.strKey+"&ExpArrive="+strTime;
				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?key="+Application.strKey+"&caseID="+Application.strCaseID+"&Status=3&EmployeeID="+objL.UserID+"&ExpArrive="+strTime;
				clsLogger.i("form_Go", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");
				JSONObject json = new JSONObject(strRequestJSON);

				json.put("Type", "3");
				if(handlerTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/*Runnable form_Go = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				objDB = new dbLocations(context);
				objDB.openDB();
				clsTask objT = objDB.LoadTask(Application.strCaseID);
				objDB.DBClose();
				
				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ Application.strCaseID + "&Status=3&obuID=" + Application.strObuID+"&key="+Application.strKey+"&ExpArrive="+objT.RecTime;
				clsLogger.i("form_Go", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");
				JSONObject json = new JSONObject(strRequestJSON);
				
				json.put("Type", "3");
				if(handlerTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	};*/

	Runnable form_success = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				objDB = new dbLocations(context);
				objDB.openDB();
				clsTask objT = objDB.LoadTask(Application.strCaseID);
				objDB.DBClose();

				clsLoginInfo objL = new clsLoginInfo(context);
				objL.Load();

				//String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ Application.strCaseID + "&Status=4&obuID=" + objL.Car+"&key="+Application.strKey+"&PayTypeID="+objT.PayType+"&PayAmount="+objT.PayAmount+"&OrderID="+objT.OrderID;
				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?key="+Application.strKey+"&caseID="+Application.strCaseID+"&Status=4&EmployeeID="+ objL.UserID+"&PayTypeID="+objT.PayType+"&PayAmount="+objT.PayAmount;
				clsLogger.i("form_success", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");
				JSONObject json = new JSONObject(strRequestJSON);

				json.put("Type", "4");
				if(handlerTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	Runnable form_next = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				objDB = new dbLocations(context);
				objDB.openDB();
				clsTask objT = objDB.LoadTask(Application.strCaseID);
				objDB.DBClose();

				clsLoginInfo objL = new clsLoginInfo(context);
				objL.Load();

				//String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ Application.strCaseID + "&Status=4&obuID=" + objL.Car+"&key="+Application.strKey+"&PayTypeID="+objT.PayType+"&PayAmount="+objT.PayAmount+"&OrderID="+objT.OrderID;
				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?key="+Application.strKey+"&caseID="+Application.strCaseID+"&Status=4&EmployeeID="+ objL.UserID+"&PayTypeID="+objT.PayType+"&PayAmount="+objT.PayAmount;
				clsLogger.i("form_success", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");
				JSONObject json = new JSONObject(strRequestJSON);

				//json.put("Type", "4");
				if(handlerTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Runnable form_upload = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				objDB = new dbLocations(context);
				objDB.openDB();
				clsTask objT = objDB.LoadTask(Application.strCaseID);
				objDB.DBClose();

				String[] strAy = new String[5];
				strAy[0] = "caseID@" + Application.strCaseID;
				strAy[1] = "Type@1";
				strAy[2] = "FileType@jpg";
				strAy[3] = "key@"+Application.strKey;
				strAy[4] = "KeyinFile@"+(Application.IsCreateData?1:0);
				strRequestJSON = objHttppost
						.PostDataAndFile(
								Application.ChtUrl+"Services/API/Motor_Dispatch/Upload_ForwardOrder.aspx",
								strAy, "/storage/DCIM/Camera/123.jpg");

				Log.e("strRequestJSON",strRequestJSON);
				JSONObject json = new JSONObject(strRequestJSON);
				Log.e("上傳拖運單", String.valueOf(json));
				json.put("Type", "5");
				if(handlerTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};

	Runnable form_Send = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				objDB = new dbLocations(context);
				objDB.openDB();
				clsTask objT = objDB.LoadTask(Application.strCaseID);
				objDB.DBClose();

				clsLoginInfo objL = new clsLoginInfo(context);
				objL.Load();

				//String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ Application.strCaseID + "&Status=6&obuID=" + carID+"&key="+Application.strKey;
				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ objT.CaseID + "&Status=6&EmployeeID=" + objL.UserID+"&key="+Application.strKey+"&PayTypeID="+objT.PayType+"&PayAmount="+objT.PayAmount;
				clsLogger.i("form_Send", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");
				JSONObject json = new JSONObject(strRequestJSON);

				json.put("Type", "6");
				if(handlerTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Runnable form_SendOK = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				objDB = new dbLocations(context);
				objDB.openDB();
				clsTask objT = objDB.LoadTask(Application.strCaseID);
				objDB.DBClose();
				clsLoginInfo objL = new clsLoginInfo(context);
				objL.Load();

				//String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ Application.strCaseID + "&Status=7&obuID=" + objL.CarID+"&key="+Application.strKey;
				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ objT.CaseID + "&Status=7&EmployeeID=" + objL.UserID+"&key="+Application.strKey;
				clsLogger.i("form_SendOK", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");
				JSONObject json = new JSONObject(strRequestJSON);

				json.put("Type", "7");
				if(handlerTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	Runnable form_SendNG = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				objDB = new dbLocations(context);
				objDB.openDB();
				clsTask objT = objDB.LoadTask(Application.strCaseID);
				clsLoginInfo objL = new clsLoginInfo(context);
				objL.Load();
				objDB.DBClose();

				//String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ Application.strCaseID + "&Status=8&obuID=" + objL.CarID+"&key="+Application.strKey+"&FailReasonID="+objT.FailReasonID;
				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ Application.strCaseID + "&Status=8&EmployeeID=" + Account+"&key="+Application.strKey+"&FailReasonID="+objT.FailReasonID;
				clsLogger.i("form_SendNG", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");
				JSONObject json = new JSONObject(strRequestJSON);

				json.put("Type", "9");
				if(handlerTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Runnable form_Back = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				objDB = new dbLocations(context);
				objDB.openDB();
				clsTask objT = objDB.LoadTask(Application.strCaseID);
				objDB.DBClose();
				clsLoginInfo objL = new clsLoginInfo(context);
				objL.Load();

				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ Application.strCaseID + "&Status=5"+"&key="+Application.strKey+"&StationID="+objT.StationID+"&StationType="+strData+"&EmployeeID="+objL.UserID;
				clsLogger.i("form_Back", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");
				JSONObject json = new JSONObject(strRequestJSON);

				json.put("Type", "11");
				if(handlerTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Runnable form_GetStation = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				objDB = new dbLocations(context);
				objDB.openDB();
				clsTask objT = objDB.LoadTask(Application.strCaseID);
				objDB.UpdateTaskStatus("41", objT.CaseID);
				objDB.DBClose();

				clsLoginInfo objL = new clsLoginInfo(context);
				objL.Load();

				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Get_StationList.aspx?Status=5&obuID=" + carID+"&key="+Application.strKey;
				clsLogger.i("form_GetStation", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");
				JSONObject json = new JSONObject(strRequestJSON);

				json.put("Type", "10");
				if(handlerTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Runnable form_upload1 = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				objDB = new dbLocations(context);
				objDB.openDB();
				clsTask objT = objDB.LoadTask(Application.strCaseID);
				objDB.DBClose();

				String[] strAy = new String[4];
				strAy[0] = "caseID@" + Application.strCaseID;
				strAy[1] = "Type@2";
				strAy[2] = "FileType@jpg";
				strAy[3] = "key@"+Application.strKey;
				String path = context.getFilesDir() + "/DCIM/100MEDIA/test.jpg";
				strRequestJSON = objHttppost
						.PostDataAndFile(
								Application.ChtUrl+"Services/API/Motor_Dispatch/Upload_ForwardOrder.aspx",
								strAy, path);
				Log.e("strRequestJSON",strRequestJSON);

				JSONObject json = new JSONObject(strRequestJSON);
				Log.e("上傳簽收單", String.valueOf(json));
				json.put("Type", "8");
				if(handlerTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};
	//有修改
	Runnable form_GetTaskData = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//
			objDB = new dbLocations(context);
			objDB.openDB();
			objDB.Delete("tblTask", "cOrderID='"+strData+"'");
			clsTask objT = objDB.LoadTask(Application.strCaseID);
			objDB.DBClose();

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			String strUrl;
			try {
				if(strData!=null){
					strUrl =Application.ChtUrl+"Services/API/Motor_Dispatch/Get_DispatchInfo.aspx?OrderID=" + strData+"&key="+Application.strKey;
				}else{
					strUrl =Application.ChtUrl+"Services/API/Motor_Dispatch/Get_DispatchInfo.aspx?OrderID=" +objT.OrderID+"&key="+Application.strKey;
				}

				clsLogger.i("form_GetTaskData", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");

				JSONObject json = new JSONObject(strRequestJSON);
				if(handlerGetTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerGetTask.sendMessage(objMessage);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}

	};

	Runnable FailReason = new Runnable() {
		@Override
		public void run() {
			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Get_DispatchFailReason.aspx?key="+Application.strKey;
				clsLogger.i("FailReason", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");

				JSONObject json = new JSONObject(strRequestJSON);

				json.put("Type", "12");
				if (handlerTask != null) {
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Runnable CardPost = new Runnable() {
		@Override
		public void run() {
			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				String strUrl = Application.ShindaUrl+"HTF31_Send.aspx";
				clsLogger.i("CardPost", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, strData);
				Log.e("strData",strData);
				JSONObject json = new JSONObject(strRequestJSON);
				Log.e("strRequestJSON",strRequestJSON);
				Log.e("jsoncarPost", String.valueOf(json));
				if(handlerInOut!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerInOut.sendMessage(objMessage);

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	public Runnable CardPost_1 = new Runnable() {
		@Override
		public void run() {
			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				String strUrl = Application.ShindaUrl+"HTF31_Arraid.aspx";
				clsLogger.i("CardPost_1", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, strData);
                Log.e("CardPost_1 strData",strData);
				JSONObject json = new JSONObject(strRequestJSON);
				Log.e("jsoncarPost", String.valueOf(json));
				//自己補
				if(handlerInOut!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerInOut.sendMessage(objMessage);

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	public Runnable CardPost_2 = new Runnable() {
		@Override
		public void run() {
			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				String strUrl = Application.ShindaUrl+"/QueryAddr.aspx";
				clsLogger.i("CardPost_2", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, strData);
				Log.e("CardPost_2 strData",strData);
				JSONObject json = new JSONObject(strRequestJSON);
				Log.e("jsoncarPost", String.valueOf(json));
				//自己補
				if(handlerInOut!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerInOut.sendMessage(objMessage);

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Runnable Resend = new Runnable() {
		@Override
		public void run() {
			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				clsLoginInfo objL = new clsLoginInfo(context);
				objL.Load();

				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ Application.strCaseID + "&Status=9&obuID=" + objL.Car+"&key="+Application.strKey+"&EmployeeID="+objL.UserID;
				clsLogger.i("Resend", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");
				JSONObject json = new JSONObject(strRequestJSON);

				json.put("Type", "13");
				if(handlerTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Runnable Form_close = new Runnable() {
		@Override
		public void run() {
			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				clsLoginInfo objL = new clsLoginInfo(context);
				objL.Load();

				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DispatchStatus.aspx?CaseID="+ Application.strCaseID + "&Status=10&obuID=" + objL.Car+"&key="+Application.strKey+"&EmployeeID="+objL.UserID;
				clsLogger.i("Form_close", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");
				JSONObject json = new JSONObject(strRequestJSON);

				json.put("Type", "14");
				if(handlerTask!=null)
				{
					Message objMessage = new Message();
					objMessage.obj = json;
					handlerTask.sendMessage(objMessage);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Runnable Logout = new Runnable() {
		@Override
		public void run() {

			//
			// TODO: http request.
			//
            clsHttpPost objHttppost = new clsHttpPost();
            String strRequestJSON = "";
			clsLoginInfo objL = new clsLoginInfo(context);
			objL.Load();

			try {
				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DeviceInfo.aspx?" +
						"DeviceID="+ regId +
						"&Status=2" +
						"&EmployeeID="+objL.UserID+
						"&Odometer="+Application.strPass+
						"&TransportID="+Application.strCar+
						"&key="+Application.strKey;
						//"&lon=121.48225"+ "&lat=25.02479";
						/*
						Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DeviceInfo.aspx?" +
						"ObuID=" + objL.CarID +
						"&Status=2&key="+Application.strKey+
						"&lon=121.48225"+
						"&lat=25.02479" ;

						"DeviceID=123" +
						"&Status=1" +
						"&EmployeeID="+Application.strAccount+
						"&Password="+Application.strPass+
						"&CarNo="+Application.strCar+
						"&key="+Application.strKey+
						"&lon=121.48225"+ "&lat=25.02479";
				 */
				clsLogger.i("Logout", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");
				JSONObject json = new JSONObject(strRequestJSON);
				if(json.getString("Result").equals("1"))
				{
					objL.Update("03");
					new Thread(LogoutLog).start();
					Intent intent = new Intent(context, Login.class);
					//startActivity(intent);
					context.startActivity(intent);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Runnable Form_Reset = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				clsLoginInfo objL = new clsLoginInfo(context);
				objL.Load();

				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DeviceInfo.aspx?" +
						"DeviceID="+ regId +
						"&Status=4" +
						"&EmployeeID="+objL.UserID+
						"&Odometer="+Application.strPass+
						"&TransportID="+Application.strCar+
						"&key="+Application.strKey;

				clsLogger.i("Form_Reset", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");
				JSONObject json = new JSONObject(strRequestJSON);


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Runnable Form_Get = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				clsLoginInfo objL = new clsLoginInfo(context);
				objL.Load();

				String strUrl = Application.ChtUrl+"Services/API/Motor_Dispatch/Send_DeviceInfo.aspx?" +
						"DeviceID="+ regId +
						"&Status=3" +
						"&EmployeeID="+objL.UserID+
						"&Odometer="+Application.strPass+
						"&TransportID="+Application.strCar+
						"&key="+Application.strKey;
				clsLogger.i("Form_Get", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, "");
				JSONObject json = new JSONObject(strRequestJSON);


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Runnable LoginLog = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsLoginInfo objLoginInfo = new clsLoginInfo(context);
			objLoginInfo.Load();

			JSONObject json = new JSONObject();
			try {
				java.util.Date now = new java.util.Date();
				String strDate = new java.text.SimpleDateFormat("yyyyMMdd").format(now);
				String strTime = new java.text.SimpleDateFormat("HHmmss").format(now);

				json.put("HT1001", objLoginInfo.DeviceID);
				json.put("HT1002", objLoginInfo.UserID);
				json.put("HT1003", objLoginInfo.StationID);
				json.put("HT1004", objLoginInfo.Car);
				json.put("HT1005", objLoginInfo.AreaID);
				json.put("HT1006", "");
				json.put("HT1007", "1");
				json.put("HT1008", "N");
				json.put("HT1013", strDate);
				json.put("HT1014", strTime);
				json.put("HT1091", strDate);
				json.put("HT1092", strTime);


			} catch (Exception e) {
				// TODO: handle exception
			}

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				String strUrl = Application.ShindaUrl+"HTF10.aspx";
				clsLogger.i("Login", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, json.toString());
				JSONObject json1 = new JSONObject(strRequestJSON);
				String strCode = json1.getString("Result");
				if(strCode!="")
				{
					objLoginInfo.StationID = strCode;
					objLoginInfo.Update(objLoginInfo.Status,objLoginInfo.StationID);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Runnable LogoutLog = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//

			clsLoginInfo objLoginInfo = new clsLoginInfo(context);
			objLoginInfo.Load();

			JSONObject json = new JSONObject();
			try {
				java.util.Date now = new java.util.Date();
				String strDate = new java.text.SimpleDateFormat("yyyyMMdd").format(now);
				String strTime = new java.text.SimpleDateFormat("HHmmss").format(now);

				json.put("HT1001", objLoginInfo.DeviceID);
				json.put("HT1002", objLoginInfo.UserID);
				json.put("HT1003", objLoginInfo.StationID);
				json.put("HT1004", objLoginInfo.Car);
				json.put("HT1005", objLoginInfo.AreaID);
				json.put("HT1006", "");
				json.put("HT1007", "0");
				json.put("HT1008", "Y");
				json.put("HT1013", strDate);
				json.put("HT1014", strTime);
				json.put("HT1091", strDate);
				json.put("HT1092", strTime);


			} catch (Exception e) {
				// TODO: handle exception
			}

			clsHttpPost objHttppost = new clsHttpPost();
			String strRequestJSON = "";
			try {
				String strUrl = Application.ShindaUrl+"HTF10.aspx";
				clsLogger.i("Logout", strUrl);
				strRequestJSON = objHttppost.Invoke(strUrl, json.toString());
				JSONObject json1 = new JSONObject(strRequestJSON);
				String strCode = json1.getString("Result");


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

}
