package com.smart.medilation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.medilation.R;
import com.smart.medilation.model.DoctorModel;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.CustomViewHolder> {

    private final Context context;
    private final List<DoctorModel.RatingModel> reviewList;

    public ReviewsAdapter(Context context, List<DoctorModel.RatingModel> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_review, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {
        if (reviewList.size() > 0) {
            holder.txtReview.setText(reviewList.get(position).review);
            String rating = "(" + reviewList.get(position).rating + ")";
            holder.txtRating.setText(rating);
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
        return reviewList == null ? 0 : reviewList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView txtReview, txtRating;

        public CustomViewHolder(View view) {
            super(view);
            txtReview = view.findViewById(R.id.txtReview);
            txtRating = view.findViewById(R.id.txtRating);

        }
    }
}
