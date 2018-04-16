package edu.mdc.entec.north.arttracker.view.gallery;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.mdc.entec.north.arttracker.contract.GalleryContract;
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


    private static final String TAG = "---+GalleryFragment";

    public static final String DIRECTORY = "images";
    public static final String EXTENSION = ".png";

    public static final int SHOWING_ART_PIECE = 1;
    public static final int SHOWING_ARTIST = 2;
    public static final int SHOWING_ART_PIECE_BY_ARTIST = 3;

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
    private ActionMode.Callback actionModeCallbacks;
    private  ActionMode actionMode;

    private ArrayList<Uri> sharedFileUris;

    private ArrayList<ArtPieceWithArtist> selectedItems = new ArrayList<ArtPieceWithArtist>();

    //Ui element
    private ProgressBar progressBar;
    private boolean isSelectableMode;

    public static Fragment newInstance(boolean showingList, int showing, ArtPieceWithArtist artPiece) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putParcelable("ART_PIECE", artPiece);
        args.putInt("SHOWING", showing);
        args.putBoolean("SHOWING_LIST", showingList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "-------------------------In the onActivityCreated() method");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {//called when orientation changes but not when another tab is pressed
        Log.d(TAG, "-------------------------In the onCreate() method");

        super.onCreate(savedInstanceState);//other nested frags are attached and created
        if (getArguments() != null) {
            artPiece = getArguments().getParcelable("ART_PIECE");
            showingList = getArguments().getBoolean("SHOWING_LIST");
            showing = getArguments().getInt("SHOWING");
        } else{
            showingList = true;
            showing = SHOWING_ART_PIECE;
        }
        galleryPresenter = new GalleryPresenter(getContext(), this);
        setRetainInstance(true);

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
            //?
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
        stopActionMode();
        showingList = false;
        showing = SHOWING_ART_PIECE;
        artPiece = ap;

        startFragments();
    }

    @Override
    public void onArtPieceLongSelected(ArtPieceWithArtist ap) {
        selectedItems.add(ap);
        actionModeCallbacks = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.action_mode_menu, menu);
                isSelectableMode = true;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        shareArtPieces();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    case R.id.action_delete:
                        deleteArtPieces();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                isSelectableMode = false;
                selectedItems.clear();
                ArtPiecesFragment fg = (ArtPiecesFragment) getChildFragmentManager().findFragmentByTag("artPiecesFragment");
                if(fg != null){
                    fg.getAdapter().notifyDataSetChanged();
                }
            }
        };

        actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallbacks);
        ArtPiecesFragment fg = (ArtPiecesFragment) getChildFragmentManager().findFragmentByTag("artPiecesFragment");
        if(fg != null){
            fg.getAdapter().notifyDataSetChanged();
        }


    }

    private void deleteArtPieces() {
        for (ArtPieceWithArtist artPieceWithArtist : selectedItems) {
            DialogFragment newFragment = ConfirmDeleteDialogFragment.newInstance(artPieceWithArtist);
            newFragment.show(getChildFragmentManager(), "confirmDeleteArtPiece");
        }
    }

    private void shareArtPieces() {
        InputStream inputStream;
        File sharedFilePath;
        File sharedFile = null;
        FileOutputStream outputStream;
        sharedFileUris = new ArrayList<>();

        for (ArtPieceWithArtist artPieceWithArtist : selectedItems) {
            Log.d(TAG, "copying artPieceWithArtist = " + artPieceWithArtist.getName());
            try {
                // Copy file from Assets to app's internal storage (images folder)
                inputStream = getActivity().getAssets().open(DIRECTORY + "/" + artPieceWithArtist.getPictureID() + EXTENSION);

                sharedFilePath = new File(getActivity().getFilesDir(), DIRECTORY);
                sharedFilePath.mkdirs();
                sharedFile = new File(sharedFilePath, artPieceWithArtist.getPictureID() + EXTENSION);

                outputStream = new FileOutputStream(sharedFile, false);

                byte[] buffer = new byte[8192];
                int length;
                while ((length = inputStream.read(buffer, 0, 8192)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

                // To securely offer a file from your app to another app, you need to configure your app to offer a secure handle to the file,
                // in the form of a content URI.
                // The Android FileProvider (part of the v4 Support Library) component generates content URIs for files, based on specifications you provide in XML.
                // Defining a FileProvider for your app requires an entry in your manifest.
                // shares directories within the files/ DIRECTORY of your app's internal storage

                Uri sharedFileUri = FileProvider.getUriForFile(getActivity(), "edu.mdc.entec.north.arttracker.fileprovider", sharedFile);
                sharedFileUris.add(sharedFileUri);
                // URI should be content://edu.mdc.entec.north.arttracker.fileprovider/images/arcos_sound.png
                Log.d(TAG, "sharedFileUri=" + sharedFileUri.toString());

            } catch (FileNotFoundException e) {
                Log.w("Warning", "The file was not found");
            } catch (IOException e) {
                Log.w("Warning", "Error reading the file ");
            }
        }


        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);

        shareIntent.putExtra(Intent.EXTRA_TEXT, R.string.share_text);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.share_text);
        List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for(Uri sharedFileUri: sharedFileUris) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    getActivity().grantUriPermission(packageName, sharedFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
        }
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, sharedFileUris);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(Intent.createChooser(shareIntent, "Share via..."));
        }
    }

    @Override
    public void onArtistSelected(int artistID) {
        stopActionMode();
        galleryPresenter.showArtist(artistID);
        showingList = false;
        showing = SHOWING_ARTIST;
    }

    @Override
    public boolean isSelectableMode() {
        return isSelectableMode;
    }

    @Override
    public boolean isSelected(ArtPieceWithArtist artPieceWithArtist) {
        return  selectedItems.contains(artPieceWithArtist);
    }

    @Override
    public void removeSelectedItem(ArtPieceWithArtist artPieceWithArtist) {
        selectedItems.remove(artPieceWithArtist);
    }

    @Override
    public void addSelectedItem(ArtPieceWithArtist artPieceWithArtist) {
        if(!selectedItems.contains(artPieceWithArtist))
        selectedItems.add(artPieceWithArtist);
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
                    Fragment fg = getChildFragmentManager().findFragmentByTag("artPiecesFragment");
                    if(fg == null){
                        startFragments();
                    }
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
        stopActionMode();
        Log.d(TAG, "-------------------------In the onPause() method");
    }

    private void stopActionMode(){
        if (actionMode != null) {
            actionMode.finish();
        }
        isSelectableMode = false;
        selectedItems.clear();
        ArtPiecesFragment fg = (ArtPiecesFragment) getChildFragmentManager().findFragmentByTag("artPiecesFragment");
        if(fg != null){
            fg.getAdapter().notifyDataSetChanged();
        }
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
        if(sharedFileUris!= null) {
            for (Uri sharedFileUri : sharedFileUris) {
                if (sharedFileUri != null)
                    getActivity().revokeUriPermission(sharedFileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
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
        Log.d(TAG, "in showArtPieces"+artPieces.size());
        this.artPieces = artPieces;
        if (artPiece == null && artPieces.size() != 0)
            artPiece = artPieces.get(0);

        startFragments();
        progressBar.setVisibility(View.GONE);
    }

    public void showArtist(Artist at, List<ArtPiece> artPiecesByArtist) {
        Log.d(TAG, "Artist = "+ at);
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
                Log.d(TAG, "Showing list of "+artPieces.size());
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

    public void setShowing(int showing) {
        this.showing = showing;
    }

    public void setArtPiece(ArtPieceWithArtist artPiece) {
        this.artPiece = artPiece;
    }

    public void setShowingList(boolean showingList) {
        this.showingList = showingList;
    }


}