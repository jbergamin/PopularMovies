package com.jakebergamin.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Jake on 11/11/2015.
 */
public class Movie implements Parcelable {
    String originalTitle;
    float voteAverage;
    String posterPath;
    String overview;
    GregorianCalendar releaseDate;

    public Movie(){

    }

    public Movie(String originalTitle, String posterPath, float voteAverage, String overview, GregorianCalendar releaseDate) {
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags){
        out.writeString(originalTitle);
        out.writeString(posterPath);
        out.writeFloat(voteAverage);
        out.writeString(overview);
        out.writeInt(releaseDate.get(GregorianCalendar.YEAR));
        out.writeInt(releaseDate.get(GregorianCalendar.MONTH));
        out.writeInt(releaseDate.get(GregorianCalendar.DAY_OF_MONTH));
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel.readString(), parcel.readString(), parcel.readFloat(), parcel.readString(),
                    new GregorianCalendar(parcel.readInt(), parcel.readInt(), parcel.readInt()));
        }

        @Override
        public Movie[] newArray(int i){
            return new Movie[i];
        }
    };

    @Override
    public String toString(){
        String str = "";
        str += originalTitle;
        str += " " + posterPath;
        str += " " + voteAverage;
        str += " " + releaseDate.get(GregorianCalendar.YEAR) + "-" +
        releaseDate.get(GregorianCalendar.MONTH) + "-" + releaseDate.get(GregorianCalendar.DAY_OF_MONTH);

        return str;
    }

}
