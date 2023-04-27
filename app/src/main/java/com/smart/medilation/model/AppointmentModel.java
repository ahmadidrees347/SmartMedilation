package com.smart.medilation.model;

import java.io.Serializable;

public class AppointmentModel implements Serializable {
    public String id;
    public String doctorId;
    public String doctorName;
    public String patientId;
    public String patientName;
    public String time;
    public String date;
    public String status;
    public String appointmentType;
    public String paymentType;
    public String payment;
    public boolean paymentReceive;

    public AppointmentModel() {
    }

    public AppointmentModel(String id, String doctorId, String doctorName, String patientId, String patientName, String time,
                            String date, String status, String appointmentType, String payment, String paymentType, boolean paymentReceive) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.time = time;
        this.date = date;
        this.status = status;
        this.appointmentType = appointmentType;
        this.payment = payment;
        this.paymentType = paymentType;
        this.paymentReceive = paymentReceive;
    }
}
