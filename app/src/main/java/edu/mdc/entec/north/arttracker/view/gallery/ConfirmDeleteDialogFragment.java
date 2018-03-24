package edu.mdc.entec.north.arttracker.view.gallery;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;


public class ConfirmDeleteDialogFragment extends DialogFragment {
    private static final String ART_PIECE = "artPiece";
    private ArtPieceWithArtist artPiece;
    private OnDeleteConfirmedListener mListener;

    public ConfirmDeleteDialogFragment() {
        // Required empty public constructor
    }

    public static ConfirmDeleteDialogFragment newInstance(ArtPieceWithArtist ap) {
        ConfirmDeleteDialogFragment fragment = new ConfirmDeleteDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ART_PIECE, ap);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment galleryFragment = this.getParentFragment();

        if (galleryFragment instanceof OnDeleteConfirmedListener) {
            mListener = (OnDeleteConfirmedListener) galleryFragment;
        } else {
            throw new RuntimeException(galleryFragment.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                // set dialog icon
                .setIcon(android.R.drawable.btn_dialog)
                // set Dialog Title
                .setTitle("Important!")
                // Set Dialog Message
                .setMessage("Do you really want to remove this art piece?")

                // positive button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onDeleteConfirmed(artPiece);
                        }
                        dialog.dismiss();
                    }
                })
                // negative button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }




    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed(ArtPieceWithArtist artPiece);
    }
}