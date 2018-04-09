package edu.mdc.entec.north.arttracker.presenter;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import edu.mdc.entec.north.arttracker.contract.GalleryContract;
import edu.mdc.entec.north.arttracker.model.ArtPiece;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.model.Artist;
import edu.mdc.entec.north.arttracker.model.db.AppDatabase;


public class GalleryPresenter implements GalleryContract.Presenter {

    private static final String TAG = "--GalleryPresenter";

    //model
    private AppDatabase mDb;

    // view
    private GalleryContract.View galleryView;


    public GalleryPresenter(final Context context, GalleryContract.View galleryView) {
        this.galleryView = galleryView;
        mDb = AppDatabase.getInstance(context);
    }


    //Set by MainActivity
    public void setView(GalleryContract.View galleryView) {
        this.galleryView = galleryView;
        galleryView.setPresenter(this);
    }

    public void deleteArtPiece(final ArtPieceWithArtist artPiece){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mDb.artPieceModel().deleteArtPiece(artPiece.getArtPieceID());
                return null;
            }

            @Override
            protected void onPostExecute(Void voids) {
                galleryView.showArtPieceDeleted(artPiece);
            }
        }.execute();
    }

    public void start() {
        new AsyncTask<Void, Void, List<ArtPieceWithArtist>>() {
            @Override
            protected List<ArtPieceWithArtist> doInBackground(Void... params) {
                return mDb.artPieceModel().findAllArtPiecesWithArtistSync();
            }

            @Override
            protected void onPostExecute(List<ArtPieceWithArtist> artPieces) {
                galleryView.showArtPieces(artPieces);
            }
        }.execute();


    }

    public void showArtist(final int artistID){
        new AsyncTask<Void, Void, List<ArtPiece>>() {
            @Override
            protected List<ArtPiece> doInBackground(Void... params) {
                return mDb.artPieceModel().findAllArtPiecesByArtistIDSync(artistID);
            }

            @Override
            protected void onPostExecute(final List<ArtPiece> artPiecesByArtist) {

                new AsyncTask<Void, Void, Artist>() {
                    @Override
                    protected Artist doInBackground(Void... params) {
                        return mDb.artistModel().loadArtistByID(artistID);
                    }

                    @Override
                    protected void onPostExecute(Artist artist) {
                        galleryView.showArtist(artist, artPiecesByArtist);
                    }
                }.execute();
            }
        }.execute();
    }
}
