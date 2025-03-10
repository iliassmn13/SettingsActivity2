package com.example.settingsactivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Bluetooth";
    private static final String DEVICE_NAME = "BrightX";  // Must match ESP32 name
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    Button connectButton;
    TextView textView5;
    ImageButton popupMenuButton;
    Button historyButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupUI();
        connectButton = findViewById(R.id.connectButton);
        textView5 = findViewById(R.id.textView5);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            textView5.setText("Bluetooth is not supported on this device.");
            return;
        }

        connectButton.setOnClickListener(v -> connectToDevice());
    }

    private void setupUI() {
        // Set up the UI
        Button buttonGotoSettings = findViewById(R.id.button_goto_settings);
        Button historyButton = findViewById(R.id.historyButton);
        ImageButton popupMenuButton = findViewById(R.id.popupMenuButton);
        buttonGotoSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        popupMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpMenu(v);
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, History.class);
                startActivity(intent);
            }
        });
    }

    private void showPopUpMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add("SETTINGS");
        popupMenu.getMenu().add("HISTORY");
        popupMenu.getMenu().add("ABOUT US");

        popupMenu.show();
    }

    private void goToSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void goToHistoryActivity() {
        Intent intent = new Intent(MainActivity.this, History.class);
        startActivity(intent);
    }

    private void saveChosenActionMenu(String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("toggle_data", value);
        editor.apply();
    }

    private void connectToDevice() {
        if (!hasPermissions()) {
            requestPermissions();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice esp32Device = null;

        if (pairedDevices != null) {
            for (BluetoothDevice device : pairedDevices) {
                if (DEVICE_NAME.equals(device.getName())) {
                    esp32Device = device;
                    break;
                }
            }
        }

        if (esp32Device == null) {
            textView5.setText("ESP32 not found among paired devices.");
            return;
        }

        try {
            bluetoothSocket = esp32Device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
            textView5.setText("Connected to ESP32!");
            readData();
        } catch (Exception e) {
            Log.e(TAG, "Connection failed: " + e.getMessage());
            textView5.setText("Connection failed.");
        }
    }

    private void readData() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytes;
            while (bluetoothSocket != null && bluetoothSocket.isConnected()) {
                try {
                    bytes = inputStream.read(buffer);
                    String data = new String(buffer, 0, bytes);
                    Log.d(TAG, "Received from ESP32: " + data);
                    runOnUiThread(() -> textView5.setText("Received: " + data));
                } catch (Exception e) {
                    Log.e(TAG, "Error reading data: " + e.getMessage());
                    break;
                }
            }
        }).start();
    }

    private boolean hasPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.BLUETOOTH_CONNECT
        }, 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (bluetoothSocket != null) bluetoothSocket.close();
        } catch (Exception e) {
            Log.e(TAG, "Error closing socket", e);
        }
    }
}