package com.anoop.iistconnectfaculty.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.anoop.iistconnectfaculty.R;

import java.util.List;

import in.rgpvnotes.alert.myresource.model.AssignmentFile;

/**
 * Created by Anoop on 08-03-2018.
 */

public class AddAssignmentAdapter extends RecyclerView.Adapter<AddAssignmentAdapter.ViewHolder>  {

    private static final String TAG = "GiveWorkAdapter";

    private List<AssignmentFile> fileList;

    public AddAssignmentAdapter(List<AssignmentFile> fileList) {

        this.fileList = fileList;

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // TODO: 02-03-2018 change layout

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_input_layout,parent,false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,int position) {

        Uri fileUri = fileList.get(position).getUri();

        String filePath = fileUri.getLastPathSegment();

        int index = filePath.lastIndexOf(".");

        String type = "";

        if(index>0){
            type =  filePath.substring(index);// File type Extension with dot .jpg, .png
        }

        if(type.equals(".jpg")){
            holder.setImageView(fileUri);
        }else {
            holder.imageView.setVisibility(View.GONE);
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fileList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());

            }
        });


    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface WorkAdapterInterface{

        void onDelete(String msg_id,String type);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private ImageButton button;

        private ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.work_row_image);
            button = itemView.findViewById(R.id.work_row_delete_button);


        }

        private void setImageView(Uri uri) {
            this.imageView.setImageURI(uri);
        }
    }


}
