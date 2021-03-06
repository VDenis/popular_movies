package com.denis.home.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    private MovieItem mMovie;
    private final String SCORE_FROM = "/10";
    private boolean isFavorite = false;

    // favorite button
    MenuItem favoriteButton;

    // Review
    private ReviewAdapter mReviewAdapter;
    ArrayList<ReviewItem> mReviewItems;

    // Trailer
    private TrailerAdapter mTrailerAdapter;
    ArrayList<TrailerItem> mTrailerItems;
    final String TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";

    // find view by id
    private ImageView mBackdropImageView;
    private ImageView mPosterImageView;
    private TextView mDetailMovieTitle;
    private TextView mDetailMovieOverview;
    private TextView mDetailMovieReleseDate;
    private TextView mDetailMovieVoteAverage;
    private TextView mDetailMoviePopularity;

    // For savedInstanceState
    public static final String PARCELABLE_REVIEW_ITEM = "PARCELABLE_REVIEW_ITEM";
    public static final String PARCELABLE_TRAILER_ITEM = "PARCELABLE_TRAILER_ITEM";

    // Share
    private static final String APP_SHARE_HASHTAG = " #PopularMovies";
    private String mShareTrailer;
    private ShareActionProvider mShareActionProvider;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");
        setHasOptionsMenu(true);
        if (savedInstanceState == null || !savedInstanceState.containsKey(PARCELABLE_REVIEW_ITEM)) {
            mReviewItems = new ArrayList<ReviewItem>();
        } else {
            mReviewItems = savedInstanceState.getParcelableArrayList(PARCELABLE_REVIEW_ITEM);
        }

        if (savedInstanceState == null || !savedInstanceState.containsKey(PARCELABLE_TRAILER_ITEM)) {
            mTrailerItems = new ArrayList<TrailerItem>();
        } else {
            mTrailerItems = savedInstanceState.getParcelableArrayList(PARCELABLE_TRAILER_ITEM);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState");
        outState.putParcelableArrayList(PARCELABLE_REVIEW_ITEM, mReviewItems);
        outState.putParcelableArrayList(PARCELABLE_TRAILER_ITEM, mTrailerItems);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movie_detail, menu);
        favoriteButton = menu.findItem(R.id.action_favorite);
        // Set favorite for current mMovie
        setFavoriteStatus();

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mShareTrailer != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        }
    }

    // Share
    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");

        //if (mMovie != null && !mTrailerItems.isEmpty() ) {
