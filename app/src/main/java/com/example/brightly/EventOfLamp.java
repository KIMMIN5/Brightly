/*import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class EventOfLamp implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    private FirebaseDatabase database;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
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
                    if (light.isFaulty == 1) {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GRAY));
                    } else {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    }
                    mMap.addMarker(markerOptions).setTag(light);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Streetlight light = (Streetlight) marker.getTag();
        // 여기에서 팝업 또는 대화상자를 표시하여 고장 여부와 신고 버튼을 보여줍니다.
        // 예를 들어, AlertDialog를 사용하여 고장 여부를 표시하고 신고 버튼을 제공할 수 있습니다.
        return true;
    }

    private class Streetlight {
        public int isFaulty;
        public int isReport;
        public double latitude;
        public double longitude;

        // 필요한 생성자, getter, setter 추가
    }
}*/
