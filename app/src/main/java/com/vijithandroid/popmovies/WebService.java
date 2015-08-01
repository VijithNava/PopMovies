package com.vijithandroid.popmovies;

import retrofit.RestAdapter;

/**
 * Created by Vijith on 7/21/2015.
 */
public class WebService{

    private static final String BASE_URL = "http://api.themoviedb.org/3";
    RestAdapter restAdapter;
    GitHubService service;

    public WebService(){
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .build();
        service = restAdapter.create(GitHubService.class);
    }
}
