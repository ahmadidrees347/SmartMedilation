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

public class AvailableSlotAdapter extends RecyclerView.Adapter<AvailableSlotAdapter.CustomViewHolder> {

    private final Context context;
    private final List<SlotModel.TimeModel> list;
    public final OnSlotClick listener;

    public AvailableSlotAdapter(Context context, List<SlotModel.TimeModel> list, OnSlotClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_day_slot, parent, false));
    }

    public ArrayList<SlotModel.TimeModel> getSelectedItem() {
        ArrayList<SlotModel.TimeModel> selectedList = new ArrayList<>();
        for (SlotModel.TimeModel item : list) {
//            if (item.isSelected)
                selectedList.add(item);
        }
        return selectedList;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {
        if (list.size() > 0) {
            holder.txtTime.setText(list.get(position).slot);
            holder.layout.setOnClickListener(v -> {
                list.get(position).isSelected = !list.get(position).isSelected;
                listener.onSlotClick(position);
                notifyDataSetChanged();
            });
        }
        Drawable backgroundDrawable;
        int color;
        if (list.get(position).isSelected) {
            backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.round_);
            color = ContextCompat.getColor(context, R.color.white);
        } else {
            backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.round_stroke);
            color = ContextCompat.getColor(context, R.color.black);
        }
        holder.txtTime.setTextColor(color);
        holder.layout.setBackground(backgroundDrawable);
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
        return list == null ? 0 : list.size();
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

    public interface OnSlotClick {
        void onSlotClick(int position);
    }
}
