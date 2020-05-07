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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import uren.com.filmktphanem.Fragments.Library.ShowSelectedPhotoFragment;
import uren.com.filmktphanem.Fragments.Person.PersonFragment;
import uren.com.filmktphanem.Interfaces.OnLibraryEventCallback;
import uren.com.filmktphanem.Interfaces.ReturnCallback;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.ShapeUtil;
import uren.com.filmktphanem.Utils.dataModelUtil.MovieUtil;
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
    private Button btn_behind_scenes;
    private Button btn_view_similar;
    private int movieId;
    private Movie movie;
    private SQLiteDatabase mDatabase;
    private FavoritesDbHelper favoritesDbHelper;
    private MyLibraryItem myLibraryItem;
    private boolean isInLibrary = false;
    private boolean showAddButton;
    private OnLibraryEventCallback onLibraryEventCallback;

    public MovieDetailFragment(int movieId, boolean showAddButton, OnLibraryEventCallback onLibraryEventCallback) {
        this.movieId = movieId;
        this.showAddButton = showAddButton;
        this.onLibraryEventCallback = onLibraryEventCallback;
    }

    @Override
    public void onStart() {
        //getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.VISIBLE);
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

        ivMoviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movie != null && movie.getPosterLarge() != null &&
                        !movie.getPosterLarge().trim().isEmpty())
                    mFragmentNavigation.pushFragment(new ShowSelectedPhotoFragment(movie.getPosterLarge()));
            }
        });

        return mView;
    }

    private void setLibraryBtnText() {
        String txtBtnFavorites = null;

        if(showAddButton)
            btn_favorites.setVisibility(View.VISIBLE);
        else {
            btn_favorites.setVisibility(View.GONE);
            return;
        }

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

                mFragmentNavigation.pushFragment(new LibraryUpdateFragment(myLibraryItem, isInLibrary, false,
                        new OnLibraryEventCallback() {
                            @Override
                            public void onReturn(String value) {
                                onLibraryEventCallback.onReturn(value);
                            }
                        }));
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
        btn_behind_scenes = mView.findViewById(R.id.btn_behind_scenes);
        btn_view_similar = mView.findViewById(R.id.btn_view_similar);
        favoritesDbHelper = new FavoritesDbHelper(getContext());

        btn_view_similar.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.DimGray, null),
                getContext().getResources().getColor(R.color.White, null), GradientDrawable.RECTANGLE, 30, 2));
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

        if(movie.getBehindSceneUrl() == null || movie.getBehindSceneUrl().isEmpty())
            btn_behind_scenes.setVisibility(View.GONE);

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
            CastRecyclerViewAdapter castAdapter = new CastRecyclerViewAdapter(getContext(), movie.getCast(), new ReturnCallback() {
                @Override
                public void onReturn(Object object) {
                    int castId = (int) object;
                    mFragmentNavigation.pushFragment(new PersonFragment(castId));
                }
            });
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

        // Movie Behind secenes
        final String behindSecenesUrl = movie.getBehindSceneUrl();
        if (behindSecenesUrl != null) {
            btn_behind_scenes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(behindSecenesUrl));
                    startActivity(i);
                }
            });
        }

        btn_view_similar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentNavigation.pushFragment(new SimilarMoviesFragment(movie));
            }
        });
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
                    movie = MovieUtil.parseMovieData(s);
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