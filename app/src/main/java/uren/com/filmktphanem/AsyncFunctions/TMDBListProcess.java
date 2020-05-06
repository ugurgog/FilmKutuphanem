package uren.com.filmktphanem.AsyncFunctions;


import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uren.com.filmktphanem.Interfaces.OnEventListener;
import uren.com.filmktphanem.Utils.dataModelUtil.MovieUtil;
import uren.com.filmktphanem.data.NetworkUtils;
import uren.com.filmktphanem.models.Movie;

import static uren.com.filmktphanem.Constants.StringConstants.TYPE_NOW_PLAYING;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_POPULAR;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TOP_RATED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TRENDING;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_UPCOMING;

public class TMDBListProcess extends AsyncTask<Void, Void, String> {

    private OnEventListener<List<Movie>> mCallBack;
    public Exception mException;
    private URL TMDBURL;

    public TMDBListProcess(OnEventListener callback, URL TMDBURL) {
        mCallBack = callback;
        this.TMDBURL = TMDBURL;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (mCallBack != null) {
            mCallBack.onTaskContinue();
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        String TMDBTrendingResults = null;
        try {
            TMDBTrendingResults = NetworkUtils.getResponseFromHttpUrl(TMDBURL);
        } catch (Exception e) {
            mException = e;
            e.printStackTrace();
        }
        return TMDBTrendingResults;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null && !s.equals("")) {
            try {
                mCallBack.onSuccess(MovieUtil.parseMovies(s, "results"));

            } catch (JSONException e) {
                e.printStackTrace();
                mCallBack.onFailure(mException);
            }
        } else {
            mCallBack.onFailure(mException);
        }
    }
}
