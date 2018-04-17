package edu.mdc.entec.north.arttracker.view.gallery;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.mdc.entec.north.arttracker.R;
import edu.mdc.entec.north.arttracker.Utils;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.view.MainActivity;

import static edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment.DIRECTORY;
import static edu.mdc.entec.north.arttracker.view.gallery.GalleryFragment.EXTENSION;

public class ArtPiecesAdapter extends RecyclerView.Adapter<ArtPiecesAdapter.ViewHolder> {
    private List<ArtPieceWithArtist> artPieces;
    private static final String TAG = "---+ArtPiecesAdapter";
    ArtPiecesFragment.OnArtPieceSelectedListener mListener;
    private Context context;

    public ArtPiecesAdapter(List<ArtPieceWithArtist> artPieces, ArtPiecesFragment.OnArtPieceSelectedListener mListener, Context context) {
        this.artPieces = artPieces;
        this.mListener = mListener;
        this.context = context;
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
        holder.artistTextView.setText(artPiece.getFirstName() + " " + artPiece.getLastName());
        holder.yearTextView.setText(Integer.toString(artPiece.getYear()));
        //holder.imageView.setImageResource(artPiece.getPictureID(context));
        try {
            holder.imageView.setImageBitmap(Utils.loadBitmapFromAssets(context, DIRECTORY + "/" + artPiece.getPictureID() + EXTENSION));
        } catch(Exception e){
        Log.e(TAG, e.getMessage());
        }

        holder.checkBox.setTag(artPiece);
        if(mListener.isSelectableMode()) {
            Log.d(TAG, "mListener.isSelectableMode()="+mListener.isSelectableMode()+"\nmListener.isSelected(artPiece)="+mListener.isSelected(artPiece));
            holder.checkBox.setVisibility(View.VISIBLE);
            if(mListener.isSelected(artPiece)) {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorDarkGray));
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            }
        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
        }

    }

    @Override
    public int getItemCount() {
        return artPieces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener
            , View.OnLongClickListener
            , CompoundButton.OnCheckedChangeListener
        {
        //Fields corresponding to the row layout elements
        public TextView nameTextView;
        public TextView artistTextView;
        public TextView yearTextView;
        public Button checkItOutButton;
        public ImageView imageView;
        public CheckBox checkBox;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            artistTextView = (TextView) itemView.findViewById(R.id.artistTextView);
            yearTextView = (TextView) itemView.findViewById(R.id.yearTextView);
            checkItOutButton = (Button) itemView.findViewById(R.id.checkItOutButton);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            checkBox.setChecked(false);
            checkBox.setOnCheckedChangeListener(this);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            checkItOutButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ArtPieceWithArtist ap = artPieces.get(position);
                if(view == itemView) {
                    mListener.onArtPieceSelected(ap);
                } else { // view == checkItOutButton
                    mListener.onArtistSelected(ap.getArtistID());
                }
            }

        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                ArtPieceWithArtist ap = artPieces.get(position);
                mListener.onArtPieceLongSelected(ap);
                return true;
            }
            return false;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked) {
                mListener.addSelectedItem((ArtPieceWithArtist) checkBox.getTag());
                cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorDarkGray));
            } else {
                mListener.removeSelectedItem((ArtPieceWithArtist) checkBox.getTag());
                cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            }
        }
    }
}
