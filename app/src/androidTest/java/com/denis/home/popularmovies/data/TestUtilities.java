package com.denis.home.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Map;
import java.util.Set;

import static com.denis.home.popularmovies.data.MoviesContract.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * Created by Denis on 18.12.2015.
 */
public class TestUtilities {

    static final long TEST_DATE = 1419033600L;  // December 20th, 2014

    public static ContentValues createJurassicWorldMovieValues() {
        ContentValues testValues = new ContentValues();

        testValues.put(MovieEntry.COLUMN_MOVIE_ID, 135397);
        testValues.put(MovieEntry.COLUMN_POSTER, "/uXZYawqUsChGSj54wcuBtEdUJbh.jpg");
        testValues.put(MovieEntry.COLUMN_TITLE, "Jurassic World");
        testValues.put(MovieEntry.COLUMN_OVERVIEW, "Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning" +
                " dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
        testValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, 7.1);
        // MovieEntry.COLUMN_POPULARITY, 88.551849 don't work
        testValues.put(MovieEntry.COLUMN_POPULARITY, 88.5518);
        testValues.put(MovieEntry.COLUMN_RELEASE_DATE, TEST_DATE);

        return testValues;
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    public static ContentValues createReviewValues(long movieRowId) {
        ContentValues reviewValues = new ContentValues();

        reviewValues.put(ReviewEntry.COLUMN_MOV_KEY, movieRowId);
        reviewValues.put(ReviewEntry.COLUMN_AUTHOR, "Travis Bell");
        reviewValues.put(ReviewEntry.COLUMN_CONTENT, "I felt like this was a tremendous end to Nolan's Batman trilogy. The Dark Knight Rises may very" +
                " well have been the weakest of all 3 films but when you're talking about a scale of this magnitude, it still makes this one of the best" +
                " movies I've seen in the past few years.\\r\\n\\r\\nI expected a little more _Batman_ than we got (especially with a runtime of 2:45)" +
                " but while the story around the fall of Bruce Wayne and Gotham City was good I didn't find it amazing. This might be in fact, one of my" +
                " only criticisms—it was a long movie but still, maybe too short for the story I felt was really being told. I feel confident in saying" +
                " this big of a story could have been split into two movies.\\r\\n\\r\\nThe acting, editing, pacing, soundtrack and overall theme were the" +
                " same 'as-close-to-perfect' as ever with any of Christopher Nolan's other films. Man does this guy know how to make a movie!\\r\\n\\r\\nYou" +
                " don't have to be a Batman fan to enjoy these movies and I hope any of you who feel this way re-consider. These 3 movies are without a doubt" +
                " in my mind, the finest display of comic mythology ever told on the big screen. They are damn near perfect.");

        return reviewValues;
    }

    public static ContentValues createVideoValues(long movieRowId) {
        ContentValues videoValues = new ContentValues();

        videoValues.put(ReviewEntry.COLUMN_MOV_KEY, movieRowId);
        videoValues.put(VideoEntry.COLUMN_TRAILER, "SUXWAEX2jlg");
        videoValues.put(VideoEntry.COLUMN_NAME, "Trailer 1");

        return videoValues;
    }
}
