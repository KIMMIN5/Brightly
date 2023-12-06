package com.example.brightly.Map;

import com.example.brightly.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MapStyleOptions;
import android.content.Context;
import android.content.res.Configuration;

public class DayAndNight {
    public static void setMapStyleBasedOnTime(GoogleMap googleMap, Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                // 야간 모드일 때 지도 스타일 설정
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.night_map_style));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                // 주간 모드일 때 지도 스타일 설정
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.day_map_style));
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                // 설정되지 않았을 때 기본 스타일 유지
                break;
        }
    }
}