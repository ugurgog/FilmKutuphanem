package uren.com.filmktphanem.Fragments.Search;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import uren.com.filmktphanem.AsyncFunctions.TMDBQueryProcess;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Interfaces.OnEventListener;
import uren.com.filmktphanem.MainActivity;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.AdMobUtils;
import uren.com.filmktphanem.adapters.MovieRecyclerViewAdapter;
import uren.com.filmktphanem.data.FavoritesDbHelper;
import uren.com.filmktphanem.data.MyLibraryItem;
import uren.com.filmktphanem.data.NetworkUtils;
import uren.com.filmktphanem.models.Movie;

import static uren.com.filmktphanem.Constants.StringConstants.TYPE_RECOMMENDED_MOVIES;

public class SearchFragment extends BaseFragment {

    View mView;

    private EditText etSearchBox;
    private Button btnSearchMovie;
    private RecyclerView recyclerView;
    private ProgressBar pbLoadingIndicator;
    private RelativeLayout rlProgressBar;
    private TextView tv_error_message;
    private ImageView imgvRefresh;

    private MovieRecyclerViewAdapter rvAdapter;

    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private GridLayoutManager gridLayoutManager;
    private int pageCount = 1;
    private boolean loading = true;

    private static final int CODE_FIRST_LOAD = 0;
    private static final int CODE_MORE_LOAD = 1;
    private int loadCode = CODE_FIRST_LOAD;
    int spanCount = 2;

    private FavoritesDbHelper dbHelper;
    private List<MyLibraryItem> myLibraryItemList;

    public SearchFragment() {

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

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        populateRecyclerView();
        getTMDBQuery();
        addListeners();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        etSearchBox = mView.findViewById(R.id.searchEdittext);
        btnSearchMovie = mView.findViewById(R.id.btn_search_movie);
        recyclerView = mView.findViewById(R.id.rv_movie_list);
        pbLoadingIndicator = mView.findViewById(R.id.pb_loading_indicator);
        rlProgressBar = mView.findViewById(R.id.rlProgressBar);
        tv_error_message = mView.findViewById(R.id.tv_error_message);
        imgvRefresh = mView.findViewById(R.id.imgvRefresh);

        dbHelper = new FavoritesDbHelper(getContext());
        myLibraryItemList = new ArrayList<>();
        myLibraryItemList = dbHelper.getAllLibraryItems();
    }

    private void addListeners() {
        btnSearchMovie.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (etSearchBox.getText() != null && !etSearchBox.getText().toString().trim().isEmpty())
                    submitSearch();
                else
                    Toast.makeText(getContext(), getResources().getString(R.string.please_write_something), Toast.LENGTH_SHORT);
            }
        });

        imgvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgvRefresh.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.tab_rotate_anim));
                resetPaginationVariables();
                rvAdapter.removeAll();
                getTMDBQuery();
            }
        });
    }

    public void resetPaginationVariables(){
        pastVisibleItems = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        pageCount = 1;
        loading = true;
        loadCode = CODE_FIRST_LOAD;
    }

    public int getRandomMovieId(){
        if(myLibraryItemList.size() == 0)
            return 20453; //3 idiots
        else if(myLibraryItemList.size() <= 2)
            return myLibraryItemList.get(0).getMovieId();
        else{
            Random rand = new Random();
            int randNum = rand.nextInt(myLibraryItemList.size());
            return myLibraryItemList.get(randNum).getMovieId();
        }
    }

    private void submitSearch() {
        String TMDBQuery = etSearchBox.getText().toString();
        mFragmentNavigation.pushFragment(new SearchResultsFragment(TMDBQuery));
    }

    private void getTMDBQuery() {
        int randMovieId = getRandomMovieId();

        rlProgressBar.setVisibility(View.VISIBLE);
        tv_error_message.setVisibility(View.GONE);
        pbLoadingIndicator.setVisibility(View.VISIBLE);

        TMDBQueryProcess tmdbQueryProcess = new TMDBQueryProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                if (object != null) {
                    setUpRecyclerView((List<Movie>) object);
                }
                rlProgressBar.setVisibility(View.GONE);
                tv_error_message.setVisibility(View.GONE);
                pbLoadingIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                showErrorMessage();
                rlProgressBar.setVisibility(View.GONE);
                tv_error_message.setVisibility(View.VISIBLE);
                pbLoadingIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTaskContinue() {
                if (loadCode == CODE_FIRST_LOAD)
                    pbLoadingIndicator.setVisibility(View.VISIBLE);
            }
        }, pageCount, TYPE_RECOMMENDED_MOVIES, randMovieId);

        tmdbQueryProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showRecyclerView() {
        tv_error_message.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        tv_error_message.setVisibility(View.VISIBLE);
    }

    private void populateRecyclerView() {
        rvAdapter = new MovieRecyclerViewAdapter(getContext(), mFragmentNavigation);
        setSpanCount();
        gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(rvAdapter);
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