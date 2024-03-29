package com.smart.medilation.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import java.io.Serializable;
public class DoctorModel implements Serializable {

    public String image;
    public String experience;
    public String specialization;
    public String qualification;
    public String about;
    public String id;
    public String name;
    public String email;
    public String password;
    public String phoneNum;
    public boolean isApproved = false;
    public boolean isRejected = false;

    public String rate;
    public String ratePhysical;
    public String rating;
    public String timeSlots;

    public static class RatingModel {
        public RatingModel(float rating, String review) {
            this.rating = rating;
            this.review = review;
        }

        public float rating;
        public String review;

        public RatingModel() {
        }
    }

    public ArrayList<RatingModel> jsonToArrayList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<RatingModel>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public String arrayListToJson(ArrayList<RatingModel> arrayList) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<RatingModel>>() {
        }.getType();
        return gson.toJson(arrayList, type);
    }


    public DoctorModel() {
    }

    public DoctorModel(String id, String name, String email, String password,
                       String phoneNum, String experience,String rate,String ratePhysical,
                       String specialization, String qualification,String about,
                       boolean isApproved, boolean isRejected,String rating, String timeSlots) {
        this.id = id;
        this.experience = experience;
        this.specialization = specialization;
        this.qualification = qualification;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
        this.about = about;
        this.isApproved = isApproved;
        this.isRejected = isRejected;
        this.rating = rating;
        this.rate = rate;
        this.ratePhysical = ratePhysical;
        this.timeSlots = timeSlots;
    }
}
