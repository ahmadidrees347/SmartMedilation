package com.smart.medilation.model;

public class DoctorModel {
    public String image;
    public String experience;
    public String specialization;
    public String qualification;
    public String id;
    public String name;
    public String email;
    public String password;
    public String phoneNum;
    public boolean isApproved = false;
    public boolean isRejected = false;

    public DoctorModel(){}

    public DoctorModel(String id, String name, String email, String password,
                       String phoneNum, String experience,
                       String specialization, String qualification,
                       boolean isApproved, boolean isRejected) {
        this.id = id;
        this.experience = experience;
        this.specialization = specialization;
        this.qualification = qualification;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
        this.isApproved = isApproved;
        this.isRejected = isRejected;
    }
}
