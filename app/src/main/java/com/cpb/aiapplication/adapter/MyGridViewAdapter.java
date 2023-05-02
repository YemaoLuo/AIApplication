package com.cpb.aiapplication.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cpb.aiapplication.R;
import com.cpb.aiapplication.helper.DBHelper;

import java.util.List;

public class MyGridViewAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<String> mData;
    private final LayoutInflater mInflater;
    private DBHelper dbHelper;

    public MyGridViewAdapter(Context context, List<String> data) {
        mContext = context;
        mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.contentTextView = convertView.findViewById(R.id.content_textview);
            viewHolder.button1 = convertView.findViewById(R.id.checkBtn);
            viewHolder.button2 = convertView.findViewById(R.id.removeBtn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String item = mData.get(position);
        viewHolder.contentTextView.setText(item);
        dbHelper = new DBHelper(mContext);
        viewHolder.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = dbHelper.load(item);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(item);
                builder.setMessage(data);
                builder.setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        viewHolder.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Confirm");
                builder.setMessage("Remove this history log?");
                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                    boolean flag = dbHelper.remove(item);
                    if (flag) {
                        Toast.makeText(mContext, "Done", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(mContext, "Cancelled", Toast.LENGTH_SHORT).show());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView contentTextView;
        Button button1;
        Button button2;
    }
}

