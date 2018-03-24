package edu.mdc.entec.north.arttracker.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;


@Entity(foreignKeys = {
        @ForeignKey(entity = Artist.class,
                parentColumns = "ID",
                childColumns = "artistID")})
public class ArtPiece  implements Parcelable{
    @PrimaryKey
    @NonNull
    private int artPieceID;
    private String name;
    private int artistID;
    private int year;
    private int pictureID;
    private String description;
    private double latitude;
    private double longitude;
    private String beaconID;
    private int stars;



    public ArtPiece(@NonNull int artPieceID, String name, int artistID, int year, int pictureID
            , String description, double latitude, double longitude, String beaconID, int stars) {
        this.artPieceID = artPieceID;
        this.name = name;
        this.artistID = artistID;
        this.year = year;
        this.pictureID = pictureID;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.beaconID = beaconID;
        this.stars = stars;
    }


    protected ArtPiece(Parcel in) {
        artPieceID = in.readInt();
        name = in.readString();
        artistID = in.readInt();
        year = in.readInt();
        pictureID = in.readInt();
        description = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        beaconID = in.readString();
        stars = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(artPieceID);
        dest.writeString(name);
        dest.writeInt(artistID);
        dest.writeInt(year);
        dest.writeInt(pictureID);
        dest.writeString(description);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(beaconID);
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

    public int getPictureID() {
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

    public void setPictureID(int pictureID) {
        this.pictureID = pictureID;
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

    public String getBeaconID() {
        return beaconID;
    }

    public void setBeaconID(String beaconID) {
        this.beaconID = beaconID;
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
                ", pictureID=" + pictureID +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", beaconID='" + beaconID + '\'' +
                ", stars=" + stars +
                '}';
    }
}