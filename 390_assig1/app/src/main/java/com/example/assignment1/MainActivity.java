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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> eventList = new ArrayList<>();
    private SharedPreferenceManager sharedPrefsManager;
    protected Button eventAButton, eventBButton, eventCButton, showCountsButton, settingsButton;
    protected TextView totalCountText;
    protected int count1, count2, count3, eventCounting=0;

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
        count1=sharedPrefsManager.getEvent1Click();
        count2=sharedPrefsManager.getEvent2Click();
        count3=sharedPrefsManager.getEvent3Click();
        eventCounting=sharedPrefsManager.getEventCounting();
    }

    private void setupUI(){
        sharedPrefsManager = new SharedPreferenceManager(this);
        settingsButton = findViewById(R.id.settingsButton);
        eventAButton = findViewById(R.id.eventAButton);
        eventBButton = findViewById(R.id.eventBButton);
        eventCButton = findViewById(R.id.eventCButton);
        totalCountText = findViewById(R.id.totalCountText);
        showCountsButton = findViewById(R.id.showCountsButton);

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

        setupButtonClickListeners();
    }
    private int accessMaxCount(){
        String maxCountSt = sharedPrefsManager.getMaxCount();
        int maxCount = 0;
        if (maxCountSt != null) {
            maxCount = Integer.parseInt(maxCountSt);
        }
        return maxCount;
    }
    private int accessEventCounting(){
        int eventCounting = sharedPrefsManager.getEventCounting();
        return eventCounting;
    }
    private void goToSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void goToDataActivity(){
        Intent intent = new Intent(this, DataActivity.class);
        startActivity(intent);
    }

    private void showCounterNames() {
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
        if (accessEventCounting() == accessMaxCount()) {
            goToSettingsActivity();
        }
        else {
            totalCountText.setText("Total Counts: " + accessEventCounting());
        }
    }

    private void setupButtonClickListeners() {
        View.OnClickListener onButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventList = sharedPrefsManager.getEventList();
                String event = "";
                int viewId = view.getId();
                if (viewId == R.id.eventAButton) {
                    event = sharedPrefsManager.getCount1Name();
                    count1++;
                    sharedPrefsManager.putEvent1Click(count1);
                } else if (viewId == R.id.eventBButton) {
                    event = sharedPrefsManager.getCount2Name();
                    count2++;
                    sharedPrefsManager.putEvent2Click(count2);
                } else if (viewId == R.id.eventCButton) {
                    event = sharedPrefsManager.getCount3Name();
                    count3++;
                    sharedPrefsManager.putEvent3Click(count3);
                }
                eventList.add(0, event); // Add event to the top of the list
                sharedPrefsManager.putEventList(eventList);
                updateCount(++eventCounting);
                sharedPrefsManager.putEventCounting(eventCounting);

                if (accessEventCounting() == accessMaxCount()) {
                    resetAndGoToSettings();
                }
            }
        };

        eventAButton.setOnClickListener(onButtonClick);
        eventBButton.setOnClickListener(onButtonClick);
        eventCButton.setOnClickListener(onButtonClick);
    }

    private void updateCount(int count) {
        sharedPrefsManager.putEventCounting(count);
        totalCountText.setText("Total Counts: " + count);
    }
    private void resetAndGoToSettings() {
        sharedPrefsManager.clearSharedPreferences();
        count1 = 0;
        count2 = 0;
        count3 = 0;
        eventCounting = 0;
        goToSettingsActivity();
    }

}