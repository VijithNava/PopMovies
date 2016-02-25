package com.vijithandroid.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vijith on 7/17/2015.
 */
public class MovieInfoObject implements Parcelable {
    private String title;
    private String imageURL;
    private String synopsis;
    private String rating;
    private String releaseDate;
    private String movieId;

    public MovieInfoObject(String title, String imgURL, String overview, String rating, String relDate, String movieId) {
        this.title = title;
        this.imageURL = imgURL;
        this.synopsis = overview;
        this.rating = rating;
        this.releaseDate = relDate;
        this.movieId = movieId;
    }

    private MovieInfoObject(Parcel in) {
        this.title = in.readString();
        this.imageURL = in.readString();
        this.synopsis = in.readString();
        this.rating = in.readString();
        this.releaseDate = in.readString();
        this.movieId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(imageURL);
        out.writeString(synopsis);
        out.writeString(rating);
        out.writeString(releaseDate);
        out.writeString(movieId);
    }

    public static final Creator<MovieInfoObject> CREATOR = new Creator<MovieInfoObject>() {
        public MovieInfoObject createFromParcel(Parcel in) {
            return new MovieInfoObject(in);
        }

        public MovieInfoObject[] newArray(int size) {
            return new MovieInfoObject[size];
        }
    };

    public String[] returnInfo() {
        return new String[]{title, imageURL, synopsis, rating, releaseDate, movieId};
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

}
