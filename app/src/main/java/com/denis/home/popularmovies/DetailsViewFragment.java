package com.denis.home.popularmovies;


import android.app.Fragment;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.denis.home.popularmovies.data.MoviesContract;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.squareup.picasso.Picasso;

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
public class DetailsViewFragment extends Fragment {

    private final String LOG_TAG = DetailsViewFragment.class.getSimpleName();

    private MovieItem movie;
    private final String SCORE_FROM = "/10";
    private boolean isFavorite = false;

    MenuItem favoriteButton; // favorite button

    private ReviewAdapter mReviewAdapter;
    ArrayList<ReviewItem> reviews;

    // For savedInstanceState
    public static final String PARCELABLE_REVIEW_ITEM = "PARCELABLE_REVIEW_ITEM";

    public DetailsViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState == null || !savedInstanceState.containsKey(PARCELABLE_REVIEW_ITEM)) {
            reviews = new ArrayList<ReviewItem>();
        } else {
            reviews = savedInstanceState.getParcelableArrayList(PARCELABLE_REVIEW_ITEM);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PARCELABLE_REVIEW_ITEM, reviews);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movie_detail, menu);
        favoriteButton = menu.findItem(R.id.action_favorite);
        // Set favorite for current movie
        setFavoriteStatus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            if (isFavorite) {
                deleteFromFavorite();
            } else {
                // Add to favorites
                addToFavorite();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        /*View rootView = inflater.inflate(R.layout.fragment_details_view, container, false);*/
        View rootView = inflater.inflate(R.layout.new_fragment_details_view, container, false);

        // The detail Activity called via intent.  Inspect the intent for movie data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(DiscoveryScreenFragment.EXTRA_MOVIE_ITEM)) {

            movie = intent.getExtras().getParcelable(DiscoveryScreenFragment.EXTRA_MOVIE_ITEM);

            ImageView backdrop_imageView = ((ImageView) rootView.findViewById(R.id.detail_backdrop_movie_image));
            if (!movie.backdrop.isEmpty()) {
                //Picasso.with(getActivity()).load(movie.backdrop).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(backdrop_imageView);
                Picasso.with(getActivity()).load(movie.backdrop).into(backdrop_imageView);
            } else {
                //backdrop_imageView.setImageResource(R.mipmap.ic_launcher);
            }
            backdrop_imageView.requestFocus();

            ((TextView) rootView.findViewById(R.id.detail_movie_title))
                    .setText(movie.title);

            ((TextView) rootView.findViewById(R.id.detail_movie_overview))
                    .setText(movie.overview);

            ((TextView) rootView.findViewById(R.id.detail_movie_releseDate))
                    .setText(movie.releaseDate);

            ((TextView) rootView.findViewById(R.id.detail_movie_voteAverage))
                    .setText(String.format("%s: %.1f", getString(R.string.label_voteAverage), movie.voteAverage) + SCORE_FROM);

            ((TextView) rootView.findViewById(R.id.detail_movie_popularity))
                    .setText(String.format("%s: %.2f", getString(R.string.label_popularity), movie.popularity));


            ImageView poster_imageView = ((ImageView) rootView.findViewById(R.id.detail_poster_movie_image));
            if (!movie.poster.isEmpty()) {
                //Picasso.with(getActivity()).load(movie.poster).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(poster_imageView);
                Picasso.with(getActivity()).load(movie.poster).into(poster_imageView);
            } else {
                //poster_imageView.setImageResource(R.mipmap.ic_launcher);
            }

            FetchReviewTask popularMoviesTask = new FetchReviewTask();
            popularMoviesTask.execute(String.valueOf(movie.id));
            mReviewAdapter = new ReviewAdapter(getActivity(), reviews);

            ExpandableHeightListView expandableListView = (ExpandableHeightListView ) rootView.findViewById(R.id.detail_movie_review_list);
            expandableListView.setAdapter(mReviewAdapter);

            // This actually does the magic
            expandableListView.setExpanded(true);
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    public void setFavoriteStatus() {
        AsyncQueryHandler mHandler;

        Log.d(LOG_TAG, "setFavoriteStatus " + movie.title);

        // A "projection" defines the columns that will be returned for each row
        String[] mProjection = {MoviesContract.MovieEntry.COLUMN_MOVIE_ID};

        //String mSelection = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " LIKE ?";
        String mSelection = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " LIKE ?";
        String[] mSelectionArgs = {String.valueOf(movie.id)};

        mHandler = new MyAsyncQueryHandler(getActivity().getContentResolver());
        mHandler.startQuery(0, null, MoviesContract.MovieEntry.CONTENT_URI, mProjection, mSelection, mSelectionArgs, null);
    }

    class MyAsyncQueryHandler extends AsyncQueryHandler {
        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            //if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0) {
                changeFavoriteStatus(true);
            } else {
                changeFavoriteStatus(false);
            }
            Log.i(LOG_TAG, "favorite status: " + isFavorite);
            cursor.close();
            super.onQueryComplete(token, cookie, cursor);
        }
    }

    public void changeFavoriteStatus(boolean status) {
        if (status)
            favoriteButton.setIcon(R.drawable.star);
        else
            favoriteButton.setIcon(R.drawable.star_outline);
        isFavorite = status;
    }


    void deleteFromFavorite() {
        AsyncQueryHandler mHandler;

        Log.d(LOG_TAG, "deleteFromFavorite " + movie.title);

        String mSelection = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " LIKE ?";
        String[] mSelectionArgs = {String.valueOf(movie.id)};

        mHandler = new MyAsyncDeleteHandler(getActivity().getContentResolver());
        mHandler.startDelete(0, null, MoviesContract.MovieEntry.CONTENT_URI, mSelection, mSelectionArgs);
    }

    class MyAsyncDeleteHandler extends AsyncQueryHandler {
        public MyAsyncDeleteHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            super.onDeleteComplete(token, cookie, result);
            changeFavoriteStatus(false);
        }
    }

    void addToFavorite() {
        AsyncQueryHandler mHandler;

        Log.d(LOG_TAG, "addToFavorite " + movie.title);

        ContentValues insertMovie = new ContentValues();
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movie.id);
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_POSTER, movie.poster);
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_BACKDROP, movie.backdrop);
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.title);
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.overview);
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.voteAverage);
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, movie.popularity);
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.releaseDate);

        mHandler = new MyAsyncInsertHandler(getActivity().getContentResolver());
        mHandler.startInsert(0, null, MoviesContract.MovieEntry.CONTENT_URI, insertMovie);
    }

    class MyAsyncInsertHandler extends AsyncQueryHandler {

        public MyAsyncInsertHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            super.onInsertComplete(token, cookie, uri);
            changeFavoriteStatus(true);
        }
    }


    // load reviews and trailers
    public class FetchReviewTask extends AsyncTask<String, Void, ArrayList<ReviewItem>> {
        private final String LOG_TAG = FetchReviewTask.class.getSimpleName();

        private ArrayList<ReviewItem> getMovieReviewDataFromJson(String movieReviewJsonStr, int numPages)
                throws JSONException {

            Log.i(LOG_TAG, "getMovieReviewDataFromJson");

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS_LIST = "results";
            final String TMDB_AUTHOR = "author";
            final String TMDB_CONTENT = "content";

            JSONObject movieReviewJson = new JSONObject(movieReviewJsonStr);
            JSONArray reviewArrayJson = movieReviewJson.getJSONArray(TMDB_RESULTS_LIST);

            ArrayList<ReviewItem> reviewList = new ArrayList<>();

            for (int i = 0; i < reviewArrayJson.length(); i++) {
                JSONObject movieJson = reviewArrayJson.getJSONObject(i);

                String author = movieJson.getString(TMDB_AUTHOR);
                String content = movieJson.getString(TMDB_CONTENT);

                reviewList.add(new ReviewItem(author, content));
            }
            return reviewList;
        }

        @Override
        protected ArrayList<ReviewItem> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            // read movie id
            final String movie_id = params[0];

            Log.i(LOG_TAG, "doInBackground");

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieReviewJsonStr = null;

            try {
                final String POPULAR_MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie";

                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(POPULAR_MOVIES_BASE_URL).buildUpon()
                        .appendPath(movie_id)
                        .appendPath("reviews")
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
                movieReviewJsonStr = buffer.toString();

                Log.d(LOG_TAG, "Movie review string: " + movieReviewJsonStr);
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
                return getMovieReviewDataFromJson(movieReviewJsonStr, 1);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ReviewItem> reviewItems) {
            //super.onPostExecute(movies);
            Log.i(LOG_TAG, "onPostExecute");

            mReviewAdapter.clear();

            if (reviewItems != null) {
                mReviewAdapter.addAll(reviewItems);
            }
        }
    }
}
