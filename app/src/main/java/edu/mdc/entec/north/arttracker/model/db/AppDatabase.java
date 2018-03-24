package edu.mdc.entec.north.arttracker.model.db;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


import edu.mdc.entec.north.arttracker.model.ArtPiece;
import edu.mdc.entec.north.arttracker.model.Artist;


@Database(entities = {ArtPiece.class, Artist.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract ArtPieceDAO artPieceModel();

    public abstract ArtistDAO artistModel();

    public static AppDatabase getInMemoryDatabase(Context context){
        if (INSTANCE == null){
            /*INSTANCE = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                    AppDatabase.class)
                    .allowMainThreadQueries()
                    .build();
                    */

            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "database").build();
        }
        return INSTANCE;
    }

    public static void destroy(){
        INSTANCE = null;
    }


}
