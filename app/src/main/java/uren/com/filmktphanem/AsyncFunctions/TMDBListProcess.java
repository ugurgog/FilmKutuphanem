package uren.com.filmktphanem.AsyncFunctions;


import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uren.com.filmktphanem.Interfaces.OnEventListener;
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

        //setTMDBURL();
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
                parseMovies(s);

            } catch (JSONException e) {
                e.printStackTrace();
                mCallBack.onFailure(mException);
            }
        } else {
            mCallBack.onFailure(mException);
        }
    }

    /*private void setTMDBURL(){
        if(type.equals(TYPE_TRENDING))
            TMDBURL = NetworkUtils.buildTrendingUrl(pageCount);
        else if(type.equals(TYPE_POPULAR))
            TMDBURL = NetworkUtils.buildPopularUrl(pageCount);
        else if(type.equals(TYPE_TOP_RATED))
            TMDBURL = NetworkUtils.buildTopratedUrl(pageCount);
        else if(type.equals(TYPE_UPCOMING))
            TMDBURL = NetworkUtils.buildUpcomingUrl(pageCount);
        else if(type.equals(TYPE_NOW_PLAYING))
            TMDBURL = NetworkUtils.buildNowPlayingUrl(pageCount);
    }*/

    private void parseMovies(String moviesJSONString) throws JSONException {
        JSONObject resultJSONObject = new JSONObject(moviesJSONString);
        JSONArray moviesJSONArray = resultJSONObject.getJSONArray("results");
        List<Movie> movies = new ArrayList<>();

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
        mCallBack.onSuccess(movies);
    }
}
