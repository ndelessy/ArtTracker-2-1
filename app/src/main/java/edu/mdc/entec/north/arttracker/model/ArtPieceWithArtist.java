package edu.mdc.entec.north.arttracker.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;


public class ArtPieceWithArtist implements Parcelable {
    private int artPieceID;
    private String name;
    private int artistID;
    private int year;
    private String pictureID;
    private String description;
    private double latitude;
    private double longitude;
    private String beaconUUID;
    private int stars;

    private int ID;
    private String firstName;
    private String lastName;
    private String details;
    private String youtubeVideoID;
    private long timestamp;

    public ArtPieceWithArtist(int artPieceID, String name, int artistID,
                              int year, String pictureID, String description,
                              double latitude, double longitude, String beaconUUID,
                              int stars, int ID, String firstName, String lastName, String details, String youtubeVideoID,
                              long timestamp
    ) {
        this.artPieceID = artPieceID;
        this.name = name;
        this.artistID = artistID;
        this.year = year;
        this.pictureID = pictureID;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.beaconUUID = beaconUUID;
        this.stars = stars;
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.details = details;
        this.youtubeVideoID = youtubeVideoID;
        this.timestamp = timestamp;
    }


    protected ArtPieceWithArtist(Parcel in) {
        artPieceID = in.readInt();
        name = in.readString();
        artistID = in.readInt();
        year = in.readInt();
        pictureID = in.readString();
        description = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        beaconUUID = in.readString();
        stars = in.readInt();
        ID = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        details = in.readString();
        youtubeVideoID = in.readString();
        timestamp = in.readLong();
    }

    public static final Creator<ArtPieceWithArtist> CREATOR = new Creator<ArtPieceWithArtist>() {
        @Override
        public ArtPieceWithArtist createFromParcel(Parcel in) {
            return new ArtPieceWithArtist(in);
        }

        @Override
        public ArtPieceWithArtist[] newArray(int size) {
            return new ArtPieceWithArtist[size];
        }
    };

    public String getName() {
        return name;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getYear() {
        return year;
    }


    public int getID() {
        return ID;
    }

    public String getPictureID() {
        return pictureID;
    }

    /*public int getPictureID(Context c){
        return c.getResources().getIdentifier("drawable/"+pictureID, null, c.getPackageName());
    }
    */

    public String getDescription() {
        return description;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPictureID(String pictureID) {
        this.pictureID = pictureID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getArtPieceID() {
        return artPieceID;
    }

    public void setArtPieceID(int artPieceID) {
        this.artPieceID = artPieceID;
    }

    public int getArtistID() {
        return artistID;
    }

    public void setArtistID(int artistID) {
        this.artistID = artistID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getBeaconUUID() {
        return beaconUUID;
    }

    public void setBeaconUUID(String beaconUUID) {
        this.beaconUUID = beaconUUID;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getYoutubeVideoID() {
        return youtubeVideoID;
    }

    public void setYoutubeVideoID(String youtubeVideoID) {
        this.youtubeVideoID = youtubeVideoID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ArtPieceWithArtist{" +
                "artPieceID=" + artPieceID +
                ", name='" + name + '\'' +
                ", artistID=" + artistID +
                ", year=" + year +
                ", pictureID=" + pictureID +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", beaconUUID='" + beaconUUID + '\'' +
                ", stars=" + stars +
                ", ID=" + ID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", details='" + details + '\'' +
                ", youtubeVideoID='" + youtubeVideoID + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(artPieceID);
        parcel.writeString(name);
        parcel.writeInt(artistID);
        parcel.writeInt(year);
        parcel.writeString(pictureID);
        parcel.writeString(description);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(beaconUUID);
        parcel.writeInt(stars);
        parcel.writeInt(ID);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(details);
        parcel.writeString(youtubeVideoID);
        parcel.writeLong(timestamp);
    }
}