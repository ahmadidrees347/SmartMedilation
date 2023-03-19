package com.smart.medilation.model;

public class AppointmentModel {
    public String id;
    public String doctorId;
    public String doctorName;
    public String patientId;
    public String patientName;
    public String time;
    public String date;
    public String status;
    String appointmentType;
    String paymentType;
    boolean paymentReceive;
    public AppointmentModel(){}
    public AppointmentModel(String id, String doctorId, String doctorName, String patientId, String patientName, String time, String date, String status, String appointmentType, String paymentType, boolean paymentReceive) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.time = time;
        this.date = date;
        this.status = status;
        this.appointmentType = appointmentType;
        this.paymentType = paymentType;
        this.paymentReceive = paymentReceive;
    }
}
