package com.example.sqlassignment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private AccessDBHelper accessDBHelper;
    private RecyclerView accessListView;
    private AccessAdapter accessAdapter;
    private Button deleteButton;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.def), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        deleteButton = findViewById(R.id.deleteButton);
        String studentIdStr = intent.getStringExtra("studentId");
        studentId = Integer.parseInt(studentIdStr);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studentId != -1) {
                    removeProfileFromMainActivity(studentId);
                } else {
                    Toast.makeText(ProfileActivity.this, "Invalid student ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        accessDBHelper = new AccessDBHelper(getBaseContext());
        setupUI();
        changeTextViews();
        logAccessEvent("Opened");
        loadAccessEvents();
    }

    private void setupUI(){
        Toolbar profileToolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(profileToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        profileToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logAccessEvent("Closed");
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        String studentIdStr = intent.getStringExtra("studentId");
        if (studentIdStr == null) {
            Log.e(TAG, "Student ID is null");
            return;
        }
        int studentId = Integer.parseInt(studentIdStr);
        Log.d(TAG, "Student ID: " + studentId);

        accessAdapter = new AccessAdapter(accessDBHelper.getAccessByStudentId(studentId));

        accessListView = findViewById(R.id.accessListView);
        if (accessListView == null) {
            Log.e(TAG, "RecyclerView is null");
        } else {
            accessListView.setLayoutManager(new LinearLayoutManager(this));
            accessListView.setAdapter(accessAdapter);
        }

        // Decorative divider line between each list element
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(accessListView.getContext(), LinearLayoutManager.VERTICAL);
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider);
        if (dividerDrawable != null) {
            dividerItemDecoration.setDrawable(dividerDrawable);
        }
        accessListView.addItemDecoration(dividerItemDecoration);

    }

    private void changeTextViews(){

        // Extract the data from the applied intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");
        String studentId = intent.getStringExtra("studentId");
        String gpa = intent.getStringExtra("gpa");

        if (name == null || surname == null || studentId == null || gpa == null) {
            Log.e(TAG, "One or more Intent extras are null");
            return;
        }

        assert studentId != null;

        int studentIdInt;
        try {
            studentIdInt = Integer.parseInt(studentId);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid student ID format", e);
            return;
        }

        accessAdapter = new AccessAdapter(accessDBHelper.getAccessByStudentId(studentIdInt));
        String creationTimeStamp = "N/A";
        if (accessAdapter.getItemCount() > 0) {
            creationTimeStamp = accessAdapter.getTimestamp(accessAdapter.getItemCount() - 1);
            Log.d(TAG, "Profile created: " + creationTimeStamp);
        }
        Log.d(TAG, "Name: " + name + ", Surname: " + surname + ", Student ID: " + studentId + ", GPA: " + gpa);

        // Set data to TextViews
        TextView nameTextView = findViewById(R.id.nameText);
        TextView surnameTextView = findViewById(R.id.surnameText);
        TextView studentIdTextView = findViewById(R.id.idText);
        TextView gpaTextView = findViewById(R.id.gpaText);
        TextView creationTimeStampTextView = findViewById(R.id.creationTimeStampText);

        if (nameTextView == null || surnameTextView == null || studentIdTextView == null || gpaTextView == null) {
            Log.e(TAG, "One or more TextViews are null");
        } else {
            nameTextView.setText("Name: " + name);
            surnameTextView.setText("Surname: " + surname);
            studentIdTextView.setText("ID: " + studentId);
            gpaTextView.setText("GPA: " + gpa);
            creationTimeStampTextView.setText("Profile created: " + creationTimeStamp);
        }
    }

    public void logAccessEvent(String accessType) {

        Intent intent = getIntent();

        String studentIdStr = intent.getStringExtra("studentId");
        if (studentIdStr == null) {
            Log.e(TAG, "Student ID is null");
            return;
        }

        int studentId = Integer.parseInt(studentIdStr);

        @SuppressLint("SimpleDateFormat") String timestamp = new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss").format(new Date());
        Access access = new Access(0, studentId, accessType, timestamp);
        accessDBHelper.addAccess(access);
    }

    private void loadAccessEvents() {
        Intent intent = getIntent();
        String studentIdStr = intent.getStringExtra("studentId");
        if (studentIdStr == null) {
            Log.e(TAG, "Student ID is null");
            return;
        }
        int studentId = Integer.parseInt(intent.getStringExtra("studentId"));
        List<Access> accessList = accessDBHelper.getAccessByStudentId(studentId);
        if (accessList == null) {
            Log.e(TAG, "Access list is null");
        } else {
            accessAdapter = new AccessAdapter(accessList);
            accessListView.setAdapter(accessAdapter);
        }
    }

    private void removeProfileFromMainActivity(int studentId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("studentId", studentId);
        intent.putExtra("action", "delete");
        startActivity(intent);
        finish();
    }
}