package com.mega.matrimony.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mega.matrimony.Model.MenuGroup;
import com.mega.matrimony.R;

import java.util.List;

public class MenuListAdapter extends BaseExpandableListAdapter {

    private Context context;
    List<MenuGroup> list;

    public MenuListAdapter(Context c,List<MenuGroup> list) {
        this.context=c;
        this.list=list;
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return list.get(i).getChildren().size();
    }

    @Override
    public Object getGroup(int i) {
        return list.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return list.get(i).getChildren().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        GroupViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new GroupViewHolder();

            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.nav_list_header, null);

            viewHolder.img_plus=(ImageView)convertView.findViewById(R.id.img_plus);
            viewHolder.img_item=(ImageView)convertView.findViewById(R.id.img_item);
            viewHolder.tv_header=(TextView)convertView.findViewById(R.id.tv_header);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }

        if (list.get(i).getChildren().size()==0){
            viewHolder.img_plus.setVisibility(View.GONE);
        }else{
            viewHolder.img_plus.setVisibility(View.VISIBLE);
            if (isExpanded){
                viewHolder.img_plus.setImageResource(R.drawable.minus_pink);
            }else
                viewHolder.img_plus.setImageResource(R.drawable.plus_pink);
        }



        viewHolder.img_item.setImageResource(list.get(i).getIcon());
        viewHolder.tv_header.setText(list.get(i).getMenuName());

        return convertView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
         ChildViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ChildViewHolder();

            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.nav_list_child, null);

            viewHolder.tv_child=(TextView)convertView.findViewById(R.id.tv_child);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }

        viewHolder.tv_child.setText(list.get(i).getChildren().get(i1).getName());

        return convertView;
    }

    static class GroupViewHolder {
        TextView tv_header;
        ImageView img_item,img_plus;
    }

    static class ChildViewHolder {
        TextView tv_child;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
