package com.tuxan.udacity.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.tuxan.udacity.popularmovies.R;

public class TestMovieContract extends AndroidTestCase {

    private static final long TEST_MOVIE_ID = 12345;

    public void testBuildMovieUri() {
        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);

        assertNotNull("buildMovieUri method return null :(", movieUri);

        assertEquals("ID don't match", Long.toString(TEST_MOVIE_ID), movieUri.getLastPathSegment());

        assertEquals("uri don't match", movieUri.toString(), "content://" + mContext.getString(R.string.content_authority) + "/movie/" + TEST_MOVIE_ID);

        Uri moviesUri = MovieContract.MovieEntry.buildMoviesUri("favorite");

        assertNotNull("buildMoviesUri method return null :(", moviesUri);

        assertEquals("filterby value don't match", "favorite", moviesUri.getLastPathSegment());

        assertEquals("uri don't match", moviesUri.toString(), "content://" + mContext.getString(R.string.content_authority) + "/movie/favorite");
    }

}
