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
import com.smart.medilation.model.DateModel;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.CustomViewHolder> {

    private final Context context;
    private final List<DateModel> dateList;

    private int lastPosition = -1;

    public DateAdapter(Context context, List<DateModel> dateList) {
        this.context = context;
        this.dateList = dateList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_date, parent, false));
    }

    public String getSelectedItem() {
        String time = "";
        for (int i = 0; i < dateList.size(); i++) {
            if (dateList.get(i).isSelected) {
                time = dateList.get(i).getDay() + " " + dateList.get(i).getDate();
                break;
            }
        }
        return time;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        if (dateList.size() > 0) {
            holder.txtDate.setText(dateList.get(position).getDate());
            holder.txtDay.setText(dateList.get(position).getDay());
            holder.layout.setOnClickListener(v -> {
                if (lastPosition == position) {
                    return;
                }
                if (lastPosition != -1) {
                    dateList.get(lastPosition).isSelected = false;
                    notifyItemChanged(lastPosition);
                }
                dateList.get(position).isSelected = true;
                lastPosition = position;

                notifyDataSetChanged();
            });
        }

        Drawable backgroundDrawable;
        int color;
        if (dateList.get(position).isSelected) {
            backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.round_);
            color = ContextCompat.getColor(context, R.color.white);
        } else {
            backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.round_stroke);
            color = ContextCompat.getColor(context, R.color.black);
        }
        holder.txtDate.setTextColor(color);
        holder.txtDay.setTextColor(color);
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
        return dateList == null ? 0 : dateList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView txtDay, txtDate;
        LinearLayout layout;

        public CustomViewHolder(View view) {
            super(view);
            txtDay = view.findViewById(R.id.txtDay);
            txtDate = view.findViewById(R.id.txtDate);
            layout = view.findViewById(R.id.layout);
        }
    }
}
