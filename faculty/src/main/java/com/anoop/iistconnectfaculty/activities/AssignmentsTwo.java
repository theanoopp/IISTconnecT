package com.anoop.iistconnectfaculty.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anoop.iistconnectfaculty.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;

import in.rgpvnotes.alert.myresource.dialog.MyProgressDialog;
import in.rgpvnotes.alert.myresource.model.AssignmentModel;
import in.rgpvnotes.alert.myresource.model.MyClassModel;

public class AssignmentsTwo extends AppCompatActivity {

    private static final String TAG = "AssignmentsTwo";

    private RecyclerView recyclerView;


    private FirebaseFirestore mDatabase;

    private MyClassModel classModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_two);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        classModel = (MyClassModel) getIntent().getSerializableExtra("model");

        getSupportActionBar().setTitle(classModel.getSubjectName()+"("+classModel.getClassId()+")");

        mDatabase = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        findViewById(R.id.add_work_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent workIntent = new Intent(AssignmentsTwo.this, AddAssignment.class);

                workIntent.putExtra("class_model",classModel);

                // TODO: 15-04-2018 delete krna h
                workIntent.putExtra("type","new");

                startActivity(workIntent);


            }
        });


        setupRecyclerView();

    }

    private void setupRecyclerView() {


        Query query = mDatabase.collection("assignments").whereEqualTo("classId",classModel.getClassId()).whereEqualTo("subjectCode",classModel.getSubjectCode());

        FirestoreRecyclerOptions<AssignmentModel> options = new FirestoreRecyclerOptions.Builder<AssignmentModel>()
                .setLifecycleOwner(this)
                .setQuery(query, AssignmentModel.class)
                .build();


        final FirestoreRecyclerAdapter mAdapter = new FirestoreRecyclerAdapter<AssignmentModel, ViewHolder>(options) {
            @Override
            public void onBindViewHolder(ViewHolder holder, final int position, final AssignmentModel assignmentModel) {

                final String doc_id = getSnapshots().getSnapshot(position).getId();

                holder.bind(assignmentModel);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence options[] = new CharSequence[]{"View","Delete"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(AssignmentsTwo.this);
                        builder.setTitle("Select Options");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                switch (i) {

                                    case 0:

                                        Intent viewAssignmentIntent = new Intent(AssignmentsTwo.this, ViewAssignment.class);

                                        viewAssignmentIntent.putExtra("model", assignmentModel);

                                        startActivity(viewAssignmentIntent);

                                        break;

                                    case 1:

                                        deleteAssignment(doc_id);
                                        break;


                                }

                            }
                        });

                        builder.show();


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

    private void deleteAssignment(final String docId) {

        final MyProgressDialog dialog = new MyProgressDialog(AssignmentsTwo.this);
        dialog.setTitle("Please wait...");
        dialog.setMessage("Deleting...");
        dialog.setCancelable(false);
        dialog.show();

        mDatabase.collection("assignments").document(docId).delete().addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("assignment_files").child(docId);
                mStorageRef.delete();

            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;

        private TextView subject;
        private TextView name;
        private TextView sDate;
        private TextView type;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            subject = itemView.findViewById(R.id.assignment_sub);
            name = itemView.findViewById(R.id.assignment_name);
            sDate =itemView.findViewById(R.id.assignment_sub_date);
            type = itemView.findViewById(R.id.assignment_type);

        }

        public void bind(AssignmentModel model){

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
