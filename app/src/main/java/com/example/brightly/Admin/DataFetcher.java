package com.example.brightly.Admin;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFetcher {
    private static DataFetcher instance;
    private FirebaseDatabase database;
    private Map<String, Streetlight> streetlights; // 거리등의 id를 기반으로 관리하기 위해 Map 사용
    private Map<String, Building> buildings; // 거리등의 id를 기반으로 관리하기 위해 Map 사용
    private List<DataChangeListener> listeners = new ArrayList<>();


    public interface BuildingDataLoadListener {
        void onBuildingDataLoaded();
    }

    private BuildingDataLoadListener buildingDataLoadListener;

    public void setBuildingDataLoadListener(BuildingDataLoadListener listener) {
        this.buildingDataLoadListener = listener;
    }
    public interface DataChangeListener {
        void onDataChanged(Map<String, Streetlight> streetlights);
        void onDataLoadComplete();
    }

    public Map<String, Building> getBuildings() {
        return buildings;
    }

    public DataFetcher() {
        database = FirebaseDatabase.getInstance();
        streetlights = new HashMap<>();
        buildings = new HashMap<>(); // buildings 맵 초기화
        loadStreetlightData();
        loadBuildingData(); // 건물 데이터도 불러오기

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
                Map<String, Streetlight> updatedLights = new HashMap<>();
                for (DataSnapshot lightSnapshot : dataSnapshot.getChildren()) {
                    Streetlight light = lightSnapshot.getValue(Streetlight.class);
                    if (light != null) {
                        updatedLights.put(lightSnapshot.getKey(), light);
                    }
                }

                // 기존 Map 업데이트
                streetlights.clear();
                streetlights.putAll(updatedLights);

                // 변경된 데이터만 리스너에 알림
                notifyDataChanged(updatedLights);
                notifyDataLoadComplete();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DataFetcher", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void notifyDataChanged(Map<String, Streetlight> updatedLights) {
        for (DataChangeListener listener : listeners) {
            listener.onDataChanged(streetlights);
        }
    }

    private void notifyDataLoadComplete() {
        for (DataChangeListener listener : listeners) {
            listener.onDataLoadComplete();
        }
    }

    public Map<String, Streetlight> getStreetlights() {
        return streetlights;
    }

    // Streetlight data model class
    public static class Streetlight {
        public int id; // 고유한 ID 필드 추가
        public boolean isFaulty;
        public boolean isReport;
        public double latitude;
        public double longitude;

        // Default constructor required for calls to DataSnapshot.getValue(Streetlight.class)
        public Streetlight() {}

        // Getters and Setters
        public int getId() {
            return id;
        }

        public boolean getIsFaulty() {
            return isFaulty;
        }

        public void setIsFaulty(boolean isFaulty) {
            this.isFaulty = isFaulty;
        }

        public boolean getIsReport() {
            return isReport;
        }

        public void setIsReport(boolean isReport) {
            this.isReport = isReport;
        }

        public double getLatitude() {
            return latitude;
        }


        public double getLongitude() {
            return longitude;
        }

    }
    // 건물 데이터 모델 클래스
    public static class Building {
        public double latitude;
        public double longitude;
        public String name;

        public Building() {} // Default constructor for Firebase

        // Getters and Setters
    }

    // 건물 데이터 로딩 메서드
    // 건물 데이터 로딩 메서드
    public void loadBuildingData() {
        DatabaseReference ref = database.getReference("Buildings");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Building> newBuildings = new HashMap<>();
                for (DataSnapshot buildingSnapshot : dataSnapshot.getChildren()) {
                    // buildingSnapshot은 각 건물을 나타냅니다 (예: "Aramhall")
                    DataSnapshot locationSnapshot = buildingSnapshot.child("Location");
                    Double latitude = locationSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = locationSnapshot.child("longitude").getValue(Double.class);

                    if (latitude != null && longitude != null) {
                        Building building = new Building();
                        building.latitude = latitude;
                        building.longitude = longitude;
                        building.name = buildingSnapshot.getKey();
                        newBuildings.put(buildingSnapshot.getKey(), building);
                    }
                }

                buildings.clear();
                buildings.putAll(newBuildings);

                // 데이터 로딩 완료 시 리스너 호출
                if (buildingDataLoadListener != null) {
                    buildingDataLoadListener.onBuildingDataLoaded();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ... 오류 처리 ...
            }
        });
    }

}