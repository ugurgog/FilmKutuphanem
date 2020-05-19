package uren.com.filmktphanem.Fragments.Movies;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uren.com.filmktphanem.AsyncFunctions.TMDBQueryProcess;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Interfaces.OnEventListener;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.AdMobUtils;
import uren.com.filmktphanem.Utils.CommonUtils;
import uren.com.filmktphanem.adapters.MovieRecyclerViewAdapter;
import uren.com.filmktphanem.models.Movie;

import static uren.com.filmktphanem.Constants.StringConstants.TYPE_BY_GENRE;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_NOW_PLAYING;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_POPULAR;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TOP_250;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TOP_RATED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TRENDING;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_UPCOMING;



@SuppressLint("ValidFragment")
public class MoviesGetFragment extends BaseFragment {

    View mView;

    @BindView(R.id.rv_movie_list)
    RecyclerView recyclerView;
    @BindView(R.id.tv_error_message)
    TextView tvErrorMessage;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar pbLoadingIndicator;
    @BindView(R.id.tvMovieType)
    TextView tvMovieType;

    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private GridLayoutManager gridLayoutManager;
    private int pageCount = 1;
    private boolean loading = true;
    private MovieRecyclerViewAdapter rvAdapter;

    private static final int CODE_FIRST_LOAD = 0;
    private static final int CODE_MORE_LOAD = 1;
    private int loadCode = CODE_FIRST_LOAD;
    private String type;

    public MoviesGetFragment(String type) {
        this.type = type;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_trending, container, false);
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
        setMovieType();
    }

    private void setMovieType() {

        switch (type) {
            case TYPE_TRENDING:
                tvMovieType.setText(getResources().getString(R.string.trending_movies));
                break;
            case TYPE_POPULAR:
                tvMovieType.setText(getResources().getString(R.string.popular_movies));
                break;
            case TYPE_TOP_RATED:
                tvMovieType.setText(getResources().getString(R.string.top_rated_movies));
                break;
            case TYPE_UPCOMING:
                tvMovieType.setText(getResources().getString(R.string.upcoming_movies));
                break;
            case TYPE_NOW_PLAYING:
                tvMovieType.setText(getResources().getString(R.string.now_playing_movies));
                break;
            case TYPE_TOP_250:
                tvMovieType.setText(getResources().getString(R.string.by_top_twohundred));
                break;
            default:
                break;
        }
    }

    private void getTMDBQuery() {
        TMDBQueryProcess tmdbQueryProcess = new TMDBQueryProcess(new OnEventListener() {
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
        }, pageCount, type, 0);

        tmdbQueryProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showRecyclerView() {
        tvErrorMessage.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

    private void populateRecyclerView() {
        rvAdapter = new MovieRecyclerViewAdapter(getContext(), mFragmentNavigation);
        gridLayoutManager = new GridLayoutManager(getContext(), CommonUtils.getSpanCount(getActivity()));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(rvAdapter);
        setRecyclerViewScroll();
    }

    private void setRecyclerViewScroll() {

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

    private void setUpRecyclerView(List<Movie> movieList) {
        loading = true;

        if (pageCount != 1)
            rvAdapter.removeProgressLoading();

        rvAdapter.addAll(movieList);
    }

}