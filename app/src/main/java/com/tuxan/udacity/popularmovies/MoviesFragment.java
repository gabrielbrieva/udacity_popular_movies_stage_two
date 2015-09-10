package com.tuxan.udacity.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.tuxan.udacity.popularmovies.data.MovieContract;
import com.tuxan.udacity.popularmovies.service.SyncMovieService;

/**
 * A fragment with a grid of poster movies.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String SELECTED_KEY = "SELECTED_POSITION";
    private static final String GRID_SCROLL_KEY = "GRID_INDEX";

    private String sortValue = null;
    private int mScrollPosition = 0;
    private int mPosition = GridView.INVALID_POSITION;

    // Custom CursorAdapter to show each movies on a GridView
    private MoviesAdapter mAdapter;
    private GridView mGVMovies;

    //private ProgressDialog mPDLoading;
    //private LinearLayout mLLOffline;

    private static final int MOVIES_LOADER = 0;

    private static final String[] MOVIES_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_IMAGE_PATH,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE
    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_POSTER = 1;
    static final int COL_MOVIE_TITLE = 2;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailMovieCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public MoviesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        /*if (mPDLoading == null)
            mPDLoading = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);

        mPDLoading.setIndeterminate(true);
        mPDLoading.setMessage(getString(R.string.loading_message));

        if (sortValue == null) {
            // get the sort value from shared preferences
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sortValue = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_value_popularity));
        }*/

        if (savedInstanceState != null && savedInstanceState.containsKey(GRID_SCROLL_KEY))
            mScrollPosition = savedInstanceState.getInt(GRID_SCROLL_KEY);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMovies() {
        Intent intent = new Intent(getActivity(), SyncMovieService.class);
        // put extras ??

        getActivity().startService(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // getting an instance of offline message...
        //mLLOffline = (LinearLayout) view.findViewById(R.id.llOffline);

        // creating a MovieAdapter
        mAdapter = new MoviesAdapter(getActivity(), null, 0);

        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        mGVMovies = (GridView) view.findViewById(R.id.gvMovies);
        // set the MoviesAdapter to GridView
        mGVMovies.setAdapter(mAdapter);

        mGVMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(
                                    MovieContract.MovieEntry.buildMovieUri(cursor.getLong(COL_MOVIE_ID))
                            );
                }

                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // saving selected position
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        // saving scroll position
        outState.putInt(GRID_SCROLL_KEY, mGVMovies.getFirstVisiblePosition());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Load list of movies in MoviesAdapter property
     */
    /*
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
                                mGVMovies.setSelection(mPosition);
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
    }*/


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String filterBy = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_value_popularity));

        Uri filterMoviesUri = MovieContract.MovieEntry.buildMoviesUri(filterBy);*/

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIES_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        if (mPosition != GridView.INVALID_POSITION)
            mGVMovies.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
