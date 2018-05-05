package edu.mdc.entec.north.arttracker.presenter;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import edu.mdc.entec.north.arttracker.contract.MapContract;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.model.roomdb.AppDatabase;


public class MapPresenter implements MapContract.Presenter {

    private static final String TAG = "--MapPresenter";

    //model
    private AppDatabase mDb;

    // view
    private MapContract.View mapView;


    public MapPresenter(final Context context, MapContract.View mapView) {
        this.mapView = mapView;
        mDb = AppDatabase.getInstance(context);
    }


    //Set by MainActivity
    public void setView(MapContract.View mapView) {
        this.mapView = mapView;
        mapView.setPresenter(this);
    }

    public void start() {
        new AsyncTask<Void, Void, List<ArtPieceWithArtist>>() {
            @Override
            protected List<ArtPieceWithArtist> doInBackground(Void... params) {
                return mDb.artPieceModel().findAllArtPiecesWithArtistSync();
            }

            @Override
            protected void onPostExecute(List<ArtPieceWithArtist> artPieces) {
                mapView.showArtPiecesOnMap(artPieces);
            }
        }.execute();
    }

}
