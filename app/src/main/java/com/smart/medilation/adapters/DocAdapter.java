package com.smart.medilation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.smart.medilation.R;
import com.smart.medilation.model.CategoriesModel;
import com.smart.medilation.model.DoctorModel;

import java.util.ArrayList;
import java.util.List;

public class DocAdapter extends RecyclerView.Adapter<DocAdapter.CustomViewHolder> implements Filterable {

    private final Context context;
    private final ClickListener listener;
    private final List<DoctorModel> docList;
    private final List<DoctorModel> docListFull;

    public DocAdapter(Context context, List<DoctorModel> docList, ClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.docList = docList;
        docListFull = new ArrayList<>(docList);
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
            holder.txtName.setText(docList.get(position).name);
            holder.image.setOnClickListener(v -> listener.onDocClick(docList.get(position)));
            Glide.with(context)
                    .load(docList.get(position).image)
                    .placeholder(R.drawable.ic_user)
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

    @Override
    public Filter getFilter() {
        return docFilter;
    }
    private final Filter docFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<DoctorModel> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(docListFull);
            }
            else {
                String filePattern = charSequence.toString().toLowerCase().trim();
                for (DoctorModel item : docListFull ){
                    if(item.name.toLowerCase().contains(filePattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            docList.clear();
            docList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        CircularImageView image;

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
