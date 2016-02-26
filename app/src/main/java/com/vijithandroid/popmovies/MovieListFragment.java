package com.vijithandroid.popmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.vijithandroid.popmovies.RetroFitResponse.MovieFullDetail;
import com.vijithandroid.popmovies.RetroFitResponse.MovieResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijithnava on 2016-02-24.
 */
public class MovieListFragment extends Fragment {

    private ImageAdapter mImageAdapter;
    private ArrayList<MovieInfoObject> list;
    boolean fromOrientation = false;

    String mSortType;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(int index, MovieInfoObject movieInfoObject);
    }

    @Override
    public void onStart(){
        SharedPreferences myPref = getActivity().getSharedPreferences("pref_general", Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor prefsEditor = myPref.edit();

        boolean fromOrientation = myPref.getBoolean("fromOrientation", false);
        if (!fromOrientation){
            updateMovieList();
        }
        prefsEditor.putBoolean("fromOrientation", false);
        prefsEditor.commit();
        super.onStart();
    }

    public void updateMovieList() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));

        moviesTask.execute(sortType);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", list);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        if(savedInstanceState == null || !savedInstanceState.containsKey("key")){
            list = new ArrayList<MovieInfoObject>();
        }
        else{
            list = savedInstanceState.getParcelableArrayList("key");
        }
        mImageAdapter = new ImageAdapter(getActivity(), list);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        /*if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gridView.setNumColumns(3);
        }
        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            gridView.setNumColumns(5);
        }
        */

        gridView.setAdapter(mImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieInfoObject m = mImageAdapter.getMoviesArray().get(position);
                ((Callback) getActivity()).onItemSelected(position, m);
            }
        });

        return rootView;

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
