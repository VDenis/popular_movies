package com.denis.home.popularmovies;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Denis on 30.10.2015.
 */
public class MovieAdapter extends ArrayAdapter<MovieItem> {
    private final String LOG_TAG = MovieAdapter.class.getSimpleName();

    // View lookup cache
    private static class ViewHolder {
        public final ImageView imageView;
        public final TextView textView;

        public ViewHolder(View view){
            imageView = (ImageView) view.findViewById(R.id.discovery_movie_image);
            textView = (TextView) view.findViewById(R.id.discovery_movie_text);
        }
    }

    public MovieAdapter(Activity context, List<MovieItem> androidMovies) {
        super(context, 0, androidMovies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MovieItem androidMovie = getItem(position);

        // Movie have poster
        if (!androidMovie.poster.isEmpty()) {
            // Set image 1
            Picasso.with(getContext()).load(androidMovie.poster).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(viewHolder.imageView);
        }
        else {
            // Set image 2
            viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
        }

        // Set text
        viewHolder.textView.setText(androidMovie.title);
        Log.d(LOG_TAG, position + " - " + convertView + " - " + androidMovie.title);

        return convertView;
    }
}
