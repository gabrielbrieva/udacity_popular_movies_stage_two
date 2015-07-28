package com.tuxan.udacity.popularmovies.model;

/**
 * Key value pair container for sort spinner
 */
public class SortValuePair {
    private String key;
    private String value;

    public SortValuePair(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
