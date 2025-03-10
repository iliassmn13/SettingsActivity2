package com.example.assignment1;

import android.os.Bundle;
import android.content.Intent;
import android.text.InputFilter;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferenceManager sharedPrefsManager;
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
        navigation();
    }

    private void setupUI(){
        sharedPrefsManager = new SharedPreferenceManager(this);
        Button saveButton = findViewById(R.id.saveButton);
        // TODO: Save the data entered to sharedpreferences from clicking save
        TextView maxCountText = findViewById(R.id.maxCounterText);
        TextView counter1Text = findViewById(R.id.counter1NameText);
        TextView counter2Text = findViewById(R.id.counter2NameText);
        TextView counter3Text = findViewById(R.id.counter3NameText);
        EditText counter1Edit = findViewById(R.id.counter1NameEdit);
        EditText counter2Edit = findViewById(R.id.counter2NameEdit);
        EditText counter3Edit = findViewById(R.id.counter3NameEdit);
        EditText maxCountEdit = findViewById(R.id.maxCountEdit);

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
                String maxCount = maxCountEdit.getText().toString();

                sharedPrefsManager.putCount1Name(counter1Name);
                sharedPrefsManager.putCount2Name(counter2Name);
                sharedPrefsManager.putCount3Name(counter3Name);
                sharedPrefsManager.putMaxCount(maxCount);

                /*SharedPreferences.Editor editor = sharedPrefs.edit();

                editor.putString("counter1Name", counter1Name);
                editor.apply();
                editor.putString("counter2Name", counter2Name);
                editor.apply();
                editor.putString("counter3Name", counter3Name);
                editor.apply();
                editor.putString("maxCount", maxCount);
                editor.apply();

                Toast.makeText(getApplicationContext(), "Saved name: ", Toast.LENGTH_LONG).show();*/
            }
            // TODO: If value entered is WRONG (ie the number for max count is not between 5 and 200 and not a number + 20 CHAR MAX LENGTH (MEASURE SIZE OF TEXT), then show a toast message that says "Invalid input" AND STAY IN EDIT MODE
            // TODO: Have button on navigation that changes page from display mode to edit mode. Make save button disappear after clicking, and switch to DISPLAY MODE
        });
    }

    private void navigation(){

    }

    private void goToDataActivity(){
        Intent intent = new Intent(this, DataActivity.class);
        startActivity(intent);
    }

    private void textEditChanger(){
        //if (saveButton.setOnClickListener==true){
            // Change to display mode
        //}
        //else{
            // Change to edit mode
        //}
    }
}