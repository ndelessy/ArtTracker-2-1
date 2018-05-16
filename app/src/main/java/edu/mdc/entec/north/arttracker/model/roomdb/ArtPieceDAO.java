package edu.mdc.entec.north.arttracker.model.roomdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import edu.mdc.entec.north.arttracker.model.ArtPiece;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
@Dao
public interface ArtPieceDAO {

    @Query("SELECT * FROM ArtPiece")
   List<ArtPiece> findAllArtPiecesSync();

    @Query("SELECT * " +
            " FROM ArtPiece INNER JOIN Artist ON ArtPiece.artistID = Artist.ID")
    List<ArtPieceWithArtist> findAllArtPiecesWithArtistSync();

    @Query("SELECT * " +
            " FROM ArtPiece INNER JOIN Artist ON ArtPiece.artistID = Artist.ID" +
            " WHERE Artist.ID = :ID")
    List<ArtPiece> findAllArtPiecesByArtistIDSync(int ID);

    @Query("SELECT * FROM ArtPiece WHERE artPieceID = :artPieceID")
   ArtPiece loadArtPieceByID(int artPieceID);

    @Query("SELECT * " +
            " FROM ArtPiece INNER JOIN Artist ON ArtPiece.artistID = Artist.ID WHERE artPieceID = :artPieceID")
    ArtPieceWithArtist loadArtPieceWithArtistByName(int artPieceID);

    @Query("DELETE FROM ArtPiece WHERE artPieceID = :artPieceID")
    void deleteArtPiece(int artPieceID);

    @Query("DELETE FROM ArtPiece")
   void deleteAll();

    @Delete
    void delete(ArtPiece artPiece);

   @Insert(onConflict = IGNORE)
   void insertArtPiece(ArtPiece artPiece);

   @Update(onConflict = REPLACE)
   void updateArtPiece(ArtPiece artPiece);

    @Query("SELECT * " +
            " FROM ArtPiece INNER JOIN Artist ON ArtPiece.artistID = Artist.ID" +
            " WHERE ArtPiece.beaconMajor = :beaconMajor AND ArtPiece.beaconMinor = :beaconMinor AND ArtPiece.beaconUUID = :beaconUUID")
    List<ArtPieceWithArtist> findAllArtPiecesCloseTo(String beaconUUID, int beaconMajor, int beaconMinor);
}
