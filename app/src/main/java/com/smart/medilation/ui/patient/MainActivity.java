package com.smart.medilation.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.medilation.R;
import com.smart.medilation.adapters.CategoriesAdapter;
import com.smart.medilation.model.CategoriesModel;
import com.smart.medilation.ui.BaseActivity;
import com.smart.medilation.ui.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements CategoriesAdapter.ClickListener {

    ImageView imageBack, imgLogout, imgProfile;
    RecyclerView recyclerCategories;
    CategoriesAdapter categoryAdapter;
    List<CategoriesModel> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageBack = findViewById(R.id.imageBack);
        imgProfile = findViewById(R.id.imgProfile);
        imageBack.setOnClickListener(v -> onBackPressed());
        imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(v -> showLogoutDialog());
        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("fromDoctor", pref.getIsDocLogin());
            startActivity(intent);
        });

        recyclerCategories = findViewById(R.id.recyclerCategories);
        recyclerCategories.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        categoryAdapter = new CategoriesAdapter(getApplicationContext(), categoryList, this);
        recyclerCategories.setAdapter(categoryAdapter);

        final String[] values = getResources().getStringArray(R.array.allCats);
        for (String child : values) {
            CategoriesModel user = new CategoriesModel(child);
            categoryList.add(user);
            Log.e("d", "" + user.getName());
        }
        categoryAdapter.notifyDataSetChanged();

        /* showLDialog();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mDatabase.getReference("Categories");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    CategoriesModel user = child.getValue(CategoriesModel.class);
                    Log.e("d", ""+user.getName());
                    categoryList.add(user);
                }
                categoryAdapter.notifyDataSetChanged();
                dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                dismissDialog();
            }
        });*/
    }

    @Override
    public void onCategoryClick(CategoriesModel model) {
        Intent intent = new Intent(MainActivity.this, SelectDoctorActivity.class);
        intent.putExtra("category", model.getName());
        startActivity(intent);
    }
}