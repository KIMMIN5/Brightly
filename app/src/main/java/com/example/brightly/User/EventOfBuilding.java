package com.example.brightly.User;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.example.brightly.Admin.DataFetcher;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.brightly.R;

import java.util.HashMap;
import java.util.Map;

public class EventOfBuilding implements GoogleMap.OnMarkerClickListener {
    private Context context; // 애플리케이션 컨텍스트
    private GoogleMap mMap; // Google Maps 객체

    // 생성자: Context와 GoogleMap 객체 초기화, 마커 클릭 리스너 설정
    public EventOfBuilding(Context context, GoogleMap map) {
        this.context = context;
        this.mMap = map;
        mMap.setOnMarkerClickListener(this); // 마커 클릭 리스너 설정
    }

    // 마커 클릭 시 호출되는 메서드
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTag() instanceof DataFetcher.Building) {
            // 클릭된 마커가 건물 정보를 포함하고 있는 경우
            Log.d("EventOfBuilding", "Marker clicked: " + marker.getTitle());
            DataFetcher.Building building = (DataFetcher.Building) marker.getTag();
            showBuildingInfo(building); // 건물 정보를 표시하는 메서드 호출
            return true; // 이벤트 처리 완료
        }
        return false; // 다른 마커 클릭 시 기본 동작 수행
    }

    // 건물 정보를 표시하는 메서드
    private void showBuildingInfo(DataFetcher.Building building) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = View.inflate(context, R.layout.building_info_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // 건물 이름 표시
        TextView buildingNameView = bottomSheetView.findViewById(R.id.building_name);
        buildingNameView.setText(building.getName());

        // 수위실 정보 표시
        TextView securityOfficeView = bottomSheetView.findViewById(R.id.security_office_info);
        securityOfficeView.setText("수위실 정보: " + building.getSecurityOfficePhone());
        // 수위실 전화번호 클릭 가능하게 설정하는 로직 추가

        // 야간강의실 정보 표시
        TextView nightClassroomView = bottomSheetView.findViewById(R.id.night_class_info);
        nightClassroomView.setText("야간강의실 정보: " + getNightClassroomInfo(building));

        bottomSheetDialog.show(); // BottomSheetDialog 표시
    }

    // 야간강의실 정보를 문자열로 반환하는 메서드
    private String getNightClassroomInfo(DataFetcher.Building building) {
        StringBuilder infoBuilder = new StringBuilder();
        HashMap<String, DataFetcher.Building.NightCourse> nightCourses = building.getNightCourses();

        if (nightCourses != null && !nightCourses.isEmpty()) {
            for (DataFetcher.Building.NightCourse course : nightCourses.values()) {
                infoBuilder.append(course.getCourseName()).append("\n");
                HashMap<String, DataFetcher.Building.CourseSession> sessions = course.getSessions();
                for (Map.Entry<String, DataFetcher.Building.CourseSession> sessionEntry : sessions.entrySet()) {
                    DataFetcher.Building.CourseSession session = sessionEntry.getValue();
                    infoBuilder.append(sessionEntry.getKey())
                            .append(" - Room: ").append(session.getRoom())
                            .append(", Time: ").append(session.getStartTime())
                            .append(" to ").append(session.getEndTime())
                            .append("\n");
                }
            }
        } else {
            infoBuilder.append("야간 강의 정보 없음");
        }

        return infoBuilder.toString().trim();
    }
}