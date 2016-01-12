package com.denis.home.popularmovies;

/**
 * Created by Denis on 23.12.2015.
 */

// Unused class
public class NewMovieAdapter {}

/*public class NewMovieAdapter extends CursorAdapter {
    private static final String LOG_TAG = NewMovieAdapter.class.getSimpleName();
    private Context mContext;
    private static int sLoaderID;

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView textView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.discovery_movie_image);
            textView = (TextView) view.findViewById(R.id.discovery_movie_text);
        }
    }

    public NewMovieAdapter(Context context, Cursor c, int flags, int loaderID) {
        super(context, c, flags);
        Log.d(LOG_TAG, "MovAdapter");
        mContext = context;
        sLoaderID = loaderID;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.movie_item;

        Log.d(LOG_TAG, "In new View");

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Log.d(LOG_TAG, "In bind View");

        int movieTitleCI = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE);
        final String movieTitle = cursor.getString(movieTitleCI);
        viewHolder.textView.setText(movieTitle);

        int moviePosterCI = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER);
        int moviePoster = cursor.getInt(moviePosterCI);
        Picasso.with(context).load(moviePoster).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(viewHolder.imageView);
    }
}*/
