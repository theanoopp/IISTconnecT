package com.anoop.iistconnectfaculty.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import in.rgpvnotes.alert.myresource.dialog.MyProgressDialog;
import in.rgpvnotes.alert.myresource.model.StudentModel;
import in.rgpvnotes.alert.myresource.utils.Constants;

public class VerifyStudentActivity extends AppCompatActivity {

    private android.app.AlertDialog dialog ;

    private String studentId;
    private StudentModel model;


    //views
    private CircleImageView photoView;
    private EditText nameET;
    private EditText enrollmentET;


    private Spinner branchSP;


    private Button verifyButton;
    private Button deleteButton;


    private String name;
    private String email;
    private String enroll;

    private String branch;

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



        branchSP = findViewById(R.id.spinnerBranch);

        verifyButton = findViewById(R.id.verify_button);
        deleteButton = findViewById(R.id.delete_button);

        nameET.setText(model.getStudentName());
        enrollmentET.setText(model.getEnrollmentNumber());


        branch = model.getStudentBranch();
        photoUrl = model.getProfileImage();
        email = model.getStudentEmail();

        switch (branch){
            case "CS": branchSP.setSelection(0);break;
            case "ME": branchSP.setSelection(1);break;
            case "CE": branchSP.setSelection(2);break;
            case "IT": branchSP.setSelection(3);break;
            case "CM": branchSP.setSelection(4);break;
            case "EC": branchSP.setSelection(5);break;
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


        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInputs()){

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

        model = new StudentModel(name,email,branch,studentId,photoUrl,enroll);

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



        enroll = enroll.toUpperCase();



        boolean n = true, e = true;

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

        return n && e;

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
