package com.example.brightly.User;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public static void checkLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            handlePermissionGranted(activity);
        }
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handlePermissionGranted(activity);
            } else {
                // 권한 거부에 대한 처리
                handlePermissionDenied(activity);
            }
        }
    }

    private static void handlePermissionGranted(Activity activity) {
        if (activity instanceof LocationPermissionHandler) {
            ((LocationPermissionHandler) activity).initializeLocationRelatedStuff();
        }
    }

    private static void handlePermissionDenied(Activity activity) {
        // 권한 거부에 대한 사용자 안내 처리
    }

    // 위치 권한 처리를 위한 인터페이스
    public interface LocationPermissionHandler {
        void initializeLocationRelatedStuff();
    }
}