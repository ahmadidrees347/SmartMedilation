package com.smart.medilation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.medilation.R;
import com.smart.medilation.model.AppointmentModel;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.CustomViewHolder> {

    private final Context context;
    private final ClickListener listener;
    private final List<AppointmentModel> docList;

    public AppointmentAdapter(Context context, List<AppointmentModel> docList, ClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.docList = docList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_apointment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {
        if (docList.size() > 0) {
            holder.patientName.setText(docList.get(position).getPatientId());
            holder.patientTime.setText(docList.get(position).getTime());
            holder.patientDate.setText(docList.get(position).getDate());
            holder.btnApproved.setOnClickListener(v -> listener.onAppointmentClick(docList.get(position), true));
            holder.btbDecline.setOnClickListener(v -> listener.onAppointmentClick(docList.get(position), false));
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

        TextView patientName, patientTime, patientDate;

        Button btnApproved, btbDecline;

        public CustomViewHolder(View view) {
            super(view);
            patientName = view.findViewById(R.id.patientName);
            patientTime = view.findViewById(R.id.patientTime);
            patientDate = view.findViewById(R.id.patientDate);

            btnApproved = view.findViewById(R.id.btnApproved);
            btbDecline = view.findViewById(R.id.btbDecline);
        }
    }

    public interface ClickListener {
        void onAppointmentClick(AppointmentModel model, boolean status);
    }
}
