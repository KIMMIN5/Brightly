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
    private Context context;
    private GoogleMap mMap;

    public EventOfBuilding(Context context, GoogleMap map) {
        this.context = context;
        this.mMap = map;
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTag() instanceof DataFetcher.Building) {
            Log.d("EventOfBuilding", "Marker clicked: " + marker.getTitle());
            DataFetcher.Building building = (DataFetcher.Building) marker.getTag();
            showBuildingInfo(building);
            return true;
        }
        return false;
    }

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

        // 필요에 따라 ScrollView 사용

        bottomSheetDialog.show();
    }

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