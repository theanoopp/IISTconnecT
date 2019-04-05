package com.anoop.iistconnect.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anoop.iistconnect.R;
import com.anoop.iistconnect.activities.AssignmentsBySubject;


import java.util.ArrayList;

import in.rgpvnotes.alert.myresource.models.SubAssignmentList;

/**
 * Created by Anoop on 08-03-2018.
 */

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder>  {

    private ArrayList<SubAssignmentList> list;
    private Context context;

    public AssignmentAdapter(ArrayList<SubAssignmentList> list,Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_assignment_list,parent,false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.subName.setText(list.get(position).getSubjectName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, AssignmentsBySubject.class);

                intent.putExtra("section",list.get(position).getSection());
                intent.putExtra("code",list.get(position).getSubjectCode());
                intent.putExtra("name",list.get(position).getSubjectName());

                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView subName;
        public View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            subName = itemView.findViewById(R.id.list_sub_name);

        }
    }




}

