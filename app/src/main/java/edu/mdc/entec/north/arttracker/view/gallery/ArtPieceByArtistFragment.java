package edu.mdc.entec.north.arttracker.view.gallery;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.mdc.entec.north.arttracker.R;
import edu.mdc.entec.north.arttracker.model.ArtPiece;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;


public class ArtPieceByArtistFragment extends Fragment {
    private static final String ART_PIECE = "artPiece";
    private ArtPiece artPiece;


    public ArtPieceByArtistFragment() {
        // Required empty public constructor
    }


    public static ArtPieceByArtistFragment newInstance(ArtPiece artPiece) {
        ArtPieceByArtistFragment fragment = new ArtPieceByArtistFragment();
        Bundle args = new Bundle();
        args.putParcelable(ART_PIECE, artPiece);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artPiece = getArguments().getParcelable(ART_PIECE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_art_piece_by_artist, container, false);

        TextView nameTextView = view.findViewById(R.id.nameTextView4);
        TextView yearTextView = view.findViewById(R.id.yearTextView4);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView4);
        ImageView imageView = view.findViewById(R.id.large_pic4);
        nameTextView.setText(artPiece.getName());
        yearTextView.setText(Integer.toString(artPiece.getYear()));
        imageView.setImageResource(artPiece.getPictureID());
        descriptionTextView.setText(artPiece.getDescription());
        return view;
    }

}



