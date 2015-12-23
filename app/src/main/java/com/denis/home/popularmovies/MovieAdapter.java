package com.denis.home.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Denis on 30.10.2015.
 */
public class MovieAdapter extends ArrayAdapter<MovieItem> {
    public MovieAdapter(Activity context, List<MovieItem> androidMovies) {
        super(context, 0, androidMovies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieItem androidMovie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.discovery_movie_image);
        if (!androidMovie.poster.isEmpty()) {

            //Picasso.with(getContext()).load(androidMovie.poster).into(iconView);
            Picasso.with(getContext()).load(androidMovie.poster).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iconView);
        }
        else {
            iconView.setImageResource(R.mipmap.ic_launcher);
        }

        return convertView;
    }
}
