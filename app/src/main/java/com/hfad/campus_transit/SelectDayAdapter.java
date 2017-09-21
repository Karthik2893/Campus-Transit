package com.hfad.campus_transit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by srika on 9/20/2017.
 */

public class SelectDayAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<String> mDataSource;

    public SelectDayAdapter(Context context) {
        this.mContext = context;
        this.mDataSource = new ArrayList<>();
        mDataSource.add("Monday");
        mDataSource.add("Tuesday");
        mDataSource.add("Wednesday");
        mDataSource.add("Thursday");
        mDataSource.add("Friday");
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.selectday_item, parent, false);
        TextView dayNameView = (TextView) rowView.findViewById(R.id.dayName);
        final String day = (String)getItem(position);
        dayNameView.setText(day);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ScheduleDisplayActivity.class);
                intent.putExtra(ScheduleDisplayActivity.SELECTED_DAY,day);
                Log.v("In Adapter","Day selected is "+day);
                v.getContext().startActivity(intent);
/*                intent.putExtra(MapActivity.SELECTED_BUILDING,building);
                v.getContext().startActivity(intent);*/
            }
        });
        return rowView;
    }

}