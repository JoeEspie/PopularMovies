package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
        mMoviePosterAdapter.add("Movie One");
        mMoviePosterAdapter.add("Movie Two");
        mMoviePosterAdapter.add("Movie Three");
        mMoviePosterAdapter.add("Movie Four");
        mMoviePosterAdapter.add("Movie Five");
    }
}
