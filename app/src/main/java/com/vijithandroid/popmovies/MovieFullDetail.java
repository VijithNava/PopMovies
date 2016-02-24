package com.vijithandroid.popmovies;

import java.util.List;

/**
 * Created by Vijith on 7/21/2015.
 */
public class MovieFullDetail {

    String adult;
    String backdrop_path;
    List<Integer> genre_ids;
    String id;
    String original_language;
    String original_title;
    String overview;
    String release_date;
    String poster_path;
    String popularity;
    String title;
    String video;
    String vote_average;
    String vote_count;

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
