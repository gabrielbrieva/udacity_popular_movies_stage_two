package com.tuxan.udacity.popularmovies.model;

import java.io.Serializable;

public class Trailer implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private long movie_id;
    private String key;
    private String name;

    public Trailer() { }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(long movie_id) {
        this.movie_id = movie_id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
