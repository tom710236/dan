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
import android.widget.TextView;

public class ListViewReason extends BaseAdapter {
	Context context;
	List items;
	ViewHolder holder;

	public ListViewReason(Context context, List items) {
		this.context = context;
		this.items = items;
	}

	// hold views for costomized listview
	private class ViewHolder {
		TextView txtIndex;
		TextView txtNo;
		TextView txtReason;
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
			convertView = mInflater.inflate(R.layout.listview_reason, null);
			holder = new ViewHolder();
			holder.txtIndex = (TextView) convertView
					.findViewById(R.id.TextViewIndex);
			holder.txtNo = (TextView) convertView
					.findViewById(R.id.TextViewNo);
			holder.txtReason = (TextView) convertView
					.findViewById(R.id.TextViewReason);
			

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ReasonItem items = (ReasonItem) getItem(position);
Log.i("reson", String.valueOf(position));
		holder.txtIndex.setText(String.valueOf( items.getIndex()));
		holder.txtNo.setText(items.getNo());
		holder.txtReason.setText(items.getReason());
		if (position % 2 == 0) // 0 even 1 odd..
			convertView.setBackgroundColor(Color.parseColor("#ffffff"));
		else
			convertView.setBackgroundColor(Color.parseColor("#F2F2F2"));
		

		return convertView;
	}

}