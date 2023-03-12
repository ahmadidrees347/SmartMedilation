package com.smart.medilation.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.medilation.R;
import com.smart.medilation.adapters.AllDocsAdapter;
import com.smart.medilation.model.DoctorModel;
import com.smart.medilation.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class AllDoctorsActivity extends BaseActivity implements AllDocsAdapter.ClickListener {


    ImageView imageBack;
    TextView txtNoDoc;
    RecyclerView recyclerDocs;
    AllDocsAdapter docAdapter;
    List<DoctorModel> docList = new ArrayList<>();

    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_doctors);


        imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());
        txtNoDoc = findViewById(R.id.txtNoDoc);

        recyclerDocs = findViewById(R.id.recyclerDocs);
        recyclerDocs.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        docAdapter = new AllDocsAdapter(getApplicationContext(), docList, this);
        recyclerDocs.setAdapter(docAdapter);

        showLDialog();

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Doctor");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                docList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    DoctorModel doc = child.getValue(DoctorModel.class);
                    if (doc != null && !doc.isApproved && !doc.isRejected)
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
    public void onDocClick(DoctorModel model, int position, boolean status) {
        showLDialog();
        if (status)
            mRef.child(model.id)
                    .child("isApproved")
                    .setValue(true)
                    .addOnCompleteListener(task -> {
                        dismissDialog();
                        docList.remove(position);
                        docAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(task -> dismissDialog());
        else
            mRef.child(model.id)
                    .child("isRejected")
                    .setValue(true)
                    .addOnCompleteListener(task -> {
                        dismissDialog();
                        docList.remove(position);
                        docAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(task -> dismissDialog());
        Toast.makeText(AllDoctorsActivity.this, "Successfully updated user", Toast.LENGTH_SHORT).show();
    }
}