package com.anoop.iistconnect.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anoop.iistconnect.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;

import in.rgpvnotes.alert.myresource.Constants;
import in.rgpvnotes.alert.myresource.activities.GalleryActivity;
import in.rgpvnotes.alert.myresource.model.StudentModel;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore mDatabase ;
    private StudentModel studentModel;

    private TextView nameT,enrollmentT;
    private ImageView photoView;

    private TextView headName,headEmail;
    private ImageView headImage;

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor userSessionEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.home_custom_bar,null);
        actionBar.setCustomView(actionBarView);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
        headName = hView.findViewById(R.id.nav_home_student_name);
        headEmail = hView.findViewById(R.id.nav_home_student_email);
        headImage = hView.findViewById(R.id.nav_home_image);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseFirestore.getInstance();

        nameT= findViewById(R.id.show_name);
        enrollmentT= findViewById(R.id.show_enrollment);
        photoView = findViewById(R.id.photo_view);

        CardView assignments = findViewById(R.id.b_assignments);
        CardView timeTable = findViewById(R.id.b_timetable);
        CardView notes = findViewById(R.id.b_notes);
        CardView library = findViewById(R.id.b_syllabus);


        assignments.setOnClickListener(this);
        timeTable.setOnClickListener(this);
        notes.setOnClickListener(this);
        library.setOnClickListener(this);

        initUser();


    }

    private void initUser() {

        if(mCurrentUser==null){

            startActivity(new Intent(HomeActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK ));
            finish();

        }else {

            DocumentReference studentsReference = mDatabase.collection(Constants.STUDENTS_COLLECTION).document(mCurrentUser.getUid());

            studentsReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                    if (documentSnapshot.exists()) {

                        studentModel = documentSnapshot.toObject(StudentModel.class);
                        if(studentModel.getVerified().equals("false")){
                            startActivity(new Intent(HomeActivity.this,AccountNotVerifiedActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK ));
                        }

                        userSessionEdit = sharedPreferences.edit();
                        userSessionEdit.putString("username", studentModel.getStudentName());
                        userSessionEdit.putString("useremail", studentModel.getStudentEmail());
                        userSessionEdit.apply();

                        nameT.setText(studentModel.getStudentName());
                        enrollmentT.setText(studentModel.getEnrollmentNumber());

                        headName.setText(studentModel.getStudentName());
                        headEmail.setText(studentModel.getStudentEmail());

                        String image = studentModel.getProfileImage();

                        switch (studentModel.getStudentBranch()){

                            case "CS" : headImage.setImageResource(R.drawable.cse_img);break;
                            case "ME" : headImage.setImageResource(R.drawable.mech_img);break;
                            case "CE" : headImage.setImageResource(R.drawable.civil);break;
                            case "IT" : headImage.setImageResource(R.drawable.it);break;
                            case "CM" : headImage.setImageResource(R.drawable.chemical);break;
                            case "EC" : headImage.setImageResource(R.drawable.extc);break;

                        }

                        if (!image.equals("default")) {
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.placeholder(R.drawable.ic_student);
                            Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions).load(image).into(photoView);
                        }

                    }else {

                        Toast.makeText(getApplicationContext(),"Please contact admin..",Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK ));
                        finish();

                    }

                }
            });

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {

            startActivity(new Intent(HomeActivity.this,GalleryActivity.class));

        }if (id == R.id.nav_share) {

            // TODO: 31-03-2018 share this app

        } else if (id == R.id.nav_feedback) {

            Toast.makeText(getApplicationContext(),"f",Toast.LENGTH_SHORT).show();
            // TODO: 31-03-2018 feedback

        }else if(id == R.id.nav_logout){

            mAuth.signOut();

            char b = studentModel.getCurrentYear().charAt(0);
            String section = studentModel.getSection()+"-"+b;
            FirebaseMessaging.getInstance().unsubscribeFromTopic(section);

            startActivity(new Intent(HomeActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK ));
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.b_assignments){

            Intent intent = new Intent(HomeActivity.this, AllAssignments.class);

            char b = studentModel.getCurrentYear().charAt(0);
            String section = studentModel.getSection()+"-"+b;

            intent.putExtra("section",section);

            startActivity(intent);

        }else if(view.getId()==R.id.b_timetable){

            Intent intent = new Intent(HomeActivity.this, StudentTimetable.class);

            char b = studentModel.getCurrentYear().charAt(0);
            String section = studentModel.getSection()+"-"+b;

            intent.putExtra("section",section);
            intent.putExtra("branch",studentModel.getStudentBranch());
            intent.putExtra("sem",studentModel.getCurrentSemester());

            startActivity(intent);

        }else if(view.getId()==R.id.b_notes){

            startActivity(new Intent(HomeActivity.this,Notes.class));

        }else if(view.getId()==R.id.b_syllabus){

            Intent intent = new Intent(HomeActivity.this, SyllabusActivity.class);

            intent.putExtra("branch",studentModel.getStudentBranch());
            intent.putExtra("sem",studentModel.getCurrentSemester());
            intent.putExtra("program",studentModel.getProgram());

            startActivity(intent);

        }

    }
}
