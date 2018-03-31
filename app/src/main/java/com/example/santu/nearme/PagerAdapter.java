package com.example.santu.nearme;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private Fragment results;
    private CharSequence c;

    public PagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                results = new MapFragment();
                break;
            case 1:
                results = new EventsFragment();
                break;
            case 2:
                results = new ArtistsFragment();
                break;
            case 3:
                results = new PubsFragment();
                break;
        }
        return results;
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 4;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                c = "Mappa";
                break;
            case 1:
                c = "Eventi";
                break;
            case 2:
                c = "Artisti";
                break;
            case 3:
                c = "Locali";

                break;
        }
        return c;
    }

}