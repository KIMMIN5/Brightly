package com.example.brightly;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;

import com.example.brightly.Admin.ButtonOfCurrent;
import com.example.brightly.Admin.StreetLightManager;
import com.example.brightly.Map.CreateMap;
import com.example.brightly.Map.CurrentLocation;
import com.example.brightly.Map.DayAndNight;
import com.example.brightly.Map.LimitedBoundary;
import com.example.brightly.User.Permissions;
import com.example.brightly.R;
import com.example.brightly.Admin.SaveMarker;
import com.example.brightly.Admin.SharedPreferencesExporter;
import com.example.brightly.databinding.BrightlyLayoutBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Set;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        Permissions.LocationPermissionHandler, CurrentLocation.LocationUpdateListener {

    private BrightlyLayoutBinding binding;
    private GoogleMap mMap;
    private CurrentLocation currentLocation;
    private ButtonOfCurrent buttonOfCurrent;
    private LimitedBoundary limitedBoundary;
    private SaveMarker saveMarker;
    private TextView tailTextView;
    private Marker selectedMarker;
    private Button deleteMarkerButton;
    private Button exportButton;
    private StreetLightManager streetLightManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BrightlyLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        saveMarker = new SaveMarker(this);

        Button currentLocationButton = findViewById(R.id.buttonCurrentLocation);
        currentLocationButton.setOnClickListener(v -> buttonOfCurrent.addMarkerAtCurrentLocation());

        tailTextView = findViewById(R.id.tail_text_view);

        deleteMarkerButton = findViewById(R.id.delete_marker_button);
        deleteMarkerButton.setOnClickListener(v -> {
            if (selectedMarker != null) {
                LatLng markerPosition = selectedMarker.getPosition();
                selectedMarker.remove();
                saveMarker.removeMarkerPosition(markerPosition);
                selectedMarker = null;
            }
        });

        exportButton = findViewById(R.id.export_button);
        exportButton.setOnClickListener(v -> exportSharedPreferences());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initializeLocationRelatedStuff();
        }

        Permissions.checkLocationPermission(this);
    }

    public void initializeLocationRelatedStuff() {
        if (mMap != null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            currentLocation = new CurrentLocation(this, mMap);
            currentLocation.initializeLocationListener();
            currentLocation.setLocationUpdateListener(this);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        buttonOfCurrent = new ButtonOfCurrent(currentLocation, mMap, saveMarker);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permissions.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DayAndNight.setMapStyleBasedOnTime(mMap, this);

        CreateMap createMap = new CreateMap(mMap);

        buttonOfCurrent = new ButtonOfCurrent(currentLocation, mMap, saveMarker);
        loadSavedMarkers();

        mMap.setOnMapLoadedCallback(() -> {
            LatLngBounds polygonBounds = createMap.getPolygonBounds();
            limitedBoundary = new LimitedBoundary(mMap, polygonBounds);
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initializeLocationRelatedStuff();
        }

        mMap.setOnMarkerClickListener(marker -> {
            selectedMarker = marker;
            LatLng latLng = marker.getPosition();
            tailTextView.setText("Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);
            return false;
        });


        streetLightManager = new StreetLightManager(mMap); // 여기에서 mMap 전달
        streetLightManager.loadStreetLights();
    }

    @Override
    public void onLocationUpdated(LatLng newLocation) {
        tailTextView.setText("현재 위치: " + newLocation.latitude + ", " + newLocation.longitude);
    }

    private void loadSavedMarkers() {
        Set<LatLng> savedMarkerLatLngs = saveMarker.loadAllMarkerPositions();
        if (savedMarkerLatLngs != null) {
            for (LatLng latLng : savedMarkerLatLngs) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("저장된 위치"));
                Log.d("MainActivity", "Adding saved marker: " + latLng.toString());
            }
        }
    }

    private void exportSharedPreferences() {
        SharedPreferencesExporter.exportSharedPreferences(this, "MarkerPref");
    }
}

