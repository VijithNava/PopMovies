package com.vijithandroid.popmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vijithandroid.popmovies.RetroFitResponse.ReviewFullDetail;
import com.vijithandroid.popmovies.RetroFitResponse.ReviewResponse;
import com.vijithandroid.popmovies.RetroFitResponse.TrailerFullDetail;
import com.vijithandroid.popmovies.RetroFitResponse.TrailerResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijithnava on 2016-02-25.
 */
public class MovieDetailFragment extends Fragment {

    MovieInfoObject mMovieInfoObject;
    int mSelectedIndex;
    static final String DETAIL_MIO = "MovieInfoObject";
    static final String DETAIL_SI = "selectedIndex";

    private ArrayAdapter<String> mReviewAdapter;
    private ShareActionProvider mShareActionProvider;
    private ArrayAdapter<String> mTrailerAdapter;
    private String[] trailerId;

    private TextView mMovieTitle;
    private ImageView mMovieImage;
    private TextView mMovieReleaseDate;
    private TextView mMovieRating;
    private TextView mMovieSynopsis;
    private ListView mTrailerList;
    private ListView mReviewList;

    public MovieDetailFragment(){setHasOptionsMenu(true);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieInfoObject = arguments.getParcelable(MovieDetailFragment.DETAIL_MIO);
            mSelectedIndex = arguments.getInt(MovieDetailFragment.DETAIL_SI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mMovieTitle = (TextView) rootView.findViewById(R.id.Movie_Title);
        mMovieImage = (ImageView) rootView.findViewById(R.id.Movie_Image);
        mMovieReleaseDate = (TextView) rootView.findViewById(R.id.Movie_ReleaseDate);
        mMovieRating = (TextView) rootView.findViewById(R.id.Movie_Rating);
        mMovieSynopsis = (TextView) rootView.findViewById(R.id.Movie_Synopsis);
        mTrailerList = (ListView) rootView.findViewById(R.id.Trailer_List);
        mReviewList = (ListView) rootView.findViewById(R.id.Movie_Reviews);
        return rootView;
    }

    @Override
    public void onStart(){
        mTrailerAdapter = new ArrayAdapter(getActivity(), R.layout.list_item_trailer, R.id.list_item_trailer_textView, new ArrayList());
        mTrailerList.setAdapter(this.mTrailerAdapter);
        mTrailerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)             {
                getActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://www.youtube.com/watch?v=" + trailerId[position])));
            }
        });
        this.mReviewAdapter = new ArrayAdapter(getActivity(), R.layout.list_item_review, R.id.list_item_review_textView, new ArrayList());
        mReviewList.setAdapter(this.mReviewAdapter);


        Intent intent = getActivity().getIntent();

        if (mMovieInfoObject != null) {
            String movieId = mMovieInfoObject.getMovieId();
            (new FetchMovieReview()).execute(movieId);
            (new FetchMovieTrailer()).execute(movieId);
            String title = mMovieInfoObject.getTitle();
            String imageURL = mMovieInfoObject.getImageURL();
            String synopsis = mMovieInfoObject.getSynopsis();
            String rating = mMovieInfoObject.getRating().isEmpty() == true ? "N/A" : mMovieInfoObject.getRating() + "/10";
            String releaseDate = mMovieInfoObject.getReleaseDate().isEmpty() == true ? "" : mMovieInfoObject.getReleaseDate().substring(0,4);
            mMovieTitle.setText(title);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w342/" + imageURL).into(mMovieImage);
            mMovieSynopsis.setText(synopsis);
            mMovieRating.setText(rating);
            mMovieReleaseDate.setText(releaseDate);
        }

        super.onStart();
    }


    private Intent createShareForecastIntent() {
        Intent localIntent = new Intent("android.intent.action.SEND");
        localIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        localIntent.setType("text/plain");
        localIntent.putExtra("android.intent.extra.TEXT", "http://www.youtube.com/watch?v=" + this.trailerId[0]);
        return localIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if ((this.trailerId != null) && (this.trailerId.length > 0)) {
            this.mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //usually Loader function will initialize loader here which load data into the member View variables
        //but we will manually load them using the arguments passed into the Intent

        super.onActivityCreated(savedInstanceState);
    }

    public class FetchMovieReview extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovieReview.class.getSimpleName();

        public FetchMovieReview() {}

        protected String[] doInBackground(String... params)
        {
            try
            {
                return getReviewsFromResponse(new WebService().getService().getReviews(params[0], "b39cecf0553576e2757cbbf18f148e58"));
            }
            catch (Exception e)
            {
                Log.e(this.LOG_TAG, "Error ", e);
            }
            return null;
        }

        public String[] getReviewsFromResponse(ReviewResponse reviewResponse) {
            List<ReviewFullDetail> reviewList = reviewResponse.getResults();
            String[] result = new String[reviewList.size()];
            for (int i = 0; i < reviewList.size(); i++) {
                result[i] = reviewList.get(i).getAuthor() + "\n\n" + reviewList.get(i).getContent();
            }
            return result;
        }

        protected void onPostExecute(String[] result)
        {
            if (result != null) {
                mReviewAdapter.clear();
                for (String s: result){
                    mReviewAdapter.add(s);
                }
            }
        }
    }

    public class FetchMovieTrailer extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovieTrailer.class.getSimpleName();

        public FetchMovieTrailer() {}

        protected String[] doInBackground(String... params)
        {
            try
            {
                String[] result = getTrailersFromResponse((new WebService()).getService().getTrailers(params[0], "b39cecf0553576e2757cbbf18f148e58"));
                return result;
            }
            catch (Exception e)
            {
                Log.e(this.LOG_TAG, "Error ", e);
            }
            return null;
        }

        public String[] getTrailersFromResponse(TrailerResponse trailerResponse)
        {
            List<TrailerFullDetail> trailerList = trailerResponse.getResults();
            String[] result = new String[trailerList.size()];
            for (int i = 0; i < trailerList.size(); i++) {
                result[i] = ((TrailerFullDetail)trailerList.get(i)).getKey();
            }
            return result;
        }

        protected void onPostExecute(String[] result)
        {
            if (result != null && result.length > 0) {
                mTrailerAdapter.clear();
                trailerId = result;
                if (mShareActionProvider!= null){ mShareActionProvider.setShareIntent(createShareForecastIntent());}
                for (int i = 0; i < result.length; i++) {
                    mTrailerAdapter.add("Trailer " + (i + 1));
                }
            }
        }
    }


}
