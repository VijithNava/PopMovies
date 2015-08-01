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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


        private MovieInfoObject[] getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MD_RESULTS = "results";
            final String MD_TITLE = "original_title";
            final String MD_IMAGEPATH = "poster_path";
            final String MD_SYNOPSIS = "overview";
            final String MD_RATING = "vote_average";
            final String MD_RELEASE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(MD_RESULTS);


            MovieInfoObject[] resultStrs = new MovieInfoObject[moviesArray.length()];


            for (int i = 0; i < moviesArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String title;
                String imageURL;
                String synopsis;
                String rating;
                String releaseDate;

                // Get the JSON object representing the day
                JSONObject movie = moviesArray.getJSONObject(i);

                title = movie.getString(MD_TITLE);
                imageURL = movie.getString(MD_IMAGEPATH);
                synopsis = movie.getString(MD_SYNOPSIS);
                rating = movie.getString(MD_RATING);
                releaseDate = movie.getString(MD_RELEASE);

                resultStrs[i] = new MovieInfoObject(title, imageURL, synopsis, rating, releaseDate);
            }


            return resultStrs;

        }

        private MovieInfoObject[] getMovieDataFromJson(Response resp) {

            // These are the names of the JSON objects that need to be extracted.
            final String MD_RESULTS = "results";
            final String MD_TITLE = "original_title";
            final String MD_IMAGEPATH = "poster_path";
            final String MD_SYNOPSIS = "overview";
            final String MD_RATING = "vote_average";
            final String MD_RELEASE = "release_date";

            List<ResultObject> moviesArray = resp.getResults();


            MovieInfoObject[] resultStrs = new MovieInfoObject[moviesArray.size()];


            for (int i = 0; i < moviesArray.size(); i++) {
                // For now, using the format "Day, description, hi/low"
                String title;
                String imageURL;
                String synopsis;
                String rating;
                String releaseDate;

                // Get the JSON object representing the day
                ResultObject movie = moviesArray.get(i);

                title = movie.getOriginal_title();
                imageURL = movie.getPoster_path();
                synopsis = movie.getOverview();
                rating = movie.getVote_average();
                releaseDate = movie.getRelease_date();

                resultStrs[i] = new MovieInfoObject(title, imageURL, synopsis, rating, releaseDate);
            }


            return resultStrs;

        }

        @Override
        protected MovieInfoObject[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            /*
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            */

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            MovieInfoObject[] result;
            String key = "b39cecf0553576e2757cbbf18f148e58";

            try {
                WebService webService = new WebService();
                Response response = webService.service.getMoviesList(params[0], key);
                // Construct the URL for the theMovieDB query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                /*
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String API_KEY = "api_key";
                final String SORT_BY = "sort_by";
                Uri.Builder builder = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, params[0])
                        .appendQueryParameter(API_KEY, key);

                URL url = new URL(builder.build().toString());

                // Log.v(LOG_TAG, "Built URI " + builder.build().toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                */
                //  Log.v(LOG_TAG, "Movies JSON String: " + moviesJsonStr);
                try {
                    result = getMovieDataFromJson(response);
                } catch (Exception e){
                //catch (JSONException e) {
                    return null;
                }

            }// catch (IOException e) {
            catch (Exception e){
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } /*finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }*/

            return result;
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
