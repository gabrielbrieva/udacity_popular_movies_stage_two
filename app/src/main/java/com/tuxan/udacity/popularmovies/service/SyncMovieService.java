package com.tuxan.udacity.popularmovies.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tuxan.udacity.popularmovies.R;
import com.tuxan.udacity.popularmovies.TMDbServiceFactory;
import com.tuxan.udacity.popularmovies.Utils;
import com.tuxan.udacity.popularmovies.data.MovieContract;
import com.tuxan.udacity.popularmovies.model.DiscoverResult;
import com.tuxan.udacity.popularmovies.model.Movie;

import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
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
            syncByFilter(MovieContract.MovieEntry.FILTER_BY_POPULARITY,
                    MovieContract.MovieEntry.FILTER_BY_VOTEAVERAGE);
        }

    }

    private void syncByFilter(String... filterBy) {

        for(String filter : filterBy) {

            Log.d(LOG_TAG, "**** Requesting to TMDb API using " + filter);
            TMDbServiceFactory.createService(getString(R.string.api_key))
                    .discover(filter, new Callback<DiscoverResult>() {
                        @Override
                        public void success(DiscoverResult discover, Response resp) {

                            if (discover != null && discover.results != null && !discover.results.isEmpty()) {

                                Log.d(LOG_TAG, "**** Request result size: " + discover.results.size());

                                Vector<ContentValues> movies = new Vector<>(discover.results.size());

                                for (Movie m : discover.results) {
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

                                    Log.d(LOG_TAG, "**** Bulk insert of results Movies...");

                                    // the bulkInsert method update a row if already exist
                                    getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
                                }
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
        }
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendIntent = new Intent(context, SyncMovieService.class);
            context.startService(sendIntent);
        }
    }
}
