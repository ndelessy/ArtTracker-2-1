package edu.mdc.entec.north.arttracker.view.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.mdc.entec.north.arttracker.R;
import edu.mdc.entec.north.arttracker.Utils;
import edu.mdc.entec.north.arttracker.model.ArtPiece;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;

import static edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment.DIRECTORY;
import static edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment.EXTENSION;

public class ArtPiecesByArtistAdapter extends RecyclerView.Adapter<ArtPiecesByArtistAdapter.ViewHolder> {
    private List<ArtPiece> artPieces;
    private static final String TAG = "ArtPieceAdapter";
    private Context context;
    ArtPiecesByArtistFragment.OnArtPieceByArtistSelectedListener mListener;

    public ArtPiecesByArtistAdapter(List<ArtPiece> artPieces, ArtPiecesByArtistFragment.OnArtPieceByArtistSelectedListener mListener, Context context) {
        this.artPieces = artPieces;
        this.mListener = mListener;
        this.context = context;
    }
    // Inflates the row layout and returns a holder
    @Override
    public ArtPiecesByArtistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View artPieceView = inflater.inflate(R.layout.art_piece_row_layout, parent, false);

        return new ViewHolder(artPieceView);
    }

    // Populates data into the item through holder
    @Override
    public void onBindViewHolder(ArtPiecesByArtistAdapter.ViewHolder holder, int position) {
        ArtPiece artPiece = artPieces.get(position);
        holder.nameTextView.setText(artPiece.getName());
        holder.yearTextView.setText(Integer.toString(artPiece.getYear()));
        //holder.imageView.setImageResource(artPiece.getPictureID(context));
        holder.imageView.setImageBitmap(Utils.loadBitmapFromAssets(context, DIRECTORY + "/" + artPiece.getPictureID() + EXTENSION));

    }

    @Override
    public int getItemCount() {
        return artPieces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Fields corresponding to the row layout elements
        public TextView nameTextView;
        public TextView yearTextView;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView3);
            yearTextView = (TextView) itemView.findViewById(R.id.yearTextView3);
            imageView = (ImageView) itemView.findViewById(R.id.imageView3);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Log.d(TAG, "Row or Button for " + artPieces.get(position).getName() + " was clicked");
                if(view == itemView) {
                    mListener.onArtPieceByArtistSelected(artPieces.get(position));
                }
            }

        }

    }
}
