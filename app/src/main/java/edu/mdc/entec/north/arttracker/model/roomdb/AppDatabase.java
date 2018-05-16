package edu.mdc.entec.north.arttracker.model.roomdb;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.mdc.entec.north.arttracker.model.ArtPiece;
import edu.mdc.entec.north.arttracker.model.Artist;



@Database(entities = {ArtPiece.class, Artist.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "--AppDatabase";
    private static final String DATABASE_NAME = "artPieces.db";

    private static AppDatabase INSTANCE;

    public abstract ArtPieceDAO artPieceModel();

    public abstract ArtistDAO artistModel();

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Artist "
                    + " ADD COLUMN youtubeVideoID TEXT");
        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //removed autoincrement
        }
    };

    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            /*
            database.execSQL("ALTER TABLE 'Artist' "
                    + " ADD COLUMN 'timestamp' INTEGER NOT NULL ");
            database.execSQL("ALTER TABLE 'ArtPiece' "
                    + " ADD COLUMN 'timestamp' INTEGER NOT NULL ");
            */
        }
    };


    public static AppDatabase getInstance(Context context){
        if (INSTANCE == null){
            final File dbPath = context.getDatabasePath(DATABASE_NAME);

            if (!dbPath.exists()) {// If the database file does not exist
                // Make sure we have a path to the file
                Log.d(TAG, "!dbPath.exists() copying");
                dbPath.getParentFile().mkdirs();
                copyPrePopulatedDb(dbPath, DATABASE_NAME, context);
            } else {
                Log.d(TAG, "dbPath.exists()");
            }

            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, DATABASE_NAME)
                    //.fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_5)
                    .addMigrations(MIGRATION_5_6)
                    .build();
        }
        return INSTANCE;
    }

    public static void destroy(){
        INSTANCE = null;
    }

    private static void copyPrePopulatedDb(File dbPath, String databaseName, Context context){

        try {
            final InputStream inputStream = context.getAssets().open(databaseName);
            final OutputStream output = new FileOutputStream(dbPath);

            byte[] buffer = new byte[8192];
            int length;

            while ((length = inputStream.read(buffer, 0, 8192)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            inputStream.close();
        }
        catch (IOException e) {
            Log.d(TAG, "Failed to open file", e);
            e.printStackTrace();
        }

    }
    /*
    private void populateDB() {
        AppDatabase mDb = getInstance(context);
        mDb.artPieceModel().deleteAll();
        mDb.artistModel().deleteAll();

        Artist artist1 = new Artist(1, "Alfredo", "Halegua", "A Uruguayan-born sculptor....");
        Artist artist2 = new Artist(2, "Rafael", "Consuegra", "Born in Havana, Cuba, Rafael Consuegra is a scultor who lived in Miami, Florida.");
        Artist artist3 = new Artist(3, "Mario", "Almaguer", "Cuban artist Mario Almaguer was born in Marianao, Cuba.");
        Artist artist4 = new Artist(4, "William", "King", "William King was born in Jacksonville, Florida, in 1925, " +
                "and grew up in Coconut Grove, Miami.");
        mDb.artistModel().insertArtist(artist1);
        mDb.artistModel().insertArtist(artist2);
        mDb.artistModel().insertArtist(artist3);
        mDb.artistModel().insertArtist(artist4);


        //Dynamite Baby
        ArtPiece ap1 = new ArtPiece(1, "Dynamite Baby", 1, 1983, "dynamite_baby", "Painted steel, 9’ x 9’ x 9’\n\nExhibited at the Civic Center, Washington, D.C., 1985. One of three different sculptures in the Park exploring the possibilities of transforming a cube into an art object.  A hole was carved through an imaginary “soft” cube, a few cuts were applied on the two open ends and the parts were bent out to convey the illusion of an exploding cube.  The result is a dynamic object instead of the original static form.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap1);
        //Pheles
        ArtPiece ap2 = new ArtPiece(2, "Pheles", 2, 1991, "pheles" , "Painted Steel, 2’ x 3’ x 8’\n\nInspired by the inner beauty of the flowers.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap2);
        //Piet
        ArtPiece ap3 = new ArtPiece(3, "Piet", 1, 1984, "piet", "Painted steel, 18’ x 6’ x 4’\n\nOne-man show, Museum of the Americas, Washington, D.C. “Piet” is the artist’s homage to Piet Mondrian.  Simple lines and solid color fields are examples of how much can be said with such few elements.  The artist used his own geometric principles to express his admiration for Piet Mondrian’s work.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap3);
        //Toboggan
        ArtPiece ap4 = new ArtPiece(4, "Toboggan", 1, 1978, "toboggan", "Painted steel, 4’ x 5’ x 12’\n\n“Toboggan” is a geometrical configuration of the principles of “contraposto”, used originally by the Renaissance artists to create the effect of dynamism when representing the human figure.  This sculpture is a massive geometric form that expresses this concept in an abstract simplified way.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap4);
        //For the Birds
        ArtPiece ap5 = new ArtPiece(5, "For The Birds", 1, 1990, "for_the_birds", "Painted steel, 6’ x 6’ x 6’\n\nThis sculpture is very simple, but by the use of color, applied in a variety of configurations, it has gone through several changes.  In every case, the same shape offers the viewer a different perception of its forms.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap5);
        //Quebrada
        ArtPiece ap6 = new ArtPiece(6, "Quebrada", 1, 1990, "quebrada", "Aluminum, 18’ x 8’ x 5’\n\nTwo similar volumes connected at a 90 degree angle form a symmetrical composition suggesting a boomerang-like shape in flight. This sculpture is a broken column trying to be something else.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap6);
        //Destellos
        ArtPiece ap7 = new ArtPiece(7, "Destellos", 3, 2004, "destellos", "Painted steel, 9’ x 8” x l, 6”\n\nA free form rendition of the stellar system. These powerful works are often created as large-scale cutouts, open to the surrounding space while they act as vertical monolithic symbols of singular force and energy.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap7);

        //Arcos
        ArtPiece ap8 = new ArtPiece(8, "Arcos", 3, 2004, "arcos_sound", "Painted steel, 9’ x 8” x  l, 6”\n\nA representation of bows used throughout history.  Graceful, yet simple metaphoric expression inspired by Afro-Cuban culture and symbols.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap8);
        //untitled#1
        ArtPiece ap9 = new ArtPiece(9, "Untitled #1", 4, 0000, "untitled1", "Aluminum, 9’ x 5’\n\nKing is known for his simplified figurative cutouts in metal, which is presented with a warm and subtle touch of humor.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap9);
        //untitled #2
        ArtPiece ap10 = new ArtPiece(10, "Untitled #2", 4, 0, "untitled2", "Aluminum, 9’ x 5’ \n\n“The slat like legs on which many of the artist’s figures stand seem emblematic of a precarious existence, and a balance that is always hard-won.”",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap10);
        //Touching point
        ArtPiece ap11 = new ArtPiece(11, "Touching Point", 1, 1980, "touching_point", "Painted steel, 8’ x 17’ x 8’\n\nExhibited at the Civic Center in Washington, D.C., 1985. This sculpture describes a massive geometrical form moving through space and ending with two extremities barely touching each other, as two giant fingers.  Light and shadows change the angular shapes throughout the day.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap11);
        //Faron
        ArtPiece ap12 = new ArtPiece(12, "Faron", 2, 1992, "faron", "Painted steel, 2’ x 1’ x 8’\n\nThe light that guides the way.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap12);
        //Habitat
        ArtPiece ap13 = new ArtPiece(13,"Habitat", 1, 1974, "habitat", "Painted steel, 6’ x 6’ x 5’\n\nOne-man show, The Baltimore Museum of Art, Baltimore, MD. “Habitat” is a potentially habitable shape if enlarged to a massive scale.  The middle cantilever volume is a structural challenge for engineers to ponder.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap13);
        //Architechtonic
        ArtPiece ap14 = new ArtPiece(14,"Architechtonic", 1, 1969, "architectonic", "Aluminum, 3’ x 3’ x 6’\n\nOne-man show, Henry Gallery in Washington, D.C. In 1969, this sculpture was conceptualized by the artist for a mixed-use apartment and/or shopping complex.  The artist felt that buildings did not have to look like a box or a column to be habitable.  Instead, a building could be conceived as a piece of sculpture, a work of art, and not as a utilitarian container.  Today, this approach is being implemented today by some of the well-known architects.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap14);
        //Cantilever
        ArtPiece ap15 = new ArtPiece(15,"Cantilever", 1, 0, "cantilever", "Aluminum, 3’x 4’x 7’\n\nA concept developed as a sculpture building.  This particular sculpture resembles a museum with cantilever exhibition halls overlooking a sculpture park, and it includes an open-air terrace café.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap15);

        //statue of limitation
        ArtPiece ap16 = new ArtPiece(16,"Statue of Limitations", 1, 1980, "statue_of_limitations", "Painted steel, 13’x 13’x 9’\n\nAlthough the title of this sculpture sounds like a legalistic term, it is instead a reference to the limits one imposes upon oneself when working within a shape selected as a point of departure for his work.  In this case the cube.  The goal was to extract parts of the cube and yet be able to maintain its basic shape.  The result was a continuous volume that touches all the angles of the cube.  To add dynamism, the sculpture was placed on one of its angles, which changed the horizontal and vertical lines into oblique ones.  This gives the sculpture a challenging balance, which in this case, it is controlled by its interior structure and its underground support.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap16);

        //pie in the sky
        ArtPiece ap17 = new ArtPiece(17,"Pie in the Sky", 1, 1984, "pie_in_the_sky", " Painted steel, 18’x 6’x 6’\n\nThis vertical composition rests on an angle at the base and ends on an angle at the top.  The sculpture is made of a spiraling sequence of rhomboidal shapes connected to each other in an ascending movement and to a series of triangles.  There are two shapes forming this structure, totaling four rhomboids and fifteen triangles.  The angularities of this columnar design allows for a continuous play of light and shadows giving the sculpture an infinite variety of visual changes.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap17);

        //against the wall
        ArtPiece ap18 = new ArtPiece(18,"Against the Wall", 1, 1974 , "against_the_wall", "Painted steel, 6’x 5’x 17’\n\nEarlier version in plexiglass was exhibited at the Baltimore Museum of Art, One-man show, 1974-1986.  On loan to the City of Washington D.C. (located at the intersection of Massachusetts Avenue and 21st Street, NW). A wall-like sculpture that bends at a soft angle.  It has a “window and a door” suggesting to the viewer that there may be something to see on the other side.  This is a nearly architectural statement that has a life of its own.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap18);

        //piece of cake
        ArtPiece ap19 = new ArtPiece(19,"Piece of cake", 1, 1981, "piece_of_cake", "Painted steel, 13’x 13’x 9’\n\nPart of a tetrahedron has been removed.  The resulting shape has a new presence.  It stands precariously on an angle pointing in several directions at the same time, and it is positioned as a flying object.  This heavy “bird” will never fly, but perhaps, a viewer’s imagination might see it fly.  One of the purposes of art is to ask questions and to present different ideas in search of answers.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap19);

        //giron iv
        ArtPiece ap20 = new ArtPiece(20,"Giron IV (OLGA)", 2, 1992, "giron_iv", "Painted steel, 2’ x 1’ x 6’\n\nAbstraction of an angel, the memory of a battle for freedom.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap20);

        //Cubolic
        ArtPiece ap21 = new ArtPiece(21,"Cubolic", 1, 1968, "cubolic", "Painted steel, 9’x 9’x 9’\n\nOne-man show, The Baltimore Museum of Art.This sculpture represents the transformation of a cube.  The interior of the cube was carved creating a sculptural space and maintaining its original form.  Each side is different and the spaces created show a variety of intriguing viewpoints revealing, what could be called, “the inner cube”.  Its title is an anagram of bucolic.",
                0, 0, "xxx-xxx-xxxx", 0);
        mDb.artPieceModel().insertArtPiece(ap21);
    }
    */

}
