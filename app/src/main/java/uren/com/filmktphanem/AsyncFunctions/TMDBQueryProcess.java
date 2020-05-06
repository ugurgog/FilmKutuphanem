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
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TOP_250;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TOP_RATED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TRENDING;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_UPCOMING;

public class TMDBQueryProcess extends AsyncTask<Void, Void, String> {

    private OnEventListener<List<Movie>> mCallBack;
    public Exception mException;
    private int pageCount;
    private String type;
    private URL TMDBURL = null;

    public TMDBQueryProcess(OnEventListener callback, int pageCount, String type) {
        mCallBack = callback;
        this.pageCount = pageCount;
        this.type = type;
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

        setTMDBURL();
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
                if (type.equals(TYPE_TOP_250))
                    mCallBack.onSuccess(MovieUtil.parseMovies(s, "items"));
                else
                    mCallBack.onSuccess(MovieUtil.parseMovies(s, "results"));

            } catch (JSONException e) {
                e.printStackTrace();
                mCallBack.onFailure(mException);
            }
        } else {
            mCallBack.onFailure(mException);
        }
    }

    private void setTMDBURL() {
        if (type.equals(TYPE_TRENDING))
            TMDBURL = NetworkUtils.buildTrendingUrl(pageCount);
        else if (type.equals(TYPE_POPULAR))
            TMDBURL = NetworkUtils.buildPopularUrl(pageCount);
        else if (type.equals(TYPE_TOP_RATED))
            TMDBURL = NetworkUtils.buildTopratedUrl(pageCount);
        else if (type.equals(TYPE_UPCOMING))
            TMDBURL = NetworkUtils.buildUpcomingUrl(pageCount);
        else if (type.equals(TYPE_NOW_PLAYING))
            TMDBURL = NetworkUtils.buildNowPlayingUrl(pageCount);
        else if (type.equals(TYPE_TOP_250))
            TMDBURL = NetworkUtils.buildTop250Url(634);
    }
}
