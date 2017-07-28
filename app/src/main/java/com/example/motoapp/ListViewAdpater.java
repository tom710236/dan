package com.example.motoapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ListViewAdpater extends BaseAdapter {
	Context context;
	List items;
	ViewHolder holder;
	public static Handler handler = null;

	public ListViewAdpater(Context context, List items) {
		this.context = context;
		this.items = items;
	}

	// hold views for costomized listview
	private class ViewHolder {
		TextView txtFormNo;
		TextView txtStatus;
		TextView txtCaseID;
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
		convertView = null ;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview, null);
			holder = new ViewHolder();
			holder.txtFormNo = (TextView) convertView
					.findViewById(R.id.TextViewFormNo);
			holder.txtStatus = (TextView) convertView
					.findViewById(R.id.TextViewStatus);
			holder.txtCaseID = (TextView) convertView
					.findViewById(R.id.TextViewCaseID);
			holder.btnDetail = (Button) convertView
					.findViewById(R.id.button_Detail);
			holder.btnDetail.setOnClickListener(new ItemButton_Click(position,
					convertView));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		NewItem items = (NewItem) getItem(position);
		Log.i("msg", String.valueOf(position));
		holder.txtFormNo.setText(items.getFormNo());
		holder.txtStatus.setText(items.getStatus());
		holder.txtCaseID.setText(items.getCaseID());
		if (position % 2 == 0) // 0 even 1 odd..
			convertView.setBackgroundColor(Color.parseColor("#ffffff"));
		else
			convertView.setBackgroundColor(Color.parseColor("#F2F2F2"));

		return convertView;
	}

	class ItemButton_Click implements OnClickListener {
		private int position;
		private View objView;

		ItemButton_Click(int pos, View view) {
			position = pos;
			objView = view;
		}

		@Override
		public void onClick(View v) {
			int vid = v.getId();
			Application.strCaseID = ((TextView) objView
					.findViewById(R.id.TextViewCaseID)).getText().toString();

			/*
			DataListFrg.type = "2";

			LinearLayout objLayout = (LinearLayout) objView.getParent()
					.getParent();
			objLayout.setVisibility(View.GONE);

			LinearLayout layout1 = (LinearLayout) objLayout.getParent();
			ScrollView ScrollView_Step1 = (ScrollView) layout1
					.findViewById(R.id.ScrollView_Step1);
			ScrollView_Step1.setVisibility(View.VISIBLE);

			ScrollView ScrollView_Step2 = (ScrollView) layout1
					.findViewById(R.id.ScrollView_Step2);
			ScrollView_Step2.setVisibility(View.GONE);
*/
			dbLocations objDB;
			objDB = new dbLocations(context);

			/* 取出資料 */
			objDB.openDB();
			clsTask objT = objDB.LoadTask(Application.strCaseID);
			objDB.DBClose();
			objDB.close();
			if(Application.strCaseID!=null){
				Log.e("LISTVIEW",Application.strCaseID);
				Log.e("handler2", String.valueOf(handler));


			}else {
				Log.e("LISTVIEW","null");
			}
			if(handler!=null) {
				Log.e("handler", String.valueOf(handler));
				Message objMessage = new Message();
				objMessage.obj = objT.Status;
				handler.sendMessage(objMessage);
				Log.e("objMessage.obj", String.valueOf(objMessage.obj));

			}else {
				Log.e("handler3", String.valueOf(handler));

			}
/*
			((TextView) layout1.findViewById(R.id.TextView_CarNo))
					.setText(Application.strCar);
			((TextView) layout1.findViewById(R.id.TextView_DateTime))
					.setText(objT.RequestDate);
			((TextView) layout1.findViewById(R.id.TextView_OrderID))
					.setText(objT.OrderID);
			((TextView) layout1.findViewById(R.id.editText_Address))
					.setText(objT.CustAddress);
			((TextView) layout1.findViewById(R.id.EditText_Size))
					.setText(objT.Size);
*/

		}
	}

}