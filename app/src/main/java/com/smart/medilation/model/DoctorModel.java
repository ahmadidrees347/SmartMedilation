package com.smart.medilation.model;

import java.io.Serializable;

public class DoctorModel  implements Serializable {
    public String image;
    public String rate;
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
                       String phoneNum, String experience, String rate,
                       String specialization, String qualification,
                       boolean isApproved, boolean isRejected) {
        this.id = id;
        this.experience = experience;
        this.rate = rate;
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
