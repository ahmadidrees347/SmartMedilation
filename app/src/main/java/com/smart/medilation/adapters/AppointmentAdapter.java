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

    public static String STATUS_Pending = "Pending";
    public static String STATUS_Complete = "Complete";
    public static String STATUS_Started = "Start";
    public static String STATUS_Cancel = "Cancel";
    public static String STATUS_Schedule = "Schedule";
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
                    listener.onAppointmentClick(docList.get(position), STATUS_Schedule, position));
            holder.btbDecline.setOnClickListener(v ->
                    listener.onAppointmentClick(docList.get(position), STATUS_Cancel, position));
            holder.btnComplete.setOnClickListener(v ->
                    listener.onAppointmentClick(docList.get(position), STATUS_Complete, position));
            holder.btnStart.setOnClickListener(v ->
                    listener.onAppointmentClick(docList.get(position), STATUS_Started, position));
            if (isFromDoctor) {
                if (docList.get(position).status.equalsIgnoreCase(STATUS_Complete) ||
                        docList.get(position).status.equalsIgnoreCase(STATUS_Cancel)) {
                    holder.layout.setVisibility(View.GONE);
                    holder.btnApproved.setVisibility(View.GONE);
                    holder.btbDecline.setVisibility(View.GONE);
                    holder.btnStart.setVisibility(View.GONE);
                    holder.btnComplete.setVisibility(View.GONE);
                } else if (docList.get(position).status.equalsIgnoreCase(STATUS_Pending)) {
                    holder.layout.setVisibility(View.VISIBLE);
                    holder.btnApproved.setVisibility(View.VISIBLE);
                    holder.btnComplete.setVisibility(View.GONE);
                    holder.btnStart.setVisibility(View.GONE);
                    holder.btbDecline.setVisibility(View.VISIBLE);
                } else if (docList.get(position).status.equalsIgnoreCase(STATUS_Schedule)) {
                    holder.layout.setVisibility(View.VISIBLE);
                    holder.btnApproved.setVisibility(View.GONE);
                    holder.btnComplete.setVisibility(View.GONE);
                    holder.btnStart.setVisibility(View.VISIBLE);
                    holder.btbDecline.setVisibility(View.VISIBLE);
                } else if (docList.get(position).status.equalsIgnoreCase(STATUS_Started)) {
                    holder.layout.setVisibility(View.VISIBLE);
                    holder.btnApproved.setVisibility(View.GONE);
                    holder.btnComplete.setVisibility(View.GONE);
                    holder.btnStart.setVisibility(View.GONE);
                    holder.btbDecline.setVisibility(View.GONE);
                }
            } else {
                if (docList.get(position).status.equalsIgnoreCase(STATUS_Complete) ||
                        docList.get(position).status.equalsIgnoreCase(STATUS_Cancel)) {
                    holder.btnApproved.setVisibility(View.GONE);
                    holder.btbDecline.setVisibility(View.GONE);
                    holder.btnStart.setVisibility(View.GONE);
                    holder.btnComplete.setVisibility(View.GONE);
                    holder.layout.setVisibility(View.GONE);
                } else if (docList.get(position).status.equalsIgnoreCase(STATUS_Schedule)) {
                    holder.layout.setVisibility(View.VISIBLE);
                    holder.btnApproved.setVisibility(View.GONE);
                    holder.btnStart.setVisibility(View.GONE);
                    holder.btnComplete.setVisibility(View.GONE);
                    holder.btbDecline.setVisibility(View.VISIBLE);
                } else if (docList.get(position).status.equalsIgnoreCase(STATUS_Pending)) {
                    holder.layout.setVisibility(View.VISIBLE);
                    holder.btnApproved.setVisibility(View.GONE);
                    holder.btnStart.setVisibility(View.GONE);
                    holder.btnComplete.setVisibility(View.GONE);
                    holder.btbDecline.setVisibility(View.VISIBLE);
                } else if (docList.get(position).status.equalsIgnoreCase(STATUS_Started)) {
                    holder.layout.setVisibility(View.VISIBLE);
                    holder.btnApproved.setVisibility(View.GONE);
                    holder.btnStart.setVisibility(View.GONE);
                    holder.btnComplete.setVisibility(View.VISIBLE);
                    holder.btbDecline.setVisibility(View.GONE);
                } else {
                    holder.layout.setVisibility(View.VISIBLE);
                    holder.btnApproved.setVisibility(View.GONE);
                    holder.btnStart.setVisibility(View.GONE);
                    holder.btnComplete.setVisibility(View.GONE);
                    holder.btbDecline.setVisibility(View.VISIBLE);
                }
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

        Button btnApproved, btbDecline, btnComplete, btnStart;
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
            btnComplete = view.findViewById(R.id.btnComplete);
            btbDecline = view.findViewById(R.id.btbDecline);
            btnStart = view.findViewById(R.id.btnStart);
        }
    }

    public interface ClickListener {
        void onAppointmentClick(AppointmentModel model, String status, int position);
    }
}
