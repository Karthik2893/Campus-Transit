package com.hfad.campus_transit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ScheduleDisplayActivity
        extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener,
        OnMapReadyCallback {

    private static final long LOCATION_INTERVAL = 10000;
    private GoogleApiClient bGoogleApiClient;
    private Location bLocation;
    private LocationManager locationManager;
    private GoogleMap bMap;
    private final String LOG_TAG = "In Map Activity";
    private LocationRequest locationRequest;
    private double latitude;
    private double longitude;
    private OkHttpClient okHttpClient;
    boolean GOOGLE_API_CONNECTED = false;
    boolean MAP_READY = false;
    boolean LOCATION_AVAILABLE = false;
    boolean MARKER_ADDED = false;
    public static final String SELECTED_DAY = "com.hfad.campus_transit.MapActivity.selected_day";
    private Marker userLocation ;
    private Marker buildingLocation;
    private Building selected_building;
    private ListView listView;
    private SlidingUpPanelLayout slider;
    private String dayOfWeek;
    HashMap<String,String> dayMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_display);
        okHttpClient = new OkHttpClient();
        bGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Intent intent = getIntent();
        dayOfWeek = intent.getStringExtra(SELECTED_DAY);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_INTERVAL);
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.scheduleMap);
        mapFragment.getMapAsync(this);
        setUpSlider();
        setUpMap();
        finishSetup();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu, menu);
        return true;
    }

    private void setUpSlider(){
        listView = (ListView)findViewById(R.id.scheduleList);
        slider = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout_scheduler);
        slider.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slider.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Toast.makeText(ScheduleDisplayActivity.this, "onItemClick", Toast.LENGTH_SHORT).show();
            }
        });
        TextView view = (TextView)findViewById(R.id.scheduleSliderName);
        view.setText("Directions Information");
        view.setBackgroundColor(0x0000FF00);
    }
    private void setUpMap(){
        dayMap = new HashMap<>();
        dayMap.put("Monday","Mon");
        dayMap.put("Tuesday","Tue");
        dayMap.put("Wednesday","Wed");
        dayMap.put("Thursday","Thr");
        dayMap.put("Friday","Fri");
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!bGoogleApiClient.isConnected()) {
            bGoogleApiClient.connect();
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        GOOGLE_API_CONNECTED = true;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bLocation = LocationServices.FusedLocationApi.getLastLocation(bGoogleApiClient);
            Log.v(LOG_TAG,"here outside");
            if(bLocation == null) {
                Log.v(LOG_TAG,"Here");
                LocationServices.FusedLocationApi.requestLocationUpdates(bGoogleApiClient,locationRequest,this);
            } else {
                LOCATION_AVAILABLE = true;
                latitude = bLocation.getLatitude();
                longitude = bLocation.getLongitude();
                Log.v(LOG_TAG,bLocation.toString());
                Log.v(LOG_TAG,String.valueOf(bLocation.getLatitude()));
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        LOCATION_AVAILABLE = true;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.v(LOG_TAG,location.toString());
        Log.v(LOG_TAG,String.valueOf(location.getLatitude()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MAP_READY = true;
        bMap = googleMap;
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.v(LOG_TAG,"In map ready"+"permission granted");
            bMap.setMyLocationEnabled(true);
        } else {
            Log.v(LOG_TAG,"In map ready"+"permission not granted");
        }
        // Hide the zoom controls as the button panel will cover it.
        bMap.getUiSettings().setZoomControlsEnabled(true);
        bMap.setOnMarkerClickListener(this);
        bMap.setOnInfoWindowClickListener(this);
    }
    private final String API_KEY = "AIzaSyB-4MZqLpRxgwyF1xHjZ4ecJFBt0KJg21w";
    public String getURL(double latitude_origin,double longitude_origin,double latitude_destination,double longitude_destination) {
        String baseURL = "https://maps.googleapis.com/maps/api/directions/json?";
        String origin = "origin="+String.valueOf(latitude_origin)+","+String.valueOf(longitude_origin);
        String destination = "destination="+String.valueOf(latitude_destination)+","+String.valueOf(longitude_destination);
        String mode = "mode=transit";
        String key = "key="+API_KEY;
        Log.v(LOG_TAG,baseURL.concat(origin).concat("&").concat(destination).concat("&").concat(mode).concat("&").concat(key));
        return baseURL.concat(origin).concat("&").concat(destination).concat("&").concat(mode).concat("&").concat(key);
    }
    private List<String> directionInfo ;
    private ArrayList<Schedule> orderedClasses;
    private void finishSetup(){
        SetupComplete setupComplete = new SetupComplete();
        setupComplete.execute();
    }
    private void getClassSchedule() {

        String dayOfWeekShort = dayMap.get(dayOfWeek);
        ArrayList<Schedule> classes = Schedule.getClassesForTheDay("schedule.json",this,dayOfWeekShort);
        Log.v(LOG_TAG,"Length of arrayList "+classes.size());
        orderedClasses = orderedSchedule(classes);
        if(orderedClasses.size() != 0) {
            addMarkerToMap(orderedClasses);
            getUserRoute();
            getDirectionInfo();
        } else {
            Toast.makeText(this,"No classes scheduled today",Toast.LENGTH_SHORT).show();

        }
    }

    public class SetupComplete extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while(!GOOGLE_API_CONNECTED){
//                Log.v(LOG_TAG,"In google_api_connected checker"+GOOGLE_API_CONNECTED);
            }
            while(!MAP_READY){
//                Log.v(LOG_TAG,"In map ready checker "+MAP_READY);
            }
            while (!LOCATION_AVAILABLE) {
//                Log.v(LOG_TAG,"In location available checker "+LOCATION_AVAILABLE);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            getClassSchedule();
        }
    }
    private DirectionsParser directionsParser = new DirectionsParser();
    private class DownloadRoute extends AsyncTask<MyTaskParams,Void,List<List<HashMap<String, String>>>> {
        protected List<List<HashMap<String, String>>> doInBackground(MyTaskParams... params) {
            JSONObject jsonObject;
            String url = params[0].url;
            String class_name = params[0].class_name;
            String response_body ;
            List<List<HashMap<String, String>>> routes = null;
            final Request request = new Request
                    .Builder()
                    .url(url)
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                if(response != null && response.isSuccessful()) {
                    response_body = response.body().string();
                    jsonObject = new JSONObject(response_body);
                    routes = directionsParser.parse(jsonObject,class_name);
                    for (List<HashMap<String, String>> list : routes) {
                        for (HashMap<String, String> hashMaps : list) {
                            for (String val : hashMaps.keySet()) {
//                                Log.v(LOG_TAG, val + "    " + hashMaps.get(val));
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return routes;
        }
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points;
            PolylineOptions polylineOptions = null;
            for(int i=0;i < routes.size();i++) {
                points = new ArrayList<>();
                polylineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    String travel_mode = point.get("transit");
//                    Log.v(LOG_TAG,"In mapping lines "+travel_mode);
                    if (travel_mode.equals("TRANSIT")) {
                        polylineOptions.color(Color.BLUE);
                    } else {
                        polylineOptions.color(Color.BLACK);
                    }
                    LatLng position = new LatLng(lat, lng);
                    polylineOptions.add(position);
//                    polylineOptions.color(Color.BLACK);
                    points.add(position);
                    polylineOptions.width(10);
                }
            }
            if(polylineOptions != null)
            {
                bMap.addPolyline(polylineOptions);
            } else
            {
                Log.v(LOG_TAG,"PolyLines not drawn");
            }
            showInstructions();
        }
    }
    private void showInstructions() {
        directionInfo = directionsParser.getDirectionInstructions();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,directionInfo);
        listView.setAdapter(arrayAdapter);
    }
    private void getUserRoute() {
        DownloadRoute routeDownloader = new DownloadRoute();
        String urlUserClass = getURL(latitude,longitude,orderedClasses.get(0).getBuilding_lat(),orderedClasses.get(0).getBuilding_long());
        String userInstuction = "Routing instructions for class at "+orderedClasses.get(0).getBuilding_name();
        MyTaskParams myTaskParams = new MyTaskParams();
        myTaskParams.url = urlUserClass;
        myTaskParams.class_name = userInstuction;
        routeDownloader.execute(myTaskParams);
    }
    private class MyTaskParams{
        public String url;
        public String class_name;
    }
    private void getDirectionInfo(){
        for(int i=0;i<orderedClasses.size()-1;i++) {
            DownloadRoute routeDownloader = new DownloadRoute();
            Schedule origin = orderedClasses.get(i);
            Schedule destination = orderedClasses.get(i+1);
            double latitude_begin = origin.getBuilding_lat();
            double longitude_begin = origin.getBuilding_long();
            double latitude_end = destination.getBuilding_lat();
            double longitude_end = destination.getBuilding_long();
            MyTaskParams taskParams = new MyTaskParams();
            String url = getURL(latitude_begin,longitude_begin,latitude_end,longitude_end);
            taskParams.url = url;
            taskParams.class_name = "Routing instructions for class at "+destination.getBuilding_name();
            routeDownloader.execute(taskParams);
        }
    }
    private void addMarkerToMap(ArrayList<Schedule> classes) {
        ArrayList<Marker> markers = new ArrayList<>();
        for(Schedule currentClass:classes) {
            LatLng latLng = new LatLng(currentClass.getBuilding_lat(),currentClass.getBuilding_long());
            Marker marker = bMap.addMarker(new MarkerOptions().position(latLng)
                    .title(currentClass.getBuilding_name())
                    .snippet("Class at "+currentClass.getStart_time()+" at"+currentClass.getBuilding_name()+" building"+" ,room number "+currentClass.getRoom()));
            markers.add(marker);
        }
        LatLng userLocation = new LatLng(latitude,longitude);
        Marker userMarker = bMap.addMarker(new MarkerOptions().position(userLocation).title("Your location"));
        markers.add(userMarker);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Marker marker:markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.20);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width,height,padding);
        bMap.animateCamera(cu);

    }
    private ArrayList<Schedule> orderedSchedule(ArrayList<Schedule> schedules){
        Schedule[] scheduleArray = schedules.toArray(new Schedule[schedules.size()]);
        for(int i=0;i<scheduleArray.length;i++) {
            int first = i;
            int index = 0;
            for(int j=i+1;j<scheduleArray.length;j++) {
                int second = j;
                int first_start = processTimeFromString(scheduleArray[first].getStart_time());
                int second_start = processTimeFromString(scheduleArray[second].getStart_time());
                Log.v(LOG_TAG,"Time check "+first_start);
                Log.v(LOG_TAG,"Time check "+second_start);
                if(first_start > second_start) {
                    index = j;
                }
            }
            Schedule temp = scheduleArray[0];
            scheduleArray[0] = scheduleArray[index];
            scheduleArray[index] = temp;
        }
        return new ArrayList<>(Arrays.asList(scheduleArray));
    }

    private int processTimeFromString(String time) {
        int hours = Integer.parseInt(time.split(":")[0])*100;
        int minutes = Integer.parseInt(time.split(":")[1].substring(0,2));
        int time_value = hours+minutes;
        if(time.substring(time.length()-2).equals("pm")) {
            time_value += 1200;
        }
        return time_value;
    }

    @Override
    protected void onStop(){
        super.onStop();
        GOOGLE_API_CONNECTED = false;
        MAP_READY = false;
        LOCATION_AVAILABLE = false;
    }
}
