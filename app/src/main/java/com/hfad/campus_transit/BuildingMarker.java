package com.hfad.campus_transit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class BuildingMarker extends AppCompatActivity {
    private ListView buildingListView;
    ArrayList<Building> buildingsList;
    BuildingAdapter buildingAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_marker);
        setupBuildingsAdapter();
    }

    public void setupBuildingsAdapter() {
        buildingListView = (ListView)findViewById(R.id.buildings_list);
        buildingsList = Building.getBuildingsFromFile("buildings.json",this);
        buildingAdapter = new BuildingAdapter(this,buildingsList);
        String[] listItems = new String[buildingsList.size()];
        for(int i=0;i < buildingsList.size(); i++) {
            Building building = buildingsList.get(i);
            listItems[i] = building.name;
        }
        buildingListView.setAdapter(buildingAdapter);
    }
}
