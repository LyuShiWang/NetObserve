package com.lyushiwang.netobserve;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by 吕世望 on 2017/4/25.
 */

public class observe_known_Adapter extends BaseAdapter {

    private Context mContext;
    private LinkedList<ContactsContract.Contacts.Data> mData;

    public observe_known_Adapter() {}

    public observe_known_Adapter(LinkedList<ContactsContract.Contacts.Data> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.z_listview_item,parent,false);
            holder = new ViewHolder();

            holder.point_name=(TextView)convertView.findViewById(R.id.textview_name);
            holder.X_coor=(TextView)convertView.findViewById(R.id.textview_X_coor);
            holder.Y_coor=(TextView)convertView.findViewById(R.id.textview_Y_coor);
            holder.Z_coor=(TextView)convertView.findViewById(R.id.textview_Z_coor);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
//        holder.img_icon.setImageResource(mData.get(position).getImgId());
//        holder.txt_content.setText(mData.get(position).getContent());
//
//        holder.point_name.setText(mData.get(position).get);
        return convertView;
    }

    private class ViewHolder{
        TextView point_name;
        TextView X_coor;
        TextView Y_coor;
        TextView Z_coor;
    }

    public void add(ContactsContract.Contacts.Data data) {
        if (mData == null) {
            mData = new LinkedList<>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }
}