package edu.mdc.entec.north.arttracker.model.roomdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import edu.mdc.entec.north.arttracker.model.ArtPiece;
import edu.mdc.entec.north.arttracker.model.Artist;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ArtistDAO {

    @Query("SELECT * FROM Artist")
    List<Artist> findAllArtistsSync();

    @Query("SELECT * FROM Artist WHERE ID = :ID")
    Artist loadArtistByID(int ID);

    @Query("DELETE FROM Artist")
    void deleteAll();

    @Delete
    void delete(Artist artist);

    @Insert(onConflict = IGNORE)
    void insertArtist(Artist artist);

    @Update(onConflict = REPLACE)
    void updateArtist(Artist artist);
}
