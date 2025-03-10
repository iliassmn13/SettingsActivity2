package com.example.settingsactivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    protected AutoCompleteTextView brandSelect;
    protected AutoCompleteTextView modelSelect;
    protected AutoCompleteTextView lensSelect;
    private GenerativeAI generativeAI;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);

        generativeAI = new GenerativeAI(this);
        brandSelect = findViewById(R.id.brandSelect);
        modelSelect = findViewById(R.id.modelSelect);
        lensSelect = findViewById(R.id.lensSelect);

        brandSelect.setThreshold(0);
        modelSelect.setThreshold(0);
        lensSelect.setThreshold(0);

        modelSelect.setEnabled(false);
        modelSelect.setHint("Please select a brand first");

        brandSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!selection.equals("Invalid input")) {
                    brandSelect.setText(selection);
                    brandSelect.dismissDropDown();
                    modelSelect.setText("");
                    lensSelect.setText("");

                    modelSelect.setEnabled(true);
                    modelSelect.setHint("Select model");
                    modelSelect.requestFocus();
                }
            }
        });

        modelSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!selection.equals("Invalid input")) {
                    modelSelect.setText(selection);
                    modelSelect.dismissDropDown();
                    lensSelect.setText("");
                    lensSelect.requestFocus();
                }
            }
        });

        lensSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!selection.equals("Invalid input")) {
                    lensSelect.setText(selection);
                    lensSelect.dismissDropDown();
                    lensSelect.clearFocus();
                }
            }
        });

        brandSelect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentText = s.toString();
                getBrandSuggestions(currentText);

                if (currentText.length() == 0) {
                    modelSelect.setEnabled(false);
                    modelSelect.setText("");
                    modelSelect.setHint("Please select a brand first");
                } else {
                    modelSelect.setEnabled(true);
                    modelSelect.setHint("Select model");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        modelSelect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentText = s.toString();
                getModelSuggestions(currentText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        lensSelect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentText = s.toString();
                getLensSuggestions(currentText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        brandSelect.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String currentText = brandSelect.getText().toString();
                    getBrandSuggestions(currentText);
                }
            }
        });

        modelSelect.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (brandSelect.getText().toString().length() == 0) {
                        Toast.makeText(SettingsActivity.this, "Please select a brand first", Toast.LENGTH_SHORT).show();
                        brandSelect.requestFocus();
                    } else {
                        String currentText = modelSelect.getText().toString();
                        getModelSuggestions(currentText);
                    }
                }
            }
        });

        lensSelect.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String currentText = lensSelect.getText().toString();
                    getLensSuggestions(currentText);
                }
            }
        });

        brandSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = brandSelect.getText().toString();
                getBrandSuggestions(currentText);
            }
        });

        modelSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (brandSelect.getText().toString().length() == 0) {
                    Toast.makeText(SettingsActivity.this, "Please select a brand first", Toast.LENGTH_SHORT).show();
                    brandSelect.requestFocus();
                } else {
                    String currentText = modelSelect.getText().toString();
                    getModelSuggestions(currentText);
                }
            }
        });

        lensSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = lensSelect.getText().toString();
                getLensSuggestions(currentText);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getBrandSuggestions(final String query) { //Function to get brand suggestions.
        Thread thread = new Thread() {
            public void run() {
                try {
                    final List<String> suggestions = generativeAI.getSuggestions(query.toLowerCase(), "brand", "");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (suggestions.size() > 0) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                        SettingsActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        suggestions
                                );
                                brandSelect.setAdapter(adapter);
                                if (brandSelect.hasFocus()) {
                                    brandSelect.showDropDown();
                                }
                            } else {
                                ArrayList<String> invalidList = new ArrayList<>();
                                invalidList.add("Invalid input");
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                        SettingsActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        invalidList
                                );
                                brandSelect.setAdapter(adapter);
                                if (brandSelect.hasFocus()) {
                                    brandSelect.showDropDown();
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void getModelSuggestions(final String query) { //Function to get model suggestions.
        final String brand = brandSelect.getText().toString();
        if (brand.length() == 0) {
            return;
        }

        Thread thread = new Thread() {
            public void run() {
                try {
                    final List<String> suggestions = generativeAI.getSuggestions(query.toLowerCase(), "model", brand);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (suggestions.size() > 0) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                        SettingsActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        suggestions
                                );
                                modelSelect.setAdapter(adapter);
                                if (modelSelect.hasFocus()) {
                                    modelSelect.showDropDown();
                                }
                            } else {
                                ArrayList<String> invalidList = new ArrayList<>();
                                invalidList.add("Invalid input");
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                        SettingsActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        invalidList
                                );
                                modelSelect.setAdapter(adapter);
                                if (modelSelect.hasFocus()) {
                                    modelSelect.showDropDown();
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void getLensSuggestions(final String query) { //Function to get lens suggestions.
        Thread thread = new Thread() {
            public void run() {
                try {
                    final List<String> suggestions = generativeAI.getSuggestions(query.toLowerCase(), "lens", "");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (suggestions.size() > 0) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                        SettingsActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        suggestions
                                );
                                lensSelect.setAdapter(adapter);
                                if (lensSelect.hasFocus()) {
                                    lensSelect.showDropDown();
                                }
                            } else {
                                ArrayList<String> invalidList = new ArrayList<>();
                                invalidList.add("Invalid input");
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                        SettingsActivity.this,
                                        android.R.layout.simple_dropdown_item_1line,
                                        invalidList
                                );
                                lensSelect.setAdapter(adapter);
                                if (lensSelect.hasFocus()) {
                                    lensSelect.showDropDown();
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}