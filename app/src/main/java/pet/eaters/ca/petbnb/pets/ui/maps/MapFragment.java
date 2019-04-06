package pet.eaters.ca.petbnb.pets.ui.maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.core.android.FragmentUtils;
import pet.eaters.ca.petbnb.core.android.NavigationFragment;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.pets.data.Pet;
import pet.eaters.ca.petbnb.pets.ui.QrCodeClickedListener;
import pet.eaters.ca.petbnb.pets.ui.details.PetDetailsFragment;
import pet.eaters.ca.petbnb.pets.ui.list.PetsListFragment;

// Reference: https://youtu.be/Cy4EraxUan4
public class MapFragment extends NavigationFragment implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 7;
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    private static final int LOCATION_UPDATE_MIN_TIME = 5000;

    private MapViewModel mViewModel;
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private View mMyLocationButton;
    private QrCodeClickedListener qrCodeClickedListener;


    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            zoomInCurrentLocation(location);
            mLocationManager.removeUpdates(mLocationListener);
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
        mMyLocationButton = view.findViewById(R.id.myLocationButton);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.maps_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.toolbar_list) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, PetsListFragment.newInstance())
                            .commit();
                    return true;
                }
                return false;
            }
        });

        view.findViewById(R.id.qrCodeFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qrCodeClickedListener == null) {
                    return;
                }
                qrCodeClickedListener.onScanQrCodeClicked();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }

        mViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        mMyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCurrentLocation();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof QrCodeClickedListener) {
            qrCodeClickedListener = (QrCodeClickedListener) context;
        }

        super.onAttach(context);
    }

    private void goToCurrentLocation() {
        if (!checkPermission()) {
            return;
        }

        String[] providers = new String[]{LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER};
        Location currentLocation = null;
        for (String provider : providers) {
            Location location = getLocationFromProvider(provider);
            if (location != null) {
                currentLocation = location;
            }
        }

        zoomInCurrentLocation(currentLocation);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mViewModel.mapLoaded();

        if (mViewModel.isNeedToAskPermission()) {
            if (checkPermission()) {
                goToCurrentLocation();
                setMyLocationEnabled();
            }
        } else {
            if (mViewModel.isPermissionGranted()) {
                setMyLocationEnabled();
            }
        }

        observePets();
        setMarkerClickable();
    }

    @SuppressLint("MissingPermission")
    private void setMyLocationEnabled() {
        mMyLocationButton.setVisibility(View.INVISIBLE);
        mMap.setMyLocationEnabled(true);
    }

    private void observePets() {
        mViewModel.getPets().observe(getViewLifecycleOwner(), new Observer<Result<List<Pet>>>() {
            @Override
            public void onChanged(Result<List<Pet>> result) {
                if (result.isSuccess()) {
                    drawMarkers(result.getData());
                } else {
                    showError(result.getException());
                }
            }
        });
    }

    private void showError(Exception exception) {
        FragmentUtils.showError(this, exception, R.string.retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.retry();
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Nullable
    private Location getLocationFromProvider(String networkProvider) {
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(networkProvider);
        if (isNetworkEnabled) {
            mLocationManager.requestLocationUpdates(networkProvider,
                    LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
            return mLocationManager.getLastKnownLocation(networkProvider);
        }
        return null;
    }

    private boolean checkPermission() {
        Context context = getContext();
        if (context == null) {
            return false;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mViewModel.permissionGranted();
            return true;
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mViewModel.permissionGranted();
                    goToCurrentLocation();
                } else {
                    Log.d("workingmmap", "Permission denied");
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void zoomInCurrentLocation(@Nullable Location location) {
        if (mMap == null || location == null) {
            return;
        }

        LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 14));
        setMyLocationEnabled();
    }

    private void drawMarkers(@Nullable List<Pet> petList) {
        if (petList == null || mMap == null) {
            return;
        }

        mMap.clear();
        for (Pet pet : petList) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(pet.getLatitude(), pet.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name)))
                    .setTag(pet);
        }
    }


    private void setMarkerClickable() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Pet petInfo = (Pet) marker.getTag();
                if (petInfo == null) {
                    return false;
                }

                openPetDetails(petInfo);
                return true;
            }
        });
    }

    private void openPetDetails(Pet petInfo) {
        getFragmentManager().beginTransaction()
                .add(R.id.content_frame, PetDetailsFragment.newInstance(petInfo.getId()))
                .addToBackStack(null)
                .commit();
    }

}
