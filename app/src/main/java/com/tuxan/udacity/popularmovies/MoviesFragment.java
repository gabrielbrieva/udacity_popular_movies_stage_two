package com.tuxan.udacity.popularmovies;

import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
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

    private static final String GRID_SCROLL_KEY = "GRID_INDEX";

    private String sortValue = null;
    private int gridScrollValue = 0;

    // Custom ArrayAdapter<Movie> to show each movies on a GridView
    private MoviesAdapter mAdapter;
    private GridView mGVMovies;

    private ProgressDialog mPDLoading;
    private LinearLayout mLLOffline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mPDLoading == null)
            mPDLoading = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);

        mPDLoading.setIndeterminate(true);
        mPDLoading.setMessage(getString(R.string.loading_message));

        if (sortValue == null) {
            // get the sort value from shared preferences
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sortValue = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_value_popularity));
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(GRID_SCROLL_KEY))
            gridScrollValue = savedInstanceState.getInt(GRID_SCROLL_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        // getting an instance of offline message...
        mLLOffline = (LinearLayout) view.findViewById(R.id.llOffline);

        // creating a MovieAdapter using movie_poster layout for each movie result
        mAdapter = new MoviesAdapter(getActivity(), R.layout.movie_poster, new ArrayList<Movie>());

        mGVMovies = (GridView) view.findViewById(R.id.gvMovies);
        // set the MoviesAdapter to GridView
        mGVMovies.setAdapter(mAdapter);

        mGVMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // start detail activity using existent Movie data
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Utils.MOVIE_DETAIL_KEY, mAdapter.getItem(position));
                startActivity(intent);
            }
        });

        // load list of movies
        loadMovies();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // saving scroll position
        outState.putInt(GRID_SCROLL_KEY, mGVMovies.getFirstVisiblePosition());
        super.onSaveInstanceState(outState);
    }

    /**
     * Load list of movies in MoviesAdapter property
     */
    private void loadMovies() {

        // show loading ProgressDialog
        mPDLoading.show();

        // checking if the device have internet connection
        if (Utils.isNetworkConnected(getActivity())) {

            // we get a TMDbService instance and we configure the tx.Observable<DiscoverResult>
            // to execute the callback on the main thread to refresh the UI with movies
            // result.
            TMDbServiceFactory.createService(getString(R.string.api_key))
                    .discover(sortValue)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<DiscoverResult>() {

                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            // show an error API request
                            Toast.makeText(getActivity(), "HTTP API ERROR", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(DiscoverResult response) {

                            if (mGVMovies != null && mGVMovies.getVisibility() == View.GONE)
                                mGVMovies.setVisibility(View.VISIBLE);

                            if (mLLOffline != null && mLLOffline.getVisibility() == View.VISIBLE)
                                mLLOffline.setVisibility(View.GONE);

                            // cleaning the MovieAdapter
                            mAdapter.clear();

                            if (response != null && response.results != null && !response.results.isEmpty()) {
                                // we use the addAll method to avoid the consecutive notifyDataSetChanged invoke
                                mAdapter.addAll(response.results);

                                // scrolling to last scroll saved status
                                mGVMovies.setSelection(gridScrollValue);
                            }

                            // hide the loading ProgressDialog
                            if (mPDLoading.isShowing())
                                mPDLoading.dismiss();
                        }
                    });
        } else {

            // TODO: get from local database (stage 2), by now we show an offline message

            if (mLLOffline != null)
                mLLOffline.setVisibility(View.VISIBLE);

            if (mGVMovies != null)
                mGVMovies.setVisibility(View.GONE);

            // hide the loading ProgressDialog
            if (mPDLoading.isShowing())
                mPDLoading.dismiss();
        }
    }


}
