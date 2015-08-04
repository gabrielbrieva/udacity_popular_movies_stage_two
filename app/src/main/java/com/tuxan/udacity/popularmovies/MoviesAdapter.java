package com.tuxan.udacity.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tuxan.udacity.popularmovies.model.Movie;

import java.util.List;

public class MoviesAdapter extends ArrayAdapter<Movie> {

    // path for load poster image
    private static final String IMG_ROOT_PATH = "http://image.tmdb.org/t/p/w185";
    // default image size, all image result must have the same size
    private static int DEFAULT_IMG_WIDTH = 185;
    private static int DEFAULT_IMG_HEIGHT = 278;

    public MoviesAdapter(Context context, int layout, List<Movie> movies) {
        super(context, layout, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.movie_poster, parent, false);
        }

        // we set the title to TextView
        final TextView title = (TextView) convertView.findViewById(R.id.tv_movie_title);
        title.setVisibility(View.GONE);
        title.setText(movie.getOriginal_title());

        ImageView poster = (ImageView) convertView.findViewById(R.id.iv_movie_poster);

        if (movie.getPoster_path() != null) {

            // create an instance of Picasso using the context
            Picasso p = Picasso.with(getContext());

            // debugging purpose
            p.setLoggingEnabled(true);

            // load the poster image
            p.load(IMG_ROOT_PATH + movie.getPoster_path())
                    // if the image don't exist we use a default drawable
                    .error(R.drawable.poster_missing)
                    .resize(DEFAULT_IMG_WIDTH, DEFAULT_IMG_HEIGHT)

                    // put the result image in poster ImageView
                    .into(poster, new Callback() {
                        @Override
                        public void onSuccess() {
                            title.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            title.setVisibility(View.VISIBLE);
                        }
                    });

        } else {
            // Default without poster image
            poster.setImageResource(R.drawable.poster_missing);
            title.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
