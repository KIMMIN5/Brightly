package com.example.brightly;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;

import com.example.brightly.Admin.BuildingManager;
import com.example.brightly.Admin.ButtonOfCurrent;
import com.example.brightly.Admin.DataFetcher;
import com.example.brightly.Admin.LampManager;
import com.example.brightly.Map.CreateMap;
import com.example.brightly.Map.CurrentLocation;
import com.example.brightly.Map.DayAndNight;
import com.example.brightly.Map.LimitedBoundary;
import com.example.brightly.User.EventOfLamp;
import com.example.brightly.User.Permissions;
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
    private Button exportButton;;
    private LampManager lampManager;
    private EventOfLamp eventOfLamp;
    private BuildingManager buildingManager;

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
                Log.d("MainActivity", "Marker deleted: " + markerPosition);
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
            Log.d("MainActivity", "Marker selected: " + latLng);
            return false;
        });

        // LampManager 및 EventOfLamp 설정
        lampManager = new LampManager(mMap);
        eventOfLamp = new EventOfLamp(this, lampManager);
        mMap.setOnMarkerClickListener(eventOfLamp);
        mMap.setOnCameraIdleListener(eventOfLamp);
        eventOfLamp.onMapReady(mMap); // EventOfLamp에 지도 설정

        // BuildingManager 인스턴스 생성 및 showBuildings 호출
        buildingManager = new BuildingManager(mMap);

        DataFetcher dataFetcher = DataFetcher.getInstance();
        dataFetcher.setBuildingDataLoadListener(new DataFetcher.BuildingDataLoadListener() {
            @Override
            public void onBuildingDataLoaded() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buildingManager.showBuildings();
                    }
                });
            }
        });
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

    public void setSelectedMarker(Marker marker) {
        this.selectedMarker = marker;
    }
}