package com.tuxan.udacity.popularmovies;

import com.tuxan.udacity.popularmovies.model.DiscoverResult;
import com.tuxan.udacity.popularmovies.model.Movie;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Factory to create an instance of TMDbService
 */
public class TMDbServiceFactory {

    /**
     * Creator of TMDbService instance using an API Key as parameter.
     *
     * @param apiKey TMDb API key
     * @return instance of TMDbService instance
     */
    public static TMDbService createService(final String apiKey)
    {
        // Using retrofit library we create a RestAdapter
        // based on TMDbService Interface.
        return new RestAdapter.Builder()
                .setEndpoint(Utils.END_POINT_PATH)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestInterceptor.RequestFacade request) {
                        // We expect a json response from API
                        request.addHeader("Accept", "application/json");

                        // "vote_average" is not a true measure of votes, a lot of movies have
                        // high vote_average value but the number of vote are small so the result
                        // of high vote_average are a unknown movies.
                        request.addQueryParam("vote_count.gte", "1000"); // get significant result

                        // we add API Key value as parameter
                        request.addQueryParam("api_key", apiKey);
                    }
                })
                //.setLogLevel(RestAdapter.LogLevel.FULL) // only by debugging purpose
                .build()
                .create(TMDbService.class);
    }

    /**
     * Interface to wrap the TMDb services we will use in the application
     * ("/discover/movie" and "/movie/{id}").
     */
    public interface TMDbService {

        /**
         * Discover 20 top most popular or votes movies
         *
         * @param sortBy R.string.pref_sort_value_popularity or R.string.pref_sort_value_rate
         * @return instance of rx.Observable<DiscoverResult>
         */
        @GET("/discover/movie")
        Observable<DiscoverResult> discover(@Query("sort_by") String sortBy);

        /**
         * Get Movie Detail from TMDb API using a Http Request
         * @param movieId Movie ID value
         * @return instance of rx.Observable<Movie>
         */
        @GET("/movie/{id}")
        Observable<Movie> movieDetail(@Path("id") int movieId);

    }

}
