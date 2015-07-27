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

    private static final String IMG_ROOT_PATH = "http://image.tmdb.org/t/p/w185";
    private static int DEFAULT_IMG_WIDTH = 185;
    private static int DEFAULT_IMG_HEIGHT = 278;

    public MoviesAdapter(Context context, int layout, List<Movie> movies) {
        super(context, layout, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.movie_poster, parent, false);

        final TextView title = (TextView) convertView.findViewById(R.id.tv_movie_title);
        title.setVisibility(View.GONE);
        title.setText(movie.original_title);

        ImageView poster = (ImageView) convertView.findViewById(R.id.iv_movie_poster);

        if (movie.poster_path != null) {
            // load image poster using Picasso library
            Picasso p = Picasso.with(getContext());
            p.setLoggingEnabled(true);

            p.load(IMG_ROOT_PATH + movie.poster_path)
                    .error(R.drawable.poster_missing)
                    .resize(DEFAULT_IMG_WIDTH, DEFAULT_IMG_HEIGHT)
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
            // Default no poster
            poster.setImageResource(R.drawable.poster_missing);
            title.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
