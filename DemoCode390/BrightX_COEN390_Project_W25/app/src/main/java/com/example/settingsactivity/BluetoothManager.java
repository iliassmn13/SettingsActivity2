package com.example.settingsactivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothManager {
    private static BluetoothManager instance;

    public static final String DEVICE_NAME = "BrightX";
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean isConnected = false;
    private sharedPreferencesHelper prefHelper;

    //Constructor for singleton
    private BluetoothManager(Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        prefHelper = new sharedPreferencesHelper(context);
    }

    //Get singleton instance
    public static synchronized BluetoothManager getInstance(Context context) {
        if (instance == null) {
            instance = new BluetoothManager(context.getApplicationContext());
        }
        return instance;
    }

    //Connect to device
    public boolean connect(Context context) {
        // Dont try to reconnect if already connected
        if (isConnected && bluetoothSocket != null && bluetoothSocket.isConnected()) {
            return true;
        }

        //Close any existing connection first
        disconnect();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        try {
            //Find the device
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            BluetoothDevice device = null;

            if (pairedDevices != null) {
                for (BluetoothDevice d : pairedDevices) {
                    if (DEVICE_NAME.equals(d.getName())) {
                        device = d;
                        break;
                    }
                }
            }

            if (device == null) {
                return false;
            }

            //Connect
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();

            isConnected = true;
            prefHelper.setBluetoothConnected(true);

            return true;
        } catch (Exception e) {
            disconnect();
            return false;
        }
    }

    //Disconnect
    public void disconnect() {
        try {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
            }
        } catch (Exception e) {
        }

        isConnected = false;
        prefHelper.setBluetoothConnected(false);
    }

    //Check if connected
    public boolean isConnected() {
        return isConnected && bluetoothSocket != null && bluetoothSocket.isConnected();
    }

    //Get input stream
    public InputStream getInputStream() {
        return inputStream;
    }

    //Get output stream
    public OutputStream getOutputStream() {
        return outputStream;
    }

    //Get bluetooth adapter
    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
}
