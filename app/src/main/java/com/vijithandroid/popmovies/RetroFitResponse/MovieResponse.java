package com.vijithandroid.popmovies.RetroFitResponse;

import java.util.List;

/**
 * Created by Vijith on 7/21/2015.
 */
public class MovieResponse {

    int page;
    List<MovieFullDetail> results;
    int totalResults;
    int totalPages;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<MovieFullDetail> getResults() {
        return results;
    }

    public void setResults(List<MovieFullDetail> results) {
        this.results = results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
