package uren.com.filmktphanem.Fragments.Library;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uren.com.filmktphanem.AsyncFunctions.TMDBGenreListProcess;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Fragments.Movies.Adapters.GenreListAdapter;
import uren.com.filmktphanem.Fragments.Movies.Models.Genre;
import uren.com.filmktphanem.Fragments.Movies.MovieDetailFragment;
import uren.com.filmktphanem.Interfaces.OnEventListener;
import uren.com.filmktphanem.Interfaces.OnLibraryEventCallback;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.AdMobUtils;
import uren.com.filmktphanem.Utils.ShapeUtil;
import uren.com.filmktphanem.data.FavoritesDbHelper;
import uren.com.filmktphanem.data.MyLibraryItem;

import static uren.com.filmktphanem.Constants.StringConstants.TYPE_ADDED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_DELETED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_UPDATED;

@SuppressLint("ValidFragment")
public class LibraryUpdateFragment extends BaseFragment {

    View mView;

    @BindView(R.id.header_backdrop)
    ImageView header_backdrop;
    @BindView(R.id.iv_movie_poster)
    ImageView iv_movie_poster;
    @BindView(R.id.tv_movie_title)
    TextView tv_movie_title;
    @BindView(R.id.btn_favorites)
    Button btn_favorites;
    @BindView(R.id.btn_watched)
    Button btn_watched;
    @BindView(R.id.btn_will_watch)
    Button btn_will_watch;
    @BindView(R.id.edtComment)
    EditText edtComment;
    @BindView(R.id.btnAdd)
    Button btnAdd;
    @BindView(R.id.btnRemove)
    Button btnRemove;
    @BindView(R.id.scaleRatingBar)
    ScaleRatingBar scaleRatingBar;
    @BindView(R.id.btnViewDetail)
    Button btnViewDetail;
    @BindView(R.id.adView)
    AdView adView;

    private MyLibraryItem myLibraryItem;
    private boolean isInLibrary;
    private FavoritesDbHelper dbHelper;
    private OnLibraryEventCallback onLibraryEventCallback;
    private boolean showViewDetail;

    private int inFavorites = 0;
    private int watched = 0;
    private int willWatched = 0;
    private float rateValue = 0.0f;

    public LibraryUpdateFragment(MyLibraryItem myLibraryItem, boolean isInLibrary, boolean showViewDetail, OnLibraryEventCallback onLibraryEventCallback) {
        this.myLibraryItem = myLibraryItem;
        this.isInLibrary = isInLibrary;
        this.onLibraryEventCallback = onLibraryEventCallback;
        this.showViewDetail = showViewDetail;
    }

    @Override
    public void onStart() {
        getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_library_update, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            checkMovieInLibrary();
            addListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        dbHelper = new FavoritesDbHelper(getContext());
        MobileAds.initialize(getContext(), getResources().getString(R.string.ADMOB_APP_ID));
        AdMobUtils.loadBannerAd(adView);
        AdMobUtils.loadInterstitialAd(getContext());
    }

    private void checkMovieInLibrary() {
        setMovieTitle();
        setImages();
        startAnimations();
        setButtonsInitialValues();
        setButtonShapes();
    }

