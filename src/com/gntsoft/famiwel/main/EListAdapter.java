package com.gntsoft.famiwel.main;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.gntsoft.famiwel.R;
import com.gntsoft.famiwel.server.MenuModel;
import com.pluslibrary.utils.PlusViewHolder;

/**
 * expandablelist 어댑터
 * 
 * @author jeff
 * 
 */
public class EListAdapter extends BaseExpandableListAdapter {

	private ArrayList<String> mGroupModel;
	private ArrayList<ArrayList<MenuModel>> mModel;
	protected LayoutInflater mLayoutInflater;


	public EListAdapter(Context context, ArrayList<String> groupModel,
			ArrayList<ArrayList<MenuModel>> model) {
		super();

		mGroupModel = groupModel;
		mModel = model;
		mLayoutInflater = LayoutInflater.from(context);

	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return mModel.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.elist_item_second,
					parent, false);
		}

		TextView title = PlusViewHolder.get(convertView, R.id.title);
		title.setText(mModel.get(groupPosition).get(childPosition).title);

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return mModel.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return mGroupModel.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return mGroupModel.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.elist_item_first,
					parent, false);
		}

		TextView category = PlusViewHolder.get(convertView, R.id.category);
		category.setText(mGroupModel.get(groupPosition));

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
