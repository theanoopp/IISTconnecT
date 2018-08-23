package com.anoop.iistconnectfaculty.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anoop.iistconnectfaculty.R;
import com.anoop.iistconnectfaculty.ViewHolder.AttendanceViewHolder;

import java.util.List;

import in.rgpvnotes.alert.myresource.model.AttendanceModel;
import in.rgpvnotes.alert.myresource.model.StudentModel;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceViewHolder> {

    private List<AttendanceModel> nameList;

    private ItemClickListener mClickListener;

    public AttendanceAdapter(List<AttendanceModel> nameList) {

        this.nameList = nameList;

    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_row,parent,false);

        return new AttendanceViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {

        holder.bind(nameList.get(position));

    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public AttendanceModel getItem(int position) {
        return nameList.get(position);
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
