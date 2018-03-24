package edu.mdc.entec.north.arttracker.view.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.mdc.entec.north.arttracker.R;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;

public class ArtPiecesAdapter extends RecyclerView.Adapter<ArtPiecesAdapter.ViewHolder> {
    private List<ArtPieceWithArtist> artPieces;
    private static final String TAG = "ArtPiecesAdapter";
    ArtPiecesFragment.OnArtPieceSelectedListener mListener;

    public ArtPiecesAdapter(List<ArtPieceWithArtist> artPieces, ArtPiecesFragment.OnArtPieceSelectedListener mListener) {
        this.artPieces = artPieces;
        this.mListener = mListener;
    }
    // Inflates the row layout and returns a holder
    @Override
    public ArtPiecesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View artPieceView = inflater.inflate(R.layout.art_piece_with_artist_row_layout, parent, false);

        return new ViewHolder(artPieceView);
    }

    // Populates data into the item through holder
    @Override
    public void onBindViewHolder(ArtPiecesAdapter.ViewHolder holder, int position) {
        ArtPieceWithArtist artPiece = artPieces.get(position);
        holder.nameTextView.setText(artPiece.getName());
        holder.artistTextView.setText(artPiece.getArtist().getFirstName() + " " + artPiece.getArtist().getLastName());
        holder.yearTextView.setText(Integer.toString(artPiece.getYear()));
        holder.imageView.setImageResource(artPiece.getPictureID());
    }

    @Override
    public int getItemCount() {
        return artPieces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        //Fields corresponding to the row layout elements
        public TextView nameTextView;
        public TextView artistTextView;
        public TextView yearTextView;
        public Button checkItOutButton;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            artistTextView = (TextView) itemView.findViewById(R.id.artistTextView);
            yearTextView = (TextView) itemView.findViewById(R.id.yearTextView);
            checkItOutButton = (Button) itemView.findViewById(R.id.checkItOutButton);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            checkItOutButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ArtPieceWithArtist ap = artPieces.get(position);
                Log.d(TAG, "Row or Button for " + artPieces.get(position).getName() + " was clicked");
                if(view == itemView) {
                    mListener.onArtPieceSelected(ap);
                } else { // view == checkItOutButton
                    mListener.onArtistSelected(ap.getArtist());
                }
            }

        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                ArtPieceWithArtist ap = artPieces.get(position);
                Log.d(TAG, "Button for "+artPieces.get(position).getName()+" was looooooong  clicked");
                mListener.onArtPieceLongSelected(ap);
                return true;
            }
            return false;
        }
    }
}
