package com.smart.medilation.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SlotModel {
    private String day;
    private String slots;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<TimeModel> getSlots() {
        return jsonToArrayList(slots);
    }

    public void setSlots(String slots) {
        this.slots = slots;
    }

    public SlotModel(String day, ArrayList<TimeModel> slots) {
        this.day = day;
        this.slots = arrayListToJson(slots);
    }

    public static class TimeModel {
        public String slot;
        public boolean isSelected;

        public TimeModel(String slot, boolean isSelected) {
            this.slot = slot;
            this.isSelected = isSelected;
        }
    }

    public ArrayList<TimeModel> jsonToArrayList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<TimeModel>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public String arrayListToJson(ArrayList<TimeModel> arrayList) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<TimeModel>>() {
        }.getType();
        return gson.toJson(arrayList, type);
    }
}

