package edu.mdc.entec.north.arttracker.view.gallery;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment;
import edu.mdc.entec.north.arttracker.view.map.MapFragment;
import edu.mdc.entec.north.arttracker.view.quiz.QuizFragment;

public class ArtFragmentPagerAdapter extends FragmentPagerAdapter {
    public ArtFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    Fragment tab1;
    Fragment tab2;
    Fragment tab3;

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if(tab1 == null) {
                    tab1 = new GalleryFragment();
                }
                return tab1;
            case 1:
                if(tab2 == null) {
                    tab2 = new QuizFragment();
                }
                return tab2;
            case 2:
                if(tab3 == null) {
                    tab3 = new MapFragment();
                }
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}