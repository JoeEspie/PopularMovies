package com.example.android.popularmovies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class to retrieve the popular movies from themoviedb.org and
 * display the posters on the main thread
 * Created by jespie on 8/3/2017.
 */
public class MainPageFragment extends Fragment {
    private ArrayAdapter<String> mMoviePosterAdapter;

    // Blank Constructor
    public MainPageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mMoviePosterAdapter = new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_posters, // The name of the layout ID.
                        R.id.list_item_poster_textview, // The ID of the textview to populate.
                        new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.v("oncreate", " after inflate");
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_main_page);
        listView.setAdapter(mMoviePosterAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        moviesTask.execute();
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MOVIEDB_ARRAY = "results";
            final String MOVIEDB_TITLE = "title";
            final String MOVIEDB_RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(MOVIEDB_ARRAY);

            String[] resultStrs = new String[10];
            Log.v(LOG_TAG, "getmoviedata 100");

            for(int i = 0; i < moviesArray.length(); i++) {
                Log.v(LOG_TAG, "getmoviedata, 102");
                // For now, using the format "Title, Release Date"
                String title;
                String releaseDate;

                // Get the JSON object representing the movie
                JSONObject movie = moviesArray.getJSONObject(i);

                title = movie.getString(MOVIEDB_TITLE);
                releaseDate = movie.getString(MOVIEDB_RELEASE_DATE);

                resultStrs[i] = title + " - " + releaseDate;
            }
            Log.v(LOG_TAG, "getmoviedata 115");
            return resultStrs;

        }
        @Override
        protected String[] doInBackground(String... params) {
            Log.v(LOG_TAG, "inside function doInBackground");
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String format = "json";
            Log.v(LOG_TAG, "doinBackground 129");
            try {
                // Construct the URL for the Movie Database query
                final String FORECAST_BASE_URL =
                        "https://api.themoviedb.org/3/movie/now_playing?";
                final String LANGUAGE_PARAM = "language";
                final String PAGE_PARAM = "page";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                        .appendQueryParameter(LANGUAGE_PARAM, "en-US")
                        .appendQueryParameter(PAGE_PARAM, "1")
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.v(LOG_TAG, "null input stream");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    Log.v(LOG_TAG, "buffer 166");
                    buffer.append(line + "\n");
                }
                Log.v(LOG_TAG, "doinBackground 167");
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
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
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            Log.v(LOG_TAG, "doinBackground 197");

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mMoviePosterAdapter.clear();
                for(String dayForecastStr : result) {
                    Log.v(LOG_TAG, "doinBackground 208");

                    mMoviePosterAdapter.add(dayForecastStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }

}
