package com.anoop.iistconnectfaculty.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import in.rgpvnotes.alert.myresource.dialog.TimeSelectDialog;
import in.rgpvnotes.alert.myresource.model.MyClassModel;

public class MyClasses extends AppCompatActivity{

    private static final String TAG = "MyClasses";

    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_classes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Classes");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);

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

        Query query = mDatabase.collection("fac_sub_map").whereEqualTo("facultyId", currentUser.getEmail());

        FirestoreRecyclerOptions<MyClassModel> options = new FirestoreRecyclerOptions.Builder<MyClassModel>()
                .setLifecycleOwner(this)
                .setQuery(query, MyClassModel.class)
                .build();


        FirestoreRecyclerAdapter mClassAdapter = new FirestoreRecyclerAdapter<MyClassModel, ViewHolder>(options) {
            @Override
            public void onBindViewHolder(ViewHolder holder, final int position, final MyClassModel model) {

                holder.bind(model);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence options[] = new CharSequence[]{"Assignments", "Take Attendance"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(MyClasses.this);

                        builder.setTitle("Select option");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                switch (i) {

                                    case 0:

                                        Intent intent = new Intent(MyClasses.this, AssignmentsTwo.class);

                                        intent.putExtra("model",model);

                                        startActivity(intent);
                                        break;

                                    case 1:

                                        Intent attendanceIntent = new Intent(MyClasses.this, Attendance.class);

                                        attendanceIntent.putExtra("model",model);

                                        startActivity(attendanceIntent);

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
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.my_class_row, group, false);
                return new ViewHolder(view);
            }
        };

        recyclerView.setAdapter(mClassAdapter);


    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView subName;
        private TextView clsName;
        private TextView year;
        private View mView;

        private ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            subName = itemView.findViewById(R.id.my_class_subname);
            clsName = itemView.findViewById(R.id.my_class_name);
            year = itemView.findViewById(R.id.my_class_year);
        }

        private void bind(MyClassModel myClassModel){

            char y = myClassModel.getClassId().charAt(4);
            switch (y){
                case '1' :year.setText("1st Year");break;
                case '2' :year.setText("2nd Year");break;
                case '3' :year.setText("3rd Year");break;
                case '4' :year.setText("4th Year");break;
            }

            String cls = myClassModel.getClassId().substring(0,3);

            subName.setText(myClassModel.getSubjectName());
            clsName.setText(cls);


        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
