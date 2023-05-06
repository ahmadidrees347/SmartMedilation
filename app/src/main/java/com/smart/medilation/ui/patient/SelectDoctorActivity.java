package com.smart.medilation.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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
import com.smart.medilation.adapters.GridSpacingItemDecoration;
import com.smart.medilation.model.DoctorModel;
import com.smart.medilation.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class SelectDoctorActivity extends BaseActivity implements DocAdapter.ClickListener {

    ImageView imageBack, imgLogout;
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

        imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(v -> showLogoutDialog());
        showLDialog();
        imageBack = findViewById(R.id.imageBack);
        txtTitle = findViewById(R.id.txtTitle);
        txtNoDoc = findViewById(R.id.txtNoDoc);
        if (!category.isEmpty())
            txtTitle.setText(category);

        imageBack.setOnClickListener(v -> onBackPressed());


        recyclerDocs = findViewById(R.id.recyclerDocs);
        setGridManager(recyclerDocs);
        docAdapter = new DocAdapter(getApplicationContext(), docList, this);
        recyclerDocs.setAdapter(docAdapter);



        EditText searchField = findViewById(R.id.search_field);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                docAdapter.getFilter().filter(editable.toString());
            }
        });

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mDatabase.getReference("Doctor");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                docList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    DoctorModel doc = child.getValue(DoctorModel.class);
                    if (doc != null && category.isEmpty())
                        docList.add(doc);
                    if (doc != null && category.equalsIgnoreCase(doc.specialization))
                        docList.add(doc);
                }
                docAdapter = new DocAdapter(getApplicationContext(), docList, SelectDoctorActivity.this);
                recyclerDocs.setAdapter(docAdapter);
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
        intent.putExtra("myModel", model);
        startActivity(intent);
    }
}