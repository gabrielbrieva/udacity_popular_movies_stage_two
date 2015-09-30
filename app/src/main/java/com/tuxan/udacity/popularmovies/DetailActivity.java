package com.tuxan.udacity.popularmovies;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tuxan.udacity.popularmovies.data.MovieContract;


public class DetailActivity extends AppCompatActivity {

    private static final String MOVIE_ID_KEY = "MOVIE_ID_KEY";
    private long mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI_KEY, getIntent().getData());

            mMovieId = Long.parseLong(getIntent().getData().getLastPathSegment());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            mMovieId = savedInstanceState.getLong(MOVIE_ID_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(String name, final Context context, AttributeSet attrs) {
        View view = super.onCreateView(name, context, attrs);

        new AsyncQueryHandler(getContentResolver()) {

            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);

                if (cursor != null && cursor.moveToFirst()) {
                    CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
                    collapsingToolbarLayout.setTitle(cursor.getString(0));

                    final ImageView ivBackdrop = (ImageView) findViewById(R.id.ivBackdrop);

                    // create an instance of Picasso using the context
                    Picasso p = Picasso.with(context);

                    // debugging purpose
                    //p.setLoggingEnabled(true);

                    // load the backdrop image
                    p.load(Utils.IMG_END_POINT + "w780" + cursor.getString(1))
                            // if the image don't exist we use a default drawable
                            .error(R.drawable.poster_missing)
                                    // put the result image in poster ImageView
                            .into(ivBackdrop);

                    cursor.close();
                }
            }
        }.startQuery(1, null,
                MovieContract.MovieEntry.buildMovieUri(mMovieId),
                new String[] { MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE_PATH },
                null,
                null,
                null);

        return view;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(MOVIE_ID_KEY, mMovieId);
    }
}
