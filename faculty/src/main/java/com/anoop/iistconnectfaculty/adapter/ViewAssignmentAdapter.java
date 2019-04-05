package com.anoop.iistconnectfaculty.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.anoop.iistconnectfaculty.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

/**
 * Created by Anoop on 08-03-2018.
 */

public class ViewAssignmentAdapter extends RecyclerView.Adapter<ViewAssignmentAdapter.ViewHolder> {

    private static final String TAG = "ViewAssignmentAdapter";

    private List<String> urlList;

    private ItemClickListener mClickListener;

    public ViewAssignmentAdapter(List<String> urlList) {

        this.urlList = urlList;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_thumbnail,parent,false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setImageView(urlList.get(position));


    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public String getItem(int position) {
        return urlList.get(position);
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private ProgressBar loading;
        private Context context;

        private ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = itemView.findViewById(R.id.thumbnail);
            loading = itemView.findViewById(R.id.loading);
            itemView.setOnClickListener(this);


        }


        private void setImageView(String url){

            Glide.with(context)
                    .load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            imageView.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    })
                    .into(imageView);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
