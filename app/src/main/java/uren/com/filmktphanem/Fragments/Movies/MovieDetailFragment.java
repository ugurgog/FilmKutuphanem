package uren.com.filmktphanem.Fragments.Movies;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.ButterKnife;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Fragments.Library.LibraryUpdateFragment;
import uren.com.filmktphanem.Interfaces.OnLibraryEventCallback;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.ShapeUtil;
import uren.com.filmktphanem.adapters.CastRecyclerViewAdapter;
import uren.com.filmktphanem.adapters.CrewRecyclerViewAdapter;
import uren.com.filmktphanem.data.FavoritesContract;
import uren.com.filmktphanem.data.FavoritesDbHelper;
import uren.com.filmktphanem.data.MyLibraryItem;
import uren.com.filmktphanem.data.NetworkUtils;
import uren.com.filmktphanem.models.Cast;
import uren.com.filmktphanem.models.Crew;
import uren.com.filmktphanem.models.Movie;

import static uren.com.filmktphanem.Constants.StringConstants.TYPE_ADDED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_DELETED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_UPCOMING;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_UPDATED;

@SuppressLint("ValidFragment")
public class MovieDetailFragment extends BaseFragment {

    View mView;

    private TextView tvMovieTitle;
    private ImageView ivMoviePoster;
    private TextView tvMovieGenres;
    private TextView tvMovieProductionCompanies;
    private TextView tvMovieRunTime;
    private TextView tvMovieOverview;
    private TextView tvMovieReleaseDate;
    private TextView tvCrew;
    private TextView tvCast;
    private TextView tvErrorMessage;
    private Button btn_favorites;
    private ProgressBar pbLoadingIndicator;
    private LinearLayout llMovieInfoHolder;
    private RatingBar rbRatingBar;
    private RecyclerView rvCast;
    private RecyclerView rvCrew;
    private Button btnTrailer;
    private int movieId;
    private Movie movie;
    private SQLiteDatabase mDatabase;
    private FavoritesDbHelper favoritesDbHelper;
    private MyLibraryItem myLibraryItem;
    private boolean isInLibrary = false;

    public MovieDetailFragment(int movieId) {
        this.movieId = movieId;
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

        mView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, mView);
        initVariables();

        makeTMDBMovieDetailQuery(movieId);

