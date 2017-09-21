package com.hfad.campus_transit;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by srika on 9/16/2017.
 */

public class DirectionsParser {
    private final String LOG_TAG = "In Direction Parser";
    private List<String> direction_instructions = new ArrayList<>();
    public List<List<HashMap<String,String>>> parse(JSONObject jsonObject,String user_inst){
        direction_instructions.add(user_inst);
        return parse(jsonObject);
    }
    public List<List<HashMap<String,String>>> parse(JSONObject jsonObject) {
        List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        try {
            jRoutes = jsonObject.getJSONArray("routes");
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<>();
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    for (int k = 0; k < jSteps.length(); k++) {
                        JSONObject currentObject = jSteps.getJSONObject(k);
                        Log.v(LOG_TAG,"This is what I want "+currentObject.getString("travel_mode"));
                        if (currentObject.getString("travel_mode").equals("TRANSIT"))
                        {
                            String polyLine = "";
                            String html_instructions = (String) ((JSONObject) jSteps.get(k)).get("html_instructions");
                            html_instructions = html_instructions.replaceAll("\\<.*?>", "");
                            JSONObject transitDetails = currentObject.getJSONObject("transit_details");
                            JSONObject departureStop = transitDetails.getJSONObject("departure_stop");
                            JSONObject departureTime = transitDetails.getJSONObject("departure_time");
                            JSONObject line = transitDetails.getJSONObject("line");
                            String depStopName = departureStop.getString("name");
                            String depTime = departureTime.getString("text");
                            String bus_name = line.getString("name");
                            String stops = String.valueOf(transitDetails.getInt("num_stops"));
                            String user_instruction = html_instructions+" "+"Take the "+bus_name+" scheduled to leave at "+depTime
                                    +" at"+depStopName+" and continue for "+stops+" stops";

                            direction_instructions.add(user_instruction);
                            Log.v(LOG_TAG, html_instructions);
                            polyLine = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyLine);
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                                hm.put("transit", "TRANSIT");
                                path.add(hm);
                            }
                        }
                        else
                        {
                            String polyLine = "";
                            String html_instructions = (String) ((JSONObject) jSteps.get(k)).get("html_instructions");
                            html_instructions = html_instructions.replaceAll("\\<.*?>", "");
                            direction_instructions.add(html_instructions);
                            JSONArray steps = currentObject.getJSONArray("steps");
                            Log.v(LOG_TAG, html_instructions);
                            for(int m=0;m<steps.length();m++)
                            {
                                String specific_instructions = (String) ((JSONObject)steps.get(m)).get("html_instructions");
                                specific_instructions = specific_instructions.replaceAll("\\<.*?>", "");
                                Log.v(LOG_TAG,"In here at the most important point"+specific_instructions);
                                direction_instructions.add(specific_instructions);
                            }
                            polyLine = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyLine);
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                                hm.put("transit", currentObject.getString("travel_mode"));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }
    public List<String> getDirectionInstructions() {
        return direction_instructions;
    }
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
