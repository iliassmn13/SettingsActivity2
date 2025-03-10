package com.example.sqlassignment;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DividerItemDecoration;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    protected List<Profiles> profilesList;
    protected ProfileAdapter profilesAdapter;
    protected RecyclerView profileListView;
    protected ProfileAdapter profileAdapter;
    protected FloatingActionButton insertProfileButton;
    protected TextView infoTextView;
    protected ImageButton displayButton;
    private DatabaseHelper databaseHelper;
    private boolean sortByID = true; // Flag to track the type of sorting order

    private static final String TAG = "MainActivity";

    @SuppressLint({"MissingInflatedId", "UnspecifiedRegisterReceiverFlag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.def), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();

        IntentFilter filter = new IntentFilter("com.example.sqlassignment.PROFILE_DELETED");
        registerReceiver(profileDeletedReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(profileDeletedReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null && "delete".equals(intent.getStringExtra("action"))) {
            int studentId = intent.getIntExtra("studentId", -1);
            if (studentId != -1) {
                removeProfile(studentId);
                loadProfiles();
            }
        }
    }

    private void setupUI() {
        insertProfileButton = findViewById(R.id.insertProfileButton);
        Toolbar mainToolbar = findViewById(R.id.toolbarMain);
        infoTextView = findViewById(R.id.infoText);
        displayButton = findViewById(R.id.displayButton);

        databaseHelper = new DatabaseHelper(getBaseContext());

        profileListView = findViewById(R.id.profileList);
        profileListView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        // Decorative divider line between each list element
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(profileListView.getContext(), LinearLayoutManager.VERTICAL);
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider);
        if (dividerDrawable != null) {
            dividerItemDecoration.setDrawable(dividerDrawable);
        }
        profileListView.addItemDecoration(dividerItemDecoration);

        loadProfiles();

        insertProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertDialogFragment insertDialogFragment = new InsertDialogFragment();
                insertDialogFragment.show(getSupportFragmentManager(), "InsertDialogFragment");
                loadProfiles();
            }
        });

        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    private void goToProfileActivity(Profiles profile) {
        if (profile.isDeleted()) {
            return; // Ignore deleted profiles
        }
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("name", profile.getName());
        intent.putExtra("surname", profile.getSurname());
        intent.putExtra("studentId", profile.getStudentIdStr());
        intent.putExtra("gpa", profile.getGpaStr());
        startActivity(intent);
    }

    public void loadProfiles() {
        profilesList = sortByID ? databaseHelper.getProfilesSortedByID() : databaseHelper.getProfilesSortedBySurname();
        // Filter out deleted profiles
        Iterator<Profiles> iterator = profilesList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isDeleted()) {
                iterator.remove();
            }
        }

        infoTextView.setText(profilesList.size() + " Profiles, " + (sortByID ? "by ID" : "by Surname"));

        profileAdapter = new ProfileAdapter(profilesList, this::goToProfileActivity, sortByID);
        profileListView.setAdapter(profileAdapter);

    }

    private void saveToggleData(String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("toggle_data", value);
        editor.apply();
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add("By ID");
        popupMenu.getMenu().add("By Surname");

        popupMenu.setOnMenuItemClickListener(item -> {
            String selectedOption = item.getTitle().toString();
            saveToggleData(selectedOption);
            sortByID = selectedOption.equals("By ID");
            loadProfiles();
            return true;
        });

        popupMenu.show();
    }

    public void removeProfile(int studentId) {
        Iterator<Profiles> iterator = profilesList.iterator();
        Intent intent = getIntent();
        String studentIdStr = intent.getStringExtra("studentId");
        boolean deleted = intent.getBooleanExtra("deleted",true);
        while (iterator.hasNext()) {
            Profiles profile = iterator.next();
            if (profile.getStudentId() == studentId) {
                profile.setDeleted(deleted);
                profileAdapter.notifyDataSetChanged();

                databaseHelper.updateProfileDeletedStatus(studentId, deleted);

                String timestamp = new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss").format(new Date());
                Access access = new Access(0, studentId, "Profile Deleted", timestamp);
                AccessDBHelper accessDBHelper = new AccessDBHelper(this);
                accessDBHelper.addAccess(access);

                break;
            }
        }
    }

    private final BroadcastReceiver profileDeletedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int studentId = intent.getIntExtra("studentId", -1);
            if (studentId != -1) {
                removeProfile(studentId);
            }
        }
    };
}


