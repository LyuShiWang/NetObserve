package com.lyushiwang.netobserve;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class TableAdapter extends BaseAdapter {

    private List<Points> list;
    private LayoutInflater inflater;

    public TableAdapter(Context context, List<Points> list){
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int ret = 0;
        if(list!=null){
            ret = list.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Points points = (Points) this.getItem(position);

        ViewHolder viewHolder;

        if(convertView == null){

            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.z_other_list_item, null);
            viewHolder.point_id = (TextView) convertView.findViewById(R.id.text_name);
            viewHolder.point_order_number = (TextView) convertView.findViewById(R.id.text_order_number);
            viewHolder.point_Hz = (TextView) convertView.findViewById(R.id.text_Hz);
            viewHolder.point_V = (TextView) convertView.findViewById(R.id.text_V);
            viewHolder.point_S = (TextView) convertView.findViewById(R.id.text_S);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.point_id.setText(points.getId());
        viewHolder.point_id.setTextSize(13);
        viewHolder.point_order_number.setText(points.getOrderNumber());
        viewHolder.point_order_number.setTextSize(13);
        viewHolder.point_Hz.setText(points.getHz()+"");
        viewHolder.point_Hz.setTextSize(13);
        viewHolder.point_V.setText(points.getV()+"");
        viewHolder.point_V.setTextSize(13);
        viewHolder.point_S.setText(points.getS()+"");
        viewHolder.point_S.setTextSize(13);

        return convertView;
    }

    public static class ViewHolder{
        public TextView point_id;
        public TextView point_order_number;
        public TextView point_Hz;
        public TextView point_V;
        public TextView point_S;
    }
}


