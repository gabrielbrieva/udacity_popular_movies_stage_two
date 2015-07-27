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
public class TheMovieDBService {

    private static final String END_POINT_PATH = "http://api.themoviedb.org/3";

    private TheMovieDBApi api;

    public TheMovieDBService(final String apiKey)
    {
        RequestInterceptor reqInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Accept", "application/json");
                request.addQueryParam("vote_count.gte", "1000"); // get significant result
                request.addQueryParam("api_key", apiKey);
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT_PATH)
                .setRequestInterceptor(reqInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        api = restAdapter.create(TheMovieDBApi.class);
    }

    /**
     * Get api attribute
     * @return instance of TheMovieDBApi to make HTPP request to API
     */
    public TheMovieDBApi getAPI()
    {
        return api;
    }

    public interface TheMovieDBApi {

        @GET("/discover/movie")
        Observable<DiscoverResult> discover(@Query("sort_by") String sortBy);

        // Add others API request method :)

    }

}
