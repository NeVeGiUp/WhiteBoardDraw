package com.ligh.whiteboardpainting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ligh.whiteboardpainting.R;


public class StyleSelectBaseAdapter extends BaseAdapter {
    private Context context;
    private int[] data;
    private final LayoutInflater inflater;

    public StyleSelectBaseAdapter(Context context, int[] data){
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.style_pupopwindow_grid_item, null);
            holder.list_item_style_pup_image = (ImageView) convertView.findViewById(R.id.list_item_style_pup_image);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.list_item_style_pup_image.setImageResource(data[position]);
        return convertView;
    }
    class ViewHolder{
        public ImageView list_item_style_pup_image;
    }
}