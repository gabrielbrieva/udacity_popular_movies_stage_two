package com.tuxan.udacity.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.tuxan.udacity.popularmovies.model.DiscoverResult;
import com.tuxan.udacity.popularmovies.model.Movie;

import java.util.ArrayList;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A fragment with a grid of poster movies.
 */
public class MoviesFragment extends Fragment {

    // Custom ArrayAdapter<Movie> to show each movies on a GridView
    private MoviesAdapter mAdapter;

    private String mSortValue = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // we load the list of movies
        loadMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        if (mSortValue == null) {
            // the first load we use the preference sort value
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mSortValue = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_value_popularity));
        }

        mAdapter = new MoviesAdapter(getActivity(), R.layout.movie_poster, new ArrayList<Movie>());

        GridView gv = (GridView) view.findViewById(R.id.gvMovies);
        gv.setAdapter(mAdapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("movie", mAdapter.getItem(position));
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * Load list of movies in MoviesAdapter property
     */
    private void loadMovies() {

        // TODO: check internet connection first

        // we get a TMDbService instance and we configure the tx.Observable<DiscoverResult>
        // to execute the callback on the main thread to refresh the UI with movies
        // result.
        TMDbServiceFactory.createService(getString(R.string.api_key))
                .discover(mSortValue)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DiscoverResult>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO: show an error API request
                        Toast.makeText(getActivity(), "HTTP API ERROR", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(DiscoverResult response) {
                        // we clear the MovieAdapter
                        mAdapter.clear();

                        if (response != null && response.results != null && !response.results.isEmpty()) {
                            // we use the addAll method to avoid the consecutive notifyDataSetChanged invoke
                            mAdapter.addAll(response.results);
                        }
                    }
                });
    }
}
