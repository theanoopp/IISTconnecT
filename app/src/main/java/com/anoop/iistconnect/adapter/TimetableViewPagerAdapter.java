package com.anoop.iistconnect.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.anoop.iistconnect.fragments.TimetableFragment;

/**
 * Created by Anoop on 08-03-2018.
 */

public class TimetableViewPagerAdapter extends FragmentPagerAdapter {

    private String section;


    public TimetableViewPagerAdapter(FragmentManager fm, String section) {
        super(fm);
        this.section=section;
    }



    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position){

            case 0: return TimetableFragment.newInstance(position + 1,section,"monday");
            case 1: return TimetableFragment.newInstance(position + 1,section,"tuesday");
            case 2: return TimetableFragment.newInstance(position + 1,section,"wednesday");
            case 3: return TimetableFragment.newInstance(position + 1,section,"thursday");
            case 4: return TimetableFragment.newInstance(position + 1,section,"friday");
            case 5: return TimetableFragment.newInstance(position + 1,section,"saturday");

        }

        return TimetableFragment.newInstance(position + 1,section,"monday");
    }





    @Override
    public int getCount() {
        return 6;
    }

}
