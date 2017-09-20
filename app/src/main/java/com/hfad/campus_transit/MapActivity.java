package com.hfad.campus_transit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MapActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
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
    public static final String SELECTED_BUILDING = "com.hfad.campus_transit.MapActivity.selected_building";
    private Marker userLocation ;
    private Marker buildingLocation;
    private Building selected_building;
    private ListView listView;
    private SlidingUpPanelLayout slider;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Bundle bundle = getIntent().getExtras();
        okHttpClient = new OkHttpClient();
        selected_building = bundle.getParcelable(SELECTED_BUILDING);
        bGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_INTERVAL);
        setUpSlider();
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void setUpSlider() {
        listView = (ListView)findViewById(R.id.list);
        slider = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slider.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slider.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Toast.makeText(MapActivity.this, "onItemClick", Toast.LENGTH_SHORT).show();
            }
        });
        TextView view = (TextView)findViewById(R.id.name);
        view.setText("Directions Information");
        view.setBackgroundColor(0x0000FF00);
    }

    @Override
    public void onBackPressed() {
        if (slider != null &&
                (slider.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slider.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slider.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG,String.valueOf(bGoogleApiClient.isConnected()));
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
                if(MAP_READY && !MARKER_ADDED) {
                    addMarkerToMap();
                }
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
    public void onMapReady(GoogleMap googleMap) {
        MAP_READY = true;
        bMap = googleMap;

        // Hide the zoom controls as the button panel will cover it.
        bMap.getUiSettings().setZoomControlsEnabled(true);
        bMap.setOnMarkerClickListener(this);
        bMap.setOnInfoWindowClickListener(this);
        if(GOOGLE_API_CONNECTED && LOCATION_AVAILABLE) {
            addMarkerToMap();
        }
//        bMap.setOnMarkerDragListener(this);
//        bMap.setOnInfoWindowCloseListener(this);
//        bMap.setOnInfoWindowLongClickListener(this);
    }

    public void addMarkerToMap() {
        Log.v(LOG_TAG,"In addMarkerToMap");
        MARKER_ADDED = true;
        Log.v(LOG_TAG,"In addMarkerToMap "+latitude+" "+longitude);
        LatLng USER_LOCATION = new LatLng(latitude,longitude);
        double building_lat = Double.valueOf(selected_building.getSt_latitude());
        double building_long = Double.valueOf(selected_building.getSt_longitude());
        LatLng BUILDING_LOCATION = new LatLng(building_lat,building_long);
        userLocation = bMap.addMarker(new MarkerOptions().position(USER_LOCATION).title("YOU"));
        buildingLocation = bMap.addMarker(new MarkerOptions().position(BUILDING_LOCATION).title(selected_building.getName()));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(userLocation.getPosition());
        builder.include(buildingLocation.getPosition());
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.20);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width,height,padding);

        bMap.animateCamera(cu);
        DownloadRoute downloadRoute = new DownloadRoute();
        downloadRoute.execute(getURL(latitude,longitude,building_lat,building_long));
//        bMap.animateCamera(CameraUpdateFactory.zoomIn());
//        bMap.animateCamera(CameraUpdateFactory.zoomTo(10),2000,null);

    }
    private final String API_KEY = "AIzaSyB-4MZqLpRxgwyF1xHjZ4ecJFBt0KJg21w";
    public String getURL(double latitude_origin,double longitude_origin,double latitude_destination,double longitude_destination) {
        String baseURL = "https://maps.googleapis.com/maps/api/directions/json?";
        String origin = "origin="+String.valueOf(latitude_origin)+","+String.valueOf(longitude_origin);
        String destination = "destination="+String.valueOf(latitude_destination)+","+String.valueOf(longitude_destination);
        String key = "key="+API_KEY;
        Log.v(LOG_TAG,baseURL.concat(origin).concat("&").concat(destination).concat("&").concat(key));
        return baseURL.concat(origin).concat("&").concat(destination).concat("&").concat(key);
    }

    Response networkResponse;
    JSONObject jsonObject;
    DirectionsParser directionsParser = new DirectionsParser();
    List<String> directionInfo ;
    public class DownloadRoute extends AsyncTask<String,Void,List<List<HashMap<String, String>>>> {
        protected List<List<HashMap<String, String>>> doInBackground(String... params) {
            JSONObject jsonObject;
            String url = params[0];
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
                    routes = directionsParser.parse(jsonObject);
                    for (List<HashMap<String, String>> list : routes) {
                        for (HashMap<String, String> hashMaps : list) {
                            for (String val : hashMaps.keySet()) {
                                Log.v(LOG_TAG, val + "    " + hashMaps.get(val));
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
                List<HashMap<String,String>> path = routes.get(i);
                for(int j =0;j<path.size();j++) {
                    HashMap<String,String> point = path.get(j);
                    double lat =  Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat,lng);
                    points.add(position);
                }
                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.RED);
                Log.v(LOG_TAG,"onPostExecute line options decoded");
                addDirections();
            }
            if(polylineOptions != null) {
                bMap.addPolyline(polylineOptions);
            } else {
                Log.v(LOG_TAG,"PolyLines not drawn");
            }
        }
    }

    private void addDirections(){

        directionInfo = directionsParser.getDirectionInstructions();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,directionInfo);
        listView.setAdapter(arrayAdapter);
    }
    private void downloadRoute() {
        final Request request = new Request
                .Builder()
                .url("https://maps.googleapis.com/maps/api/directions/json?origin=33.9548,-83.3732&destination=33.952409,-83.376685&key=AIzaSyB-4MZqLpRxgwyF1xHjZ4ecJFBt0KJg21w")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                networkResponse = response;
                String response_body;
                if(response.isSuccessful()) {
                    response_body = response.body().string();
                    try {
                        jsonObject = new JSONObject(response_body);
                        List<List<HashMap<String, String>>> routes = directionsParser.parse(jsonObject);
                        for(List<HashMap<String,String>> list : routes) {
                            for(HashMap<String,String> hashMaps: list) {
                                for(String val:hashMaps.keySet()) {
                                    Log.v(LOG_TAG,val+"    "+hashMaps.get(val));
                                }
                            }
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    Log.v(LOG_TAG,response_body);
                }
                else {
                    Log.v(LOG_TAG,"Response Failed");
                }
            }
        });



    }
    @Override
    public void onLocationChanged(Location location) {
        LOCATION_AVAILABLE = true;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.v(LOG_TAG,location.toString());
        Log.v(LOG_TAG,String.valueOf(location.getLatitude()));
        if(MAP_READY && ! MARKER_ADDED) {
            addMarkerToMap();
        }
    }
}
