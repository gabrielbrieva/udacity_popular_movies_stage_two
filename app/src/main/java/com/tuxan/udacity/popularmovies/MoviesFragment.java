package com.tuxan.udacity.popularmovies;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.tuxan.udacity.popularmovies.data.MovieContract;

/**
 * A fragment with a grid of poster movies.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private static final String SELECTED_KEY = "SELECTED_POSITION";

    private int mPosition = GridView.INVALID_POSITION;

    // Custom CursorAdapter to show each movies on a GridView
    private MoviesAdapter mAdapter;
    private GridView mGVMovies;

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
        void onItemSelected(Uri detailUri, View sharedView);
    }

    public MoviesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

                mPosition = position;

                if (cursor != null) {

                    ((Callback) getActivity())
                            .onItemSelected(
                                    MovieContract.MovieEntry.buildMovieUri(cursor.getLong(COL_MOVIE_ID)),
                                    view.findViewById(R.id.iv_movie_poster)
                            );
                }

            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The gridview probably hasn't even been populated yet.  Actually perform the
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

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String filterBy = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_value_popularity));

        Uri filterMoviesUri = MovieContract.MovieEntry.buildMoviesUri(filterBy);

        return new CursorLoader(getActivity(),
                filterMoviesUri,
                MOVIES_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        if (mPosition != GridView.INVALID_POSITION) {
            mGVMovies.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (getString(R.string.pref_sort_key).equals(key)) {
            mPosition = 0;
            getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        }
    }
}
