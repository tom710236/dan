package com.example.motoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class dbLocations extends SQLiteOpenHelper {


	private static final String DATABASE_NAME = "dbKerry.db";	//資料庫名稱
	private static final int DATABASE_VERSION = 23;	//資料庫版本
	private SQLiteDatabase objDBLocations;

	public dbLocations(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);


		// TODO Auto-generated constructor stub
	}



	public SQLiteDatabase openDB(){
		try {
			objDBLocations = this.getWritableDatabase();
		} catch (SQLException ex) {
			Log.e("openDB", ex.getMessage());
		}
		return objDBLocations;
	}

	public void DBClose()
	{
		objDBLocations.close();
	}

	public void CheckDB()
	{
		openDB();
		if(!tabIsExist("tblTask"))
		{
			CreateTable();
		}

		if(!tabIsExist("tblLoginInfo"))
		{
			CreateLoginInfo();
		}

		if(!tabIsExist("tblAS400"))
		{
			CreateAS400();
		}
		DBClose();
	}

	public void CreateTable()
	{
		try {
			String DATABASE_CREATE_TABLE = "CREATE TABLE [tblTask]("
					+ "[cCaseID] [varchar](10) NOT NULL PRIMARY KEY,"
					+ "[cOrderID] [varchar](20) NULL,"
					+ "[cCustName] [nvarchar](50) NULL,"
					+ "[cCustPhone] [varchar](20) NULL,"
					+ "[cCustAddress] [nvarchar](200) NULL,"
					+ "[cRecName] [nvarchar](50) NULL,"
					+ "[cRecPhone] [varchar](20) NULL,"
					+ "[cRecAddress] [nvarchar](200) NULL,"
					+ "[cRequestDate] [varchar](20) NULL,"
					+ "[cDistance] [varchar](10) NULL,"
					+ "[cSize] [varchar](20) NULL,"
					+ "[cItemCount] [varchar](3) NULL,"
					+ "[cType] [varchar](1) NULL,"
					+ "[cStatus] [varchar](2) NULL,"
					+ "[cPayType] [varchar](1) NULL,"
					+ "[cPayAmount] [varchar](10) NULL,"
					+ "[cIsCreateData] [varchar](1) NULL,"
					+ "[cFailReasonID] [varchar](2) NULL,"
					+ "[cStationID] [varchar](2) NULL,"
					+ "[cRecTime] [varchar](10) NULL,"
					+ "[cCash] [varchar](10) NULL,"
					+ "[cLastDate] [varchar](20) NULL,"
					+ "[cRecPicture] [varchar](200) NULL,"
					+ "[cReqPicture] [varchar](200) NULL,"
					+ "[cLoginTime] [varchar](200) NULL"+
					")";

			// 建立config資料表，詳情請參考SQL語法
			objDBLocations.execSQL(DATABASE_CREATE_TABLE);
		} catch (Exception e) {
			Log.e("建立錯誤", String.valueOf(e));
		}


	}

	private void CreateLoginInfo()
	{
		try {
			String DATABASE_CREATE_TABLE = "CREATE TABLE [tblLoginInfo]("
					+ "[UserID] [varchar](20) NOT NULL PRIMARY KEY,"
					+ "[DeviceID] [varchar](20) NULL,"
					+ "[Car] [nvarchar](50) NULL,"
					+ "[CarID] [varchar](20) NULL,"
					+ "[GCMID] [nvarchar](200) NULL,"
					+ "[UserName] [nvarchar](50) NULL,"
					+ "[StationID] [varchar](10) NULL,"
					+ "[StationName] [nvarchar](20) NULL,"
					+ "[AreaID] [varchar](20) NULL,"
					+ "[Status] [varchar](5) NULL,"
					+ "[FormNo] [varchar](20) NULL,"
					+ "[In] [varchar](10) NULL,"
					+ "[Out] [varchar](10) NULL,"
					+ "[UpdateTime] [varchar](20) NULL)";

			// 建立config資料表，詳情請參考SQL語法
			objDBLocations.execSQL(DATABASE_CREATE_TABLE);
		} catch (Exception e) {
		}

	}

	private void CreateAS400()
	{
		try {
			String DATABASE_CREATE_TABLE = "CREATE TABLE [tblAS400]("
					+ "[HT3101] [varchar](20) NOT NULL,"
					+ "[HT3102] [varchar](20) NULL,"
					+ "[HT3103] [varchar](10) NULL,"
					+ "[HT3113] [varchar](10) NULL,"
					+ "[HT3114] [varchar](10) NULL,"
					+ "[HT3181] [varchar](10) NULL,"
					+ "[HT3182] [varchar](10) NULL,"
					+ "[HT3183] [varchar](10) NULL,"
					+ "[HT3184] [varchar](20) NULL,"
					+ "[HT3185] [varchar](10) NULL,"
					+ "[HT3186] [varchar](10) NULL,"
					+ "[HT3191] [varchar](10) NULL,"
					+ "[HT3192] [varchar](10) NULL,"
					+ "[cStatus] [varchar](10) NULL)";

			// 建立config資料表，詳情請參考SQL語法
			objDBLocations.execSQL(DATABASE_CREATE_TABLE);
		} catch (Exception e) {

		}
	}

	/**
	 * 資料表是否存在
	 * @return true存在 false不存在
	 */
	public boolean tabIsExist(String pStrTableName) {
		boolean result = false;

		Cursor cursor = null;
		try {

			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
					+ pStrTableName + "' ";
			cursor = objDBLocations.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	public Cursor RawQuery(String pStrSQL, String[] pStrArgs) {
		return objDBLocations.rawQuery(pStrSQL, pStrArgs);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS tblTask");	//刪除舊有的資料表
		//db.execSQL("ALTER TABLE tblTask ADD COLUMN cCash VARCHAR");
		onCreate(db);

	}

	/** EX：
	 * 接單時，建立任務表
	 * Insert into tblTask(cCaseID,cOrderID,cCustAddress,cDistance,cSize,cItemCount,cRequestDate,cType,cStatus) value(?,?,?,?,?,?,?,?)
	 * pObjArray = new Object[]{pStrContent,pStrCreateDT,pStrType};
	 **/
	public void InsertTask(Object[] pObjArray)
	{
		//openDB();
		try{
			String sqlstr = "Insert into tblTask(cCaseID,cOrderID,cCustAddress,cDistance,cSize,cItemCount,cRequestDate,cType,cStatus) values(?,?,?,?,?,?,?,?,'02')";
			Object[] args = pObjArray;

			objDBLocations.execSQL(sqlstr, args);
		}
		catch(Exception e)
		{
			String cc = e.getMessage();
		}
	}

	/** EX：
	 * 接單時，建立任務表
	 * Insert into tblTask(cCaseID,cOrderID,cCustAddress,cDistance,cSize,cItemCount,cRequestDate,cType,cStatus) value(?,?,?,?,?,?,?,?)
	 * pObjArray = new Object[]{pStrContent,pStrCreateDT,pStrType};
	 **/
	public void InsertTaskAllData(Object[] pObjArray)
	{
		//openDB();
		try{
			String sqlstr = "Insert into tblTask(cCaseID,cOrderID,cCustAddress,cDistance,cSize,cItemCount,cRequestDate,cType,cStatus," +
					"cCustName,cCustPhone,cRecName,cRecPhone,cRecAddress,cRequestDate,cPayType,cPayAmount,cCash" +
					") values(?,?,?,?,?,?,?,?,'41',?,?,?,?,?,?,?,?,?)";
			Object[] args = pObjArray;

			objDBLocations.execSQL(sqlstr, args);
		}
		catch(Exception e)
		{
			String cc = e.getMessage();
		}
	}

	/** EX：
	 * strSQLCommand = "Insert Into tblCarStatus (cContent, cCreateDT,cType) values (?,?,?);"
	 * pObjArray = new Object[]{pStrContent,pStrCreateDT,pStrType};
	 **/
	public void Insert(String strSQLCommand,Object[] pObjArray)
	{
		//openDB();
		try{
			String sqlstr = strSQLCommand;
			Object[] args = pObjArray;

			objDBLocations.execSQL(sqlstr, args);
		}
		catch(Exception e)
		{
			String cc = e.getMessage();

		}
	}

	/**
	 * 取得任務資料類別
	 * */
	public clsTask LoadTask(String pStrCaseID)
	{
		clsTask objTask = null;
		Cursor cursor = Load1("tblTask","cCaseID='"+pStrCaseID+"'","cCaseID ASC","1");
		if(cursor!=null && cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			objTask = new clsTask();
			objTask.CaseID = pStrCaseID;
			objTask.OrderID = cursor.getString(cursor.getColumnIndex("cOrderID"));
			objTask.CustName = cursor.getString(cursor.getColumnIndex("cCustName"));
			objTask.CustPhone = cursor.getString(cursor.getColumnIndex("cCustPhone"));
			objTask.CustAddress = cursor.getString(cursor.getColumnIndex("cCustAddress"));
			objTask.RecName = cursor.getString(cursor.getColumnIndex("cRecName"));
			objTask.RecPhone = cursor.getString(cursor.getColumnIndex("cRecPhone"));
			objTask.RecAddress = cursor.getString(cursor.getColumnIndex("cRecAddress"));
			objTask.RequestDate = cursor.getString(cursor.getColumnIndex("cRequestDate"));
			objTask.Distance = cursor.getString(cursor.getColumnIndex("cDistance"));
			objTask.Size = cursor.getString(cursor.getColumnIndex("cSize"));
			objTask.ItemCount = cursor.getString(cursor.getColumnIndex("cItemCount"));
			objTask.Type = cursor.getString(cursor.getColumnIndex("cType"));
			objTask.Status = cursor.getString(cursor.getColumnIndex("cStatus"));
			objTask.PayType = cursor.getString(cursor.getColumnIndex("cPayType"));
			objTask.PayAmount = cursor.getString(cursor.getColumnIndex("cPayAmount"));
			objTask.RecTime = cursor.getString(cursor.getColumnIndex("cRecTime"));
			objTask.Distance = cursor.getString(cursor.getColumnIndex("cDistance"));
			objTask.IsCreateData = cursor.getString(cursor.getColumnIndex("cIsCreateData"));
			objTask.FailReasonID = cursor.getString(cursor.getColumnIndex("cFailReasonID"));
			objTask.StationID = cursor.getString(cursor.getColumnIndex("cStationID"));
			objTask.Cash = cursor.getString(cursor.getColumnIndex("cCash"));
			objTask.LastDate = cursor.getString(cursor.getColumnIndex("cLastDate"));
			objTask.RecPicture = cursor.getString(cursor.getColumnIndex("cRecPicture"));
			objTask.ReqPicture = cursor.getString(cursor.getColumnIndex("cReqPicture"));
			objTask.LoginTime = cursor.getString(cursor.getColumnIndex("cLoginTime"));
			cursor.close();
		}

		return objTask;
	}

	/** EX：
	 * pStrTableName = 資料表名稱
	 * pStrOrderBy = 排序 --> cDate ASC
	 * pStrTop = 取前N筆
	 **/
	public Cursor Load1(String pStrTableName,String pStrWhere,String pStrOrderBy,String pStrTop)
	{
		try {
			Cursor mCursor = objDBLocations.query(true, pStrTableName ,null , pStrWhere, null, null, null, pStrOrderBy,pStrTop );
			if(mCursor != null){
				mCursor.moveToFirst();
			}
			return mCursor;
		} catch (Exception e) {
			Log.i("Error",e.getMessage());

		}
		return null;
	}

	/** EX：
	 * pStrTableName = 資料表名稱
	 * pStrOrderBy = 排序 --> cDate ASC
	 * pStrTop = 取前N筆
	 **/
	public Cursor Load1(String pStrTableName, String pStrColumns, String pStrWhere,String pStrOrderBy,String pStrTop)
	{
		try {
			Cursor mCursor = objDBLocations.query(true, pStrTableName ,(pStrColumns.trim().length() > 0 ? pStrColumns.split(",") : null) , pStrWhere, null, null, null, pStrOrderBy,pStrTop );
			if(mCursor != null){
				mCursor.moveToFirst();
			}
			return mCursor;
		} catch (Exception e) {
			Log.i("Error",e.getMessage());

		}
		return null;
	}

	/** EX：
	 * 無須更新的欄位請留空
	 * */
	public void UpdateTask(String pStrCustAddress, String pStrCustName,String pStrPhone,
						   String pStrRecName, String pStrRecAddress,String pStrRecPhone,String pStrPayType,String pStrPayAmount,String pStrStatus,String pStrPK,String pStrOrderID,String pStrCash) {
		ContentValues args = new ContentValues();
		if(pStrCustAddress.length()>0)
			args.put("cCustAddress", pStrCustAddress);
		if(pStrCustName.length()>0)
			args.put("cCustName", pStrCustName);
		if(pStrPhone.length()>0)
			args.put("cCustPhone", pStrPhone);
		if(pStrRecName.length()>0)
			args.put("cRecName", pStrRecName);
		if(pStrRecAddress.length()>0)
			args.put("cRecAddress", pStrRecAddress);
		if(pStrRecPhone.length()>0)
			args.put("cRecPhone", pStrRecPhone);
		if(pStrPayType.length()>0)
			args.put("cPayType", pStrPayType);
		if(pStrPayAmount.length()>0)
			args.put("cPayAmount", pStrPayAmount);
		if(pStrStatus.length()>0)
			args.put("cStatus", pStrStatus);
		if(pStrOrderID.length()>0)
			args.put("cOrderID",pStrOrderID);
		if(pStrCash.length()>0)
			args.put("cCash",pStrCash);

		objDBLocations.update("tblTask", args, "cCaseID='"+pStrPK+"'", null);

	}

	/** EX：
	 * 更新預計到達時間
	 * */
	public void UpdateTaskRecTime(String pStrRecTime,String pStrPK) {
		ContentValues args = new ContentValues();

		if(pStrRecTime.length()>0)
			args.put("cRecTime", pStrRecTime);

		objDBLocations.update("tblTask", args, "cCaseID='"+pStrPK+"'", null);
	}
	/** EX：
	 * 更新託運單照片
	 * */
	public void UpdateRecPicture(String pRecPicture, String pStrPK) {
		ContentValues args = new ContentValues();

		if(pRecPicture.length()>0)
			args.put("cRecPicture", pRecPicture);

		objDBLocations.update("tblTask", args, "cCaseID='"+pStrPK+"'", null);
	}

	/**
	 * EX： 更新狀態
	 * 1 派遣請求之接單
	 * 2 派遣請求之拒絕
	 * 3 前往取件
	 * 4 取件完成
	 * 5  回站
	 * 6 前往配送
	 * 7  已送達
	 * 8 送達失敗
	 * */
	public void UpdateTaskStatus(String pStrStatus,String pStrPK) {
		ContentValues args = new ContentValues();

		if(pStrStatus.length()>0)
			args.put("cStatus", pStrStatus);

		objDBLocations.update("tblTask", args, "cCaseID='"+pStrPK+"'", null);
	}

	/** EX：
	 * 更新是否協助建檔
	 * */
	public void UpdateTaskIsCreateData(String IsCreateData,String pStrPK) {
		ContentValues args = new ContentValues();

		if(IsCreateData.length()>0)
			args.put("cIsCreateData", IsCreateData);

		objDBLocations.update("tblTask", args, "cCaseID='"+pStrPK+"'", null);
	}

	/** EX：
	 * 更新送達失敗原因
	 * */
	public void UpdateTaskFailReasonID(String pStrFailReasonID,String pStrPK) {
		ContentValues args = new ContentValues();

		if(pStrFailReasonID.length()>0)
			args.put("cFailReasonID", pStrFailReasonID);

		objDBLocations.update("tblTask", args, "cCaseID='"+pStrPK+"'", null);
	}

	/** EX：
	 * 更新站所ID
	 * */
	public void UpdateTaskStationID(String pStrStationID,String pStrPK) {
		ContentValues args = new ContentValues();

		if(pStrStationID.length()>0)
			args.put("cStationID", pStrStationID);

		objDBLocations.update("tblTask", args, "cCaseID='"+pStrPK+"'", null);
	}

	/** EX：
	 * 更新取件單號
	 * */
	public void UpdateTaskOrdID(String pStrOrdID,String pStrPK) {
		ContentValues args = new ContentValues();

		if(pStrOrdID.length()>0)
			args.put("cOrderID", pStrOrdID);

		objDBLocations.update("tblTask", args, "cCaseID='"+pStrPK+"'", null);
	}
	/** EX：
	 * 更新付款方式
	 * */
	public void UpdateTaskPayTypeID(String pStrPayTypeID,String pStrPK) {
		ContentValues args = new ContentValues();

		if(pStrPayTypeID.length()>0)
			args.put("cPayType", pStrPayTypeID);

		objDBLocations.update("tblTask", args, "cCaseID='"+pStrPK+"'", null);
	}

	/** EX：
	 * 更新付款金額
	 * */
	public void UpdateTaskPayAmount(String pStrPayAmount,String pStrPK) {
		ContentValues args = new ContentValues();

		if(pStrPayAmount.length()>0)
			args.put("cPayAmount", pStrPayAmount);

		objDBLocations.update("tblTask", args, "cCaseID='"+pStrPK+"'", null);
	}
	/** EX：
	 * 更新代收貸款
	 * */
	public void UpdateTaskCash(String pStrCash,String pStrPK) {
		ContentValues args = new ContentValues();

		if(pStrCash.length()>0)
			args.put("cPayAmount", pStrCash);

		objDBLocations.update("tblTask", args, "cCaseID='"+pStrPK+"'", null);
	}
	/** EX：
	 * 更新時間
	 * */
	public void UpdateDate(String pStrDate,String pStrPK) {
		ContentValues args = new ContentValues();

		if(pStrDate.length()>0)
			args.put("cLastDate", pStrDate);

		objDBLocations.update("tblTask", args, "cCaseID='"+pStrPK+"'", null);
	}

	/** EX：
	 * 更新登入時間
	 * */
	public void UpdateLoginTime(String pStrLoginTime,String pStrPK) {
		ContentValues args = new ContentValues();

		if(pStrLoginTime.length()>0)
			args.put("cLastDate", pStrLoginTime);

		objDBLocations.update("tblTask", args, "cCaseID='"+pStrPK+"'", null);
	}

	public void Delete(String pStrTableName, String pStrWhere) {
		objDBLocations.delete(pStrTableName, pStrWhere, null);
	}

	public void DeleteAll() {
		try {
			objDBLocations.delete("tblTask", null, null);
			objDBLocations.delete("tblLoginInfo", null, null);
			objDBLocations.delete("tblAS400", null, null);
		} catch (Exception e) {
		}
	}

	public void DeleteAll(String pStrTableName) {
		try {
			objDBLocations.delete(pStrTableName, null, null);
		} catch (Exception e) {
		}
	}

	public void ResetTable() {
		objDBLocations.execSQL("DROP TABLE IF EXISTS tblTask"); // 刪除舊有的資料表
	}

	public void ResetTable(String pStrTableName) {
		objDBLocations.execSQL("DROP TABLE IF EXISTS " + pStrTableName);
	}



}
