package com.example.motoapp;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class clsDialog {
	public enum DialogResult
	{
		NONE,
		OK,
		CANCEL,
		YES,
		NO,
	}
	private static DialogResult CurrDialogResult = DialogResult.NONE;

	/**
	 * Get Last Dialog Result
	 * @return DialogResult
	 * @see enum DialogResult
	 */
	public static DialogResult GetResponse()
	{
		return CurrDialogResult;
	}

	/**
	 * Show user-define messsage dialog
	 * @param pContext the context
	 * @param pStrTitle the title of the dialog
	 * @param pStrContent the message of the dialog
	 * @return n/a
	 */
	public static void Show(Context pContext, String pStrTitle, String pStrContent)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(pContext);
		builder.setTitle(pStrTitle);
		builder.setMessage(pStrContent);
		builder.setNegativeButton("確定" , null);
		builder.show();
	}

	public static void ShowWithOkCancel(Context pContext, String pStrTitle, String pStrContent, String pStrOK, String pStrCancel)
	{
		AlertDialog.Builder builder = new Builder(pContext);
		builder.setMessage(pStrContent);
		builder.setTitle(pStrTitle);
		builder.setPositiveButton(pStrOK, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CurrDialogResult = DialogResult.OK;
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(pStrCancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CurrDialogResult = DialogResult.CANCEL;
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	public static void ShowWithOkCancel(Context pContext, String pStrTitle, String pStrContent)
	{
		ShowWithOkCancel(pContext, pStrTitle, pStrContent, "確認", "取消");
	}

}
