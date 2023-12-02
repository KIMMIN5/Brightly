package com.example.brightly.Admin;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.*;

public class BuildingManager {
    private static final String TAG = "BuildingManager";
    private GoogleMap mMap;


    public BuildingManager(GoogleMap map) {
        this.mMap = map;
    }

    public void loadBuildings() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Buildings");

        databaseReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot buildingSnapshot : dataSnapshot.getChildren()) {
                    String buildingName = buildingSnapshot.getKey(); // 건물 이름을 키에서 가져옴
                    DataSnapshot locationSnapshot = buildingSnapshot.child("Location");
                    double latitude = locationSnapshot.child("latitude").getValue(Double.class);
                    double longitude = locationSnapshot.child("longitude").getValue(Double.class);
                    Log.d(TAG, "Building: " + buildingName + ", Latitude: " + latitude + ", Longitude: " + longitude);

                    addMarkerToMap(new LatLng(latitude, longitude), buildingName); // 마커 추가 시 건물 이름 전달
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadBuildings:onCancelled", databaseError.toException());
            }
        });
    }

    private void addMarkerToMap(LatLng latLng, String title) { // title 매개변수 추가
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title) // 마커의 제목을 건물 이름으로 설정
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        }
    }

}
