package com.tuxan.udacity.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tuxan.udacity.popularmovies.data.MovieContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static String DETAIL_URI_KEY = "DETAIL_URI_KEY";
    private Uri mUri;

    private static final int DETAIL_LOADER = 1;

    private static final String[] DETAIL_COLUMNS = new String[] {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE_PATH,
            MovieContract.MovieEntry.COLUMN_POSTER_IMAGE_PATH,
            MovieContract.MovieEntry.COLUMN_FAVORITE,
            MovieContract.MovieEntry.COLUMN_WATCHED
    };

    private static final int COL_MOVIE_ID = 0;
    private static final int COL_ORIGINAL_TITLE = 1;
    private static final int COL_RELEASE_DATE = 2;
    private static final int COL_POPULARITY = 3;
    private static final int COL_VOTE_AVERAGE = 4;
    private static final int COL_OVERVIEW = 5;
    private static final int COL_BACKDROP_IMAGE_PATH = 6;
    private static final int COL_POSTER_IMAGE_PATH = 7;
    private static final int COL_FAVORITE = 8;
    private static final int COL_WATCHED = 9;


    private ImageView mPosterView;
    private TextView mTitleView;
    private TextView mDateReleaseView;
    private TextView mVoteAverageView;
    private TextView mOverviewView;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI_KEY);
        }

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mTitleView = (TextView) view.findViewById(R.id.tvOriginalTitle);
        mOverviewView = (TextView) view.findViewById(R.id.tvOverview);
        mDateReleaseView = (TextView) view.findViewById(R.id.tvDateRelease);
        mVoteAverageView = (TextView) view.findViewById(R.id.tvVoteAverage);
        mPosterView = (ImageView) view.findViewById(R.id.ivDetailPoster);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {

            mTitleView.setText(data.getString(COL_ORIGINAL_TITLE));
            mDateReleaseView.setText(getString(R.string.movie_detail_release_date) + ": " + data.getString(COL_RELEASE_DATE));
            mVoteAverageView.setText(getString(R.string.movie_detail_rating) + ": " + data.getFloat(COL_VOTE_AVERAGE));
            mOverviewView.setText(data.getString(COL_OVERVIEW));

            // create an instance of Picasso using the context
            Picasso p = Picasso.with(getActivity());

            // debugging purpose
            p.setLoggingEnabled(true);

            // load the backdrop image
            p.load(Utils.IMG_END_POINT + "w185" + data.getString(COL_POSTER_IMAGE_PATH))
                    // if the image don't exist we use a default drawable
                    .error(R.drawable.poster_missing)
                            // put the result image in poster ImageView
                    .into(mPosterView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
