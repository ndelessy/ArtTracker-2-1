package edu.mdc.entec.north.arttracker.view.gallery;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import edu.mdc.entec.north.arttracker.utils.Config;
import edu.mdc.entec.north.arttracker.model.Artist;
import edu.mdc.entec.north.arttracker.R;


public class ArtistFragment extends Fragment
        implements YouTubePlayer.OnInitializedListener {
    private static final String TAG = "--ArtistFragment";
    public static final int YOUTUBE_RECOVERY_REQUEST = 123;

    private static final String ARTIST = "artist";
    private Artist artist;

    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView detailsTextView;

    private YouTubePlayerSupportFragment youtubeFragment;

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

        youtubeFragment = (YouTubePlayerSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.youtubeFragment);
        if(artist.getYoutubeVideoID() != null) {


            if (youtubeFragment == null) {
                youtubeFragment = YouTubePlayerSupportFragment.newInstance();
                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.youtubeFragment, youtubeFragment)
                        .commit();
            }

            //Requires Developer Key to work.
            youtubeFragment.initialize(Config.YOUTUBE_API_KEY, this);

        } else {
            if (youtubeFragment != null) {
                getChildFragmentManager()
                        .beginTransaction()
                        .hide(youtubeFragment)
                        .commit();
            }
        }
        return view;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer, boolean wasRestored) {
        Log.d(TAG, "video = " + artist.getYoutubeVideoID());
        if (!wasRestored) {
            youTubePlayer.cueVideo(artist.getYoutubeVideoID()); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        Log.d(TAG, "failure = ");
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(getActivity(), YOUTUBE_RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            try {
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            } catch (Exception e){
                Log.e(TAG, "Error");
                e.printStackTrace();
            }
        }
    }



    public YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youtubeFragment;
    }
}
