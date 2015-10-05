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

    // possible values to use with MovieEntry.builMoviesUri
    public static final String FILTER_BY_FAVORITE = "favorite";
    public static final String FILTER_BY_POPULARITY = "popularity.desc";
    public static final String FILTER_BY_VOTEAVERAGE = "vote_average.desc";

    public static final int MOVIE = 100;
    public static final int MOVIE_DETAIL = 101;
    public static final int MOVIES = 102;
    public static final int REVIEW = 200;
    public static final int REVIEWS = 201;
    public static final int TRAILER = 300;
    public static final int TRAILERS = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mDb;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_DETAIL);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/*", MOVIES);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW + "/#", REVIEWS);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER + "/#", TRAILERS);

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
                        MovieContract.MovieEntry._ID + " = ? ",
                        new String[] { uri.getLastPathSegment() },
                        null,
                        null,
                        null);
                break;
            }
            // "movie/*"
            case MOVIES: {

                String param = uri.getLastPathSegment();
                selection = null;
                String limit = null;

                if (param.equals(FILTER_BY_FAVORITE)) {
                    selection = MovieContract.MovieEntry.COLUMN_FAVORITE + " = ? ";
                    selectionArgs = new String[] { "1" };
                } else if (param.equals(FILTER_BY_VOTEAVERAGE)) {
                    sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC ";
                    limit = "20";
                } else if (param.equals(FILTER_BY_POPULARITY)) {
                    sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC ";
                    limit = "20";
                }

                c = mDb.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        limit);
                break;
            }
            // "movie"
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
            // "review"
            case REVIEW: {
                c = mDb.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "review/#"
            case REVIEWS: {
                c = mDb.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[] { uri.getLastPathSegment() },
                        null,
                        null,
                        null);
                break;
            }
            // "trailer"
            case TRAILER: {
                c = mDb.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "trailer/#"
            case TRAILERS: {
                c = mDb.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[] { uri.getLastPathSegment() },
                        null,
                        null,
                        null);
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
            case REVIEW:
            case REVIEWS: return MovieContract.ReviewEntry.CONTENT_TYPE; // type DIR
            case TRAILER:
            case TRAILERS: return MovieContract.TrailerEntry.CONTENT_TYPE; // type DIR

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
        } else if (match == TRAILER) {
            rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
        } else if (match == REVIEW) {
            rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
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
        } else if (match == TRAILER) {
            rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
        } else if (match == REVIEWS) {
            rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
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

        int rowsInserted = 0;

        if (match == MOVIE) {

            db.beginTransaction();
            Cursor c = null;

            try {
                // If the inserted row already exist
                // we update the current one
                for (ContentValues v : values) {

                    // check if exist
                    c = db.query(MovieContract.MovieEntry.TABLE_NAME,
                            null,
                            "_id = ?",
                            new String[]{v.get(MovieContract.MovieEntry._ID).toString()},
                            null,
                            null,
                            null);

                    long id = -1;

                    if (c.getCount() == 0) {
                        id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, v);
                        if (id != -1)
                            rowsInserted++;
                    } else {
                        // if exist we update the row
                        id = db.update(MovieContract.MovieEntry.TABLE_NAME,
                                v,
                                "_id = ?",
                                new String[]{v.get(MovieContract.MovieEntry._ID).toString()});

                        if (id != -1)
                            rowsInserted++;
                    }
                }

                db.setTransactionSuccessful();

            } finally {
                if (c != null)
                    c.close();

                db.endTransaction();
            }

            if (rowsInserted > 0)
                getContext().getContentResolver().notifyChange(uri, null);

            Log.d(LOG_TAG, "Total bulk insert rows result: " + rowsInserted);

            return rowsInserted;
        } else if (match == TRAILER) {

            db.beginTransaction();
            Cursor c = null;

            try {
                for (ContentValues v : values) {

                    // check if exist
                    c = db.query(MovieContract.TrailerEntry.TABLE_NAME,
                            null,
                            MovieContract.TrailerEntry.COLUMN_SOURCE + " = ?",
                            new String[]{ v.getAsString(MovieContract.TrailerEntry.COLUMN_SOURCE) },
                            null,
                            null,
                            null);

                    long id = -1;

                    if (c.getCount() == 0) {
                        id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, v);
                        if (id != -1)
                            rowsInserted++;
                    }
                }

                db.setTransactionSuccessful();

            } finally {
                if (c != null)
                    c.close();

                db.endTransaction();
            }

            if (rowsInserted > 0)
                getContext().getContentResolver().notifyChange(uri, null);

            Log.d(LOG_TAG, "Total bulk insert rows result: " + rowsInserted);

            return rowsInserted;

        } else if (match == REVIEW) {

            db.beginTransaction();
            Cursor c = null;

            try {
                for (ContentValues v : values) {

                    // check if exist
                    c = db.query(MovieContract.ReviewEntry.TABLE_NAME,
                            null,
                            MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ?",
                            new String[]{ v.getAsString(MovieContract.ReviewEntry.COLUMN_REVIEW_ID) },
                            null,
                            null,
                            null);

                    long id = -1;

                    if (c.getCount() == 0) {
                        id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, v);
                        if (id != -1)
                            rowsInserted++;
                    }
                }

                db.setTransactionSuccessful();

            } finally {
                if (c != null)
                    c.close();

                db.endTransaction();
            }

            if (rowsInserted > 0)
                getContext().getContentResolver().notifyChange(uri, null);

            Log.d(LOG_TAG, "Total bulk insert rows result: " + rowsInserted);

            return rowsInserted;
        }

        return super.bulkInsert(uri, values);
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