        // Set ratingbar color
        Drawable progress = rbRatingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.parseColor("#b9090b"));

        // Create a database helper
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(getContext());
        mDatabase = dbHelper.getWritableDatabase();

        setAddLibraryBtn();

        return mView;
    }

    private void setLibraryBtnText(){
        String txtBtnFavorites = null;

        if (favoritesDbHelper.isInMyLibrary(movieId)) {
            isInLibrary = true;
            txtBtnFavorites = getResources().getString(R.string.update_in_library);
            btn_favorites.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.DodgerBlue),
                    0, GradientDrawable.RECTANGLE, 30, 0));
        } else {
            isInLibrary = false;
            txtBtnFavorites = getResources().getString(R.string.add_my_library);
            btn_favorites.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.library_btn_color),
                    0, GradientDrawable.RECTANGLE, 30, 0));
        }

        btn_favorites.setText(txtBtnFavorites);
    }

    private void setAddLibraryBtn() {
        setLibraryBtnText();

        btn_favorites.setOnClickListener(new View.OnClickListener() {
            int duration = Toast.LENGTH_LONG;

            public void onClick(View v) {

                if (favoritesDbHelper.isInMyLibrary(movieId)) {
                    myLibraryItem = favoritesDbHelper.getLibraryItem(movieId);
                } else {
                    myLibraryItem = new MyLibraryItem(movie.getMovieId(), movie.getTitle(), movie.getPosterPath(),
                            0, 0, 0, null, 0,
                            movie.getPosterSmall(), movie.getPosterLarge(), movie.getBackDropLarge());
                }

                mFragmentNavigation.pushFragment(new LibraryUpdateFragment(myLibraryItem, isInLibrary,
                        new OnLibraryEventCallback() {
                            @Override
                            public void onReturn(String value) {
                                if(value.equals(TYPE_ADDED))
                                    Toast.makeText(getContext(), getResources().getString(R.string.movie_added), Toast.LENGTH_SHORT);
                                else if(value.equals(TYPE_DELETED))
                                    Toast.makeText(getContext(), getResources().getString(R.string.movie_deleted), Toast.LENGTH_SHORT);
                                else if(value.equals(TYPE_UPDATED))
                                    Toast.makeText(getContext(), getResources().getString(R.string.movie_updated), Toast.LENGTH_SHORT);
                            }
                        }));

                /*if (isInFavorites() && removeFromFavorites()) {
                    Toast toast = Toast.makeText(getContext(),getResources().getString(R.string.favorites_removed_1)+" '" + movie.getTitle() + "' " + getResources().getString(R.string.favorites_removed_2), duration);
                    toast.show();
                    btn_favorites.setText(getResources().getString(R.string.add_to_favorites));
                } else if (addToFavorites()) {
                    Toast toast = Toast.makeText(getContext(), getResources().getString(R.string.favorites_added_1)+" '" + movie.getTitle() + "' "+getResources().getString(R.string.favorites_added_2), duration);
                    toast.show();
                    btn_favorites.setText(getResources().getString(R.string.remove_from_favorites));
                }*/
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        tvMovieTitle = mView.findViewById(R.id.tv_movie_title);
        ivMoviePoster = mView.findViewById(R.id.iv_movie_poster);
        tvMovieGenres = mView.findViewById(R.id.tv_movie_genres);
        tvMovieProductionCompanies = mView.findViewById(R.id.tv_movie_production_companies);
        tvMovieRunTime = mView.findViewById(R.id.tv_movie_runtime);
        tvMovieReleaseDate = mView.findViewById(R.id.tv_movie_release_date);
        tvMovieOverview = mView.findViewById(R.id.tv_movie_overview);
        tvErrorMessage = mView.findViewById(R.id.tv_error_message);
        pbLoadingIndicator = mView.findViewById(R.id.pb_loading_indicator);
        llMovieInfoHolder = mView.findViewById(R.id.ll_movie_info_holder);
        rbRatingBar = mView.findViewById(R.id.rb_movie_rating);
        btn_favorites = mView.findViewById(R.id.btn_favorites);
        rvCast = mView.findViewById(R.id.rv_cast);
        rvCrew = mView.findViewById(R.id.rv_crew);
        tvCrew = mView.findViewById(R.id.tv_crew);
        tvCast = mView.findViewById(R.id.tv_cast);
        btnTrailer = mView.findViewById(R.id.btn_trailer);
        favoritesDbHelper = new FavoritesDbHelper(getContext());
    }

    /**
     * Inits a new movie detail query.
     *
     * @param movieId
     */
    private void makeTMDBMovieDetailQuery(int movieId) {
        URL TMDBMovieDetailURL = NetworkUtils.buildMovieDetailUrl(movieId);
        new MovieDetailFragment.TMDBQueryTask().execute(TMDBMovieDetailURL);
        // new MovieDetailActivity.TMDBQueryTask().execute(TMDBMovieDetailURL);
    }

    /**
     * Shows movie details.
     */
    private void showMovieDetails() {
        tvErrorMessage.setVisibility(View.GONE);
        llMovieInfoHolder.setVisibility(View.VISIBLE);
    }

    /**
     * Shows error message.
     */
    private void showErrorMessage() {
        llMovieInfoHolder.setVisibility(View.GONE);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Parse the json string data to a valid Movie object.
     *
     * @param movieJSONString
     */
    private void parseMovieData(String movieJSONString) throws JSONException {
        // Get JSON values
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
            cast.add(new Cast(castJSONObject.getString("name"), castJSONObject.getString("character"), castJSONObject.getString("profile_path")));
        }

        // Get crew
        ArrayList<Crew> crew = new ArrayList<Crew>();
        JSONArray crewJSONArray = castsJSONObject.getJSONArray("crew");
        for (int i = 0; i < crewJSONArray.length(); i++) {
            JSONObject crewJSONObject = crewJSONArray.getJSONObject(i);
            crew.add(new Crew(crewJSONObject.getString("name"), crewJSONObject.getString("job"), crewJSONObject.getString("profile_path")));
        }

        // Get first trailer
        String trailerId = null;
        JSONObject videoJSONObject = movieJSONObject.getJSONObject("videos");
        JSONArray videoJSONArray = videoJSONObject.getJSONArray("results");
        for (int i = 0; i < videoJSONArray.length(); i++) {
            if (videoJSONArray.getJSONObject(i).getString("type").equals("Trailer")) {
                trailerId = videoJSONArray.getJSONObject(i).getString("key");
                break;
            }
        }

        // Initialize movie
        movie = new Movie(movieId, movieTitle, moviePosterPath, movieBackDropPath, movieRunTime, movieRating, movieOverview, movieReleaseDate, movieGenres, movieProductionCompanies, cast, crew, trailerId);
    }

    /**
     * Binds the movie data from the Movie model to the activity's components.
     */
    private void bindMovieData() {
        // Set activity components
        ImageView headerBackdrop = mView.findViewById(R.id.header_backdrop);
        if (movie.getBackDropLarge() != null) {
            Picasso.get().load(movie.getBackDropLarge()).into(headerBackdrop);
        } else {
            Picasso.get().load(R.drawable.backdrop_fallback).into(headerBackdrop);
        }

        //setTitle(movie.getTitle());

        // Set text values
        tvMovieTitle.setText(movie.getTitle());
        if (movie.getRunTime() != null) {
            tvMovieRunTime.setText(String.valueOf(movie.getRunTime()) + " " + getResources().getString(R.string.minutes));
        } else {
            tvMovieRunTime.setText(R.string.unknown);
        }

        if (movie.getOverview() != null) {
            tvMovieOverview.setText(movie.getOverview());
        } else {
            tvMovieOverview.setText(R.string.no_description);
        }

        if (movie.getReleaseDate() != null) {
            tvMovieReleaseDate.setText(movie.getReleaseDate());
        } else {
            tvMovieReleaseDate.setText(R.string.unknown);
        }

        Glide.with(getContext())
                .load(movie.getPosterLarge())
                .apply(RequestOptions.circleCropTransform())
                .into(ivMoviePoster);

        ivMoviePoster.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.Black),
                0, GradientDrawable.OVAL, 50, 0));

        // Animations
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        Animation moveUpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.move_up);
        ivMoviePoster.startAnimation(moveUpAnimation); //Set animation to your ImageView
        headerBackdrop.startAnimation(fadeInAnimation);

        // Set rating
        rbRatingBar.setRating((float) (movie.getRating() / 2));

        // Append genres
        ArrayList<String> genres = movie.getGenres();
        tvMovieGenres.setText(getResources().getString(R.string.genres) + ": ");
        if (genres.size() == 0) {
            tvMovieGenres.append(getResources().getString(R.string.unknown));
        } else {
            for (int i = 0; i < genres.size(); i++) {
                if (i != 0) {
                    tvMovieGenres.append(", ");
                }
                tvMovieGenres.append(genres.get(i));
            }
        }

        // Append production companies
        ArrayList<String> productionCompanies = movie.getProductionCompanies();
        tvMovieProductionCompanies.setText(getResources().getString(R.string.producers) + ": ");
        if (productionCompanies.size() == 0) {
            tvMovieProductionCompanies.append(getResources().getString(R.string.unknown));
        } else {
            for (int i = 0; i < productionCompanies.size(); i++) {
                if (i != 0) {
                    tvMovieProductionCompanies.append(", ");
                }
                tvMovieProductionCompanies.append(productionCompanies.get(i));
            }
        }

        // Casts
        if (movie.getCast().size() > 0) {
            CastRecyclerViewAdapter castAdapter = new CastRecyclerViewAdapter(getContext(), movie.getCast());
            LinearLayoutManager castLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvCast.setLayoutManager(castLayoutManager);
            rvCast.setAdapter(castAdapter);
        } else {
            tvCast.setVisibility(View.GONE);
            rvCast.setVisibility(View.GONE);
        }

        // Crew
        if (movie.getCrew().size() > 0) {
            CrewRecyclerViewAdapter crewAdapter = new CrewRecyclerViewAdapter(getContext(), movie.getCrew());
            LinearLayoutManager crewLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvCrew.setLayoutManager(crewLayoutManager);
            rvCrew.setAdapter(crewAdapter);
        } else {
            tvCrew.setVisibility(View.GONE);
            rvCrew.setVisibility(View.GONE);
        }

        // Movie trailer
        final String trailerUrl = movie.getTrailerUrl();
        if (trailerUrl != null) {
            btnTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(trailerUrl));
                    startActivity(i);
                }
            });
        } else {
            btnTrailer.setText(R.string.no_trailer);
            btnTrailer.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_disabled));
        }

    }

    private Boolean isInFavorites() {
        Cursor mCursor = mDatabase.rawQuery(
                "SELECT * FROM " + FavoritesContract.FavoritesEntry.TABLE_NAME +
                        " WHERE " + FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + "= " + movieId
                , null);

        return mCursor.moveToFirst();
    }

    private Boolean addToFavorites() {
        ContentValues values = new ContentValues();
        // add values to record keys
        values.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID, movieId);
        values.put(FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        return mDatabase.insert(FavoritesContract.FavoritesEntry.TABLE_NAME, null, values) > 0;
    }

    private Boolean removeFromFavorites() {
        return mDatabase.delete(FavoritesContract.FavoritesEntry.TABLE_NAME, FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + " = " + movieId, null) > 0;
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

        /**
         * Executes when the API call is finished.
         */
        @Override
        protected void onPostExecute(String s) {
            pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if (s != null && !s.equals("")) {
                showMovieDetails();
                try {
                    parseMovieData(s);
                    bindMovieData();

                } catch (JSONException e) {
                    e.printStackTrace();
                    showErrorMessage();
                }
            } else {
                showErrorMessage();
            }
        }
    }

}