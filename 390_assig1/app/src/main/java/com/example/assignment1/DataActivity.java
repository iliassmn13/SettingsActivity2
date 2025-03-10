package com.example.assignment1;

import android.os.Bundle;
import android.content.Intent;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ListView;
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
import android.content.Context;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class DataActivity extends AppCompatActivity {
    private SharedPreferenceManager sharedPrefsManager;
    private EventAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<String> eventList;
    private boolean isNameMode = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data);

        sharedPrefsManager = new SharedPreferenceManager(this);
        recyclerView = findViewById(R.id.recyclerView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();
        popUpMenu();
        setEventList();
    }

    private void setupUI() {
        Toolbar dataToolbar = findViewById(R.id.dataToolbar);
        setSupportActionBar(dataToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dataToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void popUpMenu() {
        ImageButton toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(view -> {

            PopupMenu popup = new PopupMenu(this, view);
            popup.getMenuInflater().inflate(R.menu.menu_toggle, popup.getMenu());
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.toggleEvent) {
                    isNameMode = !isNameMode;
                    setEventList();
                }
                return true;
            });
            popup.show();
        });
    }

    private void setEventList() {
        eventList = sharedPrefsManager.getEventList();
        adapter = new EventAdapter(eventList);
        recyclerView.setAdapter(adapter);

        // Update the TextViews with the required format
        String name1 = isNameMode ? sharedPrefsManager.getCount1Name() : "1";
        String name2 = isNameMode ? sharedPrefsManager.getCount2Name() : "2";
        String name3 = isNameMode ? sharedPrefsManager.getCount3Name() : "3";
        int count1 = sharedPrefsManager.getEvent1Click();
        int count2 = sharedPrefsManager.getEvent2Click();
        int count3 = sharedPrefsManager.getEvent3Click();
        int totalEvents = sharedPrefsManager.getEventCounting();

        TextView eventATxt = findViewById(R.id.eventATxt);
        TextView eventBTxt = findViewById(R.id.eventBTxt);
        TextView eventCTxt = findViewById(R.id.eventCTxt);
        TextView totalEventTxt = findViewById(R.id.totalEventTxt);

    if (!isNameMode) {
        eventATxt.setText("Counter "+name1 + ": " + count1);
        eventBTxt.setText("Counter "+name2 + ": " + count2);
        eventCTxt.setText("Counter "+name3 + ": " + count3);
        totalEventTxt.setText("Total events: " + totalEvents);
    }
    else {
        eventATxt.setText(name1 + ": " + count1);
        eventBTxt.setText(name2 + ": " + count2);
        eventCTxt.setText(name3 + ": " + count3);
        totalEventTxt.setText("Total events: " + totalEvents);
    }

      if(!isNameMode){
        for (int i = 0; i < eventList.size(); i++) {
            eventList.set(i, String.valueOf(i + 1));
        }
        }
      else {
        eventList = sharedPrefsManager.getEventList();
      }
        adapter.updateEvents(eventList);
}
}


