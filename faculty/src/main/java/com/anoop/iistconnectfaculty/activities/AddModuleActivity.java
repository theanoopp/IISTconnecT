package com.anoop.iistconnectfaculty.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.anoop.iistconnectfaculty.R;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.rgpvnotes.alert.myresource.dialog.MyProgressDialog;


public class AddModuleActivity extends AppCompatActivity{

    private Button startDateButton;
    private Button endDateButton;

    private Calendar calendar;

    private SimpleDateFormat simpleDateFormat;

    private DatePickerDialog.OnDateSetListener startDateDialog;

    private DatePickerDialog.OnDateSetListener endDateDialog;

    private Button mSubmitButton;

    private TextInputLayout mCourseNameInput;

    private TextInputLayout mBriefInput;

    private TextInputLayout mLocationInput;

    private String courseName,courseBrief,classLocation;

    private Date startDate , endDate;

    private MyProgressDialog dialog ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_module);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new MyProgressDialog(AddModuleActivity.this);

        startDateButton = findViewById(R.id.startDate);

        endDateButton = findViewById(R.id.endDate);

        mSubmitButton = findViewById(R.id.submitCourse);

        mBriefInput = findViewById(R.id.briefInput);

        mCourseNameInput = findViewById(R.id.courseNameInput);

        mLocationInput = findViewById(R.id.location);

        calendar = Calendar.getInstance();

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        startDateDialog = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker datePicker,int year, int month, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startDate = calendar.getTime();

                String time = simpleDateFormat.format(startDate);

                startDateButton.setTextColor(Color.BLACK);

                startDateButton.setText(time);

            }
        };



        endDateDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endDate = calendar.getTime();


                String time = simpleDateFormat.format(endDate);

                endDateButton.setTextColor(Color.BLACK);

                endDateButton.setText(time);


            }
        };


        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog = new DatePickerDialog(AddModuleActivity.this, startDateDialog,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();

            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog = new DatePickerDialog(AddModuleActivity.this, endDateDialog,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();

            }
        });


        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInput()){

                    dialog.setTitle("Please wait...");
                    dialog.setMessage("Creating...");
                    dialog.setCancelable(false);

                    dialog.show();

                    mSubmitButton.setEnabled(false);

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    FirebaseFirestore database = FirebaseFirestore.getInstance();

                    Map<String,Object> moduleMap = new HashMap<>();
                    moduleMap.put("courseName",courseName);
                    moduleMap.put("courseBrief",courseBrief);
                    moduleMap.put("location",classLocation);
                    moduleMap.put("startDate",startDate);
                    moduleMap.put("endDate",endDate);
                    moduleMap.put("facultyName",firebaseUser.getDisplayName());
                    moduleMap.put("facultyEmail",firebaseUser.getEmail());

                    database.collection("modules").document().set(moduleMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(AddModuleActivity.this, "New Module created", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(AddModuleActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                }



            }
        });



    }

    private boolean checkInput() {

        boolean result = true;

        mCourseNameInput.setError(null);

        mBriefInput.setError(null);

        mLocationInput.setError(null);

        startDateButton.setTextSize(14);

        courseName = mCourseNameInput.getEditText().getText().toString();

        courseBrief = mBriefInput.getEditText().getText().toString();

        classLocation = mLocationInput.getEditText().getText().toString();

        if(courseName.length() < 4){

            mCourseNameInput.setError("Enter course name");

            result = false;

        }
        if(courseBrief.length() < 4){

            mBriefInput.setError("Enter course brief");
            result = false;

        }
        if(classLocation.length() < 4){

            mLocationInput.setError("Enter classroom location");
            result = false;

        }

        if(startDate == null){

            Toast.makeText(AddModuleActivity.this, "Select start date", Toast.LENGTH_SHORT).show();
            startDateButton.setTextColor(Color.RED);
            result = false;

        }

        if(endDate == null){

            Toast.makeText(AddModuleActivity.this, "Select end date", Toast.LENGTH_SHORT).show();
            endDateButton.setTextColor(Color.RED);
            result = false;

        }




        return result;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}

