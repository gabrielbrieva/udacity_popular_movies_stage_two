package com.tuxan.udacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tuxan.udacity.popularmovies.model.Movie;

public class DetailFragment extends Fragment {

    Movie movie;

    public DetailFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Utils.MOVIE_DETAIL_KEY, movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Utils.MOVIE_DETAIL_KEY)) {
            movie = (Movie) intent.getSerializableExtra(Utils.MOVIE_DETAIL_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_detail, container, false);

        if (savedInstanceState != null && movie == null && savedInstanceState.containsKey(Utils.MOVIE_DETAIL_KEY)) {
            movie = (Movie) savedInstanceState.getSerializable(Utils.MOVIE_DETAIL_KEY);
        }

        if (movie == null) {
            // TODO: Show error getting movie data ...

            return view;
        }

        TextView tv = (TextView) view.findViewById(R.id.tvOriginalTitle);
        tv.setText(movie.getOriginal_title());

        tv = (TextView) view.findViewById(R.id.tvOverview);
        tv.setText(movie.getOverview());

        tv = (TextView) view.findViewById(R.id.tvDateRelease);
        tv.setText(getString(R.string.movie_detail_release_date) + ": " + movie.getRelease_date());

        tv = (TextView) view.findViewById(R.id.tvVoteAverage);
        tv.setText(getString(R.string.movie_detail_rating) + ": " + movie.getVote_average());

        final ImageView ivPoster = (ImageView) view.findViewById(R.id.ivDetailPoster);

        // create an instance of Picasso using the context
        Picasso p = Picasso.with(getActivity());

        // debugging purpose
        // p.setLoggingEnabled(true);

        // load the backdrop image
        p.load(Utils.IMG_END_POINT + "w185" + movie.getPoster_path())
                // if the image don't exist we use a default drawable
                .error(R.drawable.poster_missing)
                // put the result image in poster ImageView
                .into(ivPoster);

        return view;
    }
}
