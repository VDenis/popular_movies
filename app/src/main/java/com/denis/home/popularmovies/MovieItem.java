package com.denis.home.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by Denis on 29.10.2015.
 */
public class MovieItem implements Parcelable {
    int id;
    String poster;
    String title;
    String overview;
    double voteAverage;
    double popularity;
    String releaseDate;

    public MovieItem(int id, String poster, String title, String overview, double voteAverage, double popularity, String releaseDate) {
        this.id = id;
        this.poster = poster;
        this.title = title;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
    }

    public MovieItem(Parcel in) {
        id = in.readInt();
        poster = in.readString();
        title = in.readString();
        overview = in.readString();
        voteAverage = in.readDouble();
        popularity = in.readDouble();
        releaseDate = in.readString();
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
        dest.writeDouble(popularity);
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

    // Using for sorting
    public static Comparator<MovieItem> COMPARE_BY_POPULARITY_DESC = new Comparator<MovieItem>() {
        @Override
        public int compare(MovieItem one, MovieItem other) {
            // Reverse for sort by desc
            return Double.compare(other.popularity, one.popularity);
        }
    };

    public static Comparator<MovieItem> COMPARE_BY_VOTE_AVERAGE_DESC = new Comparator<MovieItem>() {
        @Override
        public int compare(MovieItem one, MovieItem other) {
            //return one.title.compareTo(other.title);
            // Reverse for sort by desc
            return Double.compare(other.voteAverage, one.voteAverage);
        }
    };
}
