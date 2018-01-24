package com.example.john.torresjonathan_ppvi;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MainMapFragment extends MapFragment implements OnMapReadyCallback, LocationListener,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.InfoWindowAdapter {

    //Declare an Instance of the Map
    public static MainMapFragment newInstance(){
        return  new MainMapFragment();
    }

    public static final String UPDATE_DATA = "com.example.john.torresjonathan_ppvi_UPDATE_DATA";
    private UpdateMap updateMap;

    private ArrayList<RestaurantLocationObject> locationFound = new ArrayList<>();

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location networkLocation;
    private Location gpsLocation;
    private LatLng currentLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setHasOptionsMenu(true);
        //Get the Map Async to load all data the Map needs
        getMapAsync(this);


        //Code to load the users location
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10.0f, this);
            networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10.0f, this);
            gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Set listeners and google map data
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);

        //Allow Location tracking and mark the location the user is
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }

        //Pan to the location where the user is.
        if(networkLocation != null){
            LatLng closeLocation = new LatLng(networkLocation.getLatitude(),
                    networkLocation.getLongitude());
            CameraUpdate cameraLocation = CameraUpdateFactory.newLatLngZoom(closeLocation, 15);
            mMap.animateCamera(cameraLocation);

            currentLocation = closeLocation;
            locationManager.removeUpdates(this);

        }else if (gpsLocation != null){
            LatLng closeLocation = new LatLng(gpsLocation.getLatitude(),
                    gpsLocation.getLongitude());
            CameraUpdate cameraLocation = CameraUpdateFactory.newLatLngZoom(closeLocation, 15);
            mMap.animateCamera(cameraLocation);

            currentLocation = closeLocation;
            locationManager.removeUpdates(this);
        }else{
            Toast.makeText(getActivity(), "Unknown last location", Toast.LENGTH_SHORT).show();
        }

        //Download data from all the restaurants in the area
        MapLocationTask task = new MapLocationTask(getActivity());

        if (task.getStatus() == AsyncTask.Status.RUNNING) {
            Toast.makeText(getActivity(), "Currently downloading Data", Toast.LENGTH_SHORT).show();

        }else{
            if(currentLocation != null){
                task.execute("lat=" + currentLocation.latitude + "&lon="+ currentLocation.longitude);
            }else{
                Toast.makeText(getActivity(), "User location not found. Please check phone GPS or connection", Toast.LENGTH_SHORT).show();
            }

    }

        locationManager.removeUpdates(this);
    }

    //go to the restaurant's information
    @Override
    public void onInfoWindowClick(Marker marker) {

        for (int i = 0; i < locationFound.size(); i++) {

            LatLng resLocation = new LatLng(locationFound.get(i).resLat, locationFound.get(i).resLng);

            if(resLocation.equals(marker.getPosition())){

                Log.d("Match", "Match" + resLocation.toString() + " m" + marker.getPosition().toString());

                Intent toInfo = new Intent(getActivity(), InformationActivity.class);
                toInfo.putExtra("ID", locationFound.get(i).resID);
                startActivityForResult(toInfo, 0);
            }
        }
    }

    //Required methods
    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    //Broadcast to update the UI with information
    public class UpdateMap extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent != null){

                if(locationFound != null){
                    locationFound.clear();
                    mMap.clear();
                }

                //noinspection unchecked
                locationFound = (ArrayList<RestaurantLocationObject>)intent.getSerializableExtra("allLocationsData");

                MarkerOptions options = new MarkerOptions();

                for (int i = 0; i < locationFound.size(); i++) {


                    options.title(locationFound.get(i).resName);

                    LatLng resLocation = new LatLng(locationFound.get(i).resLat, locationFound.get(i).resLng);
                    options.position(resLocation);

                    if(locationFound.get(i).resAvegCost == 0){
                        options.snippet("Average price for 2: ??");

                    }else{
                        options.snippet("Average price for 2: " + locationFound.get(i).resAvegCost);
                    }
                    mMap.addMarker(options);

                }

            }
        }
    }

    //on Pause, unregister broadcaster

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(updateMap);
    }

    //on Resume, Set the broadcaster and register it
    @Override
    public void onResume() {
        super.onResume();

        updateMap = new UpdateMap();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE_DATA);
        getActivity().registerReceiver(updateMap, intentFilter);

    }

    //Set up MApp Bar buttons
    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {

        MenuInflater mInflater = getActivity().getMenuInflater();
        mInflater.inflate(R.menu.searchbar_menubar, menu);
        MenuItem item = menu.findItem(R.id.action_search);

        //Allow the user to search a specific query on a search bar
        SearchView searchView = (SearchView)item.getActionView();

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                MapLocationTask task = new MapLocationTask(getActivity());
                if (task.getStatus() == AsyncTask.Status.RUNNING) {
                    Toast.makeText(getActivity(), "Currently downloading Data", Toast.LENGTH_SHORT).show();

                }else{
                    task.execute("lat=" + currentLocation.latitude + "&lon="+ currentLocation.longitude);
                }
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MapLocationTask task = new MapLocationTask(getActivity());

                if (task.getStatus() == AsyncTask.Status.RUNNING){
                    Toast.makeText(getActivity(), "Currently downloading Data", Toast.LENGTH_SHORT).show();
                }else{
                    task.execute("q=" + query + "&lat=" + currentLocation.latitude + "&lon="+ currentLocation.longitude);
                }

                
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }

    //Send the user to the "favorites" page
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_favorites){
            Intent toInfo = new Intent(getActivity(), FavoritesActivity.class);
            startActivityForResult(toInfo, 0);
        }

        return true;
    }


}
