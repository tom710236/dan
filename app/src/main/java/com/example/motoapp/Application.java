package com.example.motoapp;

import android.os.Bundle;

public class Application extends android.app.Application {
	public static String strKey="7092a3c1-8ad6-48b5-b354-577378c282a5";
	/** API */ public static final String strAPIUrl = "http://efms.hinet.net/FMS_WS/Services/API/Motor_Dispatch/";
	public static String strCaseID="";
	public static String strObuID="";
	public static String strGoMin="";
	public static Bundle objForm;
	public static Bundle objFormInfo;
	public static int intS=0;
	public static Boolean IsCreateData=false;
	public static String strCardNo="";
	public static String strDate="";
	
	public static String strUserName;
	public static String strAccount;
	public static String strPass;
	public static String strCar;
	
	public static String TestCode="0078";
	
	
	public static String strPageIndex="2";
	public static String strDeviceID;
	/** GCM RegistId */
	public static String strRegistId="serial";
	
	//public static String ChtUrl = "http://efms.hinet.net/FMS_WS/";

	//測試
	public static String ChtUrl = "http://efms.hinet.net/FMS_WSMotor/";
	public static String ShindaUrl="http://demo.shinda.com.tw:3366/KerryWeb/";
	//public static String ShindaUrl="http://demo.shinda.com.tw:7380/KerryWeb/";
}
