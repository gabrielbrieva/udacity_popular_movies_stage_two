package com.tuxan.udacity.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
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

public class PopularMoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    private final String LOG_TAG = PopularMoviesSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    public PopularMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
    }

    public PopularMoviesSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // checking if the device have internet connection
        if (Utils.isNetworkConnected(getContext())) {
            syncByFilter(MovieContract.MovieEntry.FILTER_BY_POPULARITY,
                    MovieContract.MovieEntry.FILTER_BY_VOTEAVERAGE);
        }
    }

    private void syncByFilter(String... filterBy) {

        for(String filter : filterBy) {

            Log.d(LOG_TAG, "**** Requesting to TMDb API using " + filter);
            TMDbServiceFactory.createService(getContext().getString(R.string.api_key))
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
                                    mContentResolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
                                }
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
        }
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

            // Add the account and account type, no password or user data
            // If successful, return the Account object, otherwise report an error.
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        PopularMoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
