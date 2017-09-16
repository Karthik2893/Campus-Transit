package com.hfad.campus_transit;

import android.content.Context;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by srika on 9/16/2017.
 */

public class Building {
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
    public static ArrayList<Building> getBuildingsFromFile(String filename, Context context) {
        final ArrayList<Building> buildingsList = new ArrayList<>();
        try {
            String jsonString = loadJSONFromFile("buildings.json",context);
            JSONArray buildings = new JSONArray(jsonString);
            for (int i=0;i<buildings.length();i++) {
                Building building = new Building();
                building.id = buildings.getJSONObject(i).getString("id");
                building.name = buildings.getJSONObject(i).getString("name");
                building.st_latitude = buildings.getJSONObject(i).getString("st_latitude");
                building.st_longitude = buildings.getJSONObject(i).getString("st_longitude");
                building.st_heading = buildings.getJSONObject(i).getString("st_heading");
                building.address = buildings.getJSONObject(i).getString("address");
                building.city = buildings.getJSONObject(i).getString("city");
                building.state = buildings.getJSONObject(i).getString("state");
                building.zip = buildings.getJSONObject(i).getString("zip");
                building.country = buildings.getJSONObject(i).getString("country");
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
