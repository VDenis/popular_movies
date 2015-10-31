package com.denis.home.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Denis on 29.10.2015.
 */
public class MovieItem implements Parcelable {
    int id;
    String poster;
    String title;
    String overview;
    double voteAverage;
    String releaseDate;

    public MovieItem(int id, String poster, String title, String overview, double voteAverage, String releaseDate) {
        this.id = id;
        this.poster = poster;
        this.title = title;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public MovieItem(Parcel in) {
        id = in.readInt();
        poster = in.readString();
        title = in.readString();
        overview = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
    }

    public String getYearFromReleaseDate() {
        String convertString = "";
        return convertString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(poster);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);
    }

    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel source) {
            return new MovieItem(source);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };
}
