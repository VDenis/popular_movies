package com.denis.home.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Denis on 01.01.2016.
 */
public class TrailerAdapter extends ArrayAdapter<TrailerItem> {
    private final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    // View lookup cache
    private static class ViewHolder {
        public final ImageView imageView;
        public final TextView textView;

        public ViewHolder(View view){
            imageView = (ImageView) view.findViewById(R.id.detail_trailer_image);
            textView = (TextView) view.findViewById(R.id.discovery_movie_text);
        }
    }

    public TrailerAdapter(Activity context, List<TrailerItem> trailerItems) {
        super(context, 0, trailerItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.trailer_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TrailerItem trailerItem = getItem(position);

        // Trailer
        if (!trailerItem.thumbnail.isEmpty()) {
            // Set image 1
            Picasso.with(getContext()).load(trailerItem.thumbnail).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(viewHolder.imageView);
        }
        else {
            // Set image 2
            viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
        }

        // Set text
        viewHolder.textView.setText(trailerItem.name);

        return convertView;
    }
}
