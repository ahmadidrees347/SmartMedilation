package com.smart.medilation.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.medilation.R;
import com.smart.medilation.adapters.CategoriesAdapter;
import com.smart.medilation.model.CategoriesModel;
import com.smart.medilation.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class DomainFragment extends BaseFragment implements CategoriesAdapter.ClickListener {

    RecyclerView recyclerCategories;
    CategoriesAdapter categoryAdapter;
    List<CategoriesModel> categoryList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_domain, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addAllList();

        recyclerCategories = view.findViewById(R.id.recyclerCategories);
        recyclerCategories.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        categoryAdapter = new CategoriesAdapter(requireContext(), categoryList, this, false);
        recyclerCategories.setAdapter(categoryAdapter);

        EditText searchField = view.findViewById(R.id.search_field);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                categoryAdapter.getFilter().filter(editable.toString());
            }
        });


    }

    private void addAllList() {
        categoryList.clear();
        categoryList.add(new CategoriesModel("Anesthesiologists", R.drawable.anesthesiologists));
        categoryList.add(new CategoriesModel("Cardiologists", R.drawable.cardiologist));
        categoryList.add(new CategoriesModel("Child Specialist", R.drawable.child_specialist));
        categoryList.add(new CategoriesModel("Dentist", R.drawable.dentist));
        categoryList.add(new CategoriesModel("Dermatologists", R.drawable.dermatologists));
        categoryList.add(new CategoriesModel("Endocrinologists", R.drawable.endocrinologist));
        categoryList.add(new CategoriesModel("Eye Specialist", R.drawable.eye_specialist));
        categoryList.add(new CategoriesModel("Family Physicians", R.drawable.family_physicians));
        categoryList.add(new CategoriesModel("Gastroenterologists", R.drawable.gastroenterologist));
        categoryList.add(new CategoriesModel("General Surgeon", R.drawable.general_surgeon));
        categoryList.add(new CategoriesModel("Gynecologists", R.drawable.gynecologist));
        categoryList.add(new CategoriesModel("Hematologists", R.drawable.hematologists));
        categoryList.add(new CategoriesModel("Immunologists", R.drawable.immunologist));
        categoryList.add(new CategoriesModel("Internists", R.drawable.internists));
        categoryList.add(new CategoriesModel("Medical Geneticists", R.drawable.medical_geneticists));
        categoryList.add(new CategoriesModel("Nephrologists", R.drawable.nephrologist));
        categoryList.add(new CategoriesModel("Neurologists", R.drawable.neurologist));
        categoryList.add(new CategoriesModel("Ophthalmologists", R.drawable.ophthalmologists));
        categoryList.add(new CategoriesModel("Orthopedics", R.drawable.orthopedics));
        categoryList.add(new CategoriesModel("Psychiatrist", R.drawable.psychiatrist));
        categoryList.add(new CategoriesModel("Pulmonologist", R.drawable.pulmonologist));
        categoryList.add(new CategoriesModel("Podiatrists", R.drawable.pulmonologist));
        categoryList.add(new CategoriesModel("Skin Specialist", R.drawable.skin_specialist));
        categoryList.add(new CategoriesModel("Urologist", R.drawable.urologist));
    }

    @Override
    public void onCategoryClick(CategoriesModel model) {
        Intent intent = new Intent(requireContext(), SelectDoctorActivity.class);
        intent.putExtra("category", model.getName());
        startActivity(intent);
    }
}