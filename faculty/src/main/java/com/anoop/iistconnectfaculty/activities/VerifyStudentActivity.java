package com.anoop.iistconnectfaculty.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anoop.iistconnectfaculty.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.rgpvnotes.alert.myresource.Constants;
import in.rgpvnotes.alert.myresource.dialog.MyProgressDialog;
import in.rgpvnotes.alert.myresource.model.StudentModel;

public class VerifyStudentActivity extends AppCompatActivity {

    private android.app.AlertDialog dialog ;

    private String studentId;
    private StudentModel model;


    //views
    private CircleImageView photoView;
    private EditText nameET;
    private EditText enrollmentET;
    private EditText sectionET;
    private RadioButton BE;
    private RadioButton BTech;
    private RadioGroup radioGroup;

    private Spinner branchSP;

    private Spinner semesterSP;

    private Button verifyButton;
    private Button deleteButton;


    private String name;
    private String email;
    private String enroll;
    private String program;

    private String branch;
    private String section;
    private String semester;

    private String currentYear;
    private String photoUrl;

    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_student);
        getSupportActionBar().setTitle("New Student");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFunctions = FirebaseFunctions.getInstance();

        dialog = new MyProgressDialog(this);

        studentId = getIntent().getStringExtra("studentId");
        model = (StudentModel) getIntent().getSerializableExtra("model");

        photoView = findViewById(R.id.profile_image);

        //editTexts
        nameET = findViewById(R.id.nameET);
        enrollmentET = findViewById(R.id.enrollment_no);
        sectionET = findViewById(R.id.inputSection);

        BE = findViewById(R.id.radioBE);
        BTech = findViewById(R.id.radioBTech);
        radioGroup = findViewById(R.id.radioGroup);

        branchSP = findViewById(R.id.spinnerBranch);
        semesterSP = findViewById(R.id.spinnerSemester);

        verifyButton = findViewById(R.id.verify_button);
        deleteButton = findViewById(R.id.delete_button);

        nameET.setText(model.getStudentName());
        enrollmentET.setText(model.getEnrollmentNumber());
        sectionET.setText(model.getSection());

        branch = model.getStudentBranch();
        semester = model.getCurrentSemester();
        photoUrl = model.getProfileImage();
        program = model.getProgram();
        email = model.getStudentEmail();

        switch (branch){
            case "CS": branchSP.setSelection(0);break;
            case "ME": branchSP.setSelection(1);break;
            case "CE": branchSP.setSelection(2);break;
            case "IT": branchSP.setSelection(3);break;
            case "CM": branchSP.setSelection(4);break;
            case "EC": branchSP.setSelection(5);break;
        }


        currentYear = "";
        switch (semester){

            case "I":currentYear = "1st Year";semesterSP.setSelection(1);break;
            case "II":currentYear = "1st Year";semesterSP.setSelection(2);break;
            case "III":currentYear = "2nd Year";semesterSP.setSelection(3);break;
            case "IV":currentYear = "2nd Year";semesterSP.setSelection(4);break;
            case "V":currentYear = "3rd Year";semesterSP.setSelection(5);break;
            case "VI":currentYear = "3rd Year";semesterSP.setSelection(6);break;
            case "VII":currentYear = "4th Year";semesterSP.setSelection(7);break;
            case "VIII":currentYear = "4th Year";semesterSP.setSelection(8);break;

        }

        branchSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i){
                    case 0 : branch="CS";break;
                    case 1 : branch="ME";break;
                    case 2 : branch="CE";break;
                    case 3 : branch="IT";break;
                    case 4 : branch="CM";break;
                    case 5 : branch="EC";break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if(program.equals("BE")){
            BE.setChecked(true);
        }else {
            BTech.setChecked(true);
        }

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInputs()){

                    int id = radioGroup.getCheckedRadioButtonId();
                    if(id == R.id.radioBE ){
                        program = "BE";
                    }else {
                        program = "B.Tech";
                    }

                    verifyStudent();

                }

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 04-04-2018 delete student here

                deleteStudent(studentId);

            }
        });


    }

    private Task<String> deleteStudent(String studentId) {

        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        dialog.setCancelable(false);

        // Create the arguments to the callable function, which is just one string
        Map<String, Object> data = new HashMap<>();
        data.put("studentId", studentId);

        return mFunctions
                .getHttpsCallable("deleteStudent")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        dialog.dismiss();
                        Toast.makeText(VerifyStudentActivity.this, result, Toast.LENGTH_SHORT).show();
                        finish();
                        return result;
                    }
                });
    }

    private void verifyStudent() {

        String batch_no = enroll.substring(6,8);

        String batch = "20"+batch_no;

        switch (semester){

            case "I":currentYear = "1st Year";break;
            case "II":currentYear = "1st Year";break;
            case "III":currentYear = "2nd Year";break;
            case "IV":currentYear = "2nd Year";break;
            case "V":currentYear = "3rd Year";break;
            case "VI":currentYear = "3rd Year";break;
            case "VII":currentYear = "4th Year";break;
            case "VIII":currentYear = "4th Year";break;

        }

        char b = currentYear.charAt(0);

        String classId = section+"-"+b;

        model = new StudentModel(name,email,branch,photoUrl,enroll,batch,"true",section,currentYear,classId,semester,program);

        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
        dialog.setCancelable(false);


        FirebaseFirestore database = FirebaseFirestore.getInstance();

        DocumentReference myRef = database.collection(Constants.STUDENTS_COLLECTION).document(studentId);

        myRef.set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                dialog.dismiss();
                Toast.makeText(VerifyStudentActivity.this, "Student verified", Toast.LENGTH_SHORT).show();
                finish();

            }
        }).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(VerifyStudentActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean checkInputs() {

        name = nameET.getText().toString();
        enroll = enrollmentET.getText().toString();
        section = sectionET.getText().toString();

        section = section.toUpperCase();

        enroll = enroll.toUpperCase();

        semester = semesterSP.getSelectedItem().toString();
        TextView semErrorText = (TextView)semesterSP.getSelectedView();


        boolean n = true, e = true, sec = true, sem = true;

        sectionET.setError(null);
        nameET.setError(null);
        enrollmentET.setError(null);

        if(name.length()<3 ){
            nameET.setError("Enter valid name");
            n = false;
        }
        if(enroll.length()!=12){
            enrollmentET.setError("Enter valid enrollment");
            e=false;
        }

        if(section.length() != 3){

            sectionET.setError("Invalid section");
            sec=false;

        }

        if(semester.equals("Select Semester")){

            semErrorText.setError("");
            semErrorText.setTextColor(Color.RED);//just to highlight that this is an error
            semErrorText.setText("None selected");//changes the selected item text to this
            sem=false;

        }

        return sem && sec && n && e;

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
