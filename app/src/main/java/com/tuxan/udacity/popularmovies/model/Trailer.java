package com.tuxan.udacity.popularmovies.model;

import java.io.Serializable;

public class Trailer implements Serializable {

    private static final long serialVersionUID = 1L;

    private long movie_id;
    private String source;
    private String name;

    public Trailer() { }

    public long getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(long movie_id) {
        this.movie_id = movie_id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source){
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
