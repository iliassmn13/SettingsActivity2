package com.example.settingsactivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import java.io.InputStream;
import java.util.List;

public class DataActivity extends AppCompatActivity {
    private static final String TAG = "DataActivity";
    private BluetoothManager bluetoothManager;
    private Thread readThread;
    private boolean isReading = false;
    private boolean isPaused = false;
    private boolean isConnected = false;
    Button connectButton;
    Button setButton;
    Button getRecommendationsButton;
    TextView luxTextView;
    TextView recommendationsTextView;
    sharedPreferencesHelper sharedPreferencesHelper;
    private GenerativeAI generativeAI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.data), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initializeUIAndHelpers();
        setupButtonListeners();
    }

    private void initializeUIAndHelpers() {
        connectButton = findViewById(R.id.refreshButton);
        setButton = findViewById(R.id.setButton);
        getRecommendationsButton = findViewById(R.id.getRecommendationsButton);
        luxTextView = findViewById(R.id.luxTextView);
        recommendationsTextView = findViewById(R.id.recommendationsTextView);
        generativeAI = new GenerativeAI(this);
        sharedPreferencesHelper = new sharedPreferencesHelper(this);
        bluetoothManager = BluetoothManager.getInstance(this);

        // Check if Bluetooth is supported
        if (bluetoothManager.getBluetoothAdapter() == null) {
            luxTextView.setText("Bluetooth is not supported on this device.");
            connectButton.setEnabled(false);
        }
    }

    // Function to setup all button click listeners
    private void setupButtonListeners() {
        // Connect/Disconnect button
        connectButton.setOnClickListener(v -> {
            if (isConnected) {
                closeConnection();
                connectButton.setText("Connect");
                isConnected = false;
                luxTextView.setText("Disconnected");
            } else {
                connectToDevice();
            }
        });

        // Set/Refresh button
        setButton.setOnClickListener(v -> {
            if (isPaused) {
                // Resume data stream
                isPaused = false;
                setButton.setText("Set");

                // Restart reading if needed
                if (isConnected && !isReading) {
                    readData();
                }
            } else {
                // Pause data stream
                isPaused = true;
                setButton.setText("Refresh");

                // Save current data to preferences
                String sensorData = luxTextView.getText().toString();
                sharedPreferencesHelper.saveSettings(
                        sharedPreferencesHelper.getBrand(),
                        sharedPreferencesHelper.getModel(),
                        sharedPreferencesHelper.getLens(),
                        sensorData
                );
            }
        });

        // Get recommendations button
        getRecommendationsButton.setOnClickListener(v -> getRecommendations());
    }

    private void getRecommendations() {
        // Check if data is paused
        if (!isPaused) {
            Toast.makeText(this, "Please press 'Set' to freeze the sensor data first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get sensor data from textview
        String sensorData = luxTextView.getText().toString();
        if (sensorData.isEmpty()) {
            Toast.makeText(this, "No sensor data available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get camera settings from preferences
        String brand = sharedPreferencesHelper.getBrand();
        String model = sharedPreferencesHelper.getModel();
        String lens = sharedPreferencesHelper.getLens();

        if (brand.isEmpty() || model.isEmpty()) {
            Toast.makeText(this, "Please set camera settings first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format data for AI
        if (sensorData.startsWith("LUX (Lumens):") && !sensorData.contains("IR:") && !sensorData.contains("RGB:")) {
            String luxValue = sensorData.replace("LUX (Lumens):", "").trim();
            sensorData = "LUX: " + luxValue + ", IR: N/A, RGB Spectrum: N/A";
        }

        // Show loading message
        recommendationsTextView.setText("Getting recommendations...");

        // Get AI recommendations in background
        final String finalSensorData = sensorData;
        new Thread(() -> {
            List<String> recommendations = generativeAI.getRecommendations(finalSensorData, brand, model, lens);

            // Update UI with results
            runOnUiThread(() -> {
                if (recommendations != null && !recommendations.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (String rec : recommendations) {
                        sb.append(rec).append("\n");
                    }
                    recommendationsTextView.setText(sb.toString());
                } else {
                    recommendationsTextView.setText("Could not generate recommendations. Please try again.");
                }
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectionStatus();
    }

    @Override // Only stop reading data, don't close connection
    protected void onPause() {
        super.onPause();
        stopReading();
    }

    // Stop reading bluetooth data
    private void stopReading() {
        isReading = false;
        if (readThread != null) {
            readThread.interrupt();
            readThread = null;
        }
    }

    // Close bluetooth connection
    private void closeConnection() {
        stopReading();
        bluetoothManager.disconnect();
        isConnected = false;
        updateUI();
    }

    // Connect to ESP32 device
    private void connectToDevice() {
        isPaused = false;
        setButton.setText("Set");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            return;
        }

        if (bluetoothManager.connect(this)) {
            // Successfully connected
            isConnected = true;
            updateUI();
            readData();  // Start reading data
        } else {
            // Failed to connect
            isConnected = false;
            luxTextView.setText("Connection failed.");
            updateUI();
        }
    }

    // Update UI based on connection status
    private void updateUI() {
        if (isConnected) {
            connectButton.setText("Disconnect");
            luxTextView.setText("Connected to ESP32!");
        } else {
            connectButton.setText("Connect");
        }
    }

    // Function to read data from bluetooth
    public void readData() {
        // Stop any existing thread first
        stopReading();

        // Start new reading thread
        isReading = true;
        readThread = new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytes;

            // This timestamp helps prevent glitching by limiting update frequency
            long lastUpdateTime = 0;
            // Only update UI every 150ms, this prevents glitching while still showing live data
            final long UPDATE_THROTTLE = 150;

            while (isReading && bluetoothManager.isConnected()) {
                try {
                    // Get input stream from manager
                    InputStream inputStream = bluetoothManager.getInputStream();
                    if (inputStream == null) {
                        break;
                    }

                    // Only try to read when data is available
                    if (inputStream.available() > 0) {
                        bytes = inputStream.read(buffer);
                        final String data = new String(buffer, 0, bytes);

                        // Only update UI if enough time has passed since last update
                        // This is the key change to prevent glitching while still showing live data
                        long currentTime = System.currentTimeMillis();
                        if (!isPaused && currentTime - lastUpdateTime > UPDATE_THROTTLE) {
                            lastUpdateTime = currentTime;

                            runOnUiThread(() -> {
                                // Update raw data view
                                luxTextView.setText(data);

                                // Parse data for LUX value
                                if (data.contains("LUX:")) {
                                    int startIndex = data.indexOf("LUX:") + 4;
                                    int endIndex = data.indexOf(",", startIndex);
                                    if (endIndex == -1) endIndex = data.length();

                                    if (startIndex < endIndex && endIndex <= data.length()) {
                                        String luxValue = data.substring(startIndex, endIndex).trim();
                                        luxTextView.setText("LUX (Lumens): " + luxValue);
                                    }
                                }
                            });
                        }
                    }

                } catch (Exception e) {
                    if (isReading) {
                        runOnUiThread(() -> {
                            luxTextView.setText("Connection error: " + e.getMessage());
                            connectButton.setText("Connect"); // Reset the button
                            isConnected = false;
                        });
                    }
                    break;
                }
            }
        });

        readThread.start();
    }

    // Check connection status and update UI accordingly
    private void checkConnectionStatus() {
        isConnected = bluetoothManager.isConnected();
        updateUI();

        // If we're connected, make sure we're reading data
        if (isConnected) {
            if (!isReading) {
                readData(); // Start reading data if connected but not already reading
            }
        }
        // If we should be connected but are not, try to connect
        else if (sharedPreferencesHelper.isBluetoothConnected()) {
            connectToDevice();
        }
    }
}