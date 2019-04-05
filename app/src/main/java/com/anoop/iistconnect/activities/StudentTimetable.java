package com.anoop.iistconnect.activities;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;

import com.anoop.iistconnect.R;
import com.anoop.iistconnect.adapter.TimetableViewPagerAdapter;

import java.util.Calendar;

public class StudentTimetable extends AppCompatActivity {


    private ViewPager mViewPager;

    private TimetableViewPagerAdapter viewPagerAdapter ;

    private String section;

    private String branch ;

    private String sem ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_timetable);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Time Table");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        section = getIntent().getStringExtra("section");

        branch = getIntent().getStringExtra("branch");

        sem = getIntent().getStringExtra("sem");

        viewPagerAdapter = new TimetableViewPagerAdapter(getSupportFragmentManager(),section);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        int day = 0;

        if(dayOfWeek!=1){
            if(dayOfWeek==2)
                day=0;
            if(dayOfWeek==3)
                day=1;
            if(dayOfWeek==4)
                day=2;
            if(dayOfWeek==5)
                day=3;
            if(dayOfWeek==6)
                day=4;
            if(dayOfWeek==7)
                day=5;

            mViewPager.setCurrentItem(day);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
