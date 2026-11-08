package com.example.simplecalculator;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Pembungkus SharedPreferences: semua akses baca/tulis lewat satu kelas ini.
 * File penyimpanan: simple_calculator_prefs.xml (di data internal aplikasi).
 */
public class PrefsManager {

    private static final String PREFS_NAME = "simple_calculator_prefs";
    private static final String KEY_LAST_EXPRESSION = "last_expression";
    private static final String KEY_CALCULATOR_OPENED = "calculator_opened";

    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Rumus terakhir yang diketik (mis. "78-65")
    public void saveExpression(String expression) {
        prefs.edit().putString(KEY_LAST_EXPRESSION, expression).apply(); // asynchronous
    }

    public String getExpression() {
        return prefs.getString(KEY_LAST_EXPRESSION, "");
    }

    // Apakah pengguna sudah menekan "Buka Kalkulator" (layar pembuka dilewati)
    public void saveCalculatorOpened(boolean opened) {
        prefs.edit().putBoolean(KEY_CALCULATOR_OPENED, opened).apply();
    }

    public boolean isCalculatorOpened() {
        return prefs.getBoolean(KEY_CALCULATOR_OPENED, false);
    }
}
