package com.smart.medilation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smart.medilation.R;
import com.smart.medilation.model.DoctorModel;

import java.util.List;

public class DocAdapter extends RecyclerView.Adapter<DocAdapter.CustomViewHolder> {

    private final Context context;
    private final ClickListener listener;
    private final List<DoctorModel> docList;

    public DocAdapter(Context context, List<DoctorModel> docList, ClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.docList = docList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_doc, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {
        if (docList.size() > 0) {
            holder.txtName.setText(docList.get(position).getName());
            holder.image.setOnClickListener(v -> listener.onDocClick(docList.get(position)));
            Glide.with(context)
                    .load(docList.get(position).getImage())
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

        TextView txtName;
        ImageView image;

        public CustomViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtName);
            image = view.findViewById(R.id.image);
        }
    }

    public interface ClickListener {
        void onDocClick(DoctorModel model);
    }
}
