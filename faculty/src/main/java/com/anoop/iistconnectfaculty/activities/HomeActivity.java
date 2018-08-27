package com.anoop.iistconnectfaculty.activities;

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

import com.anoop.iistconnectfaculty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import in.rgpvnotes.alert.myresource.Constants;
import in.rgpvnotes.alert.myresource.activities.BrowseSyllabusActivity;
import in.rgpvnotes.alert.myresource.activities.GalleryActivity;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {


    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseFirestore mDatabase ;
    private DocumentReference studentsReference ;

    private TextView nameT,enrollmentT;
    private ImageView photoView;

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

            studentsReference = mDatabase.collection(Constants.FACULTY_COLLECTION).document(mCurrentUser.getUid());

            studentsReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                    if (documentSnapshot.exists()) {

                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");

                        userSessionEdit = sharedPreferences.edit();
                        userSessionEdit.putString("username", name);
                        userSessionEdit.putString("useremail", email);
                        userSessionEdit.apply();

                        nameT.setText("Hello "+name);
                        enrollmentT.setText(email);


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
    public void onClick(View view) {


        if(view.getId()==R.id.b_assignments){

            startActivity(new Intent(HomeActivity.this,MyClasses.class));

        }else if(view.getId()==R.id.b_timetable){

            Toast.makeText(getApplicationContext(),"t",Toast.LENGTH_SHORT).show();

        }else if(view.getId()==R.id.b_notes){

            startActivity(new Intent(HomeActivity.this,NotesActivity.class));

        }else if(view.getId()==R.id.b_syllabus){

            startActivity(new Intent(HomeActivity.this,BrowseSyllabusActivity.class));

        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
            startActivity(new Intent(HomeActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK ));
            finish();

        }else if(id == R.id.nav_my_class){

            startActivity(new Intent(HomeActivity.this,ClassHomeActivity.class));

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}
