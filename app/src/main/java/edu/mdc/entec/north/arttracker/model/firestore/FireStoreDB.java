package edu.mdc.entec.north.arttracker.model.firestore;


import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mdc.entec.north.arttracker.model.ArtPiece;
import edu.mdc.entec.north.arttracker.model.ArtPieceWithArtist;
import edu.mdc.entec.north.arttracker.model.Artist;
import edu.mdc.entec.north.arttracker.model.roomdb.AppDatabase;
import edu.mdc.entec.north.arttracker.model.roomdb.ArtPieceDAO;
import edu.mdc.entec.north.arttracker.utils.ImageDownloadFragment;
import edu.mdc.entec.north.arttracker.view.MainActivity;
import edu.mdc.entec.north.arttracker.view.gallery.ArtPiecesAdapter;

public class FireStoreDB {
    private static final String TAG = ",,FireStoreDB";
    private static FirebaseFirestore db;
    private static FireStoreDB INSTANCE;
    private static MainActivity mainActivity;
    private static List<Artist> artists = new ArrayList<>();

    public static FireStoreDB getInstance(MainActivity context){
        mainActivity = context;
        if(INSTANCE == null){
            INSTANCE = new FireStoreDB();
        }
        return INSTANCE;
    }

    private static FirebaseFirestore getDB(){
        if(db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }


    private static ArtPiece getArtPieceFromDocument(QueryDocumentSnapshot document) {
        Log.d(TAG, document.getId() + " ==> " + document.getData());
        GeoPoint geoPoint = document.getGeoPoint("coordinates");
        double lat = 0, lon = 0;
        if(geoPoint != null ){
            lat = document.getGeoPoint("coordinates").getLatitude();
            lon = document.getGeoPoint("coordinates").getLongitude();
        }
        int beaconMajor = 0, beaconMinor = 0;
        Long beaconMa = document.getLong("beaconMajor");
        if(beaconMa != null)
            beaconMajor = beaconMa.intValue();
        Long beaconMi = document.getLong("beaconMinor");
        if(beaconMi != null )
            beaconMinor = beaconMi.intValue();
        int year = 0;
        Long y = document.getLong("year");
        if(y != null )
            year = y.intValue();
        int stars = 0;
        Long s = document.getLong("stars");
        if(s != null)
            stars = s.intValue();
        int artistID = 0;
        Long a = document.getLong("artistID");
        if(a != null)
            artistID = a.intValue();
        int artPieceID = 0;
        Long aa = document.getLong("artPieceID");
        if(aa != null)
            artPieceID = aa.intValue();

        String name = document.getString("name");
        if(name == null)
            name = "";
        String pictureID = document.getString("pictureID");
        if(pictureID == null)
            pictureID = "default_pic";
        String description = document.getString("description");
        if(description == null)
            description = "";
        String beaconUUID = document.getString("beaconUUID");
        if(beaconUUID == null)
            beaconUUID = "B9407F30-F5F8-466E-AFF9-25556B575555";

        long timestamp = 0;
        Long t = document.getLong("timestamp");
        if(t != null)
            timestamp = t.longValue();

        return new ArtPiece(
                artPieceID,
                name,
                artistID,
                year,
                pictureID,
                description,
                lat,
                lon,
                beaconUUID,
                beaconMajor,
                beaconMinor,
                stars,
                timestamp
        );
    }

    public List<ArtPieceWithArtist> findAllArtPiecesWithArtistSync(){
        final List<ArtPieceWithArtist> list = new ArrayList<>();
        final List<Artist> artists = new ArrayList<>();
        getDB().collection("artPieces")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                final ArtPieceWithArtist artPiece1 = getArtPieceWithArtistFromDocument(document);

                                boolean artistInCache = false;
                                final Artist[] artist = {null};
                                for(Artist ar : artists){
                                    if(ar.getID() == artPiece1.getArtistID()){
                                        artistInCache = true;
                                        artist[0] = ar;
                                    }
                                }

                                if(!artistInCache) {
                                    ///////////
                                    getDB().collection("artists")
                                            .whereEqualTo("ID", artPiece1.getArtistID())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                                            artist[0] = getArtistFromDocument(document);

                                                            if (artist[0] != null) {
                                                                artists.add(artist[0]);
                                                                artPiece1.setArtistID(artist[0].getID());
                                                                artPiece1.setFirstName(artist[0].getFirstName());
                                                                artPiece1.setLastName(artist[0].getLastName());
                                                                artPiece1.setDetails(artist[0].getDetails());
                                                                artPiece1.setYoutubeVideoID(artist[0].getYoutubeVideoID());

                                                                list.add(artPiece1);
                                                            }

                                                        }
                                                    } else {
                                                        Log.d(TAG, "Error getting artist documents: ", task.getException());
                                                    }
                                                }
                                            });
                                    /////////
                                } else {
                                    artPiece1.setArtistID(artist[0].getID());
                                    artPiece1.setFirstName(artist[0].getFirstName());
                                    artPiece1.setLastName(artist[0].getLastName());
                                    artPiece1.setDetails(artist[0].getDetails());
                                    artPiece1.setYoutubeVideoID(artist[0].getYoutubeVideoID());

                                    list.add(artPiece1);
                                }



                            }
                        } else {
                            Log.w(TAG, "Error getting artpiece documents.", task.getException());
                        }
                    }
                });
        return list;

    }

    private ArtPieceWithArtist getArtPieceWithArtistFromDocument(QueryDocumentSnapshot document) {

        Log.d(TAG, document.getId() + " => " + document.getData());
        GeoPoint geoPoint = document.getGeoPoint("coordinates");
        double lat = 0, lon = 0;
        if(geoPoint != null ){
            lat = document.getGeoPoint("coordinates").getLatitude();
            lon = document.getGeoPoint("coordinates").getLongitude();
        }
        int artPieceID = 0;
        Long artPieceIDD = document.getLong("artPieceID");
        if(artPieceIDD != null)
            artPieceID = artPieceIDD.intValue();

        int beaconMajor = 0, beaconMinor = 0;
        Long beaconMa = document.getLong("beaconMajor");
        if(beaconMa != null)
            beaconMajor = beaconMa.intValue();
        Long beaconMi = document.getLong("beaconMinor");
        if(beaconMi != null )
            beaconMinor = beaconMi.intValue();
        int year = 0;
        Long y = document.getLong("year");
        if(y != null )
            year = y.intValue();
        int stars = 0;
        Long s = document.getLong("stars");
        if(s != null)
            stars = s.intValue();
        int artistID = 0;
        Long a = document.getLong("artistID");
        if(a != null)
            artistID = a.intValue();

        String name = document.getString("name");
        if(name == null)
            name = "";
        String pictureID = document.getString("pictureID");
        if(pictureID == null)
            pictureID = "default_pic";
        String description = document.getString("description");
        if(description == null)
            description = "";
        String beaconUUID = document.getString("beaconUUID");
        if(beaconUUID == null)
            beaconUUID = "B9407F30-F5F8-466E-AFF9-25556B575555";

        Log.d(TAG, "beaconUUID = "+beaconUUID);
        long timestamp = 0;
        Long t = document.getLong("timestamp");
        if(t != null)
            timestamp = t.longValue();


        return new ArtPieceWithArtist(
                artPieceID,
                name,
                artistID,
                year,
                pictureID,
                description,
                lat,
                lon,
                beaconUUID,
                stars,
                -1,
                null,
                null,
                null,
                null,
                timestamp
        );
    }

    public List<ArtPiece> findAllArtPiecesByArtistIDSync(int ID){
        final List<ArtPiece> artPieces = new ArrayList<>();
        getDB().collection("artPieces")
                .whereEqualTo("artistID", ID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArtPiece artPiece = getArtPieceFromDocument(document);
                                artPieces.add(artPiece);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return artPieces;

    }

    public ArtPiece loadArtPieceByID(int artPieceID){
        final ArtPiece[] artPieces = {null};
        getDB().collection("artPieces")
                .whereEqualTo("artPieceID", artPieceID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                artPieces[0] = getArtPieceFromDocument(document);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return artPieces[0];
    }

    public ArtPieceWithArtist loadArtPieceWithArtistByID(final int artPieceID){
        final ArtPieceWithArtist[] artPieces = {null};
        getDB().collection("artPieces")
                .whereEqualTo("artPieceID", artPieceID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                artPieces[0] = getArtPieceWithArtistFromDocument(document);

                                Artist artist = loadArtistByID(artPieces[0].getArtistID());
                                artPieces[0].setArtistID(artist.getID());
                                artPieces[0].setFirstName(artist.getFirstName());
                                artPieces[0].setLastName(artist.getLastName());
                                artPieces[0].setDetails(artist.getDetails());
                                artPieces[0].setYoutubeVideoID(artist.getYoutubeVideoID());
                                artPieces[0].setTimestamp(artist.getTimestamp());

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return artPieces[0];
    }

    public void insertArtPiece(ArtPiece artPiece){
        Map<String, Object> artPieceMap = new HashMap<>();
        artPieceMap.put("artPieceID", artPiece.getArtPieceID());
        artPieceMap.put("artistID", artPiece.getArtistID());
        artPieceMap.put("beaconMajor", artPiece.getBeaconMajor());
        artPieceMap.put("beaconMinor", artPiece.getBeaconMinor());
        artPieceMap.put("beaconUUID", artPiece.getBeaconUUID());
        artPieceMap.put("description", artPiece.getDescription());
        artPieceMap.put("name", artPiece.getName());
        artPieceMap.put("pictureID", artPiece.getPictureID());
        artPieceMap.put("stars", artPiece.getStars());
        artPieceMap.put("year", artPiece.getYear());
        artPieceMap.put("coordinates", new GeoPoint(artPiece.getLatitude(),artPiece.getLongitude()));
        artPieceMap.put("timestamp", artPiece.getTimestamp());



        // Add a new document with a generated ID
        getDB().collection("artPieces")
                .add(artPieceMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    public void loadAll(){
        final AppDatabase mDb = AppDatabase.getInstance(mainActivity);
        new GetArtistsFromWebAsyncTask().execute();
    }

    private static class GetArtistsFromWebAsyncTask extends AsyncTask<Void, Void, List<Artist>> {
        @Override
        protected List<Artist> doInBackground(Void... params) {
            Log.d(TAG, "doInBackground 1: getting Artists from the web");
            getDB().collection("artists")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete 1: after getting Artists from the web " );
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Artist artist = FireStoreDB.getArtistFromDocument(document);
                                    artists.add(artist);
                                }
                                Log.d(TAG, "onComplete 1: after getting Artists from the web gotten " + artists.size());
                                ////////////////// 1. updating artists from Room database
                                new UpdateArtistsAsyncTask().execute();
                                //}
                            } else {
                                Log.w(TAG, "Error getting artists documents.", task.getException());
                            }
                        }
                    });
            return artists;
        }
        @Override
        protected void onPostExecute(final List<Artist> artists) {
            Log.d(TAG, "onPostExecute 1");
        }
    }

    private static class UpdateArtistsAsyncTask extends AsyncTask<Void, Void, Void> {
        AppDatabase mDb = AppDatabase.getInstance(mainActivity);
        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "doInBackground 2: updating artists in DB");
            for (Artist downloadedArtist: artists) {
                Artist localArtist = mDb.artistModel().loadArtistByID(downloadedArtist.getID());
                if (localArtist == null) { //new artist
                    mDb.artistModel().insertArtist(downloadedArtist);
                    Log.d(TAG, "doInBackground 2: updating artists in DB:" + " Inserted "+ downloadedArtist.getLastName());
                } else if (localArtist.getTimestamp() < downloadedArtist.getTimestamp()){ //artist has been updated
                    mDb.artistModel().updateArtist(downloadedArtist);
                    Log.d(TAG, "doInBackground 2: updating artists in DB:" + " Updating "+ downloadedArtist.getLastName());
                } else { //artist is up-to-date
                    Log.d(TAG, "doInBackground 2: updating artists in DB:" + " Nothing to do with "+ downloadedArtist.getLastName());
                }
            }
            return null;
        }

        //////////////////// 2. Now we can get the art pieces
        @Override
        protected void onPostExecute(Void voids) {
            Log.d(TAG, "onPostExecute 2: finished updating artists from DB");
            new GetArtPiecesFromWebAsyncTask().execute(artists);
        }
    }

    private static class GetArtPiecesFromWebAsyncTask extends  AsyncTask<List<Artist>, Void, Void> {
        @Override
        protected Void doInBackground(List<Artist>... lists) {
            Log.d(TAG, "doInBackground 3: getting ArtPieces from the web");

            FireStoreDB.getDB().collection("artPieces")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                final List<ArtPiece> artPieces = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    ArtPiece artPiece1 = FireStoreDB.getArtPieceFromDocument(document);
                                    artPieces.add(artPiece1);
                                }

                                new InsertArtPiecesAsyncTask().execute(artPieces);

                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });

            return null;
        }
        //////////////////3. Now we can save the ArtPieces
        @Override
        protected void onPostExecute(Void voids) {
            Log.d(TAG, "onPostExecute 3: finished getting art pieces from the web");
        }
    }

    private static class InsertArtPiecesAsyncTask extends AsyncTask<List<ArtPiece>, Void, String[]> {
        AppDatabase mDb = AppDatabase.getInstance(mainActivity);
        final ArtPieceDAO artPieceDAO = mDb.artPieceModel();
        @Override
        protected String[] doInBackground(List<ArtPiece>... downloadedArtPieces) {
            Log.d(TAG, "doInBackground 4: inserting art pieces into DB");
            List<String> pictureIDsToDownload = new ArrayList<>();

            for (ArtPiece downloadedArtPiece: downloadedArtPieces[0]) {
                ArtPiece localArtPiece = artPieceDAO.loadArtPieceByID(downloadedArtPiece.getArtPieceID());
                if(localArtPiece == null){//new art piece
                    try {
                        Artist art = mDb.artistModel().loadArtistByID(downloadedArtPiece.getArtistID());
                        artPieceDAO.insertArtPiece(downloadedArtPiece);
                        mainActivity.insertArtPiece(new ArtPieceWithArtist(downloadedArtPiece, art));
                        pictureIDsToDownload.add(downloadedArtPiece.getPictureID());
                        Log.d(TAG, "doInBackground 4: updating art piece in DB:" + " Inserted " + downloadedArtPiece.getName());
                    } catch(SQLiteConstraintException e){
                        Log.d(TAG, "doInBackground 4: updating art piece in DB:" + " Error while inserting " + downloadedArtPiece.getName());
                        e.printStackTrace();
                    }
                } else if(localArtPiece.getTimestamp() < downloadedArtPiece.getTimestamp()) { //art piece has been updated
                    try {
                        artPieceDAO.updateArtPiece(downloadedArtPiece);
                        pictureIDsToDownload.add(downloadedArtPiece.getPictureID());
                        Log.d(TAG, "doInBackground 4: updating art piece in DB:" + " Updated " + downloadedArtPiece.getName());
                    }catch(SQLiteConstraintException e){
                        Log.d(TAG, "doInBackground 4: updating art piece in DB:" + " Error while updating " + downloadedArtPiece.getName());
                        e.printStackTrace();
                    }
                } else { //art piece  is up-to-date
                    Log.d(TAG, "doInBackground 4: updating art piece in DB:" + " Nothing to do with "+ downloadedArtPiece.getName());
                }

            }
            List<ArtPiece> localArtPieces = artPieceDAO.findAllArtPiecesSync();
            for(ArtPiece localArtPiece: localArtPieces){
                if(!downloadedArtPieces[0].contains(localArtPiece)){
                    mDb.artPieceModel().delete(localArtPiece);
                }
            }
            return pictureIDsToDownload.toArray(new String[pictureIDsToDownload.size()]);
        }

        @Override
        protected void onPostExecute(String[] pictureIDs) {
            Log.d(TAG, "onPostExecute 4: finished inserting art pieces into DB");
            ImageDownloadFragment imageDownloadFragment = ImageDownloadFragment.getInstance(mainActivity.getSupportFragmentManager(), pictureIDs);
            if (!mainActivity.mDownloading && imageDownloadFragment != null) {
                mainActivity.mDownloading = true;
            }

            new PrintLocalArtPiecesAsyncTask().execute();

        }
    }

    private static class PrintLocalArtPiecesAsyncTask extends AsyncTask<Void, Void, List<ArtPieceWithArtist>>{
        @Override
        protected List<ArtPieceWithArtist> doInBackground(Void... params) {
            return AppDatabase.getInstance(mainActivity).artPieceModel().findAllArtPiecesWithArtistSync();
        }

        @Override
        protected void onPostExecute(List<ArtPieceWithArtist> artPieces) {
            for(ArtPieceWithArtist ap: artPieces){
                Log.d(TAG, "Retrieved "+ ap.toString());
            }
        }
    }

    private static Artist getArtistFromDocument(QueryDocumentSnapshot document) {
        Log.d(TAG, document.getId() + " => " + document.getData());

        int ID = 0;
        Long IDD = document.getLong("ID");
        if(IDD != null)
            ID = IDD.intValue();
        String firstName = document.getString("firstName");
        if(firstName == null)
            firstName = "";
        String lastName = document.getString("lastName");
        if(lastName == null)
            lastName = "";
        String details = document.getString("details");
        if(details == null)
            details = "";
        String youtubeVideoID = document.getString("youtubeVideoID");
        if(youtubeVideoID == null)
            youtubeVideoID = "";

        long timestamp = 0;
        Long t = document.getLong("timestamp");
        if(t != null)
            timestamp = t.longValue();

        return new Artist(
                ID,
                firstName,
                lastName,
                details,
                youtubeVideoID,
                timestamp
        );
    }

    public Artist loadArtistByID(int ID){
        final Artist[] artist = {null};
        getDB().collection("artists")
                .whereEqualTo("ID", ID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                artist[0] = getArtistFromDocument(document);
                            }
                        } else {
                            Log.d(TAG, "Error getting artist documents: ", task.getException());
                        }
                    }
                });
        return artist[0];
    }

    public void insertArtist(Artist artist){
        Map<String, Object> artistMap = new HashMap<>();
        artistMap.put("ID", artist.getID());
        artistMap.put("details", artist.getDetails());
        artistMap.put("firstName", artist.getFirstName());
        artistMap.put("lastName", artist.getLastName());
        artistMap.put("youtubeVideoID", artist.getYoutubeVideoID());
        artistMap.put("timestamp", artist.getTimestamp());



        // Add a new document with a generated ID
        getDB().collection("artists")
                .add(artistMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }



}