    private void setButtonsInitialValues() {

        if (showViewDetail) {
            btnViewDetail.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.DimGray),
                    getContext().getResources().getColor(R.color.White), GradientDrawable.RECTANGLE, 80, 2));
            btnViewDetail.setVisibility(View.VISIBLE);
        }else {
            btnViewDetail.setVisibility(View.GONE);
        }

        if (isInLibrary) {
            btnAdd.setText(getResources().getString(R.string.update));

            if (myLibraryItem != null) {
                inFavorites = myLibraryItem.getInFavorites();
                watched = myLibraryItem.getWatched();
                willWatched = myLibraryItem.getWillWatch();

                rateValue = myLibraryItem.getMyRate();
                scaleRatingBar.setRating(myLibraryItem.getMyRate());

                if (inFavorites == 1)
                    btn_favorites.setText(getResources().getString(R.string.remove_from_favorites));
                else
                    btn_favorites.setText(getResources().getString(R.string.add_to_favorites));

                if (watched == 1) {
                    btn_watched.setText(getResources().getString(R.string.remove_from_watched));
                    btn_will_watch.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.DarkGray),
                            0, GradientDrawable.RECTANGLE, 30, 0));
                    btn_will_watch.setEnabled(false);
                } else {
                    btn_watched.setText(getResources().getString(R.string.add_to_watched));
                    btn_will_watch.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.MediumTurquoise),
                            0, GradientDrawable.RECTANGLE, 30, 0));
                    btn_will_watch.setEnabled(true);
                }

                if (willWatched == 1) {
                    btn_will_watch.setText(getResources().getString(R.string.remove_from_will_watched));
                    btn_watched.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.DarkGray),
                            0, GradientDrawable.RECTANGLE, 30, 0));
                    btn_watched.setEnabled(false);
                } else {
                    btn_will_watch.setText(getResources().getString(R.string.add_to_will_watched));
                    btn_watched.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.DodgerBlue),
                            0, GradientDrawable.RECTANGLE, 30, 0));
                    btn_watched.setEnabled(true);
                }

                if (myLibraryItem.getMyComment() != null)
                    edtComment.setText(myLibraryItem.getMyComment());
            }
        } else {
            btnAdd.setText(getResources().getString(R.string.add));
            btn_favorites.setText(getResources().getString(R.string.add_to_favorites));
            btn_watched.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.DodgerBlue),
                    0, GradientDrawable.RECTANGLE, 30, 0));
            btn_will_watch.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.MediumTurquoise),
                    0, GradientDrawable.RECTANGLE, 30, 0));
        }
    }

    private void setMovieTitle() {
        if (myLibraryItem != null && myLibraryItem.getName() != null &&
                !myLibraryItem.getName().isEmpty())
            tv_movie_title.setText(myLibraryItem.getName());
        else
            tv_movie_title.setVisibility(View.GONE);
    }

    private void setImages() {
        if (myLibraryItem != null) {

            if (myLibraryItem.getBackDropLarge() != null && !myLibraryItem.getBackDropLarge().isEmpty())
                Glide.with(getContext())
                        .load(myLibraryItem.getBackDropLarge())
                        .apply(RequestOptions.centerInsideTransform())
                        .into(header_backdrop);
            else
                Glide.with(getContext())
                        .load(R.drawable.backdrop_fallback)
                        .apply(RequestOptions.centerInsideTransform())
                        .into(header_backdrop);

            if (myLibraryItem.getPosterLarge() != null && !myLibraryItem.getPosterLarge().isEmpty()) {
                Glide.with(getContext())
                        .load(myLibraryItem.getPosterLarge())
                        .apply(RequestOptions.circleCropTransform())
                        .into(iv_movie_poster);
                iv_movie_poster.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.Black),
                        0, GradientDrawable.OVAL, 50, 0));
            }
        }
    }

    private void startAnimations() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        Animation moveUpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.move_up);
        iv_movie_poster.startAnimation(moveUpAnimation);
        header_backdrop.startAnimation(fadeInAnimation);
    }

    private void setButtonShapes() {
        btn_favorites.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.library_btn_color),
                0, GradientDrawable.RECTANGLE, 30, 0));
        btnAdd.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.DodgerBlue),
                0, GradientDrawable.RECTANGLE, 40, 0));
        btnRemove.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.library_btn_color),
                0, GradientDrawable.RECTANGLE, 40, 0));
    }

    private void fillLibraryItem() {
        myLibraryItem.setWillWatch(willWatched);
        myLibraryItem.setWatched(watched);
        myLibraryItem.setInFavorites(inFavorites);
        myLibraryItem.setMyRate(rateValue);

        if (edtComment.getText() != null)
            myLibraryItem.setMyComment(edtComment.getText().toString());
        else
            myLibraryItem.setMyComment("");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListeners() {

        iv_movie_poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myLibraryItem != null && myLibraryItem.getPosterLarge() != null &&
                        !myLibraryItem.getPosterLarge().trim().isEmpty())
                    mFragmentNavigation.pushFragment(new ShowSelectedPhotoFragment(myLibraryItem.getPosterLarge()));
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInLibrary) {
                    fillLibraryItem();
                    dbHelper.updateLibraryItem(myLibraryItem);
                    onLibraryEventCallback.onReturn(TYPE_UPDATED);
                    getActivity().onBackPressed();
                } else {
                    fillLibraryItem();
                    dbHelper.addToMyLibrary(myLibraryItem);
                    onLibraryEventCallback.onReturn(TYPE_ADDED);
                    getActivity().onBackPressed();
                }

            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInLibrary) {
                    dbHelper.deleteLibraryItem(myLibraryItem);
                    onLibraryEventCallback.onReturn(TYPE_DELETED);
                    getActivity().onBackPressed();
                } else
                    Toast.makeText(getContext(), getResources().getString(R.string.movie_not_in_library), Toast.LENGTH_SHORT);
            }
        });

        btn_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inFavorites == 0) {
                    inFavorites = 1;
                    btn_favorites.setText(getResources().getString(R.string.remove_from_favorites));
                } else {
                    inFavorites = 0;
                    btn_favorites.setText(getResources().getString(R.string.add_to_favorites));
                }
            }
        });

        btn_watched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (watched == 0) {
                    watched = 1;
                    btn_watched.setText(getResources().getString(R.string.remove_from_watched));
                    btn_will_watch.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.DarkGray),
                            0, GradientDrawable.RECTANGLE, 30, 0));
                    btn_will_watch.setEnabled(false);
                } else {
                    watched = 0;
                    btn_watched.setText(getResources().getString(R.string.add_to_watched));
                    btn_will_watch.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.MediumTurquoise),
                            0, GradientDrawable.RECTANGLE, 30, 0));
                    btn_will_watch.setEnabled(true);
                }
            }
        });

        btn_will_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (willWatched == 0) {
                    willWatched = 1;
                    btn_will_watch.setText(getResources().getString(R.string.remove_from_will_watched));
                    btn_watched.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.DarkGray),
                            0, GradientDrawable.RECTANGLE, 30, 0));
                    btn_watched.setEnabled(false);
                } else {
                    willWatched = 0;
                    btn_will_watch.setText(getResources().getString(R.string.add_to_will_watched));
                    btn_watched.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.DodgerBlue),
                            0, GradientDrawable.RECTANGLE, 30, 0));
                    btn_watched.setEnabled(true);
                }
            }
        });

        scaleRatingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating) {
                rateValue = rating;
                Log.d("xxxx", "ScaleRatingBar onRatingChange: " + rating);
            }
        });

        btnViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myLibraryItem != null)
                    mFragmentNavigation.pushFragment(new MovieDetailFragment(myLibraryItem.getMovieId(), false));
            }
        });

    }
}