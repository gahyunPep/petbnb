package pet.eaters.ca.petbnb;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.pets.data.Pet;
import pet.eaters.ca.petbnb.pets.data.PetsRepository;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 7;
    private boolean mPermissionDenied = false;
    private LocationManager mLocationManager;
    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;
    Location currentLocation;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d("Map", String.format("%f, %f", location.getLatitude(),
                        location.getLongitude()));
                zoomInCurrentLocation(location);
                mLocationManager.removeUpdates(mLocationListener);
            } else {
                Log.d("Map", "Location is null");
            }
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        currentLocation = null;
        if (!(isGPSEnabled || isNetworkEnabled)){

        } else {
            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                currentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (currentLocation != null) {
            zoomInCurrentLocation(currentLocation);
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if(enableMyLocation()){
            getCurrentLocation();
        }
    }

    private boolean enableMyLocation() {
        boolean enabled = false;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            Log.d("workingmmap","Permission to access the location is missing");

                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

        }else if (mMap != null){
            Log.d("workingmmap","Access to the location already granted");
            mMap.setMyLocationEnabled(true);
            enabled = true;
        }
        return enabled;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("workingmmap","Permission granted");
                    mMap.setMyLocationEnabled(true);
                    getCurrentLocation();

                }else{
                    Log.d("workingmmap","Permission denied");
                }
                return;
            }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void zoomInCurrentLocation(Location location) {
        if (mMap != null) {
            mMap.clear();
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 14));
            drawPetMarkers();
        }
    }

    private void drawPetMarkers() {
        PetsRepository petsRepository = new PetsRepository();
        petsRepository.getPets().observe(this, new Observer<Result<List<Pet>>>() {
            @Override
            public void onChanged(Result<List<Pet>> listResult) {
                List<Pet> petList = listResult.getData();
                if(petList != null){
                    for (Pet pet : petList) {
                        LatLng petLocation = new LatLng(pet.getLatitude(), pet.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(petLocation)
                                .title(getPetType(pet.getType())+": "+pet.getName())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name)));
                    }
                }
            }
        });
    }

    private String getPetType(String type) {
        String petType = getString(R.string.str_otherTypePet);
        switch (type){
            case "1":
                petType = getString(R.string.dog);
                break;
            case "2":
                petType = getString(R.string.cat);
                break;
            case "3":
                break;
        }
        return petType;
    }
}
