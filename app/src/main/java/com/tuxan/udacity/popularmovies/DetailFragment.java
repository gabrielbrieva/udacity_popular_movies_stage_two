package com.tuxan.udacity.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tuxan.udacity.popularmovies.model.Movie;

public class DetailFragment extends Fragment {

    Movie movie;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
