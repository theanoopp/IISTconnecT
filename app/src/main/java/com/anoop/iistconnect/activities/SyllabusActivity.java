package com.anoop.iistconnect.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.anoop.iistconnect.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import in.rgpvnotes.alert.myresource.activities.ViewSyllabusActivity;
import in.rgpvnotes.alert.myresource.models.subject.SingleSubject;

public class SyllabusActivity extends AppCompatActivity {

    private String branch;
    private String sem;
    private String program;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Syllabus");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        branch = getIntent().getStringExtra("branch");

        sem = getIntent().getStringExtra("sem");

        program = getIntent().getStringExtra("program");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("syllabus")
                .child(program)
                .child(branch)
                .child(sem);

        query.keepSynced(true);

        FirebaseRecyclerOptions<SingleSubject> options =
                new FirebaseRecyclerOptions.Builder<SingleSubject>()
                        .setLifecycleOwner(this)
                        .setQuery(query, SingleSubject.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<SingleSubject, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_assignment_list, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(ViewHolder holder, int position, SingleSubject model) {

                final String code = getRef(position).getKey();
                final String name = model.getName();

                holder.bind(model);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(SyllabusActivity.this, ViewSyllabusActivity.class);

                        intent.putExtra("name",name);
                        intent.putExtra("branch",branch);
                        intent.putExtra("sem",sem);
                        intent.putExtra("subjectCode",code);

                        startActivity(intent);
                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);



    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView subName;
        private View mView;

        private ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            subName = itemView.findViewById(R.id.list_sub_name);

        }

        private void bind(SingleSubject model) {
            subName.setText(model.getName());
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
