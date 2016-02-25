package com.vijithandroid.popmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.vijithandroid.popmovies.RetroFitResponse.MovieFullDetail;
import com.vijithandroid.popmovies.RetroFitResponse.MovieResponse;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ImageAdapter mImageAdapter;
    private ArrayList<MovieInfoObject> list;
    SharedPreferences myPref;
    SharedPreferences.Editor prefsEditor;
    boolean fromOrientation = false;

    public void updateMovieList() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortType = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));

        moviesTask.execute(sortType);
    }

    @Override
    public void onStart() {
        super.onStart();
        fromOrientation = myPref.getBoolean("fromOrientation", false);
        if(!fromOrientation){
            updateMovieList();
        }
        prefsEditor.putBoolean("fromOrientation", false);
        prefsEditor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myPref = this.getSharedPreferences("pref_general", MODE_WORLD_WRITEABLE);
        prefsEditor = myPref.edit();

        if(savedInstanceState == null || !savedInstanceState.containsKey("key")){
            list = new ArrayList<MovieInfoObject>();
        }
        else{
            list = savedInstanceState.getParcelableArrayList("key");
        }
        mImageAdapter = new ImageAdapter(this, list);

        GridView gridView = (GridView) this.findViewById(R.id.gridview);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gridView.setNumColumns(3);
        }
        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            gridView.setNumColumns(5);
        }

        gridView.setAdapter(mImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieInfoObject m = mImageAdapter.getMoviesArray().get(position);
                Intent detailIntent = new Intent(((GridView) findViewById(R.id.gridview)).getContext(), MovieDetail.class)
                        .putExtra(Intent.EXTRA_TEXT, m.returnInfo());
                startActivity(detailIntent);
            }
        });
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        prefsEditor.putBoolean("fromOrientation", true);
        prefsEditor.commit();
        return super.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", list);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState == null || !savedInstanceState.containsKey("key")){
            list = new ArrayList<MovieInfoObject>();
        }
        else{
            list = savedInstanceState.getParcelableArrayList("key");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieInfoObject[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


        private MovieInfoObject[] getMovieDataFromResponse(MovieResponse movieResponse){
              List<MovieFullDetail> movieList = movieResponse.getResults();
              MovieInfoObject[] result = new MovieInfoObject[movieList.size()];
              for (int i = 0; i < movieList.size(); i++)
              {
                MovieFullDetail localMovieFullDetail = (MovieFullDetail)movieList.get(i);
                result[i] = new MovieInfoObject(localMovieFullDetail.getOriginal_title(), localMovieFullDetail.getPoster_path(), localMovieFullDetail.getOverview(), localMovieFullDetail.getVote_average(), localMovieFullDetail.getRelease_date(), localMovieFullDetail.getId());
              }
              return result;
            }

        @Override
        protected MovieInfoObject[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            MovieInfoObject[] result;
            String key = "b39cecf0553576e2757cbbf18f148e58";

            try {
                WebService webService = new WebService();
                MovieResponse response = webService.service.getMoviesList(params[0], key);
                return getMovieDataFromResponse(response);

            }// catch (IOException e) {
            catch (Exception e){
                Log.e(LOG_TAG, "Error ", e);

                return null;
            } 

        }

        @Override
        protected void onPostExecute(MovieInfoObject[] movies) {
            if (movies != null) {
                mImageAdapter.getMoviesArray().clear();
                for (MovieInfoObject m : movies) {
                    mImageAdapter.getMoviesArray().add(m);
                }
                mImageAdapter.notifyDataSetChanged();
            }
        }
    }


}
