package com.anoop.iistconnect.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anoop.iistconnect.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import in.rgpvnotes.alert.myresource.dialog.MyProgressDialog;
import in.rgpvnotes.alert.myresource.model.StudentModel;
import in.rgpvnotes.alert.myresource.utils.Constants;

public class RegisterActivity extends AppCompatActivity{

    private static final String TAG = "Register_Activity";


    //ui elements
    private CircleImageView photoView;
    private EditText nameET;
    private EditText emailET;
    private EditText passET;
    private EditText cpassET;
    private EditText enrollmentET;

    private String name;
    private String email;
    private String enroll;
    private String pass;
    private String branch;
    private String photoUrl="default";

    private Uri resultUri;

    private android.app.AlertDialog dialog ;

    private FirebaseAuth mAuth;

    private String student_id;

    private StudentModel model;

    private boolean enrollCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Register");

        dialog = new MyProgressDialog(RegisterActivity.this);
        mAuth = FirebaseAuth.getInstance();

        photoView = findViewById(R.id.profile_image);
        nameET = findViewById(R.id.nameET);
        emailET = findViewById(R.id.emailET);
        passET = findViewById(R.id.passET);
        cpassET = findViewById(R.id.cpassET);
        enrollmentET = findViewById(R.id.enrollment_no);




        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1,1)
                        .start(RegisterActivity.this);


            }
        });

        enrollmentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if(charSequence.length()==12){

                    enroll = enrollmentET.getText().toString();
                    branch = enroll.substring(4,6);
                    branch = branch.toUpperCase();

                    switch (branch){
                        case "CS":enrollCheck=true;break;
                        case "ME":enrollCheck=true;break;
                        case "CE":enrollCheck=true;break;
                        case "IT":enrollCheck=true;break;
                        case "CM":enrollCheck=true;break;
                        case "EC":enrollCheck=true;break;
                        default: enrollCheck=false;enrollmentET.setError("Invalid enrollment");break;
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInputs()){
                    registerStudent();
                }

            }
        });






    }

    private boolean checkInputs() {

        name = nameET.getText().toString();
        email = emailET.getText().toString();
        enroll = enrollmentET.getText().toString();
        pass = passET.getText().toString();
        String cpass = cpassET.getText().toString();

        enroll = enroll.toUpperCase();

        boolean check = true;


        nameET.setError(null);
        emailET.setError(null);
        enrollmentET.setError(null);
        passET.setError(null);
        cpassET.setError(null);


        if(name.length()<3 ){
            nameET.setError("Enter valid name");
            check = false;
        }
        if(email.length()<3){
            emailET.setError("Enter valid Email");
            check = false;
        }
        if(enroll.length()!=12){
            enrollmentET.setError("Enter valid enrollment");
            enrollCheck=false;
        }

        if (pass.length() < 6) {
            passET.setError("password must be of minimum 6 character");
            check = false;
        }

        if (!pass.equals(cpass)) {
            cpassET.setError("password must be same");
            check = false;
        }


        return check && enrollCheck;

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                resultUri = result.getUri();
                photoView.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(RegisterActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void registerStudent() {

        dialog.setTitle("Loading");
        dialog.setMessage("Please wait while we make your account..");
        dialog.setCancelable(false);
        dialog.show();
        dialog.setCancelable(false);

        mAuth.createUserWithEmailAndPassword(email,pass).addOnSuccessListener(this,new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                student_id = mAuth.getCurrentUser().getUid();

                model = new StudentModel(name,email,branch,student_id,photoUrl,enroll);

                if(resultUri!=null){

                    dialog.setTitle("Uploading image");
                    dialog.setMessage("Please wait while we make your account..");

                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference("students_profile_photos").child(student_id);

                    UploadTask uploadTask = storageReference.putFile(resultUri);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            photoUrl = uri.toString();
                            model.setProfileImage(photoUrl);
                            updateDetails();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            updateDetails();
                            Log.d(TAG,e.getMessage());
                            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });

                }else {
                    updateDetails();
                }


            }
        }).addOnFailureListener(this,new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog.dismiss();
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updateDetails() {


        FirebaseFirestore database = FirebaseFirestore.getInstance();

        DocumentReference myRef = database.collection(Constants.STUDENTS_COLLECTION).document(student_id);

        myRef.set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                FirebaseUser user = mAuth.getCurrentUser();
                user.sendEmailVerification();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();

                user.updateProfile(profileUpdates).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // TODO: 04-04-2018 set message
                        mAuth.signOut();
                        dialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("Welcome to the IISTconnecT");
                        builder.setMessage("Hello "+ name +", Your account has been created. Please check  your email '" + email +"' for activating your account.");
                        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK ));
                                finish();
                            }
                        });
                        builder.show();

                    }
                });


            }
        }).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog.dismiss();
                Log.d(TAG,e.getMessage());
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
