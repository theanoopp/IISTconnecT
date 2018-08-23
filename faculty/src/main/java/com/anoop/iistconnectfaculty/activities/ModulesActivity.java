package com.anoop.iistconnectfaculty.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anoop.iistconnectfaculty.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import in.rgpvnotes.alert.myresource.model.Module;

public class ModulesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules);

        getSupportActionBar().setTitle("Modules");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FloatingActionButton addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ModulesActivity.this,AddModuleActivity.class));

            }
        });

        recyclerView = findViewById(R.id.module_list);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        mDatabase = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();


    }

    @Override
    protected void onStart() {
        super.onStart();

        setupRecyclerView();
    }

    private void setupRecyclerView() {

        Query query = mDatabase.collection("modules").whereEqualTo("facultyEmail", currentUser.getEmail());

        FirestoreRecyclerOptions<Module> options = new FirestoreRecyclerOptions.Builder<Module>()
                .setLifecycleOwner(this)
                .setQuery(query, Module.class)
                .build();


        FirestoreRecyclerAdapter mClassAdapter = new FirestoreRecyclerAdapter<Module, ViewHolder>(options) {
            @Override
            public void onBindViewHolder(final ViewHolder holder, final int position, @NonNull final Module model) {

                holder.bind(model);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String a = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                        Toast.makeText(ModulesActivity.this,a, Toast.LENGTH_SHORT).show();

                    }
                });

            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.module_single_row, group, false);
                return new ViewHolder(view);
            }
        };

        recyclerView.setAdapter(mClassAdapter);


    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView moduleName;
        private TextView courseBrief;
        private View mView;

        private ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            moduleName = itemView.findViewById(R.id.module_name);
            courseBrief = itemView.findViewById(R.id.course_brief);
        }

        private void bind(Module module){

            moduleName.setText(module.getCourseName());
            courseBrief.setText(module.getCourseBrief());

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
