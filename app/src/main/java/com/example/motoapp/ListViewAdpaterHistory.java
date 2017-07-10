package com.example.motoapp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
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
			Cursor cursor= objDB.Load1("tblTask", "cStatus='71' or cStatus='81' or cStatus='2' or cStatus='3' or cStatus='09'", "cRequestDate desc", "");

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

			objDB.DBClose();
			((TextView) objLayout.findViewById(R.id.TextView_CarNo))
					.setText(Application.strCar);
			((TextView)objLayout.findViewById(R.id.TextView_DateTime))
					.setText(objT.LastDate);
			((TextView) objLayout.findViewById(R.id.TextView_CaseID))
					.setText(objT.OrderID);
			((TextView) objLayout.findViewById(R.id.editText_Address))
					.setText(objT.RecAddress);
			((TextView) objLayout.findViewById(R.id.EditText_Size))
					.setText(objT.Size);
			((TextView) objLayout.findViewById(R.id.EditText_Count))
					.setText(objT.ItemCount);
			((TextView) objLayout.findViewById(R.id.TextView_SendMan))
					.setText(objT.CustName);
			((TextView) objLayout.findViewById(R.id.TextView_GetMan))
					.setText(objT.RecName);
			((TextView) objLayout.findViewById(R.id.TextView_TEL))
					.setText(objT.RecPhone);
			((TextView) objLayout.findViewById(R.id.EditText_Money))
					.setText(objT.PayAmount);
			((TextView) objLayout.findViewById(R.id.EditText_Cash))
					.setText(objT.Cash);

		}

	}


}