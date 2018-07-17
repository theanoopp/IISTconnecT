package com.anoop.iistconnectfaculty.activities;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anoop.iistconnectfaculty.R;
import com.anoop.iistconnectfaculty.ViewHolder.AttendanceViewHolder;
import com.anoop.iistconnectfaculty.adapter.AttendanceAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.rgpvnotes.alert.myresource.Constants;
import in.rgpvnotes.alert.myresource.dialog.TimeSelectDialog;
import in.rgpvnotes.alert.myresource.models.AttendanceModel;
import in.rgpvnotes.alert.myresource.models.MyClassModel;
import in.rgpvnotes.alert.myresource.models.StudentModel;

public class Attendance extends AppCompatActivity {

    private static final String TAG = "Attendance_test";

    private RecyclerView recyclerView;

    private Spinner semSpinner;
    private Spinner timeSpinner;
    private Spinner branchSP;
    private Spinner sectionSP;
    private Spinner subSpinner;

    private ArrayAdapter<CharSequence> sectionAdapter ;

    private MyClassModel classModel;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private FirebaseUser currentUser;
    
    private List<AttendanceModel> studentList = new ArrayList<>();

    private AttendanceAdapter attendanceAdapter;

    private CollectionReference attendanceRef;

    private String currentSession;

    private String semester;
    private String timeSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        classModel = (MyClassModel) getIntent().getSerializableExtra("model");

        getSupportActionBar().setTitle(classModel.getClassId());

        semSpinner = findViewById(in.rgpvnotes.alert.myresource.R.id.select_time_sem);
        timeSpinner = findViewById(in.rgpvnotes.alert.myresource.R.id.select_time_time);
        branchSP = findViewById(R.id.spinnerBranch);
        sectionSP = findViewById(R.id.spinnerSection);
        subSpinner = findViewById(R.id.notes_input_subject);


        Button okButton = findViewById(in.rgpvnotes.alert.myresource.R.id.select_time_button);
        Button cancelButton = findViewById(in.rgpvnotes.alert.myresource.R.id.select_time_cancel_button);

        recyclerView = findViewById(R.id.student_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        recyclerView.setAdapter(attendanceAdapter);

        mDatabase = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        mDatabase.collection("utils").document("currentSession").get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                currentSession = documentSnapshot.getString("session");

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Attendance.this, "error getting current session", Toast.LENGTH_SHORT).show();

            }
        });

        attendanceRef = mDatabase.collection("attendance").document(classModel.getClassId()).collection("students");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInputs()){
                    ConstraintLayout v = findViewById(R.id.timeView);
                    v.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    getStudentList();

                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        attendanceAdapter = new AttendanceAdapter(studentList);
        attendanceAdapter.setClickListener(new AttendanceAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });

        branchSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int m = R.array.select_class;
                switch (i){
                    case 1 : m = R.array.cs_class;break;
                    case 2 : m = R.array.me_class;break;
                    case 3 : m = R.array.ce_class;break;
                    case 4 : m = R.array.it_class;break;
                    case 5 : m = R.array.cm_class;break;
                    case 6 : m = R.array.ec_class;break;
                }

                sectionAdapter = ArrayAdapter.createFromResource(Attendance.this, m, android.R.layout.simple_spinner_item);
                sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sectionSP.setAdapter(sectionAdapter);
                sectionAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

    private void getStudentList() {

        Query query = mDatabase.collection(Constants.STUDENTS_COLLECTION).whereEqualTo("classId",classModel.getClassId());

        query.get().addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot doc: queryDocumentSnapshots ) {

                    String studentName = doc.getString("studentName");
                    String enrollmentNumber = doc.getString("enrollmentNumber");
                    Date date = Calendar.getInstance().getTime();
                    String classId = classModel.getClassId();
                    String subjectCode = classModel.getSubjectCode();
                    String subjectName = classModel.getSubjectName();
                    String facultyId = classModel.getFacultyId();
                    String facultyName = classModel.getFacultyName();

                    AttendanceModel model = new AttendanceModel(studentName,enrollmentNumber,false,date,classId,subjectCode,subjectName,semester,facultyId,facultyName,timeSlot);

                    studentList.add(model);

                }

                attendanceAdapter.notifyDataSetChanged();




            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }




    /*
    private void setupRecyclerView() {

        Query query = mDatabase.collection(Constants.STUDENTS_COLLECTION).whereEqualTo("classId",classModel.getClassId());

        FirestoreRecyclerOptions<StudentModel> options = new FirestoreRecyclerOptions.Builder<StudentModel>()
                .setLifecycleOwner(this)
                .setQuery(query, StudentModel.class)
                .build();


        FirestoreRecyclerAdapter mClassAdapter = new FirestoreRecyclerAdapter<StudentModel, AttendanceViewHolder>(options) {
            @Override
            public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position, @NonNull final StudentModel model) {

                //holder.setIsRecyclable(false);
                holder.bind(model);

            }

            @Override
            public AttendanceViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.attendance_row, group, false);
                return new AttendanceViewHolder(view);
            }
        };


        recyclerView.setAdapter(mClassAdapter);


    }

    */

    private boolean checkInputs(){

        semester = semSpinner.getSelectedItem().toString();
        timeSlot = timeSpinner.getSelectedItem().toString();

        if(semester.equals("Select Semester")){
            Toast.makeText(Attendance.this, "Select semester", Toast.LENGTH_SHORT).show();
            return false;
        }else if(timeSlot.equals("Select Time slot")){
            Toast.makeText(Attendance.this, "Select time slot", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;


    }

}
