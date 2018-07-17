package com.anoop.iistconnect.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import com.anoop.iistconnect.R;
import com.anoop.iistconnect.adapter.AssignmentAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import in.rgpvnotes.alert.myresource.models.SubAssignmentList;

public class AllAssignments extends AppCompatActivity {

    private static final String TAG = "AssignmentsActivity";

    private RecyclerView recyclerView;


    private FirebaseFirestore mDatabase;

    private AssignmentAdapter assignmentAdapter;

    private ArrayList<SubAssignmentList> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_assignments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Assignments");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.assignments_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        assignmentAdapter = new AssignmentAdapter(list,AllAssignments.this);

        recyclerView.setAdapter(assignmentAdapter);

        final String section = getIntent().getStringExtra("section");

        Query query = mDatabase.collection("assignments").whereEqualTo("classId",section);

        query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for(DocumentChange dc : documentSnapshots.getDocumentChanges()){

                    String subCode = dc.getDocument().getString("subjectCode");
                    String subName = dc.getDocument().getString("subjectName");

                    if(dc.getType() == DocumentChange.Type.ADDED){

                        SubAssignmentList a = new SubAssignmentList(subName,subCode,section);

                        if(!list.contains(a)){
                            list.add(a);
                            assignmentAdapter.notifyDataSetChanged();
                        }

                    }

                }

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
