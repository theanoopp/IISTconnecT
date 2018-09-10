package com.anoop.iistconnectfaculty.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anoop.iistconnectfaculty.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import in.rgpvnotes.alert.myresource.dialog.MyProgressDialog;
import in.rgpvnotes.alert.myresource.model.StudentModel;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private TextInputLayout emailET,passET;

    private FirebaseAuth mAuth;

    private MyProgressDialog dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        Button signBT = findViewById(R.id.signBT);
        emailET = findViewById(R.id.emailEditText);
        passET = findViewById(R.id.passEditText);

        dialog = new MyProgressDialog(LoginActivity.this);

        signBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(checkInput()){

                    dialog.setTitle("Loading...");
                    dialog.setMessage("Please wait..");

                    dialog.show();

                    String email = emailET.getEditText().getText().toString();
                    String pass = passET.getEditText().getText().toString();

                    mAuth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            mDatabase.collection("Faculty").document(currentUser.getUid()).get().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    if(documentSnapshot.exists()) {
                                        dialog.dismiss();
                                        startActivity(new Intent(LoginActivity.this,HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK ));
                                        finish();

                                    }else {

                                        dialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"No record found",Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();

                                    }

                                }
                            }).addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    dialog.dismiss();
                                    Log.d(TAG,e.getMessage());
                                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();


                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            dialog.dismiss();
                            Log.d(TAG,e.getMessage());
                            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });


                }




            }
        });




    }

    private boolean checkInput() {

        boolean e = true, p = true;

        emailET.setError(null);
        passET.setError(null);


        if (emailET.getEditText().getText().toString().length() == 0) {
            emailET.setError("Email is required!");
            e=false;

        }
        if (passET.getEditText().getText().toString().length() < 6) {
            passET.setError("Password must be of minimum 6 character");
            p=false;
        }

        return e && p;

    }


}
