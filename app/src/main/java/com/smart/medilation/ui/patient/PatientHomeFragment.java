package com.smart.medilation.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.smart.medilation.R;
import com.smart.medilation.adapters.CategoriesAdapter;
import com.smart.medilation.adapters.DocAdapter;
import com.smart.medilation.model.CategoriesModel;
import com.smart.medilation.model.DoctorModel;
import com.smart.medilation.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class PatientHomeFragment extends BaseFragment implements
        CategoriesAdapter.ClickListener, DocAdapter.ClickListener {

    TextView seeAll, seeAllDocs;
    CardView cardSearch;
    CircularImageView image;
    TextView txtName;
    RecyclerView recyclerCategories;
    CategoriesAdapter categoryAdapter;
    List<CategoriesModel> categoryList = new ArrayList<>();

    //Doctors
    RecyclerView recyclerDocs;
    DocAdapter docAdapter;
    List<DoctorModel> docList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        image = view.findViewById(R.id.image);
        txtName = view.findViewById(R.id.txtName);
        Glide.with(getContext())
                .load(pref.getUserImage())
                .placeholder(R.drawable.ic_user)
                .into(image);
        String txt = "Hi " + pref.getUserName() + "!";
        txtName.setText(txt);
        seeAll = view.findViewById(R.id.seeAll);
        seeAllDocs = view.findViewById(R.id.seeAllDocs);
        cardSearch = view.findViewById(R.id.cardSearch);
        cardSearch.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SelectDoctorActivity.class);
            intent.putExtra("category", "");
            startActivity(intent);
        });
        seeAllDocs.setOnClickListener(v -> {
            cardSearch.performClick();
        });
        seeAll.setOnClickListener(v -> {
            if (bottomMenuInterface != null)
                bottomMenuInterface.onNavChange(1);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.navDomain);
        });
        image.setOnClickListener(v -> {
            if (bottomMenuInterface != null)
                bottomMenuInterface.onNavChange(3);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.navProfile);
        });
        recyclerCategories = view.findViewById(R.id.recyclerCategories);
        recyclerCategories.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        categoryAdapter = new CategoriesAdapter(requireContext(), categoryList, this, true);
        recyclerCategories.setAdapter(categoryAdapter);

        recyclerDocs = view.findViewById(R.id.recyclerDocs);
        setGridManager(recyclerDocs);
        docAdapter = new DocAdapter(requireContext(), docList, this);
        docAdapter.isFromDashboard = true;
        recyclerDocs.setAdapter(docAdapter);


        getTopCategories();
        getTopDoctors();
    }

    private void getTopCategories() {
        categoryList.clear();
        categoryList.add(new CategoriesModel("Cardiologists", R.drawable.cardiologist));
        categoryList.add(new CategoriesModel("Child Specialist", R.drawable.child_specialist));
        categoryList.add(new CategoriesModel("Dentist", R.drawable.dentist));
        categoryList.add(new CategoriesModel("Eye Specialist", R.drawable.eye_specialist));
        categoryList.add(new CategoriesModel("Family Physicians", R.drawable.family_physicians));
        categoryList.add(new CategoriesModel("General Surgeon", R.drawable.general_surgeon));
        categoryList.add(new CategoriesModel("Psychiatrist", R.drawable.psychiatrist));
        categoryAdapter.notifyDataSetChanged();
    }

    private void getTopDoctors() {
        showLDialog();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mDatabase.getReference("Doctor");
        mRef.keepSynced(false);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                docList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    DoctorModel doc = child.getValue(DoctorModel.class);
                    if (doc != null && docList.size() < 5)
                        docList.add(doc);
                    else break;
                }
                docAdapter.notifyDataSetChanged();
                dismissDialog();
                Log.e("data*", "onDataChange dismissDialog");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("data*", "onCancelled dismissDialog");
                dismissDialog();
            }
        });
    }

    @Override
    public void onCategoryClick(CategoriesModel model) {
        Intent intent = new Intent(requireContext(), SelectDoctorActivity.class);
        intent.putExtra("category", model.getName());
        startActivity(intent);
    }

    @Override
    public void onDocClick(DoctorModel model) {
        Intent intent = new Intent(requireContext(), DoctorProfileActivity.class);
        intent.putExtra("doctorId", model.id);
        intent.putExtra("name", model.name);
        intent.putExtra("email", model.email);
        intent.putExtra("phone", model.phoneNum);
        intent.putExtra("image", model.image);
        intent.putExtra("exp", model.experience);
        intent.putExtra("qualification", model.qualification);
        intent.putExtra("specialization", model.specialization);
        startActivity(intent);
    }
}