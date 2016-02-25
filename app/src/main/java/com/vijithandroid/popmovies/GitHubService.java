package com.vijithandroid.popmovies;

import com.vijithandroid.popmovies.RetroFitResponse.MovieResponse;
import com.vijithandroid.popmovies.RetroFitResponse.ReviewResponse;
import com.vijithandroid.popmovies.RetroFitResponse.TrailerResponse;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Vijith on 7/20/2015.
 */
public interface GitHubService {

    @GET("/discover/movie")
    MovieResponse getMoviesList(@Query("sort_by") String sort, @Query("api_key") String key);

    @GET("/movie/{id}/reviews")
    ReviewResponse getReviews(@Path("id") String movieId, @Query("api_key") String key);

    @GET("/movie/{id}/videos")
    TrailerResponse getTrailers(@Path("id") String movieId, @Query("api_key") String key);
}
