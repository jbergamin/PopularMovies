package com.jakebergamin.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private ArrayAdapter<Movie> mAdapter;
    private Spinner spinner;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        mAdapter = new MoviesArrayAdapter(getContext(), new ArrayList<Movie>());
        gridView.setAdapter(mAdapter);

        // implement click listener for when movie poster is clicked
        // when clicked open new activity showing details of movie
        AdapterView.OnItemClickListener mGridItemOnClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // start new activity and pass movie object along with intent
                Movie selectedMovie = mAdapter.getItem(position);

                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("movie", selectedMovie);

                startActivity(intent);
            }
        };
        gridView.setOnItemClickListener(mGridItemOnClickListener);

        // implement spinner to change sort order
        // adapter
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_options, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        // listener
        AdapterView.OnItemSelectedListener mSpinnerItemClickListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v(LOG_TAG, (spinner.getSelectedItemPosition()+ ""));
                updateMovies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
                // do nothing
            }
        };
        spinner.setOnItemSelectedListener(mSpinnerItemClickListener);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovies();
    }

    /**
     * helper method that queries third-party api and update adapter so movies are displayed in gridview
     */
    private void updateMovies(){
        new FetchMoviesTask().execute(spinner.getSelectedItemPosition());
    }

    /**
     * AsyncTask to query api and fetch movie data in background
     */
    public class FetchMoviesTask extends AsyncTask<Integer, Void, Movie[]> {
        @Override
        protected void onPostExecute(Movie[] movieData){
            super.onPostExecute(movieData);
            if(movieData != null) {
                // clear adapter
                mAdapter.clear();
                // add each movie from returned from api call to array adapter
                for (Movie m : movieData) {
                    mAdapter.add(m);
                }
            }
        }

        @Override
        public Movie[] doInBackground(Integer... params){
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                // query themoviedb api and get resultant json as string

                String sortParam = null;
                if(params[0] == 0){
                    sortParam = "popularity.desc";
                } else if(params[0] == 1) {
                    sortParam = "vote_average.desc";
                }

                // build Uri
                Uri uri = Uri.parse(getString(R.string.query_base_url)).buildUpon()
                        .appendQueryParameter("sort_by", sortParam)
                        .appendQueryParameter("api_key", getString(R.string.api_key))
                        .build();

                URL url = new URL(uri.toString());
                Log.v(LOG_TAG, "Built Uri: " + uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(false);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null) {
                    return null;
                    // do nothing
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

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
            } finally {
                // close connection and reader
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream ", e);
                    }
                }

                // if json string is null return
                if(moviesJsonStr == null) {
                    return null;
                }

                // attempt to parse data from json we just got from server
                try {
                    JSONObject jsonObject = new JSONObject(moviesJsonStr);
                    JSONArray results = jsonObject.getJSONArray("results");
                    Movie[] resultMovies = new Movie[results.length()];
                    for(int i = 0; i < results.length(); i++) {
                        JSONObject movieJsonObject = results.getJSONObject(i);
                        String title = movieJsonObject.getString("original_title");
                        String posterPath = movieJsonObject.getString("poster_path");
                        float voteAverage = movieJsonObject.getLong("vote_average");
                        String overview = movieJsonObject.getString("overview");
                        String releaseDateStr = movieJsonObject.getString("release_date");

                        // convert releaseDate from string to GregorianCalendar
                        String[] tempArray = releaseDateStr.split("-");
                        int year, month, day;
                        // handle case when release date is null
                        if(tempArray[0].equals("null") || tempArray[0].equals("")){
                            year = 0;
                            month= 0;
                            day = 0;
                        } else {
                            year = Integer.parseInt(tempArray[0]);
                            month = Integer.parseInt(tempArray[1]);
                            day = Integer.parseInt(tempArray[2]);
                        }
                        GregorianCalendar releaseDate = new GregorianCalendar(year, month, day);

                        resultMovies[i] = new Movie(title, posterPath, voteAverage, overview, releaseDate);

                    }

                    return resultMovies;


                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error parsing JSON ", e);
                }

            }

            // this will only happen if there is an error
            return null;
        }
    }
}
