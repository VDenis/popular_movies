package com.denis.home.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.denis.home.popularmovies.data.MoviesContract.*;

/**
 * Created by Denis on 17.12.2015.
 */
public class MoviesDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = MoviesDBHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create the database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER, " +
                MovieEntry.COLUMN_POSTER + " TEXT, " +
                MovieEntry.COLUMN_BACKDROP + " TEXT, " +
                MovieEntry.COLUMN_TITLE + " TEXT, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " INTEGER," +
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";/* +
                " );";*/



/*        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + "INTEGER PRIMARY KEY, " +
                ReviewEntry.COLUMN_MOV_KEY + " INTEGER NOT NULL, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +

                // Set up the location column as a foreign key to movie table.
                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOV_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " +
                " );";

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                VideoEntry._ID + "INTEGER PRIMARY KEY, " +
                VideoEntry.COLUMN_MOV_KEY + " INTEGER NOT NULL, " +
                VideoEntry.COLUMN_TRAILER + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_NAME + " TEXT NOT NULL, " +

                // Set up the location column as a foreign key to movie table.
                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOV_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " +
                " );";*/

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
/*        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEO_TABLE);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");

        // Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
/*        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);*/

        // re-create database
        onCreate(sqLiteDatabase);
    }
}
