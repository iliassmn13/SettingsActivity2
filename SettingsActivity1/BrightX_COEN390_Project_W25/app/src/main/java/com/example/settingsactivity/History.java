package com.example.settingsactivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;


public class History extends AppCompatActivity implements OnClickListener,
        OnItemClickListener, OnItemLongClickListener {
    List<String> historyEntries = new ArrayList<>();
    List<String> timestamps = new ArrayList<>();
    Button bClear;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.toolbarHistory);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("History");
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        HistoryDB bdb = new HistoryDB(this);
        Cursor c = null;
        try {
            c = bdb.retrieveHistory();
            if (c != null && c.moveToFirst()) {
                int timestampIndex = c.getColumnIndex("timestamp");
                int recommendationIndex = c.getColumnIndex("recommendation");

                if (timestampIndex == -1 || recommendationIndex == -1) {
                    Toast.makeText(this, "Column not found in the database.", Toast.LENGTH_SHORT).show();
                    return;
                }

                do {
                    String timestamp = c.getString(timestampIndex);
                    String rec = c.getString(recommendationIndex);
                    historyEntries.add(timestamp + "\n" + rec);
                    timestamps.add(timestamp);
                } while (c.moveToNext());

                ListView books = findViewById(R.id.lvHistory);
                books.setOnItemClickListener(this);
                books.setOnItemLongClickListener(this);
                books.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyEntries));
            } else {
                Toast.makeText(this, "There are no History to show.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open History database.", Toast.LENGTH_SHORT).show();
        } finally {
            if (c != null) {
                c.close();
            }
            bdb.close();
        }

        Button bClear = findViewById(R.id.bClear);
        bClear.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.bClear) {
            HistoryDB bdb = new HistoryDB(this);
            try {
                bdb.clearHistory();
                Toast.makeText(this, "History Cleared.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to clear history.", Toast.LENGTH_SHORT).show();
            } finally {
                bdb.close();
            }

            finish();
            startActivity(getIntent());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();  // Refresh the list every time the activity resumes
    }


    private void loadHistory() {
        HistoryDB bdb = new HistoryDB(this);
        Cursor c = null;
        historyEntries.clear();  // Clear the existing data
        timestamps.clear();      // Clear the timestamps as well

        try {
            c = bdb.retrieveHistory();
            if (c != null && c.moveToFirst()) {
                int timestampIndex = c.getColumnIndex("timestamp");
                int recommendationIndex = c.getColumnIndex("recommendation");

                do {
                    String timestamp = c.getString(timestampIndex);
                    String rec = c.getString(recommendationIndex);
                    historyEntries.add(timestamp + "\n" + rec);
                    timestamps.add(timestamp);
                } while (c.moveToNext());

                ListView books = findViewById(R.id.lvHistory);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyEntries);
                books.setAdapter(adapter);
            } else {
                Toast.makeText(this, "There are no history entries to show.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Unable to load history data.", Toast.LENGTH_SHORT).show();
        } finally {
            if (c != null) {
                c.close();
            }
            bdb.close();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(History.this, HistoryItemActivity.class);
        String entryDetails = historyEntries.get(position);
        String timestamp = timestamps.get(position); // This should be the unique identifier
        intent.putExtra("details", entryDetails);
        intent.putExtra("timestamp", timestamp);
        startActivity(intent);

    }



    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        try {
            String timestamp = timestamps.get(arg2);
            HistoryDB bdb = new HistoryDB(this);
            bdb.deleteHistory(timestamp);
            bdb.close();
            startActivity(getIntent());
            Toast.makeText(this, "Record Deleted: \n" + historyEntries.get(arg2), Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}




