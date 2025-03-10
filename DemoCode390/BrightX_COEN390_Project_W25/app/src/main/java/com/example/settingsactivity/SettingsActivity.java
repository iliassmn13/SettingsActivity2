package com.example.settingsactivity;

import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private BluetoothManager bluetoothManager;
    private boolean isConnected = false;
    protected AutoCompleteTextView brandSelect;
    protected AutoCompleteTextView modelSelect;
    protected AutoCompleteTextView lensSelect;
    protected Button saveButton;
    protected Button connectionButton;
    protected TextView connectionStatus;
    private GenerativeAI generativeAI;
    private sharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupToolbar();
        initializeHelpers();
        setupUIElements();
        setupBluetooth();
        setupListeners();
        loadSavedSettings();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
    }

    private void initializeHelpers() {
        generativeAI = new GenerativeAI(this);
        sharedPreferencesHelper = new sharedPreferencesHelper(this);
        bluetoothManager = BluetoothManager.getInstance(this);
    }

    private void setupUIElements() {
        // Find UI elements
        brandSelect = findViewById(R.id.brandSelect);
        modelSelect = findViewById(R.id.modelSelect);
        lensSelect = findViewById(R.id.lensSelect);
        saveButton = findViewById(R.id.saveButton);
        connectionStatus = findViewById(R.id.connectionStatus);
        connectionButton = findViewById(R.id.connectionButton);

        // Configure autocomplete textviews
        brandSelect.setThreshold(0);
        modelSelect.setThreshold(0);
        lensSelect.setThreshold(0);

        // Disable model selection until brand is selected
        modelSelect.setEnabled(false);
    }

    private void setupBluetooth() {
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getBluetoothAdapter();
        if (bluetoothAdapter == null) {
            connectionStatus.setText("No Bluetooth device found");
            connectionStatus.setBackgroundColor(Color.RED);
            connectionButton.setEnabled(false);
        }
    }

    private void setupListeners() {
        connectionButton.setOnClickListener(v -> {
            if (isConnected) {
                disconnectFromDevice();
            } else {
                connectToDevice();
            }
        });

        saveButton.setOnClickListener(v -> saveSettings());

        // Setup autocomplete listeners
        setupAutocompleteListeners();
    }

    private void setupAutocompleteListeners() {
        // Item click listeners
        brandSelect.setOnItemClickListener((parent, view, position, id) -> {
            String selection = (String) parent.getItemAtPosition(position);
            if (!selection.equals("Invalid input")) {
                brandSelect.setText(selection);
                brandSelect.dismissDropDown();
                modelSelect.setText("");
                lensSelect.setText("");
                modelSelect.setEnabled(true);
                modelSelect.setHint("Select model");
                modelSelect.requestFocus();
            }
        });

        modelSelect.setOnItemClickListener((parent, view, position, id) -> {
            String selection = (String) parent.getItemAtPosition(position);
            if (!selection.equals("Invalid input")) {
                modelSelect.setText(selection);
                modelSelect.dismissDropDown();
                lensSelect.setText("");
                lensSelect.requestFocus();
            }
        });

        lensSelect.setOnItemClickListener((parent, view, position, id) -> {
            String selection = (String) parent.getItemAtPosition(position);
            if (!selection.equals("Invalid input")) {
                lensSelect.setText(selection);
                lensSelect.dismissDropDown();
                lensSelect.clearFocus();
            }
        });

        // Text change listeners
        brandSelect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentText = s.toString();
                getBrandSuggestions(currentText);

                if (currentText.isEmpty()) {
                    modelSelect.setEnabled(false);
                    modelSelect.setText("");
                    modelSelect.setHint("Please select a brand first");
                } else {
                    modelSelect.setEnabled(true);
                    modelSelect.setHint("Select model");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        modelSelect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getModelSuggestions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lensSelect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getLensSuggestions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Focus change listeners
        brandSelect.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                getBrandSuggestions(brandSelect.getText().toString());
            }
        });

        modelSelect.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (brandSelect.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please select a brand first", Toast.LENGTH_SHORT).show();
                    brandSelect.requestFocus();
                } else {
                    getModelSuggestions(modelSelect.getText().toString());
                }
            }
        });

        lensSelect.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                getLensSuggestions(lensSelect.getText().toString());
            }
        });

        // Click listeners
        brandSelect.setOnClickListener(v -> getBrandSuggestions(brandSelect.getText().toString()));

        modelSelect.setOnClickListener(v -> {
            if (brandSelect.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please select a brand first", Toast.LENGTH_SHORT).show();
                brandSelect.requestFocus();
            } else {
                getModelSuggestions(modelSelect.getText().toString());
            }
        });

        lensSelect.setOnClickListener(v -> getLensSuggestions(lensSelect.getText().toString()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectionStatus();
    }

    // Update connection status UI
    private void updateConnectionStatus() {
        if (isConnected) {
            connectionStatus.setText("Device: " + BluetoothManager.DEVICE_NAME + " - Connected");
            connectionStatus.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
            connectionButton.setText("Disconnect");
            connectionButton.setBackgroundColor(Color.RED);
        } else {
            connectionStatus.setText("Device: " + BluetoothManager.DEVICE_NAME + " - Disconnected");
            connectionStatus.setBackgroundColor(Color.RED);
            connectionButton.setText("Connect");
            connectionButton.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
        }
    }

    // Connect to the ESP32 device
    private void connectToDevice() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            return;
        }

        if (bluetoothManager.connect(this)) {
            isConnected = true;
            updateConnectionStatus();
            Toast.makeText(this, "Connected to " + BluetoothManager.DEVICE_NAME, Toast.LENGTH_SHORT).show();
        } else {
            isConnected = false;
            updateConnectionStatus();
            Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Disconnect from the ESP32 device
    private void disconnectFromDevice() {
        bluetoothManager.disconnect();
        isConnected = false;
        updateConnectionStatus();
        Toast.makeText(this, "Disconnected from " + BluetoothManager.DEVICE_NAME, Toast.LENGTH_SHORT).show();
    }

    // Load saved camera settings from SharedPreferences
    private void loadSavedSettings() {
        String brand = sharedPreferencesHelper.getBrand();
        String model = sharedPreferencesHelper.getModel();
        String lens = sharedPreferencesHelper.getLens();

        if (!brand.isEmpty()) {
            brandSelect.setText(brand);
            modelSelect.setEnabled(true);
            modelSelect.setHint("Select model");
        }

        if (!model.isEmpty()) {
            modelSelect.setText(model);
        }

        if (!lens.isEmpty()) {
            lensSelect.setText(lens);
        }
    }

    // Save camera settings to SharedPreferences
    private void saveSettings() {
        String brand = brandSelect.getText().toString();
        String model = modelSelect.getText().toString();
        String lens = lensSelect.getText().toString();

        if (brand.isEmpty()) {
            Toast.makeText(this, "Please select a camera brand", Toast.LENGTH_SHORT).show();
            return;
        }

        if (model.isEmpty()) {
            Toast.makeText(this, "Please select a camera model", Toast.LENGTH_SHORT).show();
            return;
        }

        sharedPreferencesHelper.saveSettings(brand, model, lens, "");
        Toast.makeText(this, "Camera settings saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Get suggestions methods
    public void getBrandSuggestions(final String query) {
        new Thread(() -> {
            try {
                final List<String> suggestions = generativeAI.getSuggestions(query.toLowerCase(), "brand", "");

                runOnUiThread(() -> {
                    if (!suggestions.isEmpty()) {
                        setAdapter(brandSelect, suggestions);
                    } else {
                        setInvalidInputAdapter(brandSelect);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void getModelSuggestions(final String query) {
        final String brand = brandSelect.getText().toString();
        if (brand.isEmpty()) {
            return;
        }

        new Thread(() -> {
            try {
                final List<String> suggestions = generativeAI.getSuggestions(query.toLowerCase(), "model", brand);

                runOnUiThread(() -> {
                    if (!suggestions.isEmpty()) {
                        setAdapter(modelSelect, suggestions);
                    } else {
                        setInvalidInputAdapter(modelSelect);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void getLensSuggestions(final String query) {
        new Thread(() -> {
            try {
                final List<String> suggestions = generativeAI.getSuggestions(query.toLowerCase(), "lens", "");

                runOnUiThread(() -> {
                    if (!suggestions.isEmpty()) {
                        setAdapter(lensSelect, suggestions);
                    } else {
                        setInvalidInputAdapter(lensSelect);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Helper methods for setting adapters
    private void setAdapter(AutoCompleteTextView view, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                items
        );
        view.setAdapter(adapter);
        if (view.hasFocus()) {
            view.showDropDown();
        }
    }

    private void setInvalidInputAdapter(AutoCompleteTextView view) {
        ArrayList<String> invalidList = new ArrayList<>();
        invalidList.add("Invalid input");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                invalidList
        );
        view.setAdapter(adapter);
        if (view.hasFocus()) {
            view.showDropDown();
        }
    }

    // Check if we should be connected and update UI accordingly
    private void checkConnectionStatus() {
        isConnected = bluetoothManager.isConnected();
        updateConnectionStatus();

        // If we should be connected but are not, try to connect
        if (sharedPreferencesHelper.isBluetoothConnected() && !isConnected) {
            connectToDevice();
        }
    }
}