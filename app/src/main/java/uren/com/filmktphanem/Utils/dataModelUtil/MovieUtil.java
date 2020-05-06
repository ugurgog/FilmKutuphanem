package uren.com.filmktphanem.Utils.dataModelUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uren.com.filmktphanem.models.Cast;
import uren.com.filmktphanem.models.Crew;
import uren.com.filmktphanem.models.Movie;

public class MovieUtil {

    public static List<Movie> parseMovies(String moviesJSONString, String name) throws JSONException {
        JSONObject resultJSONObject = new JSONObject(moviesJSONString);
        JSONArray moviesJSONArray = resultJSONObject.getJSONArray(name);
        List<Movie> movies = new ArrayList<>();

        // Loop throught the JSON array results
        for (int i = 0; i < moviesJSONArray.length(); i++) {
            JSONObject movieJSONObject = new JSONObject(moviesJSONArray.get(i).toString());
            if (!movieJSONObject.isNull("poster_path")) {
                String posterPath = movieJSONObject.getString("poster_path");
                // Add new movie object to the movie array

                Movie movie = new Movie(movieJSONObject.getInt("id"),
                        posterPath,movieJSONObject.getString("original_title"),movieJSONObject.getString("title"));
                movies.add(movie);
            }
        }
        return movies;
    }

    public static Movie parseMovieData(String movieJSONString) throws JSONException {
        JSONObject movieJSONObject = new JSONObject(movieJSONString);

        int movieId = movieJSONObject.getInt("id");
        String movieTitle = movieJSONObject.getString("title");

        String moviePosterPath = null;
        if (!movieJSONObject.isNull("poster_path")) {
            moviePosterPath = movieJSONObject.getString("poster_path");
        }

        String movieBackDropPath = null;
        if (!movieJSONObject.isNull("backdrop_path")) {
            movieBackDropPath = movieJSONObject.getString("backdrop_path");
        }

        Integer movieRunTime = null;
        if (!movieJSONObject.isNull("runtime")) {
            movieRunTime = movieJSONObject.getInt("runtime");
        }

        double movieRating = movieJSONObject.getDouble("vote_average");

        String movieOverview = null;
        if (!movieJSONObject.isNull("overview") && movieJSONObject.getString("overview").trim().length() > 0) {
            movieOverview = movieJSONObject.getString("overview");
        }

        String movieReleaseDate = null;
        if (!movieJSONObject.isNull("release_date")) {
            movieReleaseDate = movieJSONObject.getString("release_date");
        }
        // Get movie categories
        ArrayList<String> movieGenres = new ArrayList<String>();
        JSONArray movieGenresJSONArray = movieJSONObject.getJSONArray("genres");
        for (int i = 0; i < movieGenresJSONArray.length(); i++) {
            JSONObject movieGenreJSONObject = movieGenresJSONArray.getJSONObject(i);
            movieGenres.add(movieGenreJSONObject.getString("name"));
        }

        // Get production companies
        ArrayList<String> movieProductionCompanies = new ArrayList<String>();
        JSONArray movieProductionCompaniesJSONArray = movieJSONObject.getJSONArray("production_companies");
        for (int i = 0; i < movieProductionCompaniesJSONArray.length(); i++) {
            JSONObject movieProductionCompanyJSONObject = movieProductionCompaniesJSONArray.getJSONObject(i);
            movieProductionCompanies.add(movieProductionCompanyJSONObject.getString("name"));
        }

        JSONObject castsJSONObject = movieJSONObject.getJSONObject("casts");

        // Get cast
        ArrayList<Cast> cast = new ArrayList<Cast>();
        JSONArray castJSONArray = castsJSONObject.getJSONArray("cast");
        for (int i = 0; i < castJSONArray.length(); i++) {
            JSONObject castJSONObject = castJSONArray.getJSONObject(i);
            cast.add(new Cast(castJSONObject.getString("name"),
                    castJSONObject.getString("character"),
                    castJSONObject.getString("profile_path"),
                    castJSONObject.getInt("cast_id"),
                    castJSONObject.getString("credit_id"),
                    castJSONObject.getInt("id")));
        }

        // Get crew
        ArrayList<Crew> crew = new ArrayList<Crew>();
        JSONArray crewJSONArray = castsJSONObject.getJSONArray("crew");
        for (int i = 0; i < crewJSONArray.length(); i++) {
            JSONObject crewJSONObject = crewJSONArray.getJSONObject(i);
            crew.add(new Crew(crewJSONObject.getString("name"),
                    crewJSONObject.getString("job"),
                    crewJSONObject.getString("profile_path"),
                    crewJSONObject.getString("credit_id"),
                    crewJSONObject.getInt("id")));
        }

        // Get first trailer
        String trailerId = null;
        String behindSceneId = null;

        JSONObject videoJSONObject = movieJSONObject.getJSONObject("videos");
        JSONArray videoJSONArray = videoJSONObject.getJSONArray("results");
        for (int i = 0; i < videoJSONArray.length(); i++) {
            if (videoJSONArray.getJSONObject(i).getString("type").equals("Trailer")) {
                trailerId = videoJSONArray.getJSONObject(i).getString("key");
                continue;
            }

            if (videoJSONArray.getJSONObject(i).getString("type").equals("Behind the Scenes")) {
                behindSceneId = videoJSONArray.getJSONObject(i).getString("key");
                continue;
            }

            if(trailerId != null && behindSceneId != null)
                break;
        }

        // Initialize movie
        Movie movie = new Movie(movieId, movieTitle, moviePosterPath,
                movieBackDropPath, movieRunTime, movieRating, movieOverview,
                movieReleaseDate, movieGenres, movieProductionCompanies, cast, crew, trailerId, behindSceneId);

        return movie;
    }
}
