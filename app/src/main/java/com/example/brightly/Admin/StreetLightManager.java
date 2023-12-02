package com.example.brightly.Admin;
import static android.content.ContentValues.TAG;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.*;

import com.google.android.gms.maps.model.LatLng;
import android.util.Log;

public class StreetLightManager {
    public GoogleMap mMap;



    public StreetLightManager(GoogleMap map) {
        this.mMap = map;
    }
    public void loadStreetLights() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("streetlights");

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot lightSnapshot : dataSnapshot.getChildren()) {
                    String lightId = lightSnapshot.getKey(); // 가로등의 인덱스(아이디)를 가져옴
                    double latitude = lightSnapshot.child("latitude").getValue(Double.class);
                    double longitude = lightSnapshot.child("longitude").getValue(Double.class);

                    addMarkerToMap(new LatLng(latitude, longitude), lightId); // 마커 추가 시 인덱스(아이디) 전달
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadStreetLights:onCancelled", databaseError.toException());
            }
        });
    }

    private void addMarkerToMap(LatLng latLng, String title) { // title 매개변수 추가
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title)); // 마커의 제목을 가로등 인덱스(아이디)로 설정
        }
    }
}
