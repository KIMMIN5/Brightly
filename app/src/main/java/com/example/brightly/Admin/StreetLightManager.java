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
                    double latitude = lightSnapshot.child("latitude").getValue(Double.class);
                    double longitude = lightSnapshot.child("longitude").getValue(Double.class);

                    addMarkerToMap(new LatLng(latitude, longitude));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadStreetLights:onCancelled", databaseError.toException());
            }
        });
    }


    private void addMarkerToMap(LatLng latLng) {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(latLng).title("Street Light"));
        }
    }

}
