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

import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.R;
import edu.mdc.entec.north.arttracker.model.Artist;


public class ArtPiecesFragment extends Fragment  {
    private static final String TAG = "ArtPieceListFragment";

    private static final String ART_PIECES = "artPieces";
    private ArtPiecesAdapter adapter;
    private RecyclerView recyclerView;
    private OnArtPieceSelectedListener mListener;

    private List<ArtPieceWithArtist> artPieces;

    public ArtPiecesFragment() {
        // Required empty public constructor
    }

          public static ArtPiecesFragment newInstance(List<ArtPieceWithArtist> artPieces) {
              ArtPiecesFragment fragment = new ArtPiecesFragment();
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
        View view = inflater.inflate(R.layout.fragment_art_piece_list, container, false);


        //Lookup the recyclerview
        recyclerView = view.findViewById(R.id.rv_art_pieces);
        // Create adapter passing in the data
        adapter = new ArtPiecesAdapter(artPieces, mListener, getContext());
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
        if (galleryFragment instanceof OnArtPieceSelectedListener) {
            mListener = (OnArtPieceSelectedListener) galleryFragment;
        } else {
            Log.d(TAG, "The listener must be implemented in GalleryFragment");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void showDeleted(ArtPieceWithArtist artPiece) {
        artPieces.remove(artPiece);
        getAdapter().notifyDataSetChanged();
    }

    public void addArtPiece(ArtPieceWithArtist artPieceWithArtist){
        artPieces.add(artPieceWithArtist);
    }

    public interface OnArtPieceSelectedListener {
        void onArtPieceSelected(ArtPieceWithArtist artPieceWithArtist);
        void onArtPieceLongSelected(ArtPieceWithArtist artPieceWithArtist);
        void onArtistSelected(int artistID);
        boolean isSelectableMode();
        boolean isSelected(ArtPieceWithArtist artPieceWithArtist);

        void removeSelectedItem(ArtPieceWithArtist artPieceWithArtist);

        void addSelectedItem(ArtPieceWithArtist artPieceWithArtist);
    }

    public ArtPiecesAdapter getAdapter() {
        return adapter;
    }
}