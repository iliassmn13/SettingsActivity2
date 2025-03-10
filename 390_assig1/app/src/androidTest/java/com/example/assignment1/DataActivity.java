package com.example.assignment1;

import android.os.Bundle;

import android.os.Bundle;
import android.content.Intent;
import android.text.InputFilter;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
// TODO: Change between default (EVENT A B C) to custome names on toggle button on navigation

public class DataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();
    }

    private void setupUI() {
        Button saveButton = findViewById(R.id.saveButton);
    }
}