package com.abhi.caremate;

public class DoctorModel {
    private String name;
    private String department;

    public DoctorModel() {
        // Required empty constructor for Firebase
    }

    public DoctorModel(String name, String department) {
        this.name = name;
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }
}
