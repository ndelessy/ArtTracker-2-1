package edu.mdc.entec.north.arttracker.view.gallery;


import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import edu.mdc.entec.north.arttracker.utils.Utils;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.R;

import static edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment.DIRECTORY;
import static edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment.EXTENSION;


public class ArtPieceFragment extends Fragment {
    private static final String ART_PIECE = "artPiece";

    private final static String TAG = "ArtPieceFragment";
    private ArtPieceWithArtist artPiece;
    private Context context;
    private MediaPlayer audioPlayer;
    private boolean audioPlayerIsOn;

    private VideoView videoView;
    private int position = 0;
    private MediaController mediaController;



    public ArtPieceFragment() {
        // Required empty public constructor
    }


    public static ArtPieceFragment newInstance(ArtPieceWithArtist artPiece) {
        ArtPieceFragment fragment = new ArtPieceFragment();
        Bundle args = new Bundle();
        args.putParcelable(ART_PIECE, artPiece);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artPiece = getArguments().getParcelable(ART_PIECE);

        }
        if(savedInstanceState != null){
            position = savedInstanceState.getInt("Position");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_art_piece, container, false);

        TextView nameTextView = view.findViewById(R.id.nameTextView2);
        TextView artistTextView = view.findViewById(R.id.artistTextView2);
        TextView yearTextView = view.findViewById(R.id.yearTextView2);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);
        ImageView imageView = view.findViewById(R.id.large_pic);
        nameTextView.setText(artPiece.getName());
        artistTextView.setText(artPiece.getFirstName() + " " + artPiece.getLastName());
        yearTextView.setText(Integer.toString(artPiece.getYear()));
        //imageView.setImageResource(artPiece.getPictureID(context));
        imageView.setImageBitmap(Utils.loadBitmapFromAssets(getContext(), DIRECTORY + "/" + artPiece.getPictureID() + EXTENSION));
        descriptionTextView.setText(artPiece.getDescription());


        int audioID = getActivity().getResources().getIdentifier( artPiece.getPictureID()+"_sound", "raw", getActivity().getPackageName());
        if(audioID != 0 ) {
            audioPlayer = MediaPlayer.create(getActivity(), audioID);
            audioPlayer.start();
            audioPlayerIsOn = true;
        }
        final ImageButton volumeButton = (ImageButton) view.findViewById(R.id.volumeButton);
        if(audioID != 0 ) {
            volumeButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (audioPlayerIsOn) {
                        volumeButton.setImageResource(R.drawable.ic_action_volume_on);
                        audioPlayer.pause();
                    } else {
                        volumeButton.setImageResource(R.drawable.ic_action_volume_off);
                        audioPlayer.start();
                    }
                    audioPlayerIsOn = !audioPlayerIsOn;
                }
            });
        } else {
            volumeButton.setVisibility(View.GONE);
        }



        videoView = (VideoView) view.findViewById(R.id.videoView);
        int videoID = getActivity().getResources().getIdentifier( artPiece.getPictureID()+"_video", "raw", getActivity().getPackageName());
        Log.d(TAG, "videoID = "+ videoID);
        if(videoID != 0 ) {
            if (mediaController == null) {
                mediaController = new MediaController(getActivity());
            }
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + videoID));
            videoView.start();
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    videoView.seekTo(position);
                    if (position == 0) {
                        videoView.start();
                    } else {
                        //if we come from a resumed activity, video playback will be paused
                        //videoView.pause();
                        videoView.seekTo(position);
                    }
                }
            });
        } else {
            videoView.setVisibility(View.GONE);
        }




        Log.d("HERE", "videoView.isPlaying() = "+ videoView.isPlaying());
        return view;
    }



    @Override
    public void onPause() {
        if(audioPlayer != null) {
            audioPlayer.pause();
            audioPlayerIsOn = false;
        }
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("Position", videoView.getCurrentPosition());
        videoView.pause();
    }


}

