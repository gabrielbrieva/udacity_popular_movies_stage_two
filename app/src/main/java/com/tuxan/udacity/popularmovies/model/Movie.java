package com.tuxan.udacity.popularmovies.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Movie pojo for deserialization from TMDb API response
 */
public class Movie implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private String poster_path;
    private String backdrop_path;
    private String original_title;
    private String overview;
    private float popularity;
    private float vote_average;
    private String release_date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    @Override
    public boolean equals(Object o) {

        if (o == null)
            return false;

        if (!(o instanceof Movie))
            return false;

        Movie m = (Movie) o;
        long movieId = m.getId();

        if (movieId == 0)
            return false;

        return id == m.getId();

    }
}
