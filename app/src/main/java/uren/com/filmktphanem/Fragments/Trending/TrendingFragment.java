package uren.com.filmktphanem.Fragments.Trending;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.MainActivity;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.adapters.MovieRecyclerViewAdapter;
import uren.com.filmktphanem.data.NetworkUtils;
import uren.com.filmktphanem.models.Movie;

public class TrendingFragment extends BaseFragment {

    View mView;

    private TextView tvErrorMessage;
    private ProgressBar pbLoadingIndicator;
    private RecyclerView recyclerView;
    private ArrayList<Movie> movies;

    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private GridLayoutManager gridLayoutManager;
    private int pageCount = 0;
    private boolean loading = true;
    private MovieRecyclerViewAdapter rvAdapter;

    public TrendingFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(mView == null) {
            mView = inflater.inflate(R.layout.fragment_trending, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            makeTMDBTrendingQuery();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        recyclerView = mView.findViewById(R.id.rv_movie_list);
        recyclerView.setNestedScrollingEnabled(false);
        tvErrorMessage = mView.findViewById(R.id.tv_error_message);
        pbLoadingIndicator = mView.findViewById(R.id.pb_loading_indicator);
    }

    private void makeTMDBTrendingQuery() {
        URL TMDBTrendingURL = NetworkUtils.buildTrendingUrl();
        new TMDBQueryTask().execute(TMDBTrendingURL);
    }

    private void showRecyclerView() {
        tvErrorMessage.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

    private void parseMovies(String moviesJSONString) throws JSONException {
        JSONObject resultJSONObject = new JSONObject(moviesJSONString);
        JSONArray moviesJSONArray = resultJSONObject.getJSONArray("results");
        movies = new ArrayList<>();

        // Loop throught the JSON array results
        for (int i = 0; i < moviesJSONArray.length(); i++) {
            JSONObject movieJSONObject = new JSONObject(moviesJSONArray.get(i).toString());
            if (!movieJSONObject.isNull("poster_path")) {
                String posterPath = movieJSONObject.getString("poster_path");
                int movieId = movieJSONObject.getInt("id");

                // Add new movie object to the movie array
                movies.add(new Movie(movieId, posterPath));
            }
        }
    }

    private void populateRecyclerView() {
        rvAdapter = new MovieRecyclerViewAdapter(getContext(), movies, mFragmentNavigation);

        // Decide the number of columns based on the screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int spanCount = 2;
        if (width > 1400) {
            spanCount = 5;
        } else if (width > 700) {
            spanCount = 3;
        }

        gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(rvAdapter);
    }

    /*private void setRecyclerViewScroll() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisibleItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            pageCount++;
                            adapter.addProgressLoading();
                            loadCode = CODE_MORE_LOAD;
                            getFollowingList();
                        }
                    }
                }
            }
        });
    }

    private void setUpRecyclerView(FollowInfoListResponse followInfoListResponse) {
        loading = true;

        if (page != 1)
            followingAdapter.removeProgressLoading();

        followingAdapter.addAll(followInfoListResponse.getItems());
    }*/

    public class TMDBQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String TMDBTrendingResults = null;
            try {
                TMDBTrendingResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return TMDBTrendingResults;
        }

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
}