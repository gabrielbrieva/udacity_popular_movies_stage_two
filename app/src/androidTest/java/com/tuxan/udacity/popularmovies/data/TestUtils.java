package com.tuxan.udacity.popularmovies.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.tuxan.udacity.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

public class TestUtils extends AndroidTestCase{

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createMovieValues(long movieId) {
        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieContract.MovieEntry._ID, movieId);
        movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Inside Out");
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "The best of pixar :)");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "release date");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 0.999);
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 1);
        movieValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, 1);
        movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE_PATH, "image path");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_IMAGE_PATH, "image path");
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
        movieValues.put(MovieContract.MovieEntry.COLUMN_WATCHED, 1);

        return movieValues;
    }

    static class TestContentObserver extends ContentObserver {

        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObjserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();

            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObjserver();
    }
}
