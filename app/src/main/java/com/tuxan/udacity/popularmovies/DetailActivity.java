package com.tuxan.udacity.popularmovies;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tuxan.udacity.popularmovies.model.Movie;


public class DetailActivity extends AppCompatActivity {

    Movie movie;
    CollapsingToolbarLayout collapsingToolbarLayout;

    // path for load poster image
    private static final String IMG_ROOT_PATH = "http://image.tmdb.org/t/p/w780";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            movie = (Movie) intent.getSerializableExtra("movie");
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        // TODO: set background and title of toolbar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(movie.getOriginal_title());

        final ImageView ivBackdrop = (ImageView) findViewById(R.id.ivBackdrop);

        // create an instance of Picasso using the context
        Picasso p = Picasso.with(this);

        // debugging purpose
        p.setLoggingEnabled(true);

        // load the backdrop image
        p.load(IMG_ROOT_PATH + movie.getBackdrop_path())
                // if the image don't exist we use a default drawable
                //.error(R.drawable.poster_missing)
                //.resize(DEFAULT_IMG_WIDTH, DEFAULT_IMG_HEIGHT)

                // put the result image in poster ImageView
                .into(ivBackdrop);
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
