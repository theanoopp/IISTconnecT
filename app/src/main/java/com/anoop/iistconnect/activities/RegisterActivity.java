package com.anoop.iistconnect.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anoop.iistconnect.R;
import com.anoop.iistconnect.fragments.RegistrationFragment1;
import com.anoop.iistconnect.fragments.RegistrationFragment2;
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
import in.rgpvnotes.alert.myresource.Constants;
import in.rgpvnotes.alert.myresource.dialog.MyProgressDialog;
import in.rgpvnotes.alert.myresource.models.StudentModel;

public class RegisterActivity extends AppCompatActivity implements RegistrationFragment1.RegistrationFragment1Listener,RegistrationFragment2.OnSubmitListener {

    private static final String TAG = "Register_Activity";

    private CircleImageView photoView;

    private String name;
    private String email;
    private String pass;
    private String enroll;
    private String program;
    private String photoUrl="default";

    private Uri resultUri;

    private android.app.AlertDialog dialog ;

    private FirebaseAuth mAuth;

    private String student_id;

    private StudentModel model;

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

        RegistrationFragment1 first = RegistrationFragment1.newInstance();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame,first, "first")
                // Add this transaction to the back stack
                .commit();



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

    @Override
    public void onSubmit(String name,String email,String pass,String enroll,String program,String branch) {

        this.name = name;
        this.email = email;
        this.pass = pass;
        this.enroll = enroll;
        this.program = program;

        RegistrationFragment2 second = RegistrationFragment2.newInstance(branch);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame,second, "second")
                // Add this transaction to the back stack
                .addToBackStack(null)
                .commit();

    }


    @Override
    public void OnSubmit(String branch,String section,String semester) {

        String batch_no = enroll.substring(6,8);

        String batch = "20"+batch_no;

        String currentYear = "";
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

        model = new StudentModel(name,email,branch,photoUrl,enroll,batch,"false",section,currentYear,classId,semester,program);

        registerStudent();
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

                if(resultUri!=null){

                    dialog.setTitle("Uploading image");
                    dialog.setMessage("Please wait while we make your account..");

                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference("students_profile_photos").child(student_id);

                    storageReference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            photoUrl = storageReference.getDownloadUrl().toString();
                            model.setProfileImage(photoUrl);
                            updateDetails();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // TODO: 17/2/18 add image uploading progress

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
