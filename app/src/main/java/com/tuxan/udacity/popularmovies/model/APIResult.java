package com.tuxan.udacity.popularmovies.model;

import java.util.List;

/**
 * Wrapper of paged TMDb API response
 */
public class APIResult<T> {
    public List<T> results;
}
