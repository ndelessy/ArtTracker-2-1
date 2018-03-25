package edu.mdc.entec.north.arttracker.view.gallery;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.mdc.entec.north.arttracker.R;
import edu.mdc.entec.north.arttracker.model.ArtPiece;


public class ArtPiecesByArtistFragment extends Fragment
      {
    private static final String ART_PIECES = "artPieces";
    private static final String TAG = "ArtPieceListFragment";
    private List<ArtPiece> artPieces;
    private ArtPiecesByArtistAdapter adapter;
    private RecyclerView recyclerView;
    private OnArtPieceByArtistSelectedListener mListener;

    public ArtPiecesByArtistFragment() {
        // Required empty public constructor
    }


    public static ArtPiecesByArtistFragment newInstance(List<ArtPiece> artPieces) {
        ArtPiecesByArtistFragment fragment = new ArtPiecesByArtistFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ART_PIECES, (ArrayList<? extends Parcelable>) artPieces);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artPieces = getArguments().getParcelableArrayList(ART_PIECES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_art_piece_list_no_button, container, false);


        //Lookup the recyclerview
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_art_pieces_no_button);
        // Create adapter passing in the data
        adapter = new ArtPiecesByArtistAdapter(artPieces, mListener, getContext());
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment galleryFragment = this.getParentFragment();
        if (galleryFragment instanceof OnArtPieceByArtistSelectedListener) {
            mListener = (OnArtPieceByArtistSelectedListener) galleryFragment;
        } else {
            Log.d(TAG, "The listener must be implemented in GalleryFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnArtPieceByArtistSelectedListener {
        void onArtPieceByArtistSelected(ArtPiece artPiece);
    }


}