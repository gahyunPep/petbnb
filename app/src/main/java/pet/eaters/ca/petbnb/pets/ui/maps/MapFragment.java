package pet.eaters.ca.petbnb.pets.ui.maps;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.pets.data.Pet;
import pet.eaters.ca.petbnb.pets.data.PetsRepository;
import pet.eaters.ca.petbnb.pets.ui.details.PetDetailsFragment;

// Reference: https://youtu.be/Cy4EraxUan4
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapViewModel mViewModel;
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


    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (enableMyLocation()) {
            getCurrentLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        currentLocation = null;
        if (!(isGPSEnabled || isNetworkEnabled)) {

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

    private boolean enableMyLocation() {
        boolean enabled = false;

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("workingmmap", "Permission to access the location is missing");

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

        } else if (mMap != null) {
            Log.d("workingmmap", "Access to the location already granted");
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("workingmmap", "Permission granted");
                    mMap.setMyLocationEnabled(true);
                    getCurrentLocation();

                } else {
                    Log.d("workingmmap", "Permission denied");
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void zoomInCurrentLocation(Location location) {
        if (mMap != null) {
            mMap.clear();
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 14));
            drawPetMarkers();
            setMarkerClickable();
        }
    }

    private void drawPetMarkers() {
        PetsRepository petsRepository = new PetsRepository();
        petsRepository.getPets().observe(this, new Observer<Result<List<Pet>>>() {
            @Override
            public void onChanged(Result<List<Pet>> listResult) {
                List<Pet> petList = listResult.getData();
                if (petList != null) {
                    for (Pet pet : petList) {
                        LatLng petLocation = new LatLng(pet.getLatitude(), pet.getLongitude());
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(petLocation)
                                .title(getPetType(pet.getType()) + ": " + pet.getName())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name)));
                        marker.setTag(pet);
                    }
                }
            }
        });
    }

    private String getPetType(String type) {
        switch (type) {
            case "1":
                return getString(R.string.dog);
            case "2":
                return getString(R.string.cat);
            case "3":
                return getString(R.string.str_otherTypePet);
            default:
                return getString(R.string.str_otherTypePet);
        }
    }

    private void setMarkerClickable() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Pet petInfo = (Pet)marker.getTag();
                if(petInfo != null) {
                    Fragment petDetailsFragment = PetDetailsFragment.newInstance(petInfo.getId());

                    getFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, petDetailsFragment)
                            .addToBackStack(null)
                            .commit();
                }
                return false;
            }
        });
    }

}
