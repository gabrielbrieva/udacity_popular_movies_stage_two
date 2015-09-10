package com.tuxan.udacity.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MovieProvider extends ContentProvider{

    private final String LOG_TAG = MovieProvider.class.getSimpleName();

    public static final int MOVIE = 100;
    public static final int MOVIE_DETAIL = 101;
    public static final int MOVIES = 102;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mDb;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_DETAIL);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/*", MOVIES);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mDb = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c;
        switch (sUriMatcher.match(uri)) {
            // "movie/#"
            case MOVIE_DETAIL: {
                c = mDb.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID + " = ? ",
                        new String[] {uri.getLastPathSegment()},
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "movie/*"
            case MOVIES: {
                c = mDb.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        //MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        //new String[]{uri.getLastPathSegment()},
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIE: {
                c = mDb.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Log.d(LOG_TAG, "Query count result: " + c.getCount());

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
            case MOVIES: return MovieContract.MovieEntry.CONTENT_TYPE; // type DIR
            case MOVIE_DETAIL: return MovieContract.MovieEntry.CONTENT_ITEM_TYPE; // type ITEM
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDb.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        Uri returnUri;

        if (match == MOVIE) {
            long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

            Log.d(LOG_TAG, "Insert Id result: " + id);

            if (id > 0)
                returnUri = MovieContract.MovieEntry.buildMovieUri(id);
            else
                throw new SQLException("Failed to insert row into " + uri);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDb.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsDeleted;

        if (null == selection)
            selection = "1";

        if (match == MOVIE) {
            rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        Log.d(LOG_TAG, "Delete rows result: " + rowsDeleted);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDb.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsUpdated;

        if (null == selection)
            selection = "1";

        if (match == MOVIE) {
            rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        Log.d(LOG_TAG, "Updated rows result: " + rowsUpdated);

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDb.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        if (match == MOVIE) {

            db.beginTransaction();
            int rowsInserted = 0;
            try {

                for (ContentValues v : values) {
                    long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, v);
                    if (id != -1)
                        rowsInserted++;
                }

                db.setTransactionSuccessful();

            } finally {
                db.endTransaction();
            }

            getContext().getContentResolver().notifyChange(uri, null);

            Log.d(LOG_TAG, "Total bulk insert rows result: " + rowsInserted);

            return rowsInserted;
        } else {
            return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mDb.close();
        super.shutdown();
    }
}
