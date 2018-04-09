package edu.mdc.entec.north.arttracker.contract;

import java.util.List;

import edu.mdc.entec.north.arttracker.model.ArtPiece;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.model.Artist;
import edu.mdc.entec.north.arttracker.presenter.BasePresenter;
import edu.mdc.entec.north.arttracker.view.BaseView;

public interface GalleryContract {

    interface View extends BaseView<Presenter> {

        void showArtPieceDeleted(ArtPieceWithArtist artPiece);

        void showArtPieces(List<ArtPieceWithArtist> artPieces);

        void showArtist(Artist artist, List<ArtPiece> artPiecesByArtist);
    }

    interface Presenter extends BasePresenter {

        void showArtist(int artistID);

        void deleteArtPiece(ArtPieceWithArtist artPiece);
    }
}
