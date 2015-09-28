package com.tuxan.udacity.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestProvider extends AndroidTestCase {

    /**
     * Delete all from Movie table and test if all was deleted.
     */
    private void deleteAllFromProvider() {
        mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);

        Cursor c = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        assertEquals("Error: Records not deleted from Movie table :(", 0, c.getCount());
        c.close();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // start with an empty content provider
        deleteAllFromProvider();
    }

    /**
     * Test to check if the provider is registered correctly.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());

        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);

        } catch (PackageManager.NameNotFoundException ex) {
            assertTrue("Error: MovieProvider not register at " + mContext.getPackageName(), false);
        }
    }

    /**
     * Test to check the type returned by the MovieProvider.
     */
    public void testGetType() {
        // content://com.tuxan.udacity.popularmovies/movie/
        String type =  mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);

        // vnd.android.cursor.dir/com.tuxan.udacity.popularmovies/movie
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        String filterBy = "byFavoritte";
        // content://com.tuxan.udacity.popularmovies/movie/byFavoritte
        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMoviesUri(filterBy));
        // vnd.android.cursor.dir/com.tuxan.udacity.popularmovies/movie
        assertEquals("Error: the MovieEntry CONTENT_URI with movieId should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        long testMovieId = 123456;
        // content://com.tuxan.udacity.popularmovies/movie/123456
        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMovieUri(testMovieId));
        // vnd.android.cursor.item/com.tuxan.udacity.popularmovies/movie
        assertEquals("Error: the MovieEntry CONTENT_URI with movieId should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);

    }

    /**
     * Test general using of MovieProvider (insert, update, query, observer cursor and delete)
     */
    public void testBasicMovieQuery() {

        ContentValues movieValues = TestUtils.createMovieValues(1234);

        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
        long movieRowId = ContentUris.parseId(movieUri);

        assertTrue(movieRowId != -1);

        movieValues = new ContentValues(movieValues);
        movieValues.put(MovieContract.MovieEntry._ID, movieRowId);
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "updated");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor movieCursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        TestUtils.TestContentObserver tco = TestUtils.TestContentObserver.getTestContentObjserver();
        movieCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                movieValues, MovieContract.MovieEntry._ID + " = ?",
                new String[] { Long.toString(movieRowId) });

        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // If the code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        // we test if the row was updated
        Cursor c = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // *
                MovieContract.MovieEntry._ID + " = ?", // where logic
                new String[] {Long.toString(movieRowId)}, // where values
                null // sort
        );

        assertTrue(c.getCount() > 0);
        TestUtils.validateCursor("Error validating movie entry update",
                c, movieValues);

        c.close();

        // we test delete process
        count = mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry._ID + " = ?",
                new String[] {Long.toString(movieRowId)}
        );

        assertEquals(count, 1);
    }
}
