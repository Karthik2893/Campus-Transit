package com.hfad.campus_transit;

import android.app.Application;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by srika on 9/20/2017.
 */

public class Schedule implements Parcelable{
    private String buildingId;
    private String building_name;
    private String room;
    private String day;
    private String start_time;
    private String end_time;
    private double building_lat;
    private double building_long;
    private static ArrayList<Building> buildingList;

    public String getStart_time(){
        return start_time;
    }
    public String getEnd_time(){
        return end_time;
    }
    public String getBuildingId(){
        return buildingId;
    }
    public String getBuilding_name(){
        return building_name;
    }
    public String getRoom(){
        return room;
    }
    public String getDay(){
        return day;
    }
    public double getBuilding_lat(){
        return building_lat;
    }
    public double getBuilding_long(){
        return building_long;
    }

    public Schedule(String buildingId,String building_name,String room,String day,String start_time,
                    String end_time,double building_lat,double building_long) {
        this.buildingId = buildingId;
        this.building_name = building_name;
        this.room = room;
        this.day = day;
        this.start_time = start_time;
        this.end_time = end_time;
        this.building_lat = building_lat;
        this.building_long = building_long;
    }

    public Schedule(Parcel in) {
        this.buildingId = in.readString();
        this.building_name = in.readString();
        this.room = in.readString();
        this.day = in.readString();
        this.start_time = in.readString();
        this.end_time = in.readString();
        this.building_lat = in.readDouble();
        this.building_long = in.readDouble();

    }
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    public static ArrayList<Schedule> getClassesForTheDay(String filename,Context context,String day){
        if(buildingList == null) {
            buildingList = Building.getBuildingsFromFile("buildings.json",context);
        }
        String jsonString = loadJSONFromFile(filename,context);
        ArrayList<Schedule> classList = new ArrayList<>();
        try {
            JSONArray classes = new JSONArray(jsonString);
            for(int i=0;i<classes.length();i++) {
                String building_name;
                String building_id;
                String room;
                String dayOfWeek;
                String start_time;
                String end_time;
                double building_lat = 0.0;
                double building_long = 0.0;
                if(((JSONObject)classes.getJSONObject(i).get("start")).getString("day").equals(day)) {
                    building_id = classes.getJSONObject(i).getString("buildingID");
                    building_name = classes.getJSONObject(i).getString("Name");
                    dayOfWeek = day;
                    room = classes.getJSONObject(i).getString("Room");
                    start_time = ((JSONObject) classes.getJSONObject(i).get("start")).getString("time");
                    end_time = ((JSONObject) classes.getJSONObject(i).get("end")).getString("time");
                    for (Building building : buildingList) {
                        if (building.getId().equals(building_id)) {
                            building_lat = Double.parseDouble(building.getSt_latitude());
                            building_long = Double.parseDouble(building.getSt_longitude());
                        }
                    }
                    Schedule lecture = new Schedule(building_id, building_name, room, dayOfWeek, start_time, end_time, building_lat, building_long);
                    classList.add(lecture);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return classList;

    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(String.valueOf(this.buildingId));
        dest.writeString(this.building_name);
        dest.writeString(this.room);
        dest.writeString(this.day);
        dest.writeString(this.start_time);
        dest.writeString(this.end_time);
        dest.writeString(String.valueOf(this.building_lat));
        dest.writeString(String.valueOf(this.building_long));
    }
    private static String loadJSONFromFile(String filename,Context context){
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer,"UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return json;
    }
}
