package edu.mdc.entec.north.arttracker.contract;

import java.util.List;

import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.presenter.BasePresenter;
import edu.mdc.entec.north.arttracker.view.BaseView;

public interface MapContract {

    interface View extends BaseView<Presenter> {

        void showArtPiecesOnMap(List<ArtPieceWithArtist> artPieces);

    }

    interface Presenter extends BasePresenter {

    }
}
