package com.denis.home.popularmovies;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
public class DiscoveryScreenFragment extends Fragment {

    private final String LOG_TAG = DiscoveryScreenFragment.class.getSimpleName();
    public static final String PARCELABLE_MOVIE_ITEM = "PARCELABLE_MOVIE_ITEM";

    private MovieAdapter mMoviesAdapter;

    public DiscoveryScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView!");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_discovery_screen, container, false);

        ArrayList<MovieItem> movies = new ArrayList<MovieItem>();
        mMoviesAdapter = new MovieAdapter(getActivity(), movies);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(mMoviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(LOG_TAG, "Click in movie");

                Intent intent = new Intent(getActivity(), DetailsViewActivity.class).putExtra(PARCELABLE_MOVIE_ITEM, mMoviesAdapter.getItem(position));
                startActivity(intent);
            }
        });

        return rootView;
    }


    private void updatePopularMovies() {
        FetchPopularMoviesTask popularMoviesTask = new FetchPopularMoviesTask();
        popularMoviesTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart!");
        updatePopularMovies();
    }


    public class FetchPopularMoviesTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {
        private final String LOG_TAG = FetchPopularMoviesTask.class.getSimpleName();

        private ArrayList<MovieItem> getPopularMoviesDataFromJson(String popularMoviesJsonStr, int numPages)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.

            final String TMDB_ID = "id";
            final String TMDB_RESULTS_LIST = "results";
            final String TMDB_POSTER= "poster_path";
            final String TMDB_TITLE = "original_title";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_RELESE_DATE = "release_date";

            JSONObject popularMoviesJson = new JSONObject(popularMoviesJsonStr);
            JSONArray moviesArrayJson = popularMoviesJson.getJSONArray(TMDB_RESULTS_LIST);

            ArrayList<MovieItem> moviesList = new ArrayList<>();

            for(int i = 0; i < moviesArrayJson.length(); i++) {
                JSONObject movieJson = moviesArrayJson.getJSONObject(i);

                int id = movieJson.getInt(TMDB_ID);
                String poster = Constants.MOVIES_BASE_URL + Constants.getImageQuality() + movieJson.getString(TMDB_POSTER);
                String title = movieJson.getString(TMDB_TITLE);
                String overview = movieJson.getString(TMDB_OVERVIEW);
                double voteAverage = movieJson.getDouble(TMDB_VOTE_AVERAGE);
                String releseDate = movieJson.getString(TMDB_RELESE_DATE);

                moviesList.add(new MovieItem(id, poster, title, overview, voteAverage, releseDate));
            }

            return moviesList;
        }

        @Override
        protected ArrayList<MovieItem> doInBackground(String... params) {
/*            if (params.length == 0) {
                return null;
            }*/

            Log.i(LOG_TAG, "doInBackground");

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String popularMoviesJsonStr = null;

            try {
                final String POPULAR_MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
                final String SORT_BY_PARAM = "sort_by";
                String sortBy = "popularity.desc";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(POPULAR_MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, sortBy)
                        .appendQueryParameter(API_KEY_PARAM, Constants.THE_MOVIE_DB_API_TOKEN)
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

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieItem> movies) {
            //super.onPostExecute(movies);
            Log.i(LOG_TAG, "onPostExecute");

            if (movies != null) {
                mMoviesAdapter.clear();
                for(MovieItem movie : movies) {
                    mMoviesAdapter.add(movie);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}
