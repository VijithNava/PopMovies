package com.vijithandroid.popmovies.RetroFitResponse;

/**
 * Created by vijithnava on 2016-02-23.
 */
public class ReviewFullDetail {

    String author;
    String content;
    String id;
    String url;

    public String getAuthor()
    {
        return this.author;
    }

    public String getContent()
    {
        return this.content;
    }

    public void setAuthor(String paramString)
    {
        this.author = paramString;
    }

    public void setContent(String paramString)
    {
        this.content = paramString;
    }
}
