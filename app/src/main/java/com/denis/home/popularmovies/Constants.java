package com.denis.home.popularmovies;

/**
 * Created by Denis on 30.10.2015.
 */
public class Constants {

    public static final String MOVIES_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String[] IMAGE_QUALITY = {"w92", "w154", "w185", "w342", "w500", "w780", "original"};

    public static String getNormalImageQuality() {
        return IMAGE_QUALITY[2];
    }

    public static String getHighImageQuality() {
        return IMAGE_QUALITY[5];
    }

}
