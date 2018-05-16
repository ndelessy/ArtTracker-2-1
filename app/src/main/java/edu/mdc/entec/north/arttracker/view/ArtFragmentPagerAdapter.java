package edu.mdc.entec.north.arttracker.view;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment;
import edu.mdc.entec.north.arttracker.view.map.MapFragment;
import edu.mdc.entec.north.arttracker.view.quiz.QuizFragment;

public class ArtFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArtPieceWithArtist artPiece;
    private int showing;
    private boolean showingList;
    private Fragment tab1;
    //private Fragment tab2;
    private Fragment tab3;

    public ArtFragmentPagerAdapter(FragmentManager fm, boolean showingList, int showing, ArtPieceWithArtist artPiece) {
        super(fm);
        this.artPiece = artPiece;
        this.showing = showing;
        this.showingList = showingList;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if(tab1 == null) {
                    tab1 = GalleryFragment.newInstance(showingList, showing, artPiece);
                }
                return tab1;
            /*
            case 1:
                if(tab2 == null) {
                    tab2 = new QuizFragment();
                }
                return tab2;
            */
            case 1:
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
        //return 3;
        return 2;
    }
}