package com.tuxan.udacity.popularmovies.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.tuxan.udacity.popularmovies.R;
import com.tuxan.udacity.popularmovies.TMDbServiceFactory;
import com.tuxan.udacity.popularmovies.Utils;
import com.tuxan.udacity.popularmovies.data.MovieContract;
import com.tuxan.udacity.popularmovies.model.DiscoverResult;
import com.tuxan.udacity.popularmovies.model.Movie;

import java.util.Vector;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SyncMovieService extends IntentService {

    private final String LOG_TAG = SyncMovieService.class.getSimpleName();

    public SyncMovieService() {
        super("PopularMovie");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // checking if the device have internet connection
        if (Utils.isNetworkConnected(this)) {

            Log.d(LOG_TAG, "Requesting to TMDb API");
            TMDbServiceFactory.createService(getString(R.string.api_key))
                    .discover("")
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<DiscoverResult>() {

                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            // log an error API request
                        }

                        @Override
                        public void onNext(DiscoverResult response) {

                            Log.d(LOG_TAG, "Request result: " + response.toString());

                            if (response != null && response.results != null && !response.results.isEmpty()) {

                                Log.d(LOG_TAG, "Request result size: " + response.results.size());

                                Vector<ContentValues> movies = new Vector<>(response.results.size());

                                for (Movie m : response.results) {
                                    ContentValues values = new ContentValues();

                                    values.put(MovieContract.MovieEntry._ID, m.getId());
                                    values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, m.getOriginal_title());
                                    values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, m.getOverview());
                                    values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, m.getRelease_date());
                                    values.put(MovieContract.MovieEntry.COLUMN_POPULARITY, m.getPopularity());
                                    values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, m.getVote_average());
                                    values.put(MovieContract.MovieEntry.COLUMN_POSTER_IMAGE_PATH, m.getPoster_path());
                                    values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE_PATH, m.getBackdrop_path());
                                    values.put(MovieContract.MovieEntry.COLUMN_VIDEO, 0);
                                    values.put(MovieContract.MovieEntry.COLUMN_WATCHED, 0);
                                    values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);

                                    movies.add(values);
                                }

                                if (movies.size() > 0) {
                                    ContentValues[] cvArray = new ContentValues[movies.size()];
                                    movies.toArray(cvArray);

                                    Log.d(LOG_TAG, "Bulk insert of results Movies...");

                                    getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
                                }
                            }
                        }
                    });

        }

    }
}
