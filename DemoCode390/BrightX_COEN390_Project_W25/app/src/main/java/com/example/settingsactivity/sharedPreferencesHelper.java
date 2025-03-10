package com.example.settingsactivity;
import android.content.Context;
import android.content.SharedPreferences;

public class sharedPreferencesHelper {
    private static final String PREFS_NAME = "camera_settings";

    // Preference keys
    private static final String KEY_BRAND = "brand";
    private static final String KEY_MODEL = "model";
    private static final String KEY_LENS = "lens";
    private static final String KEY_SENSOR_DATA = "sensor_data";
    private static final String KEY_BT_CONNECTED = "is_bluetooth_connected";
    private static final String KEY_BT_LAST_CONNECTED = "bluetooth_last_connected";

    private SharedPreferences sharedPreferences;
    private Context context;

    public sharedPreferencesHelper(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Camera settings methods
    public void saveSettings(String brand, String model, String lens, String sensorData) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_BRAND, brand);
        editor.putString(KEY_MODEL, model);
        editor.putString(KEY_LENS, lens);
        editor.putString(KEY_SENSOR_DATA, sensorData);
        editor.apply();
    }

    public String getBrand() {
        return sharedPreferences.getString(KEY_BRAND, "");
    }

    public String getModel() {
        return sharedPreferences.getString(KEY_MODEL, "");
    }

    public String getLens() {
        return sharedPreferences.getString(KEY_LENS, "");
    }

    public String getSensorData() {
        return sharedPreferences.getString(KEY_SENSOR_DATA, "");
    }

    // Bluetooth connection state
    public void setBluetoothConnected(boolean isConnected) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_BT_CONNECTED, isConnected);
        editor.apply();
    }

    public boolean isBluetoothConnected() {
        return sharedPreferences.getBoolean(KEY_BT_CONNECTED, false);
    }

}