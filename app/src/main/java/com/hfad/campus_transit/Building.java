package com.hfad.campus_transit;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by srika on 9/16/2017.
 */

public class Building implements Parcelable{
    public String id;
    public String name;
    public String st_latitude;
    public String st_longitude;
    public String st_heading;
    public String address;
    public String city;
    public String state;
    public String zip;
    public String country;

    public Building(String id, String name,String st_latitude,String st_longitude,String st_heading,String address,
                    String city,String state,String zip,String country) {
        this.id = id;
        this.name = name;
        this.st_latitude = st_latitude;
        this.st_longitude = st_longitude;
        this.st_heading = st_heading;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getSt_latitude(){
        return st_latitude;
    }
    public String getSt_longitude(){
        return st_longitude;
    }
    public String getSt_heading(){
        return st_heading;
    }
    public String getAddress(){
        return address;
    }
    public String getCity() {
        return city;
    }
    public String getState() {
        return state;
    }
    public String getZip(){
        return zip;
    }
    public String getCountry(){
        return country;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Building createFromParcel(Parcel in) {
            return new Building(in);
        }
        public Building[] newArray(int size) {
            return new Building[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.st_latitude);
        dest.writeString(this.st_longitude);
        dest.writeString(this.st_heading);
        dest.writeString(this.address);
        dest.writeString(this.city);
        dest.writeString(this.state);
        dest.writeString(this.zip);
        dest.writeString(this.country);
    }

    public Building(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.st_latitude = in.readString();
        this.st_longitude = in.readString();
        this.st_heading = in.readString();
        this.address = in.readString();
        this.city = in.readString();
        this.state = in.readString();
        this.zip = in.readString();
        this.country = in.readString();
    }

    public static ArrayList<Building> getBuildingsFromFile(String filename, Context context) {
        final ArrayList<Building> buildingsList = new ArrayList<>();
        try {
            String jsonString = loadJSONFromFile("buildings.json",context);
            JSONArray buildings = new JSONArray(jsonString);
            for (int i=0;i<buildings.length();i++) {
                String id = buildings.getJSONObject(i).getString("id");
                String name = buildings.getJSONObject(i).getString("name");
                String st_latitude = buildings.getJSONObject(i).getString("st_latitude");
                String st_longitude = buildings.getJSONObject(i).getString("st_longitude");
                String st_heading = buildings.getJSONObject(i).getString("st_heading");
                String address = buildings.getJSONObject(i).getString("address");
                String city = buildings.getJSONObject(i).getString("city");
                String state = buildings.getJSONObject(i).getString("state");
                String zip = buildings.getJSONObject(i).getString("zip");
                String country = buildings.getJSONObject(i).getString("country");
                Building building = new Building(id,name,st_latitude,st_longitude,st_heading,address,city,state,zip,country);
                buildingsList.add(building);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return buildingsList;
    }

    private static String loadJSONFromFile(String filename, Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }
}
