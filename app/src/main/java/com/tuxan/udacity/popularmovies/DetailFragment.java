package com.tuxan.udacity.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tuxan.udacity.popularmovies.data.MovieContract;
import com.tuxan.udacity.popularmovies.model.APIResult;
import com.tuxan.udacity.popularmovies.model.Review;
import com.tuxan.udacity.popularmovies.model.Trailer;
import com.tuxan.udacity.popularmovies.model.TrailerResult;
import com.tuxan.udacity.popularmovies.picasso.PicassoBigCache;

import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static String DETAIL_URI_KEY = "DETAIL_URI_KEY";
    private Uri mUri;
    private long mMovieId;
    private boolean mTwoPane;
    private TMDbServiceFactory.TMDbService mService;

    private static final int DETAIL_LOADER = 1;
    private static final int TRAILERS_LOADER = 2;
    private static final int REVIEWS_LOADER = 3;

    private static final String[] MOVIE_DETAIL_COLUMNS = new String[] {
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


    private static class MovieColumnIndex {
        public static final int COL_MOVIE_ID = 0;
        public static final int COL_ORIGINAL_TITLE = 1;
        public static final int COL_RELEASE_DATE = 2;
        public static final int COL_POPULARITY = 3;
        public static final int COL_VOTE_AVERAGE = 4;
        public static final int COL_OVERVIEW = 5;
        public static final int COL_BACKDROP_IMAGE_PATH = 6;
        public static final int COL_POSTER_IMAGE_PATH = 7;
        public static final int COL_FAVORITE = 8;
        public static final int COL_WATCHED = 9;
    }

    private static final String[] REVIEW_COLUMNS = new String [] {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
            MovieContract.ReviewEntry.COLUMN_MOVIE_ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT
    };

    public static class ReviewColumnIndex {
        public static final int _ID = 0;
        public static final int COL_REVIEW_ID = 1;
        public static final int COL_MOVIE_ID = 2;
        public static final int COL_AUTHOR = 3;
        public static final int COL_CONTENT = 4;
    }

    private static final String[] TRAILER_COLUMNS = new String [] {
            MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
            MovieContract.TrailerEntry.COLUMN_SOURCE,
            MovieContract.TrailerEntry.COLUMN_NAME
    };

    public static class TrailerColumnIndex {
        public static final int _ID = 0;
        public static final int COL_MOVIE_ID = 1;
        public static final int COL_SOURCE = 2;
        public static final int COL_NAME = 3;
    }

    private Toolbar mToolbar;

    private CoordinatorLayout mClContainer;

    private ShareActionProvider mShareActionProvider;
    private String shareContent = null;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView mToolbarImage;

    private CardView mCvHeader;
    private CardView mCvOverview;
    private CardView mCvTrailers;
    private CardView mCvReviews;

    private ImageView mPosterView;
    private TextView mDateReleaseView;
    private TextView mVoteAverageView;
    private TextView mOverviewView;
    private LinearLayout mReviewsView;
    private LinearLayout mTrailersView;

    private FloatingActionButton mBtFavoriteView;
    private boolean mIsFavorite = false;

    private Picasso p;

    public static class ReviewViewHolder {

        public final TextView author;
        public final TextView content;

        public ReviewViewHolder(View view) {
            author = (TextView) view.findViewById(R.id.tv_review_author);
            content = (TextView) view.findViewById(R.id.tv_review_content);
        }
    }

    public static class TrailerViewHolder {

        public final TextView name;
        public final ImageView image;
        public final Button button;

        public TrailerViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.tv_trailer_name);
            image = (ImageView) view.findViewById(R.id.iv_trailer_image);
            button = (Button) view.findViewById(R.id.bt_trailer);
        }
    }

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mService = TMDbServiceFactory.createService(getString(R.string.api_key));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        p = PicassoBigCache.INSTANCE.getPicassoBigCache(getActivity());

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI_KEY);
            mMovieId = Long.parseLong(mUri.getLastPathSegment());
        }

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mClContainer = (CoordinatorLayout) view.findViewById(R.id.clContainer);

        mCollapsingToolbarLayout = ((CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbarLayout));
        mToolbarImage = (ImageView) view.findViewById(R.id.ivBackdrop);

        mTwoPane = getActivity().findViewById(R.id.main_fragment) != null;

        if(!mTwoPane) {
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mCvHeader = (CardView) view.findViewById(R.id.cvMovieHeader);
        mCvOverview = (CardView) view.findViewById(R.id.cvMovieOverview);
        mCvTrailers = (CardView) view.findViewById(R.id.cvMovieTrailers);
        mCvReviews = (CardView) view.findViewById(R.id.cvMovieReviews);

        mOverviewView = (TextView) view.findViewById(R.id.tvOverview);
        mDateReleaseView = (TextView) view.findViewById(R.id.tvDateRelease);
        mVoteAverageView = (TextView) view.findViewById(R.id.tvVoteAverage);
        mPosterView = (ImageView) view.findViewById(R.id.ivDetailPoster);
        mReviewsView = (LinearLayout) view.findViewById(R.id.llReviewContainer);
        mTrailersView = (LinearLayout) view.findViewById(R.id.llTrailerContainer);

        mBtFavoriteView = (FloatingActionButton) view.findViewById(R.id.fabBtn);

        mBtFavoriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues favValues = new ContentValues();
                favValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, (mIsFavorite ? 0 : 1));

                int updateMovies = getActivity().getContentResolver().update(
                        MovieContract.MovieEntry.CONTENT_URI,
                        favValues,
                        MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID + " = ? ",
                        new String[] { Long.toString(mMovieId) });

                if (updateMovies > 0) {
                    // toggle favorite
                    mIsFavorite = !mIsFavorite;
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.share, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (shareContent != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed(); //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        postponeTransition();

        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null && mMovieId > -1) {
            if (id == DETAIL_LOADER) {
                // load movie
                return new CursorLoader(
                        getActivity(),
                        mUri,
                        MOVIE_DETAIL_COLUMNS,
                        null,
                        null,
                        null
                );
            } else if (id == REVIEWS_LOADER) {
                // load reviews
                return new CursorLoader(
                        getActivity(),
                        MovieContract.ReviewEntry.CONTENT_URI,
                        REVIEW_COLUMNS,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{Long.toString(mMovieId)},
                        null
                );
            } else if (id == TRAILERS_LOADER) {
                // load trailers
                return new CursorLoader(
                        getActivity(),
                        MovieContract.TrailerEntry.CONTENT_URI,
                        TRAILER_COLUMNS,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{Long.toString(mMovieId)},
                        MovieContract.TrailerEntry.COLUMN_INDEX + " asc"
                );
            }
        }

        return null;
    }

    private void postponeTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getActivity().postponeEnterTransition();
    }

    private void initTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getActivity().startPostponedEnterTransition();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {

        if (loader == null)
            return;

        if (loader.getId() == DETAIL_LOADER) {

            if (data != null && data.moveToFirst()) {

                mDateReleaseView.setText(getString(R.string.movie_detail_release_date) + ": " + data.getString(MovieColumnIndex.COL_RELEASE_DATE));
                mVoteAverageView.setText(data.getFloat(MovieColumnIndex.COL_VOTE_AVERAGE) + "/10");
                mOverviewView.setText(data.getString(MovieColumnIndex.COL_OVERVIEW));

                mIsFavorite = data.getInt(MovieColumnIndex.COL_FAVORITE) > 0;

                if (mIsFavorite) {
                    mBtFavoriteView.setImageResource(R.drawable.ic_favorite_white);
                } else {
                    mBtFavoriteView.setImageResource(R.drawable.ic_favorite_border_white);
                }

                mCollapsingToolbarLayout.setTitle(data.getString(MovieColumnIndex.COL_ORIGINAL_TITLE));

                if(mTwoPane) {
                    mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.PopularMovieCollapsedAppBar);
                }

                mClContainer.setVisibility(View.VISIBLE);
                mCvHeader.setVisibility(View.VISIBLE);
                mCvOverview.setVisibility(View.VISIBLE);

                // load the backdrop image
                p.load(Utils.IMG_END_POINT + "w780" + data.getString(MovieColumnIndex.COL_BACKDROP_IMAGE_PATH))
                        // put the result image in poster ImageView
                        .into(mToolbarImage);

                // load the poster image
                p.load(Utils.IMG_END_POINT + "w185" + data.getString(MovieColumnIndex.COL_POSTER_IMAGE_PATH))
                        // if the image don't exist we use a default drawable
                        .error(R.drawable.poster_missing)
                        // put the result image in poster ImageView
                        .into(mPosterView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                initTransition();
                            }

                            @Override
                            public void onError() {
                                initTransition();
                            }
                        });
            }

        } else if (loader.getId() == REVIEWS_LOADER) {

            if (data != null && data.moveToFirst()) {

                mReviewsView.removeAllViews();

                do {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.review, mReviewsView, false);
                    final ReviewViewHolder reviewViewHolder = new ReviewViewHolder(view);
                    reviewViewHolder.author.setText(data.getString(ReviewColumnIndex.COL_AUTHOR));
                    reviewViewHolder.content.setText(data.getString(ReviewColumnIndex.COL_CONTENT));

                    view.setTag(reviewViewHolder);
                    mReviewsView.addView(view);
                } while (data.moveToNext());

                mCvReviews.setVisibility(View.VISIBLE);

            } else if (Utils.isNetworkConnected(getActivity())) {

                mService.movieReviews(mMovieId, new Callback<APIResult<Review>>() {
                    @Override
                    public void success(final APIResult<Review> result, Response response) {

                        if (result != null && result.results.size() > 0) {

                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    Vector<ContentValues> reviews = new Vector<>(result.results.size());

                                    for (Review review : result.results) {
                                        ContentValues values = new ContentValues();

                                        values.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review.getId());
                                        values.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
                                        values.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
                                        values.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, mMovieId);

                                        reviews.add(values);
                                    }

                                    ContentValues[] cvArray = new ContentValues[reviews.size()];
                                    reviews.toArray(cvArray);

                                    Context ctx = getActivity();

                                    if (ctx != null)
                                        getActivity().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);

                                    return null;
                                }
                            }.execute();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
            }

        } else if (loader.getId() == TRAILERS_LOADER) {

            if (data != null && data.moveToFirst()) {

                mTrailersView.removeAllViews();

                do {
                    if (shareContent == null)
                        shareContent = Utils.YOUTUBE_VIDEO_END_POINT + data.getString(TrailerColumnIndex.COL_SOURCE);

                    if (mShareActionProvider != null) {
                        mShareActionProvider.setShareIntent(createShareForecastIntent());
                    }

                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.trailer, mTrailersView, false);
                    TrailerViewHolder trailerViewHolder = new TrailerViewHolder(view);
                    trailerViewHolder.name.setText(data.getString(TrailerColumnIndex.COL_NAME));

                    p.load(Utils.YOUTUBE_IMAGE_END_POINT + data.getString(TrailerColumnIndex.COL_SOURCE) + "/default.jpg")
                            .error(R.drawable.movie_missing) // if the image don't exist we use a default drawable
                            .placeholder(R.drawable.movie_missing)
                            .into(trailerViewHolder.image); // put the result image in ImageView

                    trailerViewHolder.button.setOnClickListener(new View.OnClickListener() {

                        final Uri youtubeUri = Uri.parse(Utils.YOUTUBE_VIDEO_END_POINT + data.getString(TrailerColumnIndex.COL_SOURCE));

                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, youtubeUri));
                        }
                    });

                    view.setTag(trailerViewHolder);

                    mTrailersView.addView(view);
                } while (data.moveToNext());

                mCvTrailers.setVisibility(View.VISIBLE);
            } else if (Utils.isNetworkConnected(getActivity())){

                mService.movieTrailers(mMovieId, new Callback<TrailerResult>() {
                    @Override
                    public void success(final TrailerResult result, Response response) {

                        if (result != null && result.youtube.size() > 0) {

                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    Vector<ContentValues> trailers = new Vector<>(result.youtube.size());

                                    for (Trailer trailer : result.youtube) {
                                        ContentValues values = new ContentValues();

                                        values.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, mMovieId);
                                        values.put(MovieContract.TrailerEntry.COLUMN_INDEX, result.youtube.indexOf(trailer));
                                        values.put(MovieContract.TrailerEntry.COLUMN_SOURCE, trailer.getSource());
                                        values.put(MovieContract.TrailerEntry.COLUMN_NAME, trailer.getName());

                                        trailers.add(values);
                                    }

                                    ContentValues[] cvArray = new ContentValues[trailers.size()];
                                    trailers.toArray(cvArray);

                                    Context ctx = getActivity();

                                    if (ctx != null)
                                        getActivity().getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);

                                    return null;
                                }
                            }.execute();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });

            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == REVIEWS_LOADER)
            mReviewsView.removeAllViews();
        else if (loader.getId() == TRAILERS_LOADER)
            mTrailersView.removeAllViews();
    }
}
