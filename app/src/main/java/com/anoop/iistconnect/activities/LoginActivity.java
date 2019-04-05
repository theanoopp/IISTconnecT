package com.anoop.iistconnect.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anoop.iistconnect.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import in.rgpvnotes.alert.myresource.dialog.MyProgressDialog;
import in.rgpvnotes.alert.myresource.utils.Constants;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailET,passET;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private AlertDialog dialog ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Button signBT = findViewById(R.id.signBT);
        Button regBT = findViewById(R.id.regBT);
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
                    dialog.setCancelable(false);

                    String email = emailET.getEditText().getText().toString();
                    String pass = passET.getEditText().getText().toString();

                    mAuth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            currentUser = mAuth.getCurrentUser();
                            if(!currentUser.isEmailVerified()){
                                dialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setCancelable(false);
                                builder.setTitle("Email not verified");
                                builder.setMessage("Please verify your email for login...");
                                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mAuth.signOut();
                                    }
                                });
                                builder.setNegativeButton("Resend", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        currentUser.sendEmailVerification();
                                        mAuth.signOut();
                                    }
                                });
                                builder.show();
                            }else {
                                checkUser();
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });


                }




            }
        });


        regBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });






    }

    private void checkUser() {

        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

        mDatabase.collection(Constants.STUDENTS_COLLECTION).document(currentUser.getUid()).get().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<DocumentSnapshot>() {
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
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();


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
