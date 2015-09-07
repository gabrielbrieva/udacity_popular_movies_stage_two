package com.tuxan.udacity.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // start with an empty DB
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    public void testCreateDb() {

        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        assertEquals("Db is not open :(", true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        // we made a list with all the tables must be created on the DB
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Some of the tables was not created :(", tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> columnHashSet = new HashSet<String>();
        columnHashSet.add(MovieContract.MovieEntry._ID);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_VIDEO);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE_PATH);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_IMAGE_PATH);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_FAVORITE);
        columnHashSet.add(MovieContract.MovieEntry.COLUMN_WATCHED);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                columnHashSet.isEmpty());
        db.close();
    }

    public void testMovieTable() {

        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        assertEquals("Db is not open :(", true, db.isOpen());

        ContentValues movieValues = TestUtils.createMovieValues(1234);

        long insertedMovieId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);
        assertTrue(insertedMovieId != -1);

        Cursor c = db.query(MovieContract.MovieEntry.TABLE_NAME,
                null, null, null, null, null, null);

        assertTrue("table is empty :(", c.moveToFirst());

        TestUtils.validateCurrentRecord("unexpected value was inserted :(", c, movieValues);
        assertFalse("more than one record from movie query ?? ¬¬", c.moveToNext());

        c.close();
        db.close();
    }
}
