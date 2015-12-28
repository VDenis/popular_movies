package com.denis.home.popularmovies;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Denis on 28.12.2015.
 */
public class ReviewAdapter extends ArrayAdapter<ReviewItem> {
    private final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    // View lookup cache
    private static class ViewHolder {
        public final TextView reviewAuthor;
        public final TextView reviewContent;

        public ViewHolder(View view){
            reviewAuthor = (TextView) view.findViewById(R.id.details_review_author);
            reviewContent = (TextView) view.findViewById(R.id.details_review_content);
        }
    }

    public ReviewAdapter(Activity context, List<ReviewItem> reviewList) {
        super(context, 0, reviewList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.review_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ReviewItem reviewItem = getItem(position);

        // Set text
        viewHolder.reviewAuthor.setText(reviewItem.author);
        viewHolder.reviewContent.setText(reviewItem.content);

        Log.d(LOG_TAG, position + " - " + convertView + " - " + reviewItem.author);

        return convertView;
    }
}
