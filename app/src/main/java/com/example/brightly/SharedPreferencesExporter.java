package com.example.brightly;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class SharedPreferencesExporter {

    public static void exportSharedPreferences(Context context, String sharedPreferencesName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, sharedPreferencesName + ".txt");

        try (FileWriter writer = new FileWriter(file)) {
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                writer.append(entry.getKey()).append(": ").append(entry.getValue().toString()).append("\n");
            }
            writer.flush();
            Log.d("SharedPreferencesExport", "SharedPreferences exported to " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("SharedPreferencesExport", "Error writing SharedPreferences to file", e);
        }
    }
}
