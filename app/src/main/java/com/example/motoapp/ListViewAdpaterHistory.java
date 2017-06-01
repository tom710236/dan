package com.example.motoapp;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.opengl.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class ListViewAdpaterHistory extends BaseAdapter {
	Context context;
	List items;
	ViewHolder holder;

	public ListViewAdpaterHistory(Context context, List items) {
		this.context = context;
		this.items = items;
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
			if (vid == holder.btnDetail.getId())
				Log.v("ola_log", String.valueOf(position));
			
		 LinearLayout objLayout = (LinearLayout)objView.getParent().getParent().getParent();
		 LinearLayout LinearLayout_list = (LinearLayout)objLayout.findViewById(R.id.LinearLayout_list);
		 ScrollView ScrollView_H1 = (ScrollView)objLayout.findViewById(R.id.ScrollView_H1);
		 LinearLayout_list.setVisibility(View.GONE);
		 ScrollView_H1.setVisibility(View.VISIBLE);
		}
	}

}