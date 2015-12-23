package com.denis.home.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;
import static com.denis.home.popularmovies.data.MoviesContract.*;
/**
 * Created by Denis on 17.12.2015.
 */
public class TestDb extends AndroidTestCase {
    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MoviesDBHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    @Override
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieEntry.TABLE_NAME);
        tableNameHashSet.add(ReviewEntry.TABLE_NAME);
        tableNameHashSet.add(VideoEntry.TABLE_NAME);

        mContext.deleteDatabase(MoviesDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new MoviesDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain movie entry, review entry
        // and video entry entry tables
        assertTrue("Error: Your database was created without the movie entry, review and video entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(MovieEntry._ID);
        locationColumnHashSet.add(MovieEntry.COLUMN_MOVIE_ID);
        locationColumnHashSet.add(MovieEntry.COLUMN_OVERVIEW);
        locationColumnHashSet.add(MovieEntry.COLUMN_POPULARITY);
        locationColumnHashSet.add(MovieEntry.COLUMN_POSTER);
        locationColumnHashSet.add(MovieEntry.COLUMN_RELEASE_DATE);
        locationColumnHashSet.add(MovieEntry.COLUMN_TITLE);
        locationColumnHashSet.add(MovieEntry.COLUMN_VOTE_AVERAGE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required movie
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    public void testMovieTable() {
        insertMovie();
    }

    public void testReviewTable() {
        // First insert the movie, and then use the movieRowId to insert
        // the review. Make sure to cover as many failure cases as you can.

        // Instead of rewriting all of the code we've already written in testMovieTable
        // we can move this code to insertMovie and then call insertMovie from both
        // tests. Why move it? We need the code to return the ID of the inserted movie
        // and our testMovieTable can only return void because it's a test.

        long movieRowId = insertMovie();

        // Make sure we have a valid row ID.
        assertFalse("Error: Movie Not Inserted Correctly", movieRowId == -1L);

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        MoviesDBHelper dbHelper = new MoviesDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step (Review): Create review values
        ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);

        // Third Step (Review): Insert ContentValues into database and get a row ID back
        long reviewRowId = db.insert(ReviewEntry.TABLE_NAME, null, reviewValues);
        assertTrue(reviewRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor reviewCursor = db.query(
                ReviewEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue("Error: No Records returned from review query", reviewCursor.moveToFirst());

        // Fifth Step: Validate the review Query
        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate",
                reviewCursor, reviewValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from weather query",
                reviewCursor.moveToNext() );

        // Sixth Step: Close cursor and database
        reviewCursor.close();
        dbHelper.close();
    }

    public void testVideoTable() {
        // First insert the movie, and then use the movieRowId to insert
        // the video. Make sure to cover as many failure cases as you can.

        // Instead of rewriting all of the code we've already written in testMovieTable
        // we can move this code to insertMovie and then call insertMovie from both
        // tests. Why move it? We need the code to return the ID of the inserted movie
        // and our testMovieTable can only return void because it's a test.

        long movieRowId = insertMovie();

        // Make sure we have a valid row ID.
        assertFalse("Error: Movie Not Inserted Correctly", movieRowId == -1L);

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        MoviesDBHelper dbHelper = new MoviesDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step (Video): Create review values
        ContentValues videoValues = TestUtilities.createVideoValues(movieRowId);

        // Third Step (Video): Insert ContentValues into database and get a row ID back
        long reviewRowId = db.insert(VideoEntry.TABLE_NAME, null, videoValues);
        assertTrue(reviewRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor videoCursor = db.query(
                VideoEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue("Error: No Records returned from review query", videoCursor.moveToFirst());

        // Fifth Step: Validate the review Query
        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate",
                videoCursor, videoValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from weather query",
                videoCursor.moveToNext());

        // Sixth Step: Close cursor and database
        videoCursor.close();
        dbHelper.close();
    }

    public long insertMovie() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        MoviesDBHelper dbHelper = new MoviesDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        // (you can use the createJurassicWorldMovieValues if you wish)
        ContentValues testValues = TestUtilities.createJurassicWorldMovieValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long reviewRowId;
        reviewRowId = db.insert(MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(reviewRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                MovieEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return reviewRowId;
    }
}
