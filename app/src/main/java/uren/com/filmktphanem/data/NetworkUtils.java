/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uren.com.filmktphanem.data;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    private final static String TMDB_TRENDING_BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private final static String TMDB_POPULER_BASE_URL = "https://api.themoviedb.org/3/movie/popular";
    private final static String TMDB_TOP_RATED_BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";
    private final static String TMDB_UPCOMING_BASE_URL = "https://api.themoviedb.org/3/movie/upcoming";
    private final static String TMDB_NOW_PLAYING_BASE_URL = "https://api.themoviedb.org/3/movie/now_playing";
    private final static String TMDB_GENRE_LIST_BASE_URL = "https://api.themoviedb.org/3/genre/movie/list";
    private final static String TMDB_GENRE_LIST_DETAIL_BASE_URL = "https://api.themoviedb.org/3/discover/movie";

    private final static String TMDB_SEARCH_BASE_URL = "https://api.themoviedb.org/3/search/movie";
    private final static String TMDB_DETAIL_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final static String PARAM_QUERY = "query";
    private final static String PARAM_API_KEY = "api_key";
    private final static String PARAM_PAGE = "page";
    //private final static String VALUE_API_KEY = "977da42f84c289b566542292c3343bc6";
    private final static String VALUE_API_KEY = "3e815e3ab893b6c7da4a48fce51fe168";

    private final static String PARAM_APPEND_TO_RESPONSE = "append_to_response";
    private final static String VALUE_APPEND_TO_RESPONSE = "videos,casts";
    private final static String PARAM_LANGUAGE = "language";
    private final static String PARAM_WITH_GENRES = "with_genres";

    public static URL buildSearchUrl(String TMDBSearchQuery, int page) {
        Uri builtUri = Uri.parse(TMDB_SEARCH_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, TMDBSearchQuery)
                .appendQueryParameter(PARAM_API_KEY, VALUE_API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, getLanguage())
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildTrendingUrl(int page) {
        Uri builtUri = Uri.parse(TMDB_TRENDING_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, VALUE_API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, getLanguage())
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildPopularUrl(int page) {
        Uri builtUri = Uri.parse(TMDB_POPULER_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, VALUE_API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, getLanguage())
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildTopratedUrl(int page) {
        Uri builtUri = Uri.parse(TMDB_TOP_RATED_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, VALUE_API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, getLanguage())
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUpcomingUrl(int page) {
        Uri builtUri = Uri.parse(TMDB_UPCOMING_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, VALUE_API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, getLanguage())
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildNowPlayingUrl(int page) {
        Uri builtUri = Uri.parse(TMDB_NOW_PLAYING_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, VALUE_API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, getLanguage())
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildGenreListUrl() {
        Uri builtUri = Uri.parse(TMDB_GENRE_LIST_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, VALUE_API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, getLanguage())
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildGenreListDetailUrl(int listId, int page) {
        Uri builtUri = Uri.parse(TMDB_GENRE_LIST_DETAIL_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, VALUE_API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, getLanguage())
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .appendQueryParameter(PARAM_WITH_GENRES, String.valueOf(listId))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildMovieDetailUrl(int movieId) {
        String detailUrl = TMDB_DETAIL_BASE_URL + movieId;
        Uri detailUri = Uri.parse(detailUrl).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, VALUE_API_KEY)
                .appendQueryParameter(PARAM_APPEND_TO_RESPONSE, VALUE_APPEND_TO_RESPONSE)
                .appendQueryParameter(PARAM_LANGUAGE, getLanguage())
                .build();
        URL url = null;
        try {
            url = new URL(detailUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    private static String getLanguage() {
        String language = Locale.getDefault().getLanguage();
        if (language.equals("nl")) {
            return "nl";
        } else if (language.equals("de")) {
            return "de";
        } else if (language.equals("tr")) {
            return "tr";
        }
        return "en";
    }
}