package com.example.bandup.userprofile;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.bandup.R;

import java.util.Arrays;
import java.util.List;

public class ItemsListAdapter extends BaseAdapter {

    private Context context;
    private List<Item> list;

    public ItemsListAdapter(Context context, List<Item> list) {
        this.context = context;
        this.list = list;
    }

    public String[] getSelectedItems() {
        int size = getCount();
        int j = 0;
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            if (list.get(i).isChecked()) {
                result[j] = list.get(i).getItemString();
                j++;
            }
        }
        return Arrays.copyOfRange(result, 0 ,j);
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ItemListViewHolder itemListViewHolder = new ItemListViewHolder();
        if (rowView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_row, null);
            itemListViewHolder.setCheckBox((CheckBox) rowView.findViewById(R.id.rowCheckBox));
            itemListViewHolder.setText((TextView) rowView.findViewById(R.id.rowTextView));
            rowView.setTag(itemListViewHolder);
        } else {
            itemListViewHolder = (ItemListViewHolder) rowView.getTag();
        }
        itemListViewHolder.getCheckBox().setChecked(list.get(position).isChecked());
        itemListViewHolder.getText().setText(list.get(position).getItemString());
        itemListViewHolder.getCheckBox().setTag(position);
        itemListViewHolder.getCheckBox().setChecked(list.get(position).isChecked());
        itemListViewHolder.getCheckBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean newState = !list.get(position).isChecked();
                list.get(position).setChecked(newState);
            }
        });
        return rowView;
    }
}