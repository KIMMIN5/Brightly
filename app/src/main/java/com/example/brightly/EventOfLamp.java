/*
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class EventOfLamp implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {
    private GoogleMap mMap;
    private FirebaseDatabase database;
    private List<Marker> streetlightMarkers = new ArrayList<>();
    private static final float MIN_ZOOM_LEVEL_FOR_MARKERS = 15.0f; // 마커를 표시할 최소 줌 레벨 설정

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraIdleListener(this);
        loadStreetlightData();
    }

    private void loadStreetlightData() {
        database = FirebaseDatabase.getInstance();
        DatabaseReference streetlightsRef = database.getReference("streetlights");

        streetlightsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot lightSnapshot : dataSnapshot.getChildren()) {
                    Streetlight light = lightSnapshot.getValue(Streetlight.class);
                    LatLng position = new LatLng(light.latitude, light.longitude);

                    MarkerOptions markerOptions = new MarkerOptions().position(position);
                    // 마커 옵션 설정
                    Marker marker = mMap.addMarker(markerOptions);
                    marker.setTag(light);
                    streetlightMarkers.add(marker);
                }
                updateMarkerVisibility();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void updateMarkerVisibility() {
        float zoomLevel = mMap.getCameraPosition().zoom;
        for (Marker marker : streetlightMarkers) {
            marker.setVisible(zoomLevel >= MIN_ZOOM_LEVEL_FOR_MARKERS);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // 마커 클릭 이벤트 처리
        return true;
    }

    @Override
    public void onCameraIdle() {
        updateMarkerVisibility();
    }

    private class Streetlight {
        public int isFaulty;
        public int isReport;
        public double latitude;
        public double longitude;

        // 필요한 생성자, getter, setter 추가
    }
}
*/