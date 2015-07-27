package com.tuxan.udacity.popularmovies;

import com.tuxan.udacity.popularmovies.model.DiscoverResult;
import com.tuxan.udacity.popularmovies.model.Movie;

import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * To make HTTP request to TheMovieDB API and
 * return deserialized results (like Movie instances).
 */
public class TMDbServiceFactory {

    private static final String END_POINT_PATH = "http://api.themoviedb.org/3";

    public static TMDbService createService(final String apiKey)
    {
        return new RestAdapter.Builder()
                .setEndpoint(END_POINT_PATH)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestInterceptor.RequestFacade request) {
                        request.addHeader("Accept", "application/json");
                        request.addQueryParam("vote_count.gte", "1000"); // get significant result
                        request.addQueryParam("api_key", apiKey);
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(TMDbService.class);
    }

    public interface TMDbService {

        @GET("/discover/movie")
        Observable<DiscoverResult> discover(@Query("sort_by") String sortBy);

        // Add others API request method :)

    }

}
