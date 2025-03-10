package com.example.settingsactivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        connectButton = findViewById(R.id.connectButton);
        textView5 = findViewById(R.id.textView5);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            textView5.setText("Bluetooth not supported on this device.");
            return;
        }

        connectButton.setOnClickListener(v -> connectToDevice());
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