package com.smart.medilation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
    public boolean isFromDoctor = true;
    public boolean isFromHistory = false;

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
            holder.doctorName.setText(docList.get(position).doctorName);
            holder.patientName.setText(docList.get(position).patientName);
            holder.patientTime.setText(docList.get(position).time);
            holder.patientDate.setText(docList.get(position).date);
            holder.status.setText(docList.get(position).status);
            holder.btnApproved.setOnClickListener(v ->
                    listener.onAppointmentClick(docList.get(position), true, position));
            holder.btbDecline.setOnClickListener(v ->
                    listener.onAppointmentClick(docList.get(position), false, position));
            if (isFromDoctor) {
                if (docList.get(position).status.equalsIgnoreCase("Pending"))
                    holder.layout.setVisibility(View.VISIBLE);
                else
                    holder.layout.setVisibility(View.GONE);
            } else {
                holder.layout.setVisibility(View.GONE);
            }
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

        TextView patientName, doctorName, patientTime, patientDate, status;

        Button btnApproved, btbDecline;
        LinearLayout layout;

        public CustomViewHolder(View view) {
            super(view);
            doctorName = view.findViewById(R.id.doctorName);
            patientName = view.findViewById(R.id.patientName);
            patientTime = view.findViewById(R.id.patientTime);
            patientDate = view.findViewById(R.id.patientDate);
            status = view.findViewById(R.id.status);

            layout = view.findViewById(R.id.layout);

            btnApproved = view.findViewById(R.id.btnApproved);
            btbDecline = view.findViewById(R.id.btbDecline);
        }
    }

    public interface ClickListener {
        void onAppointmentClick(AppointmentModel model, boolean status, int position);
    }
}
