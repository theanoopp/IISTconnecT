package com.anoop.iistconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anoop.iistconnect.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;

import in.rgpvnotes.alert.myresource.model.AssignmentModel;

public class AssignmentsBySubject extends AppCompatActivity {

    private RecyclerView recyclerView;


    private FirebaseFirestore mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_by_subject);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        String name = getIntent().getStringExtra("name");

        toolbar.setTitle(name);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mDatabase = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.assignments_sub_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        setupRecyclerView();


    }

    private void setupRecyclerView() {

        String section = getIntent().getStringExtra("section");
        String code = getIntent().getStringExtra("code");

        Query query = mDatabase.collection("assignments").whereEqualTo("classId",section).whereEqualTo("subjectCode",code);

        FirestoreRecyclerOptions<AssignmentModel> options = new FirestoreRecyclerOptions.Builder<AssignmentModel>()
                .setLifecycleOwner(this)
                .setQuery(query, AssignmentModel.class)
                .build();


        FirestoreRecyclerAdapter mAdapter = new FirestoreRecyclerAdapter<AssignmentModel, ViewHolder>(options) {
            @Override
            public void onBindViewHolder(final ViewHolder holder, int position, final AssignmentModel model) {

                final String doc_id = getSnapshots().getSnapshot(position).getId();

                holder.bind(model);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent viewAssignmentIntent = new Intent(AssignmentsBySubject.this, ViewAssignment.class);

                        viewAssignmentIntent.putExtra("model", model);

                        startActivity(viewAssignmentIntent);


                    }
                });

            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.assignment_row, group, false);
                return new ViewHolder(view);
            }
        };

        recyclerView.setAdapter(mAdapter);


    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView subject;
        private TextView name;
        private TextView sDate;
        private TextView type;

        private ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            subject = itemView.findViewById(R.id.assignment_sub);
            name = itemView.findViewById(R.id.assignment_name);
            sDate =itemView.findViewById(R.id.assignment_sub_date);
            type = itemView.findViewById(R.id.assignment_type);

        }

        private void bind(AssignmentModel model){

            name.setText(model.getTitle());
            subject.setText(model.getSubjectName());
            type.setText(model.getType());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

            String time = simpleDateFormat.format(model.getSubmitDate());
            sDate.setText(time);

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
