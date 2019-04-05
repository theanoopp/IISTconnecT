package com.anoop.iistconnect.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anoop.iistconnect.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import in.rgpvnotes.alert.myresource.models.SingleLecture;

/**
 * Created by Anoop on 08-03-2018.
 */

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.TimetableViewHolder> {

    private static final String TAG = "TimeTableAdapter";

    private FirebaseFirestore mDatabase;

    private List<SingleLecture> mList ;
    private String section;
    private Context context;

    public TimeTableAdapter(List<SingleLecture> mList, String section, Context context){
        this.mList = mList;
        this.section = section;
        this.context = context;
    }

    @Override
    public TimetableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timetable_row,parent,false);

        return new TimetableViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TimetableViewHolder holder, int position) {

        final SingleLecture singleLecture = mList.get(position);

        int a = position+1;

        holder.setSubName(a+" : "+singleLecture.getSubjectName());

        holder.setLectureTime("Time : "+singleLecture.getTime());

        mDatabase = FirebaseFirestore.getInstance();

        Query query = mDatabase.collection("fac_sub_map").whereEqualTo("classId",section).whereEqualTo("subjectCode",singleLecture.getSubjectCode());

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {


                for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {

                    switch (dc.getType()) {
                        case ADDED:
                            String facName = dc.getDocument().getString("facultyName");
                            holder.setFacName(facName);
                            break;


                        case MODIFIED:

                            break;

                        case REMOVED:

                            break;
                    }
                }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(context,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

            }
        });



    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class TimetableViewHolder extends RecyclerView.ViewHolder {

        private TextView subName;
        private TextView lectureTime;
        private TextView facName;

        public TimetableViewHolder(View itemView) {
            super(itemView);

            subName = itemView.findViewById(R.id.my_class_subname);
            lectureTime = itemView.findViewById(R.id.my_class_name);
            facName = itemView.findViewById(R.id.facName);

        }

        public void setSubName(String subName){

            this.subName.setText(subName);

        }

        public void setLectureTime(String lecture) {

            this.lectureTime.setText(lecture);

        }

        public void setFacName(String name){

            this.facName.setText(name);
            this.facName.setVisibility(View.VISIBLE);

        }

    }

}
