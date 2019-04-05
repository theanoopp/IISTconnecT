package com.anoop.iistconnectfaculty.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anoop.iistconnectfaculty.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import de.hdodenhof.circleimageview.CircleImageView;
import in.rgpvnotes.alert.myresource.model.Student;
import in.rgpvnotes.alert.myresource.utils.Constants;

public class NewStudents extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_students);
        getSupportActionBar().setTitle("New Students");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.new_students_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupRecyclerView();

    }

    private void setupRecyclerView() {

        Query query = FirebaseFirestore.getInstance().collection(Constants.STUDENTS_COLLECTION).whereEqualTo("verified","false");

        FirestoreRecyclerOptions<Student> options = new FirestoreRecyclerOptions.Builder<Student>()
                .setLifecycleOwner(this)
                .setQuery(query, Student.class)
                .build();


        FirestoreRecyclerAdapter mClassAdapter = new FirestoreRecyclerAdapter<Student, ViewHolder>(options) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position, final Student model) {

                holder.bind(model);

                final String id = getSnapshots().getSnapshot(position).getId();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(NewStudents.this,VerifyStudentActivity.class);
                        intent.putExtra("studentId",id);
                        intent.putExtra("model",model);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.student_row, group, false);
                return new ViewHolder(view);
            }
        };

        recyclerView.setAdapter(mClassAdapter);


    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView studentName;
        private TextView studentEnrollment;
        private CircleImageView imageView;
        private   View mView;

        private ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            studentName = itemView.findViewById(R.id.student_row_name);
            studentEnrollment = itemView.findViewById(R.id.student_row_enroll);
            imageView = itemView.findViewById(R.id.student_row_photo);
        }

        public void bind(Student model){

            studentName.setText(model.getStudentName());
            studentEnrollment.setText(model.getEnrollmentNumber());
            if(!model.getProfileImage().equals("default")){
                Glide.with(imageView.getContext()).load(model.getProfileImage()).into(imageView);
            }


        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
