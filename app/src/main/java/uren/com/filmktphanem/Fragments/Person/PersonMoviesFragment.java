package uren.com.filmktphanem.Fragments.Person;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uren.com.filmktphanem.AsyncFunctions.TMDBQueryProcess;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Fragments.Movies.MovieDetailFragment;
import uren.com.filmktphanem.Interfaces.OnEventListener;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.dataModelUtil.MovieUtil;
import uren.com.filmktphanem.adapters.MovieRecyclerViewAdapter;
import uren.com.filmktphanem.data.NetworkUtils;
import uren.com.filmktphanem.models.Movie;

public class PersonMoviesFragment extends BaseFragment {

    View mView;

    @BindView(R.id.rv_movie_list)
    RecyclerView recyclerView;
    @BindView(R.id.tv_error_message)
    TextView tvErrorMessage;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar pbLoadingIndicator;
    @BindView(R.id.tvMovieType)
    TextView tvMovieType;

    private GridLayoutManager gridLayoutManager;
    private MovieRecyclerViewAdapter rvAdapter;

    int spanCount = 2;
    private int personId;
    private String name;

    public PersonMoviesFragment(int personId, String name) {
        this.personId = personId;
        this.name = name;
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
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        tvMovieType.setText(name.concat(" ").concat(getContext().getResources().getString(R.string.all_movies)));
        URL TMDBMovieDetailURL = NetworkUtils.buildPersonMoviesUrl(personId);
        new TMDBQueryTask().execute(TMDBMovieDetailURL);
    }

    public class TMDBQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            Log.e("url", searchUrl.toString());
            String TMDBResults = null;
            try {
                TMDBResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return TMDBResults;
        }

        @Override
        protected void onPostExecute(String s) {
            pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if (s != null && !s.equals("")) {
                try {
                    List<Movie> movies = MovieUtil.parseMovies(s, "cast");
                    rvAdapter.addAll(movies);
                    pbLoadingIndicator.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    showErrorMessage();
                }
            } else {
                showErrorMessage();
            }
        }
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        tvErrorMessage.setVisibility(View.VISIBLE);
        pbLoadingIndicator.setVisibility(View.GONE);
    }

    private void populateRecyclerView() {
        rvAdapter = new MovieRecyclerViewAdapter(getContext(), mFragmentNavigation);
        setSpanCount();
        gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(rvAdapter);
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
}