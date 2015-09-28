package com.tuxan.udacity.popularmovies.model;

import java.io.Serializable;

public class Review implements Serializable{

    private static final long serialVersionUID = 1L;

    private String id;
    private long movie_id;
    private String author;
    private String content;

    public Review() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(long movie_id) {
        this.movie_id = movie_id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
