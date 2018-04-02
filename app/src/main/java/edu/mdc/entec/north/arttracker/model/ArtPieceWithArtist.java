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
    private String beaconID;
    private int stars;

    private int ID;
    private String firstName;
    private String lastName;
    private String details;

    public ArtPieceWithArtist(int artPieceID, String name, int artistID,
                              int year, String pictureID, String description,
                              double latitude, double longitude, String beaconID,
                              int stars, int ID, String firstName, String lastName, String details) {
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
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.details = details;
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
        beaconID = in.readString();
        stars = in.readInt();
        ID = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        details = in.readString();
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

    public Artist getArtist(){
        return new Artist(firstName, lastName, details);
    }

    public String getPictureID() {
        return pictureID;
    }

    public int getPictureID(Context c){
        return c.getResources().getIdentifier("drawable/"+pictureID, null, c.getPackageName());
    }

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
        return "ArtPieceWithArtist{" +
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
                ", ID=" + ID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", details='" + details + '\'' +
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
        parcel.writeString(beaconID);
        parcel.writeInt(stars);
        parcel.writeInt(ID);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(details);
    }
}