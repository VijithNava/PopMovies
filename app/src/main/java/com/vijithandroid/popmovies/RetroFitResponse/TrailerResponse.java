package com.vijithandroid.popmovies.RetroFitResponse;

import java.util.List;

/**
 * Created by vijithnava on 2016-02-23.
 */
public class TrailerResponse {
    int id;
    List<TrailerFullDetail> results;

    public List<TrailerFullDetail> getResults()
    {
        return this.results;
    }
}
