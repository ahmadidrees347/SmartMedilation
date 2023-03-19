package com.smart.medilation.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.medilation.R;
import com.smart.medilation.adapters.DocAdapter;
import com.smart.medilation.model.DoctorModel;
import com.smart.medilation.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class SelectDoctorActivity extends BaseActivity implements DocAdapter.ClickListener {

    ImageView imageBack;
    TextView txtTitle, txtNoDoc;
    String category = "";

    RecyclerView recyclerDocs;
    DocAdapter docAdapter;
    List<DoctorModel> docList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_doctor);

        category = getIntent().getStringExtra("category");

        showLDialog();
        imageBack = findViewById(R.id.imageBack);
        txtTitle = findViewById(R.id.txtTitle);
        txtNoDoc = findViewById(R.id.txtNoDoc);
        txtTitle.setText(category);

        imageBack.setOnClickListener(v->onBackPressed());


        recyclerDocs = findViewById(R.id.recyclerDocs);
        recyclerDocs.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        docAdapter = new DocAdapter(getApplicationContext(), docList, this);
        recyclerDocs.setAdapter(docAdapter);


        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mDatabase.getReference("Doctor");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                docList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    DoctorModel doc = child.getValue(DoctorModel.class);
                    if (doc != null && category.equalsIgnoreCase(doc.specialization))
                        docList.add(doc);
                }
                docAdapter.notifyDataSetChanged();
                dismissDialog();
                if (docList.isEmpty()) {
                    txtNoDoc.setVisibility(View.VISIBLE);
                } else {
                    txtNoDoc.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dismissDialog();
            }
        });
    }

    @Override
    public void onDocClick(DoctorModel model) {
        Intent intent = new Intent(SelectDoctorActivity.this, DoctorProfileActivity.class);
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