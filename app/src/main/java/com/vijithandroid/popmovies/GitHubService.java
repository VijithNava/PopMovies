package com.vijithandroid.popmovies;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Vijith on 7/20/2015.
 */
public interface GitHubService {

    @GET("/discover/movie")
    Response getMoviesList(@Query("sort_by") String sort, @Query("api_key") String key);
}
