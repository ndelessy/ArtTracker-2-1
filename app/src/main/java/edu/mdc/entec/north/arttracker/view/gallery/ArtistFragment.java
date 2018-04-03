package edu.mdc.entec.north.arttracker.view.gallery;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.model.Artist;
import edu.mdc.entec.north.arttracker.R;
import edu.mdc.entec.north.arttracker.view.MainActivity;


public class ArtistFragment extends Fragment  {
    private static final String TAG = "--ArtistFragment";

    private static final String ARTIST = "artist";
    private Artist artist;

    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView detailsTextView;


    public ArtistFragment() {
        // Required empty public constructor
    }


    public static ArtistFragment newInstance(Artist artist) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARTIST, artist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artist = getArguments().getParcelable(ARTIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_artist, container, false);

        firstNameTextView = view.findViewById(R.id.firstNameTextView);
        lastNameTextView = view.findViewById(R.id.lastNameTextView);
        detailsTextView = view.findViewById(R.id.detailsTextView);

        firstNameTextView.setText(artist.getFirstName());
        lastNameTextView.setText(artist.getLastName());
        detailsTextView.setText(artist.getDetails());

        return view;
    }

}
