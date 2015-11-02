package com.denis.home.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsViewFragment extends Fragment {

    private MovieItem movie;

    private final String SCORE_FROM = "/10";

    public DetailsViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_details_view, container, false);

        // The detail Activity called via intent.  Inspect the intent for movie data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(DiscoveryScreenFragment.PARCELABLE_MOVIE_ITEM)) {
            movie = intent.getExtras().getParcelable(DiscoveryScreenFragment.PARCELABLE_MOVIE_ITEM);
            ((TextView) rootView.findViewById(R.id.detail_movie_title))
                    .setText(movie.title);

            ((TextView) rootView.findViewById(R.id.detail_movie_overview))
                    .setText(movie.overview);

            ((TextView) rootView.findViewById(R.id.detail_movie_releseDate))
                    .setText(movie.releaseDate);

            ((TextView) rootView.findViewById(R.id.detail_movie_voteAverage))
                    .setText(String.format("%s: %.2f", getString(R.string.label_voteAverage), movie.voteAverage) + SCORE_FROM);

            ((TextView) rootView.findViewById(R.id.detail_movie_popularity))
                    .setText(String.format("%s: %.2f", getString(R.string.label_popularity), movie.popularity));

            ImageView imageView = ((ImageView) rootView.findViewById(R.id.detail_movie_image));
            Picasso.with(getActivity()).load(movie.poster).into(imageView);

        }

        // Inflate the layout for this fragment
        return rootView;
    }


}
