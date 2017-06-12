package com.example.motoapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

public class clsTask {
	/**
	 * 案件編號，中華電信的編號
	 * */
	public String CaseID;
	/**
	 * 託運單號，大榮的編號
	 * */
	public String OrderID;
	/**
	 * 取件人姓名
	 * */
	public String CustName;
	/**
	 * 取件人電話
	 * */
	public String CustPhone;
	/**
	 * 取件人地址
	 * */
	public String CustAddress;
	/**
	 * 收件人姓名
	 * */
	public String RecName;
	/**
	 * 收件人電話
	 * */
	public String RecPhone;
	/**
	 * 收件人地址
	 * */
	public String RecAddress;
	/**
	 * 派送時間
	 * */
	public String RequestDate;
	/**
	 * 距離
	 * */
	public String Distance;
	/**
	 * 貨品大小
	 * */
	public String Size;
	/**
	 * 貨件數
	 * */
	public String ItemCount;
	/**
	 * 任務類型 0:一般任務 1:指派任務
	 * */
	public String Type;
	/**
	 * 任務狀態
	 * */
	public String Status;
	/**
	 * 付款方式 0:月結 1:現金 2:到付
	 * */
	public String PayType;
	/**
	 * 付款金額
	 * */
	public String PayAmount;
	/**
	 * 是否協助建檔
	 * */
	public String IsCreateData;
	/**
	 * 送達失敗原因ID
	 * */
	public String FailReasonID;
	/**
	 * 預計到達時間(分鐘)
	 * */
	public String RecTime;

	/**
	 * 站所ID
	 * */
	public String StationID;

	public static String GetStatus(String pStrStatus)
	{
		if(pStrStatus.equals("02"))
			return "接單中";
		if(pStrStatus.equals("21"))
			return "接單確認";
		if(pStrStatus.equals("03"))
			return "前往取件";
		if(pStrStatus.equals("04"))
			return "取件完成";
		if(pStrStatus.equals("41"))
			return "取件完成";
		if(pStrStatus.equals("51"))
			return "回站中";
		if(pStrStatus.equals("06"))
			return "直送";
		if(pStrStatus.equals("07"))
			return "已送達";
		if(pStrStatus.equals("71"))
			return "已送達";
		if(pStrStatus.equals("08"))
			return "送達失敗";
		if(pStrStatus.equals("81"))
			return "送達失敗";
		if(pStrStatus.equals("09"))
			return "卸貨";
		if(pStrStatus.equals("2"))
			return "接單失敗";
		if(pStrStatus.equals("3"))
			return "接單逾時";
		if(pStrStatus.equals("00"))
			return "拒絕";
		return "";
	}

	public static String GetPayType(String pStrPayType)
	{
		if(pStrPayType.equals("0"))
			return "月結";
		if(pStrPayType.equals("1"))
			return "現金";
		if(pStrPayType.equals("2"))
			return "到付";
		return "";

	}

	public static void postToAS400(Context context,String pStrOrderID, String pStrStatus)
	{
		clsLoginInfo objLoginInfo=  new clsLoginInfo(context);
		objLoginInfo.Load();

		JSONObject json = new JSONObject();
		try {
			java.util.Date now = new java.util.Date();
			String strDate = new java.text.SimpleDateFormat("yyyyMMdd").format(now);
			String strMDate = String.valueOf(Integer.parseInt(strDate)-19110000);
			String strTime = new java.text.SimpleDateFormat("HHmmss").format(now);

			json.put("HT3101", pStrStatus);
			json.put("HT3102", pStrOrderID);
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
			Log.e("postToAS400", String.valueOf(json));
		} catch (Exception e) {
			// TODO: handle exception
		}


		String strPOSTData =json.toString();
		new clsHttpPostAPI().CallAPI(context, "API023", strPOSTData);

	}
}
