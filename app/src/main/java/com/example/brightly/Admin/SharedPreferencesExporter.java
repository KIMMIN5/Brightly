package com.example.brightly.Admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class SharedPreferencesExporter {

    public static void exportSharedPreferences(Context context, String sharedPreferencesName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        File exportDir = context.getExternalFilesDir(null);
        if (exportDir == null) {
            Log.e("SharedPreferencesExport", "External storage not available");
            return;
        }

        File file = new File(exportDir, sharedPreferencesName + ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                writer.append(entry.getKey()).append(": ").append(entry.getValue().toString()).append("\n");
            }
            writer.flush();
        } catch (IOException e) {
            Log.e("SharedPreferencesExport", "Error writing SharedPreferences to file", e);
        }
    }
}