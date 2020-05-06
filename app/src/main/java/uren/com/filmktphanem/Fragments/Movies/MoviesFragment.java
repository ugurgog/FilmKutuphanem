package uren.com.filmktphanem.Fragments.Movies;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uren.com.filmktphanem.AsyncFunctions.TMDBQueryProcess;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Interfaces.OnEventListener;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.AdMobUtils;
import uren.com.filmktphanem.adapters.MovieRecyclerViewAdapter;
import uren.com.filmktphanem.models.Movie;

import static uren.com.filmktphanem.Constants.StringConstants.TYPE_NOW_PLAYING;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_POPULAR;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TOP_250;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TOP_RATED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TRENDING;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_UPCOMING;

public class MoviesFragment extends BaseFragment implements View.OnClickListener {

    View mView;

    @BindView(R.id.llTrend)
    LinearLayout llTrend;
    @BindView(R.id.llPopular)
    LinearLayout llPopular;
    @BindView(R.id.llTopRated)
    LinearLayout llTopRated;
    @BindView(R.id.llUpcoming)
    LinearLayout llUpcoming;
    @BindView(R.id.llNowPlaying)
    LinearLayout llNowPlaying;
    @BindView(R.id.llByGenre)
    LinearLayout llByGenre;
    @BindView(R.id.llTop250)
    LinearLayout llTop250;

    public MoviesFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_movies, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
        }
        return mView;
    }

    @Override
    public void onStart() {
        //getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.VISIBLE);
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        llTrend.setOnClickListener(this);
        llPopular.setOnClickListener(this);
        llTopRated.setOnClickListener(this);
        llUpcoming.setOnClickListener(this);
        llNowPlaying.setOnClickListener(this);
        llByGenre.setOnClickListener(this);
        llTop250.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v == llTrend){
            mFragmentNavigation.pushFragment(new MoviesGetFragment(TYPE_TRENDING));
        }

        if(v == llPopular){
            mFragmentNavigation.pushFragment(new MoviesGetFragment(TYPE_POPULAR));
        }

        if(v == llTopRated){
            mFragmentNavigation.pushFragment(new MoviesGetFragment(TYPE_TOP_RATED));
        }

        if(v == llUpcoming){
            mFragmentNavigation.pushFragment(new MoviesGetFragment(TYPE_UPCOMING));
        }

        if(v == llNowPlaying){
            mFragmentNavigation.pushFragment(new MoviesGetFragment(TYPE_NOW_PLAYING));
        }

        if(v == llByGenre){
            mFragmentNavigation.pushFragment(new GenreListFragment());
        }

        if(v == llTop250){
            mFragmentNavigation.pushFragment(new MoviesGetFragment(TYPE_TOP_250));
        }
    }
}