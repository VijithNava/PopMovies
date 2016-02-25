package com.vijithandroid.popmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class MovieDetail extends ActionBarActivity {

    private ArrayAdapter<String> mReviewAdapter;
    private ShareActionProvider mShareActionProvider;
    private ArrayAdapter<String> mTrailerAdapter;
    private String[] trailerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mTrailerAdapter = new ArrayAdapter(this, R.layout.list_item_trailer, R.id.list_item_trailer_textView, new ArrayList());
        ListView trailerListView = (ListView)findViewById(R.id.Trailer_List);
        trailerListView.setAdapter(this.mTrailerAdapter);
        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)             {
                MovieDetail.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://www.youtube.com/watch?v=" + trailerId[position])));
            }
        });
        this.mReviewAdapter = new ArrayAdapter(this, R.layout.list_item_review, R.id.list_item_review_textView, new ArrayList());
        ((ListView)findViewById(R.id.Movie_Reviews)).setAdapter(this.mReviewAdapter);


        Intent intent = this.getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String movieId = intent.getStringArrayExtra(Intent.EXTRA_TEXT)[5];
            (new FetchMovieReview()).execute(movieId);
            (new FetchMovieTrailer()).execute(movieId);
            String title = intent.getStringArrayExtra(Intent.EXTRA_TEXT)[0];
            String imageURL = intent.getStringArrayExtra(Intent.EXTRA_TEXT)[1];
            String synopsis = intent.getStringArrayExtra(Intent.EXTRA_TEXT)[2];
            String rating = intent.getStringArrayExtra(Intent.EXTRA_TEXT)[3];
            String releaseDate = intent.getStringArrayExtra(Intent.EXTRA_TEXT)[4];
            ((TextView) findViewById(R.id.Movie_Title)).setText(title);
            Picasso.with(this).load("http://image.tmdb.org/t/p/w342/" + imageURL).into(((ImageView) findViewById(R.id.Movie_Image)));
            ((TextView) findViewById(R.id.Movie_Synopsis)).setText(synopsis);
            ((TextView) findViewById(R.id.Movie_Rating)).setText(rating);
            ((TextView) findViewById(R.id.Movie_ReleaseDate)).setText(releaseDate);


        }
    }

    private Intent createShareForecastIntent() {
        Intent localIntent = new Intent("android.intent.action.SEND");
        localIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        localIntent.setType("text/plain");
        localIntent.putExtra("android.intent.extra.TEXT", "http://www.youtube.com/watch?v=" + this.trailerId[0]);
        return localIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if ((this.trailerId != null) && (this.trailerId.length > 0)) {
          this.mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
     
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchMovieReview extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovieReview.class.getSimpleName();

        public FetchMovieReview() {}

        protected String[] doInBackground(String... params)
        {
          try
          {
            String[] arrayOfString = getReviewsFromResponse(new WebService().getService().getReviews(params[0], "b39cecf0553576e2757cbbf18f148e58"));
            return arrayOfString;
          }
          catch (Exception localException)
          {
            Log.e(this.LOG_TAG, "Error ", localException);
          }
          return null;
        }

        public String[] getReviewsFromResponse(ReviewResponse reviewResponse) {
          List<ReviewFullDetail> reviewList = reviewResponse.getResults();
          String[] arrayOfString = new String[reviewList.size()];
          for (int i = 0; i < reviewList.size(); i++) {
            arrayOfString[i] = (((ReviewFullDetail)reviewList.get(i)).getAuthor() + "\n\n" + ((ReviewFullDetail)reviewList.get(i)).getContent());
          }
          return arrayOfString;
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
      catch (Exception localException)
      {
        Log.e(this.LOG_TAG, "Error ", localException);
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
      if (result != null) {
        mTrailerAdapter.clear();
        trailerId = result; 
        mShareActionProvider.setShareIntent(MovieDetail.this.createShareForecastIntent());
        for (int i = 0; i < result.length; i++) {
          mTrailerAdapter.add("Trailer " + (i + 1));
        }
      }
    }
  }
    
}
