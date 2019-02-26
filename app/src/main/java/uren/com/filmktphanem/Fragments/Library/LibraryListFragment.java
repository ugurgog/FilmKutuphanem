package uren.com.filmktphanem.Fragments.Library;


import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Interfaces.OnLibraryEventCallback;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.ShapeUtil;
import uren.com.filmktphanem.data.FavoritesDbHelper;
import uren.com.filmktphanem.data.MyLibraryItem;

import static uren.com.filmktphanem.Constants.StringConstants.LIB_ITEM_ALL;
import static uren.com.filmktphanem.Constants.StringConstants.LIB_ITEM_FAVORITES;
import static uren.com.filmktphanem.Constants.StringConstants.LIB_ITEM_WATCHED;
import static uren.com.filmktphanem.Constants.StringConstants.LIB_ITEM_WILL_WATCH;
import static uren.com.filmktphanem.Constants.StringConstants.ORDER_BY_ASC;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_ADDED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_DELETED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_UPDATED;

public class LibraryListFragment extends BaseFragment {

    View mView;

    @BindView(R.id.imgvFavorites)
    ImageView imgvFavorites;
    @BindView(R.id.imgvWatched)
    ImageView imgvWatched;
    @BindView(R.id.imgvWillWatch)
    ImageView imgvWillWatch;
    @BindView(R.id.imgvAll)
    ImageView imgvAll;

    @BindView(R.id.tvFavorites)
    TextView tvFavorites;
    @BindView(R.id.tvWatched)
    TextView tvWatched;
    @BindView(R.id.tvWillWatch)
    TextView tvWillWatch;
    @BindView(R.id.tvAll)
    TextView tvAll;

    @BindView(R.id.tvfavtot)
    TextView tvfavtot;
    @BindView(R.id.tvwatchedtot)
    TextView tvwatchedtot;
    @BindView(R.id.tvwillwatchtot)
    TextView tvwillwatchtot;
    @BindView(R.id.tvalltot)
    TextView tvalltot;

    @BindView(R.id.llFavorites)
    LinearLayout llFavorites;
    @BindView(R.id.llWatched)
    LinearLayout llWatched;
    @BindView(R.id.llWillWatch)
    LinearLayout llWillWatch;
    @BindView(R.id.llAll)
    LinearLayout llAll;

    private List<MyLibraryItem> favoritesList;
    private List<MyLibraryItem> watchedList;
    private List<MyLibraryItem> willWatchedList;
    private List<MyLibraryItem> allList;

    private FavoritesDbHelper dbHelper;

    public LibraryListFragment() {

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
        mView = inflater.inflate(R.layout.fragment_library_list, container, false);
        ButterKnife.bind(this, mView);
        initVariables();
        getLibraryItems();
        setShapes();
        addListeners();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        dbHelper = new FavoritesDbHelper(getContext());
        favoritesList = new ArrayList<>();
        watchedList = new ArrayList<>();
        willWatchedList = new ArrayList<>();
        allList = new ArrayList<>();
    }

    private void getLibraryItems() {
        favoritesList = dbHelper.getFavoritesItems(1, ORDER_BY_ASC);
        watchedList = dbHelper.getWatchedLibraryItems(1, ORDER_BY_ASC);
        willWatchedList = dbHelper.getWillWatchLibraryItems(1, ORDER_BY_ASC);
        allList = dbHelper.getAllLibraryItems();
        setImages();
        setTotalCounts();
    }

    private void setTotalCounts() {
        tvfavtot.setText(getResources().getString(R.string.total_fil_count) + " " + favoritesList.size());
        tvwatchedtot.setText(getResources().getString(R.string.total_fil_count) + " " + watchedList.size());
        tvwillwatchtot.setText(getResources().getString(R.string.total_fil_count) + " " + willWatchedList.size());
        tvalltot.setText(getResources().getString(R.string.total_fil_count) + " " + allList.size());
    }

