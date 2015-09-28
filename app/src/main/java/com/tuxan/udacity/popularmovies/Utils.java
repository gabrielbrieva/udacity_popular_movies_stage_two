package com.tuxan.udacity.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utilities class ...
 */
public class Utils {

    public static final String YOUTUBE_VIDEO_END_POINT = "https://www.youtube.com/watch?v="; // endpoint used to load a youtube video using youtube page or android app
    public static final String YOUTUBE_IMAGE_END_POINT = "http://img.youtube.com/vi/"; // endpoint used to load youtube trailer thumbs images.
    public static final String IMG_END_POINT = "http://image.tmdb.org/t/p/"; // endpoint used to load movie poster and backdrop images.
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
