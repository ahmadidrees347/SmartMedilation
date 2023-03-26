package com.smart.medilation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smart.medilation.R;
import com.smart.medilation.model.CategoriesModel;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CustomViewHolder> {

    private final Context context;
    private final ClickListener listener;
    private final List<CategoriesModel> categoryList;

    public CategoriesAdapter(Context context, List<CategoriesModel> categoryList, ClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.categoryList = categoryList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {
        if (categoryList.size() > 0) {
            holder.txtName.setText(categoryList.get(position).getName());
            Glide.with(context)
                    .load(categoryList.get(position).getImg())
                    .placeholder(R.drawable.doctor_name)
                    .into(holder.image);
//            holder.image.setImageResource(categoryList.get(position).getImg());
            holder.root.setOnClickListener(v -> listener.onCategoryClick(categoryList.get(position)));
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
        return categoryList == null ? 0 : categoryList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        CardView root;
        ImageView image;

        public CustomViewHolder(View view) {
            super(view);
            root = view.findViewById(R.id.root);
            image = view.findViewById(R.id.image);
            txtName = view.findViewById(R.id.txtName);
        }
    }

    public interface ClickListener {
        void onCategoryClick(CategoriesModel model);
    }
}
