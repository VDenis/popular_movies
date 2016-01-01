package com.denis.home.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Denis on 17.12.2015.
 */
public class MoviesContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.denis.home.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

/*    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_VIDEO = "video";*/

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }


    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        // Movie id as returned by API
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_BACKDROP = "backdrop";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


    public static final class ReviewEntry implements BaseColumns {

        public static final String TABLE_NAME = "review";

        // Column with the foreign key into the movie table.
        //public static final String COLUMN_MOV_KEY = "movie_id";
        public static final String COLUMN_MOV_KEY = MovieEntry.COLUMN_MOVIE_ID;
        // In json "author": "Travis Bell"
        public static final String COLUMN_AUTHOR = "author";
        // In json  "content": "I felt like th ... over characters"
        public static final String COLUMN_CONTENT = "content";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReviewWithMovieId(long movieId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(movieId)).build();
        }

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
    }


    public static final class VideoEntry implements BaseColumns {

        public static final String TABLE_NAME = "video";

        // Column with the foreign key into the movie table.
        public static final String COLUMN_MOV_KEY = "movie_id";
        // In json "key": "SUXWAEX2jlg"
        public static final String COLUMN_TRAILER = "video_key";
        // In json "name": "Trailer 1"
        public static final String COLUMN_NAME = "name";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildVideoWithMovieId(long movieId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(movieId)).build();
        }

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // public static final String COLUMN_SIZE = "size";
        // public static final String COLUMN_SITE = "site";
        // In json "site": "YouTube"


        // In json "size": 720

        // In json "type": "Trailer"
        // public static final String COLUMN_TYPE = "type";
    }

/*
    public static final class ImageEntry implements BaseColumns {

        public static final String TABLE_NAME = "image";


    }*/


}
