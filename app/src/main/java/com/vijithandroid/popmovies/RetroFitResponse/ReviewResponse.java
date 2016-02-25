package com.vijithandroid.popmovies.RetroFitResponse;

import java.util.List;

/**
 * Created by vijithnava on 2016-02-23.
 */
public class ReviewResponse {
    String id;
    String page;
    List<ReviewFullDetail> results;
    String totalPages;
    String totalResults;

    public List<ReviewFullDetail> getResults()
    {
        return this.results;
    }
}
