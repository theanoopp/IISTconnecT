package com.anoop.iistconnectfaculty.activities;


import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;

import com.anoop.iistconnectfaculty.R;
import com.anoop.iistconnectfaculty.fragments.MyNotesFragment;
import com.anoop.iistconnectfaculty.fragments.MyUploadsFragment;

import in.rgpvnotes.alert.myresource.activities.BrowseNotes;
import in.rgpvnotes.alert.myresource.activities.SubmitNotesActivity;
import in.rgpvnotes.alert.myresource.utils.Constants;

public class NotesActivity extends AppCompatActivity implements MyNotesFragment.MyNotesListener,MyUploadsFragment.MyUploadsListener {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Notes");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    @Override
    public void myNotesInteraction() {

        Intent notesIntent = new Intent(NotesActivity.this,BrowseNotes.class);

        notesIntent.putExtra("user_type", Constants.FACULTY_COLLECTION);

        startActivity(notesIntent);

    }

    @Override
    public void myUploadInteraction() {

        startActivity(new Intent(NotesActivity.this,SubmitNotesActivity.class));

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0){
                return MyNotesFragment.newInstance();
            }else {
                return MyUploadsFragment.newInstance();
            }

        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
