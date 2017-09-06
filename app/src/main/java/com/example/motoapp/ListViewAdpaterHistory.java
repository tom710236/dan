package com.example.motoapp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListViewAdpaterHistory extends BaseAdapter implements AdapterView.OnItemClickListener {
	public static Handler handler = null;
	Context context;
	List items;
	ViewHolder holder;
	HashMap<Integer, String> addMap;
	ArrayList<Map<Integer, String>> myList;
	String today;
	public static String caseID;

	public ListViewAdpaterHistory(Context context, List items) {
		this.context = context;
		this.items = items;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}


	// hold views for costomized listview
	private class ViewHolder {
		TextView txtFormNo;
		TextView txtStatus;
		TextView txtDate;
		Button btnDetail;
	}


	@Override
	// How many items are in the data set represented by this Adapter.
	public int getCount() {
		return items.size();
	}

	@Override
	// Get the data item associated with the specified position in the data set.
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	// Get the row id associated with the specified position in the list.
	public long getItemId(int position) {
		return position;
	}

	@Override
	// Get a View that displays the data at the specified position in the data
	// set.
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		//list 下拉item不會重讀
		convertView = null ;
		if (convertView == null) {

			convertView = mInflater.inflate(R.layout.listview_history, null);
			holder = new ViewHolder();
			holder.txtFormNo = (TextView) convertView
					.findViewById(R.id.TextViewFormNo);
			holder.txtDate = (TextView) convertView
					.findViewById(R.id.TextViewDate);
			holder.txtStatus = (TextView) convertView
					.findViewById(R.id.TextViewStatus);
			holder.btnDetail = (Button) convertView
					.findViewById(R.id.button_Detail);
			holder.btnDetail.setOnClickListener(new ItemButton_Click(position,
					convertView));
			convertView.setTag(holder);


		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HistoryItem items = (HistoryItem) getItem(position);

		holder.txtFormNo.setText(items.getFormNo());
		holder.txtStatus.setText(items.getStatus());
		holder.txtDate.setText(items.getSDate());

		if (position % 2 == 0) // 0 even 1 odd..
			convertView.setBackgroundColor(Color.parseColor("#ffffff"));
		else
			convertView.setBackgroundColor(Color.parseColor("#F2F2F2"));

		return convertView;
	}

	class ItemButton_Click implements View.OnClickListener {
		private int position;
		private View objView;

		ItemButton_Click(int pos, View view) {
			position = pos;
			objView = view;
		}

		@Override
		public void onClick(View v) {

			int vid = v.getId();
			if (vid == holder.btnDetail.getId())
				Log.v("ola_log", String.valueOf(position));

			LinearLayout objLayout = (LinearLayout) objView.getParent().getParent().getParent();
			LinearLayout LinearLayout_list = (LinearLayout) objLayout.findViewById(R.id.LinearLayout_list);
			ScrollView ScrollView_H1 = (ScrollView) objLayout.findViewById(R.id.ScrollView_H1);
			LinearLayout_list.setVisibility(View.GONE);
			ScrollView_H1.setVisibility(View.VISIBLE);

			dbLocations objDB ;
			objDB = new dbLocations(context);


			objDB.openDB();
			//清單位置
			Cursor cursor= objDB.Load1("tblTask", "cStatus='71' or cStatus='81' or cStatus='2' or cStatus='3' or cStatus='09'or cStatus='00' or cStatus='CC'or cStatus='AA'or cStatus='DD'", "cRequestDate desc", "");

			List rowitem = new ArrayList();
			myList = new ArrayList<>();
			int i = 0;
			if(cursor!=null && cursor.getCount() > 0)
			{

				while(true)
				{
					String strOrderID = cursor.getString(cursor.getColumnIndex("cOrderID"));
					String strCaseID = cursor.getString(cursor.getColumnIndex("cCaseID"));
					String strStatus = cursor.getString(cursor.getColumnIndex("cCaseID"));

					rowitem.add(new HistoryItem(strOrderID,clsTask.GetStatus(strStatus),cursor.getString(cursor.getColumnIndex("cRequestDate"))));
					addMap = new LinkedHashMap<>();
					addMap.put(i,strCaseID);
					i++;
					myList.add(addMap);
					//Log.e("strCaseID",strCaseID);
					if(cursor.isLast())
						break;
					cursor.moveToNext();
				}
				Log.e("myList", String.valueOf(myList));
				cursor.close();
				Log.e("position", String.valueOf(position));
				Log.e("getItem", String.valueOf(getItemId(position)));
				Log.e("CASEID",myList.get(position).get(position));
				caseID = myList.get(position).get(position);
			}
			/* 取出資料 */

			objDB.openDB();
			clsTask objT = objDB.LoadTask(caseID);
			String RecAddress = null;
			String RecPhone = null;
			String RecName= null;
			String cCASH = null;
			String cCASH2 = null;

			if(objT.RecAddress != null && !objT.RecAddress.equals("")){
				RecAddress = setDecrypt(objT.RecAddress);
			}else {
				if(objT.CustAddress != null && !objT.CustAddress.equals("")){
					Log.e("CustAddress",objT.CustAddress);
					RecAddress = setDecrypt(objT.CustAddress);//配達
				}else {
					RecAddress = null; //拒絕

				}

			}
			if(objT.PayAmount != null && !objT.PayAmount.equals("")){
				cCASH = objT.PayAmount;
			}else if (RecAddress != null && !RecAddress.equals("")){
				cCASH = objT.Size;//配達
			}else {
				cCASH = null; //拒絕
			}
			if(objT.Cash != null && !objT.Cash.equals("")){
				cCASH2 = objT.Cash;
			}else {
				cCASH2 = objT.Distance;//配達
			}

			RecPhone = setDecrypt(objT.RecPhone);
			RecName = setDecrypt(objT.RecName);


			String CustName = setDecrypt(objT.CustName);
			String RequestDate;
			RequestDate = objT.RequestDate.substring(11,objT.RequestDate.length()-3);
			objDB.DBClose();
			((TextView) objLayout.findViewById(R.id.TextView_CarNo))
					.setText(Application.strCar);
			if(objT.LastDate != null && !objT.LastDate.equals("")){
				((TextView)objLayout.findViewById(R.id.TextView_DateTime))
						.setText(objT.LastDate);
			}else {
				((TextView)objLayout.findViewById(R.id.TextView_DateTime))
						.setText(RequestDate);
			}

			((TextView) objLayout.findViewById(R.id.TextView_CaseID))
					.setText(objT.OrderID);
			((TextView) objLayout.findViewById(R.id.editText_Address))
					.setText(RecAddress);
			((TextView) objLayout.findViewById(R.id.EditText_Size))
					.setText(objT.Size);
			((TextView) objLayout.findViewById(R.id.EditText_Count))
					.setText(objT.ItemCount);
			((TextView) objLayout.findViewById(R.id.TextView_SendMan))
					.setText(CustName);
			((TextView) objLayout.findViewById(R.id.TextView_GetMan))
					.setText(RecName);
			((TextView) objLayout.findViewById(R.id.TextView_TEL))
					.setText(RecPhone);
			((TextView) objLayout.findViewById(R.id.EditText_Money))
					.setText(cCASH);
			((TextView) objLayout.findViewById(R.id.EditText_Cash))
					.setText(cCASH2);
			if(objT.PayType!=null&&!objT.PayType.equals("")){
				if(objT.PayType.equals("1")){
					((TextView) objLayout.findViewById(R.id.TextMoney))
							.setText("現金     		：");
				}else if (objT.PayType.equals("2")){
					((TextView) objLayout.findViewById(R.id.TextMoney))
							.setText("到付     		：");
				}else if (objT.PayType.equals("0")){
					((TextView) objLayout.findViewById(R.id.TextMoney))
							.setText("月結     		：");
				}

			}else {
				((TextView) objLayout.findViewById(R.id.TextMoney))
						.setText("到付金額 ：");
			}

		}

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
	//解密
	private String setDecrypt (String DecryptString){
		if(DecryptString!=null && !DecryptString.equals("")){
			SetAES AES = new SetAES();
			EncrypMD5 encrypMD5 = new EncrypMD5();
			EncrypSHA encrypSHA = new EncrypSHA();
			try {
				byte[] TextByte2 = AES.DecryptAES(encrypMD5.eccrypt(),encrypSHA.eccrypt(), Base64.decode(DecryptString.getBytes(),Base64.DEFAULT));
				DecryptString = new String(TextByte2);

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return DecryptString;
		}
		return DecryptString;
	}

}