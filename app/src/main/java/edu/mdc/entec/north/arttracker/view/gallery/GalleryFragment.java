package edu.mdc.entec.north.arttracker.view.gallery;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import edu.mdc.entec.north.arttracker.GalleryContract;
import edu.mdc.entec.north.arttracker.model.ArtPiece;
import edu.mdc.entec.north.arttracker.model.Artist;
import edu.mdc.entec.north.arttracker.presenter.GalleryPresenter;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.R;

public class GalleryFragment extends Fragment
        implements GalleryContract.View
                    , ArtPiecesFragment.OnArtPieceSelectedListener
                    , ConfirmDeleteDialogFragment.OnDeleteConfirmedListener
                    , ArtPiecesByArtistFragment.OnArtPieceByArtistSelectedListener {

    private static final String TAG = "--GalleryFragment";

    private static final int SHOWING_ART_PIECE = 1;
    private static final int SHOWING_ARTIST = 2;
    private static final int SHOWING_ART_PIECE_BY_ARTIST = 3;

    private GalleryContract.Presenter galleryPresenter;

    //UI state
    private boolean isLandscape;
    private boolean showingList;
    private int showing;

    private ArtPiece artPieceByArtist;
    private ArtPieceWithArtist artPiece;
    private Artist artist;
    private List<ArtPieceWithArtist> artPieces;
    private List<ArtPiece> artPiecesByArtist;

    //Ui element
    private ProgressBar progressBar;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "-------------------------In the onActivityCreated() method");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {//called when orientation changes but not when another tab is pressed
        Log.d(TAG, "-------------------------In the onCreate() method");
        galleryPresenter = new GalleryPresenter(getContext(), this);
        setRetainInstance(true);
        showingList = true;
        showing = SHOWING_ART_PIECE;
        super.onCreate(savedInstanceState);//other nested frags are attached and created
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        Log.d(TAG, "-------------------------In the onCreateView() method");
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);


        if(view.findViewById(R.id.container2) != null){
            isLandscape = true;
        } else {
            isLandscape = false;
        }

        if(savedInstanceState != null) {
            artPieceByArtist = savedInstanceState.getParcelable("artPieceByArtist");
            artPiece = savedInstanceState.getParcelable("artPiece");
            artist = savedInstanceState.getParcelable("artist");
            artPieces = savedInstanceState.getParcelableArrayList("artPieces");
            artPiecesByArtist = savedInstanceState.getParcelableArrayList("artPiecesByArtist");

            showingList = savedInstanceState.getBoolean("showingList");
            showing = savedInstanceState.getInt("showing");
        } else {//savedInstanceState is null but instance was retained...Nothing to do

        }

        galleryPresenter.start();
        return view;
    }

    public void setPresenter(GalleryContract.Presenter galleryPresenter) {
        this.galleryPresenter = galleryPresenter;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "-------------------------In the onAttach() method");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "-------------------------In the onStart() method");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "-------------------------In the onResume() method");
        super.onResume();
    }

    //UI logic
    @Override
    public void onArtPieceSelected(ArtPieceWithArtist ap) {
        showingList = false;
        showing = SHOWING_ART_PIECE;
        artPiece = ap;

        startFragments();
    }

    @Override
    public void onArtPieceLongSelected(ArtPieceWithArtist ap) {
        DialogFragment newFragment = ConfirmDeleteDialogFragment.newInstance(ap);
        newFragment.show(getChildFragmentManager(), "confirmDeleteArtPiece");
    }

    @Override
    public void onArtistSelected(Artist at) {
        showingList = false;
        showing = SHOWING_ARTIST;
        artist = at;

        galleryPresenter.showArtist(at);

    }

    @Override
    public void onArtPieceByArtistSelected(ArtPiece ap) {
        showingList = false;
        showing = SHOWING_ART_PIECE_BY_ARTIST;
        artPieceByArtist = ap;

        startFragments();
    }

    /**
     * @return true = if this fragment can handle the backPress
     */
    public boolean onBackPressed() {
        if(isLandscape){
            if (showing == SHOWING_ART_PIECE_BY_ARTIST) {
                getChildFragmentManager().popBackStackImmediate("artPieceByArtistFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                showing = SHOWING_ARTIST;
                return true;
            } else {
                return false;
            }
        } else { // portrait
            if (!showingList) {
                if (showing == SHOWING_ART_PIECE) {
                    getChildFragmentManager().popBackStackImmediate("artPieceFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    showingList = true;
                    return true;
                } else if (showing == SHOWING_ARTIST) {
                    getChildFragmentManager().popBackStackImmediate("artPiecesByArtistFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getChildFragmentManager().popBackStackImmediate("artistFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    showingList = true;
                    return true;
                } else if (showing == SHOWING_ART_PIECE_BY_ARTIST) {
                    getChildFragmentManager().popBackStackImmediate("artPieceByArtistFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    showing = SHOWING_ARTIST;
                    return true;
                } else {
                    return false;
                }
            } else { //portrait and showing list
                return false;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "-------------------------In the onSaveInstanceState() method");
        super.onSaveInstanceState(outState);
        outState.putParcelable("artPiece", artPiece);
        outState.putParcelable("artPieceByArtist", artPieceByArtist);
        outState.putParcelable("artist", artist);
        outState.putParcelableArrayList("artPieces", (ArrayList<? extends Parcelable>) artPieces);
        outState.putParcelableArrayList("artPiecesByArtist", (ArrayList<? extends Parcelable>) artPiecesByArtist);

        outState.putBoolean("showingList", showingList);
        outState.putInt("showing", showing);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "-------------------------In the onPause() method");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "-------------------------In the onStop() method");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "-------------------------In the onDestroyView() method");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "-------------------------In the onDestroy() method");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "-------------------------In the onDetach() method");
    }
    
    // Commands to presenters
    public void setLoadingIndicator(boolean loading) {
        if (loading)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDeleteConfirmed(ArtPieceWithArtist artPiece) {
        galleryPresenter.deleteArtPiece(artPiece);
    }

    //Update UI

    public void showArtPieceDeleted(ArtPieceWithArtist artPiece) {
        ArtPiecesFragment artPieceListFragment =
                (ArtPiecesFragment) getChildFragmentManager()
                .findFragmentByTag("artPiecesFragment");
        artPieceListFragment.showDeleted(artPiece);
    }

    public void showArtPieces(List<ArtPieceWithArtist> artPieces) {
        this.artPieces = artPieces;
        if (artPiece == null)
            artPiece = artPieces.get(0);

        startFragments();
        progressBar.setVisibility(View.GONE);
    }

    public void showArtist(Artist at, List<ArtPiece> artPiecesByArtist) {
        showingList = false;
        showing = SHOWING_ARTIST;
        artist = at;
        this.artPiecesByArtist = artPiecesByArtist;

        startFragments();
        progressBar.setVisibility(View.GONE);
    }

    ////////////////////////// private methods ///////////////////////////////////////

    private void startFragments() {
        if(isLandscape){
            startArtPiecesFragment(artPieces, R.id.container);
            if(showing == SHOWING_ART_PIECE){
                startArtPieceFragment(artPiece, R.id.container2);
            } else if(showing == SHOWING_ARTIST){
                startArtistFragment(artist, R.id.container2);
                startArtPiecesByArtistFragment(artPiecesByArtist, R.id.containerArtPieces);
            } else if(showing == SHOWING_ART_PIECE_BY_ARTIST){
                startArtPieceByArtistFragment(artPieceByArtist, R.id.containerArtPieces);
            } else {
            }
        } else { //portrait
            if(showingList){
                startArtPiecesFragment(artPieces, R.id.container);
            } else {
                if(showing == SHOWING_ART_PIECE){
                    startArtPieceFragment(artPiece, R.id.container);
                } else if(showing == SHOWING_ARTIST){
                    startArtistFragment(artist, R.id.container);
                    startArtPiecesByArtistFragment(artPiecesByArtist, R.id.containerArtPieces);
                } else if(showing == SHOWING_ART_PIECE_BY_ARTIST){
                    startArtPieceByArtistFragment(artPieceByArtist, R.id.containerArtPieces);
                } else {
                }
            }
        }
    }

    private void startArtPiecesByArtistFragment(List<ArtPiece> artPiecesByArtist, int containerArtPieces) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        ArtPiecesByArtistFragment artPiecesByArtistFragment = ArtPiecesByArtistFragment.newInstance(artPiecesByArtist);
        transaction.replace(containerArtPieces, artPiecesByArtistFragment, "artPiecesByArtistFragment");
        transaction.addToBackStack("artPiecesByArtistFragment");
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    private void startArtPiecesFragment(List<ArtPieceWithArtist> artPieces, int container) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ArtPiecesFragment artPiecesFragment = ArtPiecesFragment.newInstance(artPieces);
        ft.replace(container, artPiecesFragment, "artPiecesFragment");
        ft.addToBackStack("artPiecesFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void startArtPieceFragment(ArtPieceWithArtist ap, int container) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        ArtPieceFragment artPieceFragment = ArtPieceFragment.newInstance(ap);
        transaction.replace(container, artPieceFragment, "artPieceFragment");
        transaction.addToBackStack("artPieceFragment");
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    private void startArtPieceByArtistFragment(ArtPiece ap, int containerArtPieces) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        ArtPieceByArtistFragment artPieceByArtistFragment = ArtPieceByArtistFragment.newInstance(ap);
        transaction.replace(containerArtPieces, artPieceByArtistFragment, "artPieceByArtistFragment");
        transaction.addToBackStack("artPieceByArtistFragment");
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    private void startArtistFragment(Artist artist, int container) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        ArtistFragment artistFragment = ArtistFragment.newInstance(artist);
        transaction.replace(container, artistFragment, "artistFragment");
        transaction.addToBackStack("artistFragment");
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }
}