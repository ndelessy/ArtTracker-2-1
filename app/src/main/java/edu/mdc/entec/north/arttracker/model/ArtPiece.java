package edu.mdc.entec.north.arttracker.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;


@Entity
        (foreignKeys = {
        @ForeignKey(entity = Artist.class,
                parentColumns = "ID",
                childColumns = "artistID")})

public class ArtPiece  implements Parcelable{
    @PrimaryKey //(autoGenerate = true)
    @NonNull
    private int artPieceID;
    private String name;
    private int artistID;
    private int year;
    private String pictureID;
    private String description;
    private double latitude;
    private double longitude;
    private String beaconUUID;
    private int beaconMajor;
    private int beaconMinor;
    private int stars;


    public ArtPiece(int artPieceID, String name, int artistID, int year, String pictureID, String description,
                    double latitude, double longitude,
                    String beaconUUID, int beaconMajor, int beaconMinor,
                    int stars) {
        this.artPieceID = artPieceID;
        this.name = name;
        this.artistID = artistID;
        this.year = year;
        this.pictureID = pictureID;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.beaconUUID = beaconUUID;
        this.beaconMajor = beaconMajor;
        this.beaconMinor = beaconMinor;
        this.stars = stars;
    }


    protected ArtPiece(Parcel in) {
        artPieceID = in.readInt();
        name = in.readString();
        artistID = in.readInt();
        year = in.readInt();
        pictureID = in.readString();
        description = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        beaconUUID = in.readString();
        beaconMajor = in.readInt();
        beaconMinor = in.readInt();
        stars = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(artPieceID);
        dest.writeString(name);
        dest.writeInt(artistID);
        dest.writeInt(year);
        dest.writeString(pictureID);
        dest.writeString(description);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(beaconUUID);
        dest.writeInt(beaconMajor);
        dest.writeInt(beaconMinor);
        dest.writeInt(stars);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ArtPiece> CREATOR = new Creator<ArtPiece>() {
        @Override
        public ArtPiece createFromParcel(Parcel in) {
            return new ArtPiece(in);
        }

        @Override
        public ArtPiece[] newArray(int size) {
            return new ArtPiece[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getArtistID() {
        return artistID;
    }

    public int getYear() {
        return year;
    }

    public String getPictureID() {
        return pictureID;
    }

    public String getDescription() {
        return description;
    }

    public void setArtist(int artist) {
        this.artistID = artist;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPictureID(String pictureID) {
        this.pictureID = pictureID;
    }

    public int getPictureID(Context c){
        return c.getResources().getIdentifier("drawable/"+pictureID, null, c.getPackageName());
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public int getArtPieceID() {
        return artPieceID;
    }

    public void setArtPieceID(@NonNull int artPieceID) {
        this.artPieceID = artPieceID;
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

    public int getBeaconMajor() {
        return beaconMajor;
    }

    public void setBeaconMajor(int beaconMajor) {
        this.beaconMajor = beaconMajor;
    }

    public int getBeaconMinor() {
        return beaconMinor;
    }

    public void setBeaconMinor(int beaconMinor) {
        this.beaconMinor = beaconMinor;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    @Override
    public String toString() {
        return "ArtPiece{" +
                "artPieceID=" + artPieceID +
                ", name='" + name + '\'' +
                ", artistID=" + artistID +
                ", year=" + year +
                ", pictureID='" + pictureID + '\'' +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", beaconUUID='" + beaconUUID + '\'' +
                ", beaconMajor=" + beaconMajor +
                ", beaconMinor=" + beaconMinor +
                ", stars=" + stars +
                '}';
    }
}