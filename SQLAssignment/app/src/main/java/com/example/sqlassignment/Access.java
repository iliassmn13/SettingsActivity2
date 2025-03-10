package com.example.sqlassignment;

public class Access {
    private String accessType, timestamp;
    private int accessId, studentId;
    public Access(int accessId, int studentId, String accessType, String timestamp) {
        this.accessId = accessId;
        this.studentId = studentId;
        this.accessType = accessType;
        this.timestamp = timestamp;
    }

    public int getAccessId() {
        return accessId;
    }

    public void setAccessId(int accessId) {
        this.accessId = accessId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
