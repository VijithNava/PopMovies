package com.vijithandroid.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class MovieDetail extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = this.getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String title = intent.getStringArrayExtra(Intent.EXTRA_TEXT)[0];
            String imageURL = intent.getStringArrayExtra(Intent.EXTRA_TEXT)[1];
            String synopsis = intent.getStringArrayExtra(Intent.EXTRA_TEXT)[2];
            String rating = intent.getStringArrayExtra(Intent.EXTRA_TEXT)[3];
            String releaseDate = intent.getStringArrayExtra(Intent.EXTRA_TEXT)[4];
            ((TextView) findViewById(R.id.Movie_Title)).setText(title);
            Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + imageURL).into(((ImageView) findViewById(R.id.Movie_Image)));
            ((TextView) findViewById(R.id.Movie_Synopsis)).setText(synopsis);
            ((TextView) findViewById(R.id.Movie_Rating)).setText(rating);
            ((TextView) findViewById(R.id.Movie_ReleaseDate)).setText(releaseDate);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
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
}
