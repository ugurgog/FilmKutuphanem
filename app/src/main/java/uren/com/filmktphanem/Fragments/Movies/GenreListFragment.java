package uren.com.filmktphanem.Fragments.Movies;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uren.com.filmktphanem.AsyncFunctions.TMDBGenreListProcess;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Fragments.Movies.Adapters.GenreListAdapter;
import uren.com.filmktphanem.Fragments.Movies.Models.Genre;
import uren.com.filmktphanem.Interfaces.OnEventListener;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.data.NetworkUtils;

import static uren.com.filmktphanem.Constants.StringConstants.TYPE_NOW_PLAYING;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_POPULAR;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TOP_RATED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TRENDING;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_UPCOMING;

public class GenreListFragment extends BaseFragment{

    View mView;

    @BindView(R.id.tvMovieType)
    TextView tvMovieType;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvErrorMessage)
    TextView tvErrorMessage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private List<Genre> genreList;
    private LinearLayoutManager mLayoutManager;
    private GenreListAdapter genreListAdapter;

    public GenreListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_genre_list, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            setLayoutManager();
            getGenreList();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        genreList = new ArrayList<>();
    }

    private void setLayoutManager() {
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setAdapter() {
        genreListAdapter = new GenreListAdapter(getContext(), genreList, mFragmentNavigation);
        recyclerView.setAdapter(genreListAdapter);
    }

    private void getGenreList(){
        TMDBGenreListProcess tmdbGenreListProcess = new TMDBGenreListProcess(new OnEventListener() {
            @Override
            public void onSuccess(Object object) {
                if(object != null){
                    genreList = (List<Genre>) object;
                    setAdapter();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTaskContinue() {

            }
        });
        tmdbGenreListProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}