package com.example.sqlassignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class Profiles {
    private String name, surname;
    private long id;
    private int studentId;
    private double gpa;
    private boolean deleted;

    public Profiles(long id, String surname, String name, int studentId, double gpa, boolean deleted) {
        this.id = id; // always 1,  as it is auto generated
        this.surname = surname;
        this.name = name;
        this.studentId = studentId;
        this.gpa = gpa;
        this.deleted = deleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getGpa() {
        return gpa;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public String getGpaStr() {
        return String.format("%.2f", gpa);
    }

    public String getStudentIdStr() {
        return String.valueOf(studentId);
    }
}
