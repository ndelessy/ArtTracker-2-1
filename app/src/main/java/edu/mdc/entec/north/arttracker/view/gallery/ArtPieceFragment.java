package edu.mdc.entec.north.arttracker.view.gallery;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.R;


public class ArtPieceFragment extends Fragment {
    private static final String ART_PIECE = "artPiece";
    private ArtPieceWithArtist artPiece;


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
        View view =  inflater.inflate(R.layout.fragment_art_piece, container, false);

        TextView nameTextView = view.findViewById(R.id.nameTextView2);
        TextView artistTextView = view.findViewById(R.id.artistTextView2);
        TextView yearTextView = view.findViewById(R.id.yearTextView2);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);
        ImageView imageView = view.findViewById(R.id.large_pic);
        nameTextView.setText(artPiece.getName());
        artistTextView.setText(artPiece.getArtist().getFirstName() + " " + artPiece.getArtist().getLastName());
        yearTextView.setText(Integer.toString(artPiece.getYear()));
        imageView.setImageResource(artPiece.getPictureID());
        descriptionTextView.setText(artPiece.getDescription());
        return view;
    }

}




