package uren.com.filmktphanem.Fragments.Search;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uren.com.filmktphanem.AsyncFunctions.TMDBQueryProcess;
import uren.com.filmktphanem.AsyncFunctions.TMDBSearchMovieProcess;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Interfaces.OnEventListener;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.AdMobUtils;
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

    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private GridLayoutManager gridLayoutManager;
    private int pageCount = 1;
    private boolean loading = true;
    private MovieRecyclerViewAdapter rvAdapter;

    private static final int CODE_FIRST_LOAD = 0;
    private static final int CODE_MORE_LOAD = 1;
    private int loadCode = CODE_FIRST_LOAD;
    int spanCount = 2;

    public SearchResultsFragment(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Override
    public void onStart() {
        //getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.VISIBLE);
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(mView == null) {
            mView = inflater.inflate(R.layout.fragment_search_results, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            populateRecyclerView();
            getTMDBQuery();
        }
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
        tvResultsTitle.setText(searchQuery);
    }

    private void getTMDBQuery() {
        TMDBSearchMovieProcess tmdbSearchMovieProcess = new TMDBSearchMovieProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                if (object != null) {
                    setUpRecyclerView((List<Movie>) object);
                }
                pbLoadingIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                showErrorMessage();
                pbLoadingIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {
                if (loadCode == CODE_FIRST_LOAD)
                    pbLoadingIndicator.setVisibility(View.VISIBLE);
            }
        }, pageCount, searchQuery);

        tmdbSearchMovieProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void populateRecyclerView() {
        rvAdapter = new MovieRecyclerViewAdapter(getContext(), mFragmentNavigation);
        setSpanCount();
        gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        rvMovieList.setLayoutManager(gridLayoutManager);
        rvMovieList.setAdapter(rvAdapter);
        setRecyclerViewScroll();
    }

    private void setSpanCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        if (width > 1400) {
            spanCount = 5;
        } else if (width > 700) {
            spanCount = 3;
        }
    }

    private void showRecyclerView() {
        tvErrorMessage.setVisibility(View.INVISIBLE);
        rvMovieList.setVisibility(View.VISIBLE);
    }

    private void setRecyclerViewScroll() {

        rvMovieList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            rvAdapter.addProgressLoading();
                            loadCode = CODE_MORE_LOAD;
                            showRecyclerView();
                            getTMDBQuery();
                        }
                    }
                }
            }
        });
    }

    private void showErrorMessage() {
        rvMovieList.setVisibility(View.INVISIBLE);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

    private void setUpRecyclerView(List<Movie> movieList) {
        loading = true;

        if (pageCount != 1)
            rvAdapter.removeProgressLoading();

        rvAdapter.addAll(movieList);
    }

}