    private void setImages() {
        //Favorites
        if (favoritesList != null && favoritesList.size() > 0) {
            Glide.with(getContext())
                    .load(favoritesList.get(0).getPosterLarge())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgvFavorites);

            if ((favoritesList.size() - 1) == 0)
                tvFavorites.setText(favoritesList.get(0).getName());
            else
                tvFavorites.setText(favoritesList.get(0).getName() + " + " +
                        String.valueOf((favoritesList.size() - 1)) + " " + getResources().getString(R.string.others));
        } else {
            tvFavorites.setText(getResources().getString(R.string.have_no_favorite_movies));
            Glide.with(getContext())
                    .load(R.drawable.icon_no_movie)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgvFavorites);
        }

        //Watched
        if (watchedList != null && watchedList.size() > 0) {
            Glide.with(getContext())
                    .load(watchedList.get(0).getPosterLarge())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgvWatched);

            if ((watchedList.size() - 1) == 0)
                tvWatched.setText(watchedList.get(0).getName());
            else
                tvWatched.setText(watchedList.get(0).getName() + " + " +
                        String.valueOf((watchedList.size() - 1)) + " " + getResources().getString(R.string.others));
        } else {
            tvWatched.setText(getResources().getString(R.string.have_no_watched_movies));
            Glide.with(getContext())
                    .load(R.drawable.icon_no_movie)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgvWatched);
        }

        //Will Watched
        if (willWatchedList != null && willWatchedList.size() > 0) {
            Glide.with(getContext())
                    .load(willWatchedList.get(0).getPosterLarge())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgvWillWatch);

            if ((willWatchedList.size() - 1) == 0)
                tvWillWatch.setText(willWatchedList.get(0).getName());
            else
                tvWillWatch.setText(willWatchedList.get(0).getName() + " + " +
                        String.valueOf((willWatchedList.size() - 1)) + " " + getResources().getString(R.string.others));
        } else {
            tvWillWatch.setText(getResources().getString(R.string.have_no_will_watched_movies));
            Glide.with(getContext())
                    .load(R.drawable.icon_no_movie)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgvWillWatch);
        }

        //All
        if (allList != null && allList.size() > 0) {
            Glide.with(getContext())
                    .load(allList.get(0).getPosterLarge())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgvAll);

            if ((allList.size() - 1) == 0)
                tvAll.setText(allList.get(0).getName());
            else
                tvAll.setText(allList.get(0).getName() + " + " +
                        String.valueOf((allList.size() - 1)) + " " + getResources().getString(R.string.others));
        } else {
            tvAll.setText(getResources().getString(R.string.have_no_all_movies));
            Glide.with(getContext())
                    .load(R.drawable.icon_no_movie)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgvAll);
        }
    }

    private void setShapes() {
        imgvFavorites.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.Black),
                getContext().getResources().getColor(R.color.White), GradientDrawable.OVAL, 50, 2));
        imgvWatched.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.Black),
                getContext().getResources().getColor(R.color.White), GradientDrawable.OVAL, 50, 2));
        imgvWillWatch.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.Black),
                getContext().getResources().getColor(R.color.White), GradientDrawable.OVAL, 50, 2));
        imgvAll.setBackground(ShapeUtil.getShape(getContext().getResources().getColor(R.color.Black),
                getContext().getResources().getColor(R.color.White), GradientDrawable.OVAL, 50, 2));
    }

    private void addListeners() {
        llFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentNavigation.pushFragment(new LibraryFragment(LIB_ITEM_FAVORITES));
            }
        });

        llWatched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentNavigation.pushFragment(new LibraryFragment(LIB_ITEM_WATCHED));
            }
        });

        llWillWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentNavigation.pushFragment(new LibraryFragment(LIB_ITEM_WILL_WATCH));
            }
        });

        llAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentNavigation.pushFragment(new LibraryAllFragment());
            }
        });
    }


}