package com.example.assignment1;

import android.os.Handler;
import android.os.Bundle;
import android.content.Intent;
import android.text.InputFilter;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assignment1.SharedPreferenceManager;
import com.example.assignment1.DataActivity;
import com.example.assignment1.MainActivity;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferenceManager sharedPrefsManager;
    private ImageView editSettingsButton;
    private Button saveButton;
    private EditText counter1Edit, counter2Edit, counter3Edit, maxCountEdit;
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

        setupUI();
        popUpMenu();
        switchToViewMode(); // start in view Mode
    }

    private void setupUI(){
        Toolbar settingsToolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        settingsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        sharedPrefsManager = new SharedPreferenceManager(this);
        saveButton = findViewById(R.id.saveButton);

        counter1Edit = findViewById(R.id.counter1NameEdit);
        counter2Edit = findViewById(R.id.counter2NameEdit);
        counter3Edit = findViewById(R.id.counter3NameEdit);
        maxCountEdit = findViewById(R.id.maxCountEdit);

        // Setting the length of the edit text to a max of 20 characters
        int maxLength = 20;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        counter1Edit.setFilters(FilterArray);
        counter2Edit.setFilters(FilterArray);
        counter3Edit.setFilters(FilterArray);

        // Setting the length of the edit text to a max of 3 digit number
        int maxLength1 = 3;
        InputFilter[] FilterArray1 = new InputFilter[1];
        FilterArray1[0] = new InputFilter.LengthFilter(maxLength1);
        maxCountEdit.setFilters(FilterArray1);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String counter1Name = counter1Edit.getText().toString();
                String counter2Name = counter2Edit.getText().toString();
                String counter3Name = counter3Edit.getText().toString();
                String maxCountStr = maxCountEdit.getText().toString();

                // Validate max count
                int maxCount;
                try {
                    maxCount = Integer.parseInt(maxCountStr);
                    if (maxCount < 5 || maxCount > 200) {
                        Toast.makeText(SettingsActivity.this, "Invalid input: Max count must be between 5 and 200", Toast.LENGTH_SHORT).show();
                        return; // Stay in edit mode
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(SettingsActivity.this, "Invalid input: Max count must be a number", Toast.LENGTH_SHORT).show();
                    return; // Stay in edit mode
                }

                // Validate name lengths and characters (alphabetical characters and spaces only)
                String namePattern = "^[a-zA-Z\\s]+$";
                if (counter1Name.length() > 20 || !counter1Name.matches(namePattern) ||
                        counter2Name.length() > 20 || !counter2Name.matches(namePattern) ||
                        counter3Name.length() > 20 || !counter3Name.matches(namePattern)) {
                    Toast.makeText(SettingsActivity.this, "Invalid input: Names must be 20 characters or less and contain only alphabetical characters and spaces", Toast.LENGTH_SHORT).show();
                    return; // Stay in edit mode
                }

                // Save valid inputs
                sharedPrefsManager.putCount1Name(counter1Name);
                sharedPrefsManager.putCount2Name(counter2Name);
                sharedPrefsManager.putCount3Name(counter3Name);
                sharedPrefsManager.putMaxCount(maxCountStr);

                switchToViewMode(); // switch to view mode after saving
            }
        });
    }

    private void popUpMenu(){
        editSettingsButton = findViewById(R.id.editSettingsButton);
        editSettingsButton.setOnClickListener(view -> {

            PopupMenu popup = new PopupMenu(this, view);
            popup.getMenuInflater().inflate(R.menu.menu_settings, popup.getMenu());
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.editSettings) {
                    sharedPrefsManager.clearSharedPreferences();
                    switchToEditMode(); // switch to edit mode when the edit button is clicked
                }
                return true;
            });
            popup.show();
        });
    }

    private void goToDataActivity(){
        Intent intent = new Intent(this, DataActivity.class);
        startActivity(intent);
    }

    private void switchToViewMode(){
        counter1Edit.setEnabled(false);
        counter2Edit.setEnabled(false);
        counter3Edit.setEnabled(false);
        maxCountEdit.setEnabled(false);
        saveButton.setVisibility(View.GONE);
    }

    private void switchToEditMode() {
        counter1Edit.setEnabled(true);
        counter2Edit.setEnabled(true);
        counter3Edit.setEnabled(true);
        maxCountEdit.setEnabled(true);
        saveButton.setVisibility(View.VISIBLE);
    }
}