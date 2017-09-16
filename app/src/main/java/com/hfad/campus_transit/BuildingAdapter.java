package com.hfad.campus_transit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by srika on 9/16/2017.
 */

public class BuildingAdapter extends BaseAdapter {
    private Context bContext;
    private LayoutInflater bInflater;
    private ArrayList<Building> bDataSource;

    public BuildingAdapter(Context context, ArrayList<Building> buildings) {
        this.bContext = context;
        this.bDataSource = buildings;
        bInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return bDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return bDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = bInflater.inflate(R.layout.building_item, parent, false);
        TextView buildingNameView = (TextView) rowView.findViewById(R.id.buildingItemName);
        TextView buildingNumberView = (TextView)rowView.findViewById(R.id.buildingItemNumber);
        Building building = (Building)getItem(position);
        buildingNameView.setText(building.name);
        buildingNumberView.setText(building.id);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(bContext,MapActivity.class);
                v.getContext().startActivity(intent);
            }
        });
        return rowView;
    }

}
