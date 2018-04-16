package edu.mdc.entec.north.arttracker.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity
public class Artist implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int ID;
    private String firstName;
    private String lastName;
    private String details;
    private String youtubeVideoID;

    public Artist(String firstName, String lastName, String details, String youtubeVideoID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.details = details;
        this.youtubeVideoID = youtubeVideoID;
    }

    protected Artist(Parcel in) {
        ID = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        details = in.readString();
        youtubeVideoID = in.readString();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getYoutubeVideoID() {
        return youtubeVideoID;
    }

    public void setYoutubeVideoID(String youtubeVideoID) {
        this.youtubeVideoID = youtubeVideoID;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "ID=" + ID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", details='" + details + '\'' +
                ", youtubeVideoID='" + youtubeVideoID + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(details);
        parcel.writeString(youtubeVideoID);
    }
}
