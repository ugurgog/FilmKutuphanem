package uren.com.filmktphanem.Fragments.Search;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.ButterKnife;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.adapters.MovieRecyclerViewAdapter;
import uren.com.filmktphanem.data.NetworkUtils;
import uren.com.filmktphanem.models.Movie;

@SuppressLint("ValidFragment")
public class SearchResultsFragment extends BaseFragment {

    View mView;

    private String searchQuery;
    private TextView tvErrorMessage;
    private TextView tvResultsTitle;
    private ProgressBar pbLoadingIndicator;
    private RecyclerView rvMovieList;
    private ArrayList<Movie> movies;

    public SearchResultsFragment(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_search_results, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        addListeners();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        tvErrorMessage = mView.findViewById(R.id.tv_error_message);
        pbLoadingIndicator = mView.findViewById(R.id.pb_loading_indicator);
        tvResultsTitle = mView.findViewById(R.id.tv_results_title);
        rvMovieList = mView.findViewById(R.id.rv_movie_list);
        rvMovieList.setNestedScrollingEnabled(false);

        URL TMDBSearchURL = NetworkUtils.buildSearchUrl(searchQuery);
        new SearchResultsFragment.TMDBQueryTask().execute(TMDBSearchURL);
    }

    private void addListeners() {

    }


    public class TMDBQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String TMDBSearchResults = null;
            try {
                TMDBSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return TMDBSearchResults;
        }

        /**
         * Executes when the API call is finished.
         */
        @Override
        protected void onPostExecute(String s) {
            pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if (s != null && !s.equals("")) {
                showRecyclerView();
                try {
                    parseMovies(s);
                    populateRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                    showErrorMessage();
                }
            } else {
                showErrorMessage();
            }
        }
    }

    /**
     * Parses the movie JSON string and inserts the results as a new Movie object into the Movie array.
     *
     * @param moviesJSONString
     * The JSON string containing the results.
     */
    private void parseMovies(String moviesJSONString) throws JSONException {
        JSONObject resultJSONObject = new JSONObject(moviesJSONString);
        JSONArray moviesJSONArray = resultJSONObject.getJSONArray("results");
        movies = new ArrayList<Movie>();

        // Loop through json array
        for (int i = 0; i < moviesJSONArray.length(); i++) {
            JSONObject movieJSONObject = new JSONObject(moviesJSONArray.get(i).toString());
            if (!movieJSONObject.isNull("poster_path")) {
                int movieId = movieJSONObject.getInt("id");
                String posterPath = movieJSONObject.getString("poster_path");

                // Add movie object
                movies.add(new Movie(movieId, posterPath));
            }
        }

        // Set result count
        tvResultsTitle.setText(movies.size() + " " +  getResources().getString(R.string.results_heading) + " '" + searchQuery + "'");
    }

    /**
     * Populates the recyclerview with the retrieved movies.
     */
    private void populateRecyclerView() {
        MovieRecyclerViewAdapter rvAdapter = new MovieRecyclerViewAdapter(getContext(), movies, mFragmentNavigation);
        rvMovieList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvMovieList.setAdapter(rvAdapter);
    }

    /**
     * Show recycler view.
     */
    private void showRecyclerView() {
        tvErrorMessage.setVisibility(View.INVISIBLE);
        rvMovieList.setVisibility(View.VISIBLE);
    }

    /**
     * Show error message.
     */
    private void showErrorMessage() {
        rvMovieList.setVisibility(View.INVISIBLE);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }


}