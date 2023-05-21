package com.smart.medilation.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.medilation.R;
import com.smart.medilation.model.SlotModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.CustomViewHolder> {

    private final Context context;
    private final List<SlotModel> dateList;

    public String timeSlot = "";
    public final OnDayClick listener;

    public SlotAdapter(Context context, List<SlotModel> dateList, OnDayClick listener) {
        this.context = context;
        this.dateList = dateList;
        this.listener = listener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_timeslot, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {
        if (dateList.size() > 0) {
            holder.txtTime.setText(dateList.get(position).getDay());
            holder.layout.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDayClick(dateList.get(position), position);
                }
            });
        }
        Drawable backgroundDrawable;
        int color;
        if (Objects.equals(dateList.get(position).getDay(), timeSlot)) {
            backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.round_);
            color = ContextCompat.getColor(context, R.color.white);
        } else {
            backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.round_stroke);
            color = ContextCompat.getColor(context, R.color.black);
        }
        holder.txtTime.setTextColor(color);
        holder.layout.setBackground(backgroundDrawable);
    }

    public ArrayList<SlotModel> getSelectedSlots() {
        return new ArrayList<>(dateList);
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
        return dateList == null ? 0 : dateList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView txtTime;
        LinearLayout layout;

        public CustomViewHolder(View view) {
            super(view);
            txtTime = view.findViewById(R.id.txtTime);
            layout = view.findViewById(R.id.layout);

        }
    }

    public interface OnDayClick {
        void onDayClick(SlotModel slotModel, int position);
    }
}
