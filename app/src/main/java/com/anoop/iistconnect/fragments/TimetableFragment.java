package com.anoop.iistconnect.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anoop.iistconnect.R;
import com.anoop.iistconnect.adapter.TimeTableAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

import in.rgpvnotes.alert.myresource.models.SingleDay;
import in.rgpvnotes.alert.myresource.models.SingleLecture;

/**
 * Created by Anoop on 08-03-2018.
 */

public class TimetableFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";


    private List<SingleLecture> singleLectures = new ArrayList<>();
    private TimeTableAdapter timeTableAdapter ;

    private FirebaseFirestore mDatabase;

    private String section;
    private String day;

    private RecyclerView recyclerView;



    public TimetableFragment() {
    }

    public static TimetableFragment newInstance(int sectionNumber,String section,String day) {

        TimetableFragment fragment = new TimetableFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("section",section);
        args.putString("day",day);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_student_timetable, container, false);

        mDatabase = FirebaseFirestore.getInstance();

        recyclerView = rootView.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        section = getArguments().getString("section");
        day = getArguments().getString("day");

        timeTableAdapter = new TimeTableAdapter(singleLectures,section,getActivity());

        recyclerView.setAdapter(timeTableAdapter);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        mDatabase.collection("time_table").document(section+"-"+day).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                    SingleDay dayMap = documentSnapshot.toObject(SingleDay.class);

                    singleLectures.clear();

                    singleLectures.add(dayMap.getLecture1());
                    singleLectures.add(dayMap.getLecture2());
                    singleLectures.add(dayMap.getLecture3());
                    singleLectures.add(dayMap.getLecture4());
                    singleLectures.add(dayMap.getLecture5());
                    singleLectures.add(dayMap.getLecture6());
                    singleLectures.add(dayMap.getLecture7());
                    singleLectures.add(dayMap.getLecture8());

                    timeTableAdapter.notifyDataSetChanged();

                }




            }
        });


    }

}
