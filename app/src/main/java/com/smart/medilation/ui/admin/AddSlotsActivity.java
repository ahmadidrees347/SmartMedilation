package com.smart.medilation.ui.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smart.medilation.R;
import com.smart.medilation.model.CategoriesModel;
import com.smart.medilation.ui.BaseActivity;

public class AddSlotsActivity extends BaseActivity {

    EditText edt_slot;
    Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_slots);

        edt_slot = findViewById(R.id.edt_slot);
        btn_add = findViewById(R.id.btn_add);

        btn_add.setOnClickListener(v -> {
            String str = edt_slot.getText().toString();
            if (!str.isEmpty()) {
                showLDialog();
                CategoriesModel model = new CategoriesModel(str);
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                DatabaseReference mRef = mDatabase.getReference("Slots");

                mRef.push().setValue(model).addOnCompleteListener(task -> {
                    dismissDialog();
                    if (task.isSuccessful()) {
                        edt_slot.setText("");
                        Toast.makeText(AddSlotsActivity.this, "Add Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddSlotsActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(task -> dismissDialog());
            }
        });
    }
}