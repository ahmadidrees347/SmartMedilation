package com.smart.medilation.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.smart.medilation.R;

public class AdminActivity extends AppCompatActivity {

    Button btn_category, btn_slots, btn_docs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btn_docs = findViewById(R.id.btn_docs);
        btn_category = findViewById(R.id.btn_category);
        btn_slots = findViewById(R.id.btn_slots);

        btn_docs.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AllDoctorsActivity.class);
            startActivity(intent);
        });
        btn_category.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AddCategoriesActivity.class);
            startActivity(intent);
        });
    }
}