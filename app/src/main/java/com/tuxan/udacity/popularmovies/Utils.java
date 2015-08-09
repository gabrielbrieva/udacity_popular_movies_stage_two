package com.tuxan.udacity.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utilities class ...
 */
public class Utils {

    public static final String MOVIE_DETAIL_KEY = "MOVIE_DETAIL"; // key used to save state of DetailFragment and pass data from MoviesFragment to DetailActivity and DetailFragment.
    public static final String IMG_END_POINT = "http://image.tmdb.org/t/p/"; // end point used to load movie poster and backdrop images.
    public static final String END_POINT_PATH = "http://api.themoviedb.org/3"; // end point used by API http request.

    /**
     * Method to check if the device have access to internet
     * @param context
     * @return true if internet connection is available
     */
    public static boolean isNetworkConnected(Context context) {

        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            }
        }

        return false;
    }
}
