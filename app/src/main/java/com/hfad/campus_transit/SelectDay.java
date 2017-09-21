package com.hfad.campus_transit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by srika on 9/20/2017.
 */

public class SelectDay extends AppCompatActivity {
    private ListView listView;
    ArrayList<String> list;
    SelectDayAdapter selectDayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_day);
        setUpSelectDayAdapter();
    }

    public void setUpSelectDayAdapter(){
        listView = (ListView)findViewById(R.id.dayList);
        list = new ArrayList<>();
        selectDayAdapter = new SelectDayAdapter(this);
        listView.setAdapter(selectDayAdapter);
    }

}
