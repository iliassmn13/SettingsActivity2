package com.example.sqlassignment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InsertDialogFragment extends DialogFragment {
    protected EditText nameEditText, surnameEditText, gpaEditText, idEditText;
    protected Button saveButton, cancelButton;

    public InsertDialogFragment() {}

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_insert_dialog, container, false);

        surnameEditText = view.findViewById(R.id.editSurname);
        nameEditText = view.findViewById(R.id.editName);
        gpaEditText = view.findViewById(R.id.editGPA);
        idEditText = view.findViewById(R.id.editID);
        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        // Displays an error message while editing if the surname contains anything other than letters
        surnameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().matches("[a-zA-Z]*")) {
                    surnameEditText.setError("Only letters are allowed");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Displays an error message while editing if the name contains anything other than letters
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().matches("[a-zA-Z]*")) {
                    nameEditText.setError("Only letters are allowed");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        idEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String studentIdStr = s.toString();
                if (!studentIdStr.isEmpty()) {
                    try {
                        int studentId = Integer.parseInt(studentIdStr);
                        if (studentId < 10000000 || studentId > 99999999) {
                            idEditText.setError("The Student ID must be between 10000000 and 99999999");
                        }
                    } catch (NumberFormatException e) {
                        idEditText.setError("The Student ID must be a valid 8-digit number");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Displays an error message while editing if the GPA is out of range
        gpaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    float gpa = Float.parseFloat(s.toString());
                    if (gpa < 0 || gpa > 4.3) {
                        gpaEditText.setError("GPA must be between 0 and 4.3");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String surname = surnameEditText.getText().toString();
                String gpaStr = gpaEditText.getText().toString();
                String idStr = idEditText.getText().toString();
                double gpa;
                int studentId;

                try {
                    gpa = Float.parseFloat(gpaStr);
                    studentId = Integer.parseInt(idStr);
                } catch (NumberFormatException e) {
                    return;
                }

                if (name.isEmpty() || surname.isEmpty() || gpaStr.isEmpty() || idStr.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (gpa < 0 || gpa > 4.3) {
                        Toast.makeText(getActivity(), "GPA must be between 0 and 4.3", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (studentId < 10000000 || studentId > 99999999) {
                        idEditText.setError("The Student ID must be between 10000000 and 99999999");
                    } else if (!name.matches("[a-zA-Z]*") || !surname.matches("[a-zA-Z]*")) {
                        Toast.makeText(getActivity(), "Name and Surname must contain only letters", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity().getBaseContext());
                        AccessDBHelper accessDBHelper = new AccessDBHelper(getActivity().getBaseContext());
                        if (databaseHelper.isProfileIdExists(studentId)) {
                            Toast.makeText(getActivity(), "ID already exists", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        @SuppressLint("SimpleDateFormat") String timestamp = new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss").format(new Date());

                        Profiles profiles = new Profiles(-1, surname, name, studentId, gpa, false);
                        Access access = new Access(0, studentId, "Created", timestamp);

                        databaseHelper.addProfile(profiles);
                        accessDBHelper.addAccess(access);
                        ((MainActivity) getActivity()).loadProfiles();
                        if (getActivity() instanceof ProfileActivity) {
                            ((ProfileActivity) getActivity()).logAccessEvent("Created");
                        }
                        dismiss();
                    }
                }
            }
        });
        return view;
    }
}