package com.example.brightly.Admin;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class DataFetcher {
    private static DataFetcher instance;
    private FirebaseDatabase database;
    private List<Streetlight> streetlights;
    private List<DataChangeListener> listeners = new ArrayList<>();

    public interface DataChangeListener {
        void onDataChanged(List<Streetlight> streetlights);
    }

    public DataFetcher() {
        database = FirebaseDatabase.getInstance();
        streetlights = new ArrayList<>();
        loadStreetlightData();
    }

    public static synchronized DataFetcher getInstance() {
        if (instance == null) {
            instance = new DataFetcher();
        }
        return instance;
    }

    public void addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
    }

    public void removeDataChangeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }

    void loadStreetlightData() {
        DatabaseReference streetlightsRef = database.getReference("streetlights");

        streetlightsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                streetlights.clear();
                for (DataSnapshot lightSnapshot : dataSnapshot.getChildren()) {
                    Streetlight light = lightSnapshot.getValue(Streetlight.class);
                    if (light != null) {
                        streetlights.add(light);
                    }
                }
                notifyDataChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log or handle the databaseError
            }
        });
    }

    private void notifyDataChanged() {
        for (DataChangeListener listener : listeners) {
            listener.onDataChanged(streetlights);
        }
    }

    public List<Streetlight> getStreetlights() {
        return streetlights;
    }

    // Streetlight data model class
    public static class Streetlight {
        public int isFaulty;
        public int isReport;
        public double latitude;
        public double longitude;

        // Default constructor required for calls to DataSnapshot.getValue(Streetlight.class)
        public Streetlight() {}

        // Getters and Setters
        public int getIsFaulty() {
            return isFaulty;
        }

        public void setIsFaulty(int isFaulty) {
            this.isFaulty = isFaulty;
        }

        public int getIsReport() {
            return isReport;
        }

        public void setIsReport(int isReport) {
            this.isReport = isReport;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}