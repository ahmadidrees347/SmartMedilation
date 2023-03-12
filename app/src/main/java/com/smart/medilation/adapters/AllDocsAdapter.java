package com.smart.medilation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smart.medilation.R;
import com.smart.medilation.model.DoctorModel;

import java.util.List;

public class AllDocsAdapter extends RecyclerView.Adapter<AllDocsAdapter.CustomViewHolder> {

    private final Context context;
    private final ClickListener listener;
    private final List<DoctorModel> docList;

    public AllDocsAdapter(Context context, List<DoctorModel> docList, ClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.docList = docList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_doctor_aproval, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {
        if (docList.size() > 0) {
            holder.doctorName.setText(docList.get(position).getName());
            holder.doctorSpec.setText(docList.get(position).getSpecialization());
            holder.doctorQualification.setText(docList.get(position).getQualification());
            holder.btnApproved.setOnClickListener(v -> listener.onDocClick(docList.get(position),position, true));
            holder.btbDecline.setOnClickListener(v -> listener.onDocClick(docList.get(position),position, false));
            Glide.with(context)
                    .load(docList.get(position).getImage())
                    .centerCrop()
                    .into(holder.image);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return docList == null ? 0 : docList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView doctorName, doctorSpec, doctorQualification;
        ImageView image;

        Button btnApproved, btbDecline;

        public CustomViewHolder(View view) {
            super(view);
            doctorName = view.findViewById(R.id.doctorName);
            doctorSpec = view.findViewById(R.id.doctorSpec);
            doctorQualification = view.findViewById(R.id.doctorQualification);
            image = view.findViewById(R.id.image);
            btnApproved = view.findViewById(R.id.btnApproved);
            btbDecline = view.findViewById(R.id.btbDecline);
        }
    }

    public interface ClickListener {
        void onDocClick(DoctorModel model,int position, boolean status);
    }
}
