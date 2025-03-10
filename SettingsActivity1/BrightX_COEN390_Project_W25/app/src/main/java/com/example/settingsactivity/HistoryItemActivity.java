package com.example.settingsactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HistoryItemActivity extends AppCompatActivity {

    private String timestamp;  // Variable to hold the timestamp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_item);

        Toolbar toolbar = findViewById(R.id.toolbarHistoryItem);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Detailed View");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();  // This will handle back navigation intuitively
            }
        });

        // Retrieve details and timestamp from intent
        TextView detailsTextView = findViewById(R.id.detailsTextView);
        String details = getIntent().getStringExtra("details");
        timestamp = getIntent().getStringExtra("timestamp");  // Retrieve the timestamp passed from History Activity
        detailsTextView.setText(details);

        // Set up the delete button
        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHistoryItem();  // Call the method to delete the item
            }
        });
    }

    private void deleteHistoryItem() {
        HistoryDB bdb = new HistoryDB(this);
        try {
            bdb.deleteHistory(timestamp);  // Use the timestamp to delete the history item
            Toast.makeText(this, "History item deleted", Toast.LENGTH_SHORT).show();
            finish();  // Close this activity and return to the History list
        } catch (Exception e) {
            Toast.makeText(this, "Failed to delete history item", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            bdb.close();
        }
    }
}


