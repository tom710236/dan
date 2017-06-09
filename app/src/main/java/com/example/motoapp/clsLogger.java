package com.example.motoapp;

import android.util.Log;

public class clsLogger {

	public static String StrSplit = "^";
	public static boolean boolEnableDebug = true;
	public static boolean boolEnableInfo = true;
	public static boolean boolEnableWarn = true;
	public static boolean boolEnableError = true;

	/**
	 * Log "除錯"輸出
	 * @param		pStrTagName  標籤名稱
	 * @param		pStrMessage  記錄內容
	 * @return      void
	 * @see         void
	 */
	public static void Debug(String pStrTagName, String pStrMessage)
	{
		if (boolEnableDebug)
			Log.e(pStrTagName, android.text.format.DateFormat.format("hh:mm", new java.util.Date()).toString()+ StrSplit + pStrMessage);
	}

	/**
	 * Log "除錯"輸出
	 * @param		pStrTagName  標籤名稱
	 * @param		pStrMessage  記錄內容
	 * @return      void
	 * @see         void
	 */
	public static void d(String pStrTagName, String pStrMessage)
	{
		Debug(pStrTagName, pStrMessage);
	}

	/**
	 * Log "提示"輸出
	 * @param		pStrTagName  標籤名稱
	 * @param		pStrMessage  記錄內容
	 * @return      void
	 * @see         void
	 */
	public static void Information(String pStrTagName, String pStrMessage)
	{
		if (boolEnableInfo)
			Log.e(pStrTagName, pStrMessage);
	}

	/**
	 * Log "提示"輸出
	 * @param		pStrTagName  標籤名稱
	 * @param		pStrMessage  記錄內容
	 * @return      void
	 * @see         void
	 */
	public static void i(String pStrTagName, String pStrMessage)
	{
		Information(pStrTagName, pStrMessage);
	}

	/**
	 * Log "警告"輸出
	 * @param		pStrTagName  標籤名稱
	 * @param		pStrMessage  記錄內容
	 * @return      void
	 * @see         void
	 */
	public static void Warn(String pStrTagName, String pStrMessage)
	{
		if (boolEnableWarn)
			Log.e(pStrTagName, pStrMessage);
	}

	/**
	 * Log "警告"輸出
	 * @param		pStrTagName  標籤名稱
	 * @param		pStrMessage  記錄內容
	 * @return      void
	 * @see         void
	 */
	public static void w(String pStrTagName, String pStrMessage)
	{
		Warn(pStrTagName, pStrMessage);
	}



	/**
	 * Log "錯誤"輸出
	 * @param		pStrTagName  標籤名稱
	 * @param		pStrMessage  記錄內容
	 * @return      void
	 * @see         void
	 */
	public static void Error(String pStrTagName, String pStrMessage)
	{
		if (boolEnableError)
			Log.e(pStrTagName, pStrMessage);
	}

	/**
	 * Log "錯誤"輸出
	 * @param		pStrTagName  標籤名稱
	 * @param		pStrMessage  記錄內容
	 * @return      void
	 * @see         void
	 */
	public static void e(String pStrTagName, String pStrMessage)
	{
		Error(pStrTagName, pStrMessage);
	}

}
