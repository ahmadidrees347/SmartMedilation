package com.smart.medilation.model;

public class AppointmentModel {
    public String doctorId;
    public String patientId;
    public String time;
    public String date;
    public String status;
    String appointmentType;
    String paymentType;
    boolean paymentReceive;
    public AppointmentModel(){}
    public AppointmentModel(String doctorId, String patientId, String time, String date, String status, String appointmentType, String paymentType, boolean paymentReceive) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.time = time;
        this.date = date;
        this.status = status;
        this.appointmentType = appointmentType;
        this.paymentType = paymentType;
        this.paymentReceive = paymentReceive;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
