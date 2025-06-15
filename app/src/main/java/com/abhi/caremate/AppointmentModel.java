package com.abhi.caremate;

public class AppointmentModel {
    private String name;
    private String date;
    private String reason;

    public AppointmentModel() {}

    public AppointmentModel(String name, String date, String reason) {
        this.name = name;
        this.date = date;
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getReason() {
        return reason;
    }
}
