package com.tuxan.udacity.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    // Our content authority for popular movies
    public static final String CONTENT_AUTHORITY = "com.tuxan.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    // {scheme}://{authority}/{location}/{query}
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_TRAILER = "trailer";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        /**
         * Build URI to get a movie by movie id
         * @param id: movie id
         * @return
         */
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Build a URI to get list of movies filter by a filter type
         * @param filterBy: FILTER_BY_FAVORITE, FILTER_BY_POPULARITY or FILTER_BY_VOTEAVERAGE
         * @return
         */
        public static Uri buildMoviesUri(String filterBy) {
            return CONTENT_URI.buildUpon().appendPath(filterBy).build();
        }

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_BACKDROP_IMAGE_PATH = "backdrop_image_path";
        public static final String COLUMN_POSTER_IMAGE_PATH = "poster_image_path";

        // flag to identify which one is saved as favorite and is watched
        public static final String COLUMN_FAVORITE = "favorite";
        public static final String COLUMN_WATCHED = "watched";

    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static Uri buildReviewsByMovieUri(long movieId) {
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }

        public static final String TABLE_NAME = "review";

        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";

    }

    public static final class TrailerEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static Uri buildTrailerByMovieUri(long movieId) {
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }

        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_SOURCE = "source";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_INDEX = "t_index";
    }
}
