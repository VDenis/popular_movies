package com.denis.home.popularmovies;


import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.denis.home.popularmovies.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoveryFragment extends Fragment {

    private final String LOG_TAG = DiscoveryFragment.class.getSimpleName();

    // For savedInstanceState
    public static final String PARCELABLE_MOVIE_ITEM = "PARCELABLE_MOVIE_ITEM";

    public static final String EXTRA_MOVIE_ITEM = "EXTRA_MOVIE_ITEM";

    // Load favorites from database true, For AsyncTask
    public static final String LOAD_FAVORITES = "LOAD_FAVORITES_FROM_DB";
    public static final String LOAD_NOT_FAVORITES = "LOAD_NOT_FAVORITES";

    // For ContentProvider
    private static final String[] MOVIE_COLUMNS = {
            //MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_POSTER,
            MoviesContract.MovieEntry.COLUMN_BACKDROP,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MovieEntry.COLUMN_POPULARITY,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE
    };
    //static final int COL__ID = 0;
    static final int COL_COLUMN_MOVIE_ID = 0;
    static final int COL_COLUMN_POSTER = 1;
    static final int COL_COLUMN_BACKDROP = 2;
    static final int COL_COLUMN_TITLE = 3;
    static final int COL_COLUMN_OVERVIEW = 4;
    static final int COL_COLUMN_VOTE_AVERAGE = 5;
    static final int COL_COLUMN_POPULARITY = 6;
    static final int COL_COLUMN_RELEASE_DATE = 7;


    // Grid
    private MovieAdapter mMoviesAdapter;
    private ArrayList<MovieItem> mMovieItems;
    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    // callback interface. activity must implement
    public interface Callback {
        public void onItemSelected(MovieItem movie);
    }

    public DiscoveryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");
        if (savedInstanceState != null && savedInstanceState.containsKey(PARCELABLE_MOVIE_ITEM)) {
            mMovieItems = savedInstanceState.getParcelableArrayList(PARCELABLE_MOVIE_ITEM);
        } else {
            mMovieItems = new ArrayList<MovieItem>();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState");
        if (mMovieItems != null && mMovieItems.isEmpty()) {
            outState.putParcelableArrayList(PARCELABLE_MOVIE_ITEM, mMovieItems);
        }
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView!");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_discovery, container, false);

        mMoviesAdapter = new MovieAdapter(getActivity(), mMovieItems);

        mGridView = (GridView) rootView.findViewById(R.id.movies_grid);
        mGridView.setAdapter(mMoviesAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(LOG_TAG, "Click in movie");
                ((Callback) getActivity()).onItemSelected(mMoviesAdapter.getItem(position));
                mPosition = position;
/*                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(EXTRA_MOVIE_ITEM, mMoviesAdapter.getItem(position));
                startActivity(intent);*/
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }


    private void updatePopularMovies() {
        Log.i(LOG_TAG, "updatePopularMovies");
        FetchMoviesTask popularMoviesTask = new FetchMoviesTask();

        if (Utility.getPreferredSortOrder(getActivity()).equals(getString(R.string.pref_sort_by_favorites))) {
            popularMoviesTask.execute(LOAD_FAVORITES);
        }
        else {
            // Load from internet
            if (Utility.isNetworkAvailable(getActivity())) {
                popularMoviesTask.execute(LOAD_NOT_FAVORITES);
            } else {
                // no internet connection

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart!");

        // TODO or use movie adapter
        if (mMovieItems.isEmpty()) {
            updatePopularMovies();
        }
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private ArrayList<MovieItem> getPopularMoviesDataFromJson(String popularMoviesJsonStr, int numPages)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_ID = "id";
            final String TMDB_RESULTS_LIST = "results";
            final String TMDB_POSTER = "poster_path";
            final String TMDB_BACKDROP = "backdrop_path";
            final String TMDB_TITLE = "original_title";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_POPULARITY = "popularity";
            final String TMDB_RELESE_DATE = "release_date";

            JSONObject popularMoviesJson = new JSONObject(popularMoviesJsonStr);
            JSONArray moviesArrayJson = popularMoviesJson.getJSONArray(TMDB_RESULTS_LIST);

            ArrayList<MovieItem> moviesList = new ArrayList<>();

            for (int i = 0; i < moviesArrayJson.length(); i++) {
                JSONObject movieJson = moviesArrayJson.getJSONObject(i);

                int id = movieJson.getInt(TMDB_ID);

                String poster = "";
                String tempPoster = movieJson.getString(TMDB_POSTER);

                if (!tempPoster.contains("null")) {
                    poster = Constants.MOVIES_BASE_URL + Constants.getNormalImageQuality() + tempPoster;
                }

                String backdrop = "";
                String tempBackdrop = movieJson.getString(TMDB_BACKDROP);

                if (!tempBackdrop.contains("null")) {
                    backdrop = Constants.MOVIES_BASE_URL + Constants.getHighImageQuality() + tempBackdrop;
                }

                String title = movieJson.getString(TMDB_TITLE);
                String overview = movieJson.getString(TMDB_OVERVIEW);
                double voteAverage = movieJson.getDouble(TMDB_VOTE_AVERAGE);
                double popularity = movieJson.getDouble(TMDB_POPULARITY);
                String releaseDate = movieJson.getString(TMDB_RELESE_DATE);

                moviesList.add(new MovieItem(id, poster, backdrop, title, overview, voteAverage, popularity, releaseDate));
            }

/*            Log.d(LOG_TAG, "Sort by: " + sortBy);
            if (sortBy.equals(getString(R.string.pref_sort_by_most_popular))) {
                Collections.sort(moviesList, MovieItem.COMPARE_BY_POPULARITY_DESC);
            } else if (sortBy.equals(getString(R.string.pref_sort_by_highest_rated))) {
                Collections.sort(moviesList, MovieItem.COMPARE_BY_VOTE_AVERAGE_DESC);
            } else {
                Log.d(LOG_TAG, "SortBy not found: " + sortBy);
            }*/

            return moviesList;
        }

        @Override
        protected ArrayList<MovieItem> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            // load from internet
            if (params[0].equals(LOAD_NOT_FAVORITES)) {

                Log.i(LOG_TAG, "doInBackground");

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String popularMoviesJsonStr = null;

                try {
                    final String POPULAR_MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
                    final String SORT_BY_PARAM = "sort_by";

                    // Sort by "popularity.desc" - default value
                    String sortBy = "popularity.desc";
                    final String API_KEY_PARAM = "api_key";

                    // Read sort by parameter from SharedPreferences
                    String sortBySetting = Utility.getPreferredSortOrder(getActivity());

                    Log.d(LOG_TAG, "Sort by: " + sortBySetting);
                    if (sortBySetting.equals(getString(R.string.pref_sort_by_most_popular))) {
                        ;
                    } else if (sortBySetting.equals(getString(R.string.pref_sort_by_highest_rated))) {
                        sortBy = "vote_average.desc";
                    } else {
                        Log.d(LOG_TAG, "SortBy not found: " + sortBySetting);
                    }

                    Uri builtUri = Uri.parse(POPULAR_MOVIES_BASE_URL).buildUpon()
                            .appendQueryParameter(SORT_BY_PARAM, sortBy)
                            .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_TOKEN)
                            .build();

                    URL url = new URL(builtUri.toString());

                    Log.d(LOG_TAG, "Built URI " + builtUri.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    popularMoviesJsonStr = buffer.toString();

                    Log.d(LOG_TAG, "Forecast string: " + popularMoviesJsonStr);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error", e);
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }

                try {
                    return getPopularMoviesDataFromJson(popularMoviesJsonStr, 1);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            } // load from content provider
            else {
                Cursor mCursor = getActivity().getContentResolver().query(
                        MoviesContract.MovieEntry.CONTENT_URI,   // The content URI of the words table
                        MOVIE_COLUMNS,                        // The columns to return for each row
                        null,                    // Selection criteria
                        null,                     // Selection criteria
                        null);                        // The sort order for the returned rows
                // Some providers return null if an error occurs, others throw an exception
                if (mCursor == null) {
                /*
                 * Insert code here to handle the error. Be sure not to use the cursor! You may want to
                 * call android.util.Log.e() to log this error.
                 *
                 */
                // If the Cursor is empty, the provider found no matches
                } else if (mCursor.getCount() < 1) {

                /*
                 * Insert code here to notify the user that the search was unsuccessful. This isn't necessarily
                 * an error. You may want to offer the user the option to insert a new row, or re-type the
                 * search term.
                 */

                } else {
                    // Insert code here to do something with the results

                    ArrayList<MovieItem> moviesList = new ArrayList<>();
                    while (mCursor.moveToNext()) {
                        // Gets the value from the column.

                        int id = mCursor.getInt(COL_COLUMN_MOVIE_ID);

                        // TODO: check poster
                        String poster = "";
                        String tempPoster = mCursor.getString(COL_COLUMN_POSTER);

                        if (!tempPoster.contains("null")) {
                            poster = Constants.MOVIES_BASE_URL + Constants.getNormalImageQuality() + tempPoster;
                        }

                        String backdrop = "";
                        String tempBackdrop = mCursor.getString(COL_COLUMN_BACKDROP);

                        if (!tempBackdrop.contains("null")) {
                            backdrop = Constants.MOVIES_BASE_URL + Constants.getNormalImageQuality() + tempBackdrop;
                        }

                        String title = mCursor.getString(COL_COLUMN_TITLE);
                        String overview = mCursor.getString(COL_COLUMN_OVERVIEW);
                        double voteAverage = mCursor.getDouble(COL_COLUMN_VOTE_AVERAGE);
                        double popularity = mCursor.getDouble(COL_COLUMN_POPULARITY);
                        String releseDate = mCursor.getString(COL_COLUMN_RELEASE_DATE);

                        moviesList.add(new MovieItem(id, poster, backdrop, title, overview, voteAverage, popularity, releseDate));
                        // end of while loop
                    }
                    mCursor.close();
                    return moviesList;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieItem> movies) {
            //super.onPostExecute(mMovieItems);
            Log.i(LOG_TAG, "onPostExecute");

            mMoviesAdapter.clear();

            if (movies != null) {
                mMoviesAdapter.addAll(movies);
            }

            // scroll to selected position or to first position
            if (mPosition != GridView.INVALID_POSITION && mPosition >= GridView.SCROLLBAR_POSITION_DEFAULT && mPosition < mMoviesAdapter.getCount()) {
                mGridView.smoothScrollToPosition(mPosition);
            }
            // open first movie grid in detail page
/*            else if (!mMoviesAdapter.isEmpty()) {
                final int position = GridView.SCROLLBAR_POSITION_DEFAULT;
                mGridView.smoothScrollToPosition(position);
                ((Callback) getActivity()).onItemSelected(mMoviesAdapter.getItem(position));
            }*/
        }
    }
}
