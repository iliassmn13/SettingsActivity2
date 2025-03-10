// By Iliass Bouhsane
// ID: 40263483
package com.example.assignment1;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import android.util.Log;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.content.Context;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assignment1.SharedPreferenceManager;
import com.example.assignment1.SettingsActivity;
import com.example.assignment1.DataActivity;

public class MainActivity extends AppCompatActivity {

    // Initializing the variables
    private SharedPreferenceManager sharedPrefsManager;
    protected Button eventAButton, eventBButton, eventCButton, showCountsButton, settingsButton;
    protected TextView totalCountText;
    protected View.OnClickListener messageOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.A), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        showCounterNames();
        showCounterNumber();
    }

    protected void onStop(){
        super.onStop();
        sharedPrefsManager.clearSharedPreferences();
    }

    private void setupUI(){
        sharedPrefsManager = new SharedPreferenceManager(this);
        settingsButton = findViewById(R.id.settingsButton);
        eventAButton = findViewById(R.id.eventAButton);
        eventBButton = findViewById(R.id.eventBButton);
        eventCButton = findViewById(R.id.eventCButton);
        totalCountText = findViewById(R.id.totalCountText);
        showCountsButton = findViewById(R.id.showCountsButton);
        // TODO: Set on click for each button, if clicked increment the count. If counts surpass the max, then reset the count to 0
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettingsActivity();
            }
        });

        showCountsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDataActivity();
            }
        });
    }
    private void goToSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        //TODO: Check if the strings of count are not empty. If they are, send to settings activity
        startActivity(intent);
    }

    private void goToDataActivity(){
        Intent intent = new Intent(this, DataActivity.class);
        //TODO: Check if the strings of count are not empty. If they are, send to settings activity
        startActivity(intent);
    }

    private void showCounterNames() {
        SharedPreferences sharedPrefs = getSharedPreferences("SettingsActivity", Context.MODE_PRIVATE);
        String name1 = sharedPrefsManager.getCount1Name();
        String name2 = sharedPrefsManager.getCount2Name();
        String name3 = sharedPrefsManager.getCount3Name();
        if (name1 == null || name2 == null || name3 == null) {
            goToSettingsActivity();
        }
        else {
            eventAButton.setText(name1);
            eventBButton.setText(name2);
            eventCButton.setText(name3);
        }
    }

    private void showCounterNumber() {
        SharedPreferences sharedPrefs = getSharedPreferences("SettingsActivity", Context.MODE_PRIVATE);
        String counts = sharedPrefs.getString("maxCount", null);
        if (counts == null) {
            goToSettingsActivity();
        }
        else {
            totalCountText.setText("Total Counts: " + counts);
        }
    }
}