/*            shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie.title + ", " + mTrailerItems.get(0).name
                    + ", TRAILER_BASE_URL" + mTrailerItems.get(0).link);    */
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareTrailer + APP_SHARE_HASHTAG);

        return shareIntent;
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
        Log.i(LOG_TAG, "onCreateView");
        // Inflate the layout for this fragment
        /*View rootView = inflater.inflate(R.layout.fragment_detail, container, false);*/
        final View rootView = inflater.inflate(R.layout.new_fragment_detail, container, false);

        // The detail Activity called via intent.  Inspect the intent for mMovie data.
        //Intent intent = getActivity().getIntent();
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = arguments.getParcelable(DiscoveryFragment.EXTRA_MOVIE_ITEM);
/*        }
        if (intent != null && intent.hasExtra(DiscoveryFragment.EXTRA_MOVIE_ITEM)) {
            mMovie = intent.getExtras().getParcelable(DiscoveryFragment.EXTRA_MOVIE_ITEM);*/

            mBackdropImageView = ((ImageView) rootView.findViewById(R.id.detail_backdrop_movie_image));
            // Request focus for image, otherwise list view get focus
            mBackdropImageView.requestFocus();
            mDetailMovieTitle = ((TextView) rootView.findViewById(R.id.detail_movie_title));
            mDetailMovieOverview = ((TextView) rootView.findViewById(R.id.detail_movie_overview));
            mDetailMovieReleseDate = ((TextView) rootView.findViewById(R.id.detail_movie_releseDate));
            mDetailMovieVoteAverage = ((TextView) rootView.findViewById(R.id.detail_movie_voteAverage));
            mDetailMoviePopularity = ((TextView) rootView.findViewById(R.id.detail_movie_popularity));
            mPosterImageView = ((ImageView) rootView.findViewById(R.id.detail_poster_movie_image));

            // Trailer
            mTrailerAdapter = new TrailerAdapter(getActivity(), mTrailerItems);
            ExpandableHeightGridView gridView = (ExpandableHeightGridView) rootView.findViewById(R.id.detail_movie_trailer_grid);
            gridView.setAdapter(mTrailerAdapter);
            gridView.setExpanded(true);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(LOG_TAG, "Click in trailer");
                    TrailerItem trailer = mTrailerAdapter.getItem(position);
                    playMovieInYoutube(trailer.link);
                }
            });

            // Review
            mReviewAdapter = new ReviewAdapter(getActivity(), mReviewItems);
            ExpandableHeightListView expandableListView = (ExpandableHeightListView) rootView.findViewById(R.id.detail_movie_review_list);
            expandableListView.setAdapter(mReviewAdapter);
            // This actually does the magic for listview in scrollview
            expandableListView.setExpanded(true);
        }

        if (mMovie != null) {
            String movieTitle = mMovie.title;
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(movieTitle);
            mDetailMovieTitle.setText(movieTitle);
            mDetailMovieOverview.setText(mMovie.overview);
            mDetailMovieReleseDate.setText(mMovie.releaseDate);
            mDetailMovieVoteAverage.setText(String.format("%s: %.1f", getString(R.string.label_voteAverage), mMovie.voteAverage) + SCORE_FROM);
            mDetailMoviePopularity.setText(String.format("%s: %.2f", getString(R.string.label_popularity), mMovie.popularity));
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onActivityCreated");
        if (mMovie != null) {
            if (!mMovie.backdrop.isEmpty()) {
                //Picasso.with(getActivity()).load(mMovie.backdrop).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(mBackdropImageView);
                Picasso.with(getActivity()).load(mMovie.backdrop).into(mBackdropImageView);
            } else {
                //mBackdropImageView.setImageResource(R.mipmap.ic_launcher);
            }
            if (!mMovie.poster.isEmpty()) {
                //Picasso.with(getActivity()).load(mMovie.poster).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(mPosterImageView);
                Picasso.with(getActivity()).load(mMovie.poster).into(mPosterImageView);
            } else {
                //mPosterImageView.setImageResource(R.mipmap.ic_launcher);
            }
/*            FetchReviewTask popularMoviesTask = new FetchReviewTask();
            popularMoviesTask.execute(String.valueOf(mMovie.id));*/
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMovie != null) {
            FetchReviewTask popularMoviesTask = new FetchReviewTask();
            if (Utility.isNetworkAvailable(getActivity())) {
                popularMoviesTask.execute(String.valueOf(mMovie.id));
            } else {
                // no internet connection
            }
        }
    }

    public void setFavoriteStatus() {
        if (mMovie == null)
            return;

        AsyncQueryHandler mHandler;

        Log.d(LOG_TAG, "setFavoriteStatus " + mMovie.title);

        // A "projection" defines the columns that will be returned for each row
        String[] mProjection = {MoviesContract.MovieEntry.COLUMN_MOVIE_ID};

        //String mSelection = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " LIKE ?";
        String mSelection = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " LIKE ?";
        String[] mSelectionArgs = {String.valueOf(mMovie.id)};

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
        if (mMovie == null)
            return;

        AsyncQueryHandler mHandler;

        Log.d(LOG_TAG, "deleteFromFavorite " + mMovie.title);

        String mSelection = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " LIKE ?";
        String[] mSelectionArgs = {String.valueOf(mMovie.id)};

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
        if (mMovie == null)
            return;

        Log.d(LOG_TAG, "addToFavorite " + mMovie.title);

        ContentValues insertMovie = new ContentValues();
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.id);
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_POSTER, mMovie.poster);
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_BACKDROP, mMovie.backdrop);
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_TITLE, mMovie.title);
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, mMovie.overview);
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, mMovie.voteAverage);
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, mMovie.popularity);
        insertMovie.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.releaseDate);

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


    // load mReviewItems and trailers
    public class FetchReviewTask extends AsyncTask<String, Void, Wrapper> {
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

        private ArrayList<TrailerItem> getMovieTrailerDataFromJson(String movieTrailerJsonStr, int numPages)
                throws JSONException {

            Log.i(LOG_TAG, "getMovieTrailerDataFromJson");

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS_LIST = "results";
            final String TMDB_NAME = "name";
            final String TMDB_KEY = "key";

//            final String TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";
            final String THUMBNAIL_URL_PREFIX = "http://img.youtube.com/vi/";
            final String THUMBNAIL_URL_SUFFIX = "/0.jpg";

            JSONObject movieTrailerJson = new JSONObject(movieTrailerJsonStr);
            JSONArray trailerArrayJson = movieTrailerJson.getJSONArray(TMDB_RESULTS_LIST);

            ArrayList<TrailerItem> trailerList = new ArrayList<>();

            for (int i = 0; i < trailerArrayJson.length(); i++) {
                JSONObject movieJson = trailerArrayJson.getJSONObject(i);

                String name = movieJson.getString(TMDB_NAME);
                String key = movieJson.getString(TMDB_KEY);

                // thumbnail http://img.youtube.com/vi/SUXWAEX2jlg/0.jpg
                // youtube video https://www.youtube.com/watch?v=SUXWAEX2jlg
                trailerList.add(new TrailerItem(key, name, THUMBNAIL_URL_PREFIX + key + THUMBNAIL_URL_SUFFIX));
            }
            return trailerList;
        }

        @Override
        protected Wrapper doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            // read mMovie id
            final String movie_id = params[0];

            Log.i(LOG_TAG, "doInBackground");

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String resultJsonStr = null;
            Wrapper result = new Wrapper();

            // if i == 0 Reviews
            // if i == 1 Trailers
            for (int i = 0; i < 2; i++) {
                try {
                    final String POPULAR_MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie";
                    final String API_KEY_PARAM = "api_key";
                    final String TARGET = i == 0 ? "reviews" : "videos";

                    Uri builtUri = Uri.parse(POPULAR_MOVIES_BASE_URL).buildUpon()
                            .appendPath(movie_id)
                            .appendPath(TARGET)
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
                    resultJsonStr = buffer.toString();

                    Log.d(LOG_TAG, "Movie review string: " + resultJsonStr);
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
                    if (i == 0) {
                        result.reviewItems = getMovieReviewDataFromJson(resultJsonStr, 1);
                    } else if (i == 1) {
                        result.trailerItems = getMovieTrailerDataFromJson(resultJsonStr, 1);
                        return result;
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Wrapper result) {
            //super.onPostExecute(mMovieItems);
            Log.i(LOG_TAG, "onPostExecute");
            ArrayList<ReviewItem> reviewItems = result.reviewItems != null ? result.reviewItems : null;
            ArrayList<TrailerItem> trailerItems = result.trailerItems != null ? result.trailerItems : null;

            mReviewAdapter.clear();
            mTrailerAdapter.clear();

            if (reviewItems != null) {
                mReviewAdapter.addAll(reviewItems);
            }

            if (trailerItems != null) {
                mTrailerAdapter.addAll(trailerItems);
            }

            // update share intent
            // We still need this for the share intent
            //mMovie.title + ", " + mTrailerItems.get(0).name + ", TRAILER_BASE_URL" + mTrailerItems.get(0).link
            if (mMovie != null && !mTrailerItems.isEmpty()) {
                mShareTrailer = String.format("%s - %s, %s", mMovie.title, mTrailerItems.get(0).name, TRAILER_BASE_URL + mTrailerItems.get(0).link);

                // If onCreateOptionsMenu has already happened, we need to update the share intent now.
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareMovieIntent());
                }
            }
        }
    }


    // Pair Trailer and Review
    public class Wrapper {
        public ArrayList<TrailerItem> trailerItems;
        public ArrayList<ReviewItem> reviewItems;
    }


    // launch youtube application via intent
    void playMovieInYoutube(String youtubeLinkId) {
/*            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + youtubeLinkId));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
        Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeLinkId));
        //intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent1.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent1);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + youtubeLinkId + ", no receiving apps installed!");
            //"http://www.youtube.com/watch?v="
            Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(TRAILER_BASE_URL + youtubeLinkId));
            //intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent2);
        }
    }

    // another method
    public void watchYoutubeVideo(String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            //"http://www.youtube.com/watch?v="
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(TRAILER_BASE_URL + id));
            startActivity(intent);
        }
    }
}
