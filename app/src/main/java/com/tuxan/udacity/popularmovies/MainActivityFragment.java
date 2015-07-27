package com.tuxan.udacity.popularmovies;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
public class MainActivityFragment extends Fragment {

    private MoviesAdapter mAdapter;

    public MainActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            loadMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mAdapter = new MoviesAdapter(getActivity(), R.layout.movie_poster, new ArrayList<Movie>());

        GridView gv = (GridView) view.findViewById(R.id.gvMovies);
        gv.setAdapter(mAdapter);

        return view;
    }

    private void loadMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_value_popularity));

        // add API Key value to resource string values
        new TheMovieDBService(getString(R.string.api_key))
                .getAPI()
                .discover(sortBy)
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
                        mAdapter.clear();

                        if (response != null && response.results != null && !response.results.isEmpty())
                            mAdapter.addAll(response.results);
                    }
                });
    }
}
