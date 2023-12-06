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
    private BuildingDataLoadListener buildingDataLoadListener;


    public interface BuildingDataLoadListener {
        void onBuildingDataLoaded();
    }

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

    public static class Building {
        private double latitude;
        private double longitude;
        private String name;
        private String securityOfficePhone; // 수위실 전화번호
        private HashMap<String, NightCourse> nightCourses; // 야간강의실 정보

        // 기본 생성자
        public Building() {
            // Firebase를 위한 기본 생성자
            nightCourses = new HashMap<>();
        }

        // Getter 메서드들
        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getName() {
            return name;
        }

        public String getSecurityOfficePhone() {
            return securityOfficePhone != null ? securityOfficePhone : "정보 없음";
        }

        public HashMap<String, NightCourse> getNightCourses() {
            return nightCourses;
        }

        // 야간 강의실 정보를 문자열로 리턴하는 메소드
        public String getNightClassroomInfo() {
            if (nightCourses.isEmpty()) {
                return "야간 강의 정보 없음";
            } else {
                StringBuilder infoBuilder = new StringBuilder();
                for (Map.Entry<String, NightCourse> entry : nightCourses.entrySet()) {
                    NightCourse course = entry.getValue();
                    infoBuilder.append(course.getCourseName()).append("\n");
                    for (Map.Entry<String, CourseSession> sessionEntry : course.getSessions().entrySet()) {
                        CourseSession session = sessionEntry.getValue();
                        infoBuilder.append(sessionEntry.getKey())
                                .append(" - Room: ").append(session.getRoom())
                                .append(", Time: ").append(session.getStartTime())
                                .append(" to ").append(session.getEndTime())
                                .append("\n");
                    }
                }
                return infoBuilder.toString().trim();
            }
        }

        // 야간 강의 정보를 나타내는 내부 클래스
        public class NightCourse {
            private String courseName;
            private HashMap<String, CourseSession> sessions; // 요일별 세션 정보

            public NightCourse() {
                sessions = new HashMap<>();
            }

            // Getter 메서드들
            public String getCourseName() {
                return courseName;
            }

            public HashMap<String, CourseSession> getSessions() {
                return sessions;
            }
        }

        // 강의 세션 정보를 나타내는 내부 클래스
        public class CourseSession {
            private String startTime;
            private String endTime;
            private String room;

            // Getter 메서드들
            public String getStartTime() {
                return startTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public String getRoom() {
                return room;
            }
        }
    }

    // 건물 데이터 로딩 메서드
    public void loadBuildingData() {
        DatabaseReference ref = database.getReference("Buildings");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Building> newBuildings = new HashMap<>();
                for (DataSnapshot buildingSnapshot : dataSnapshot.getChildren()) {
                    Building building = new Building();
                    building.name = buildingSnapshot.getKey();

                    // 위치 정보
                    DataSnapshot locationSnapshot = buildingSnapshot.child("Location");
                    building.latitude = locationSnapshot.child("latitude").getValue(Double.class);
                    building.longitude = locationSnapshot.child("longitude").getValue(Double.class);

                    // 수위실 정보
                    DataSnapshot securityOfficeSnapshot = buildingSnapshot.child("SecurityOffice");
                    if (securityOfficeSnapshot.exists()) {
                        building.securityOfficePhone = securityOfficeSnapshot.child("PhoneNumber").getValue(String.class);
                    }

                    // 야간강의 정보
                    DataSnapshot nightCoursesSnapshot = buildingSnapshot.child("NightCourses");
                    if (nightCoursesSnapshot.exists()) {
                        for (DataSnapshot courseSnapshot : nightCoursesSnapshot.getChildren()) {
                            Building.NightCourse nightCourse = building.new NightCourse();
                            nightCourse.courseName = courseSnapshot.child("courseName").getValue(String.class);

                            for (DataSnapshot sessionSnapshot : courseSnapshot.getChildren()) {
                                if (!sessionSnapshot.getKey().equals("courseName")) { // 강의명을 제외한 나머지 데이터
                                    Building.CourseSession session = building.new CourseSession();
                                    session.startTime = sessionSnapshot.child("StartTime").getValue(String.class);
                                    session.endTime = sessionSnapshot.child("EndTime").getValue(String.class);
                                    session.room = sessionSnapshot.child("Room").getValue(String.class);
                                    nightCourse.sessions.put(sessionSnapshot.getKey(), session); // 요일을 키로 사용
                                }
                            }
                            building.nightCourses.put(courseSnapshot.getKey(), nightCourse);
                        }
                    }

                    newBuildings.put(building.name, building);
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
                // 오류 처리
            }
        });
    }
}