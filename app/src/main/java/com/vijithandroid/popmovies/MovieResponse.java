package com.vijithandroid.popmovies;

import java.util.List;

/**
 * Created by Vijith on 7/21/2015.
 */
public class MovieResponse {

    int page;
    List<ResultObject> results;
    int totalResults;
    int totalPages;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<ResultObject> getResults() {
        return results;
    }

    public void setResults(List<ResultObject> results) {
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
