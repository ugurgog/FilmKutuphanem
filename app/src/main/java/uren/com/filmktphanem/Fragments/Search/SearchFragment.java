package uren.com.filmktphanem.Fragments.Search;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.AdMobUtils;
import uren.com.filmktphanem.adapters.MovieRecyclerViewAdapter;
import uren.com.filmktphanem.data.NetworkUtils;
import uren.com.filmktphanem.models.Movie;

public class SearchFragment extends BaseFragment {

    View mView;

    private EditText etSearchBox;
    private Button btnSearchMovie;

    @BindView(R.id.adView)
    AdView adView;

    public SearchFragment() {

    }

    @Override
    public void onStart() {
        getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.VISIBLE);
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
        addListeners();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        etSearchBox = mView.findViewById(R.id.et_search_box);
        btnSearchMovie = mView.findViewById(R.id.btn_search_movie);
        MobileAds.initialize(getContext(), getResources().getString(R.string.ADMOB_APP_ID));
        AdMobUtils.loadBannerAd(adView);
    }

    private void addListeners() {
        btnSearchMovie.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (etSearchBox.getText() != null && etSearchBox.getText().toString() != null &&
                        !etSearchBox.getText().toString().trim().isEmpty())
                    submitSearch();
                else
                    Toast.makeText(getContext(), getResources().getString(R.string.please_write_something), Toast.LENGTH_SHORT);
            }
        });
    }

    private void submitSearch() {
        String TMDBQuery = etSearchBox.getText().toString();
        mFragmentNavigation.pushFragment(new SearchResultsFragment(TMDBQuery));
    }
}