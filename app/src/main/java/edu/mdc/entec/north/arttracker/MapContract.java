package edu.mdc.entec.north.arttracker;

import java.util.List;

import edu.mdc.entec.north.arttracker.model.ArtPiece;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.model.Artist;

public interface MapContract {

    interface View extends BaseView<Presenter>{

        void showArtPiecesOnMap(List<ArtPieceWithArtist> artPieces);

    }

    interface Presenter extends BasePresenter {

    }
}
