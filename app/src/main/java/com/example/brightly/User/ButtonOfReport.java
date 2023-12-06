package com.example.brightly.User;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;

public class ButtonOfReport {
    private Context context;
    private Button reportButton;

    public ButtonOfReport(Context context, Button reportButton) {
        this.context = context;
        this.reportButton = reportButton;

        setupButton();
    }

    private void setupButton() {
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 전화 권한 확인 및 요청
                Permissions.checkCallPhonePermission((Activity) context);

                // 권한이 이미 부여되었다면, 바로 전화 기능 수행
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    callEmergencyNumber();
                }
            }
        });
    }

    private void callEmergencyNumber() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:112"));
        context.startActivity(callIntent);
    }
}