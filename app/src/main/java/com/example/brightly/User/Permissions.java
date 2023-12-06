package com.example.brightly.User;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final int CALL_PHONE_PERMISSION_REQUEST_CODE = 2;

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

    // 전화 권한 확인 및 요청 메서드
    public static void checkCallPhonePermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CALL_PHONE},
                    CALL_PHONE_PERMISSION_REQUEST_CODE);
        } else {
            // 전화 권한이 이미 부여되었다면 필요한 작업 수행
            // 예: 전화 걸기 기능 활성화
        }
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE || requestCode == CALL_PHONE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handlePermissionGranted(activity);
            } else {
                handlePermissionDenied(activity, requestCode); // 권한 거부 처리
            }
        }
    }

    private static void handlePermissionGranted(Activity activity) {
        if (activity instanceof LocationPermissionHandler) {
            ((LocationPermissionHandler) activity).initializeLocationRelatedStuff();
        }
    }

    private static void handlePermissionDenied(final Activity activity, int requestCode) {
        String message = "";
        String title = "";

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            title = "위치 권한 필요";
            message = "이 기능을 사용하기 위해서는 위치 권한이 필요합니다. 설정에서 권한을 허용해주세요.";
        } else if (requestCode == CALL_PHONE_PERMISSION_REQUEST_CODE) {
            title = "전화 권한 필요";
            message = "이 기능을 사용하기 위해서는 전화 권한이 필요합니다. 설정에서 권한을 허용해주세요.";
        }

        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("설정으로 이동", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 사용자를 앱 설정 화면으로 이동
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", activity.getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 다이얼로그 닫기
                    }
                })
                .show();
    }

    // 위치 권한 처리를 위한 인터페이스
    public interface LocationPermissionHandler {
        void initializeLocationRelatedStuff();
    }
}