package com.tuxan.udacity.popularmovies.data;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.tuxan.udacity.popularmovies.model.Movie;

public class MovieDetailLoader extends AsyncTaskLoader<Movie> {

    public MovieDetailLoader(Context context) {
        super(context);
    }

    @Override
    public Movie loadInBackground() {
        return null;
    }
}
