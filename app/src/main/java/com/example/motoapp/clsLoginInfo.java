package com.example.motoapp;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class clsLoginInfo {

	public dbLocations objDB;
	public Context context;
	public clsLoginInfo(Context ObjContext)
	{
		objDB = new dbLocations(ObjContext);
		context = ObjContext;
	}

	/**
	 * 設備ID
	 * */
	public String DeviceID;

	/**
	 * 使用者工號
	 * */
	public String UserID;

	/**
	 * 車號
	 * */
	public String Car;

	/**
	 * 車輛編號
	 * */
	public String CarID;

	/**
	 * GCM註冊ID
	 * */
	public String GCMID;

	/**
	 * 使用者名稱
	 * */
	public String UserName;

	/**
	 * 站所ID
	 * */
	public String StationID;

	/**
	 * 區碼ID
	 * */
	public String AreaID;

	/**
	 * 託運單號
	 * */
	public String FormNo;


	/**
	 * 站所名稱
	 * */
	public String StationName;

	/**
	 * 狀態
	 * */
	public String Status;

	/**
	 * 更新時間
	 * */
	public String UpdateTime;

	/**
	 * 攜出數
	 * */
	public String In="0";

	/**
	 * 銷卡數
	 * */
	public String Out="0";

	/**
	 * 公司名
	 * */
	public String Company="0";

	/**
	 * 運輸單號
	 * */
	public String ObuID="0";

	/**
	 * 寫入
	 * */
	public void Insert() {
		java.util.Date now = new java.util.Date();
		String strDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(now);
		UpdateTime = strDate;
		Status = "01";

		String strSQL = "Insert into tblLoginInfo(DeviceID,UserID,Car,CarID,GCMID,UserName,StationID,StationName,Status,UpdateTime,AreaID,FormNo,[In],[Out]"
				+ ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		List<String> objList = new ArrayList<>();
		objList.add(DeviceID);
		objList.add(UserID);
		objList.add(Car);
		objList.add(CarID);
		objList.add(GCMID);
		objList.add(UserName);
		objList.add(StationID);
		objList.add(StationName);
		objList.add(Status);
		objList.add(UpdateTime);
		objList.add(AreaID);
		objList.add(FormNo);
		objList.add(In);
		objList.add(Out);

		objDB.openDB();
		objDB.Delete("tblLoginInfo", "1=1");
		objDB.Insert(strSQL, objList.toArray());
		objDB.close();

	}

	/**
	 * 載入
	 * */
	public int Load() {
		int intDone = -1;
		objDB.openDB();
		Cursor objC = objDB.Load1("tblLoginInfo", "*", "1=1", null, null);
		objDB.close();

		if (objC != null && objC.getCount() > 0) {
			DeviceID = objC.getString(objC.getColumnIndex("DeviceID"));
			UserID = objC.getString(objC.getColumnIndex("UserID"));
			Car = objC.getString(objC.getColumnIndex("Car"));
			CarID = objC.getString(objC.getColumnIndex("CarID"));
			GCMID = objC.getString(objC.getColumnIndex("GCMID"));
			UserName = objC.getString(objC.getColumnIndex("UserName"));
			StationID = objC.getString(objC.getColumnIndex("StationID"));
			StationName = objC.getString(objC.getColumnIndex("StationName"));
			AreaID = objC.getString(objC.getColumnIndex("AreaID"));
			Status = objC.getString(objC.getColumnIndex("Status"));
			UpdateTime = objC.getString(objC.getColumnIndex("UpdateTime"));
			FormNo = objC.getString(objC.getColumnIndex("FormNo"));
			In = objC.getString(objC.getColumnIndex("In"));
			Out = objC.getString(objC.getColumnIndex("Out"));
			intDone = 1;
		}
		objC.close();

		return intDone;
	}

	/**
	 * 更新
	 * */
	public void Update(String pStrStatus)
	{
		java.util.Date now = new java.util.Date();
		String strDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(now);
		UpdateTime = strDate;
		Status=pStrStatus;

		String strSQL = "update tblLoginInfo set Status=?,UpdateTime=? where UserID=?";

		List<String> objList = new ArrayList<>();
		objList.add(Status);
		objList.add(UpdateTime);
		objList.add(UserID);
		objDB.openDB();
		objDB.Insert(strSQL, objList.toArray());
		objDB.close();
	}

	/**
	 * 更新
	 * */
	public void Update(String pStrStatus,String pStrStationID)
	{
		java.util.Date now = new java.util.Date();
		String strDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(now);
		UpdateTime = strDate;
		Status=pStrStatus;
		StationID = pStrStationID;
		String strSQL = "update tblLoginInfo set Status=?,StationID=?,UpdateTime=? where UserID=?";

		List<String> objList = new ArrayList<>();
		objList.add(Status);
		objList.add(StationID);
		objList.add(UpdateTime);
		objList.add(UserID);
		objDB.openDB();
		objDB.Insert(strSQL, objList.toArray());
		objDB.close();
	}

	/**
	 * 更新
	 * */
	public void UpdateInOut(String pStrType)
	{
		java.util.Date now = new java.util.Date();
		String strDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(now);

		if(pStrType.equals("In"))
		{
			this.In = String.valueOf(Integer.valueOf(this.In)+1);
		}

		if(pStrType.equals("Out"))
		{
			this.Out = String.valueOf(Integer.valueOf(this.Out)+1);
		}

		UpdateTime = strDate;
		String strSQL = "update tblLoginInfo set [In]=?,[Out]=?,UpdateTime=? where UserID=?";

		List<String> objList = new ArrayList<>();
		objList.add(this.In);
		objList.add(this.Out);
		objList.add(UpdateTime);
		objList.add(UserID);
		objDB.openDB();
		objDB.Insert(strSQL, objList.toArray());
		objDB.close();
	}

	/**
	 * 判斷隔夜問題
	 * 回傳0表示隔夜
	 * 回傳1表示沒有隔夜
	 * 回傳2表示第一次登入
	 * 回傳3表示已登出
	 * */
	public int Check() {
		int isOk = 0;
		java.util.Date now = new java.util.Date();
		String strDate = new java.text.SimpleDateFormat("yyyy-MM-dd")
				.format(now);

		if (this.UpdateTime != null && this.UpdateTime.length() > 0) {//隔夜
			if (!strDate.equals(this.UpdateTime.substring(0, 10))) {
				// Delete All Data
				objDB.openDB();
				objDB.DeleteAll();
				objDB.close();
				isOk = 0;
			}else
				isOk=1;
		}

		if (this.UpdateTime == null)
		{
			isOk=2;
		}

		if (this.Status != null && this.Status.equals("03"))
		{
			isOk=3;
		}

		return isOk;
	}

	/**
	 * 取得狀態
	 * */
	public String GetStatus()
	{
		if(Status.equals("01"))
			return "接單中";
		if(Status.equals("02"))
			return "休息中";
		if(Status.equals("03"))
			return "登出中";

		return "";
	}
}
