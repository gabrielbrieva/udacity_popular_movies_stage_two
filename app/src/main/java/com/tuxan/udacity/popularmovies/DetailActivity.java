package com.tuxan.udacity.popularmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class DetailActivity extends AppCompatActivity {

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

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }

        /*CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(movie.getOriginal_title());

        final ImageView ivBackdrop = (ImageView) findViewById(R.id.ivBackdrop);

        // create an instance of Picasso using the context
        Picasso p = Picasso.with(this);

        // debugging purpose
        //p.setLoggingEnabled(true);

        // load the backdrop image
        p.load(Utils.IMG_END_POINT + "w780" + movie.getBackdrop_path())
                // if the image don't exist we use a default drawable
                .error(R.drawable.poster_missing)
                // put the result image in poster ImageView
                .into(ivBackdrop);*/
    }
}
