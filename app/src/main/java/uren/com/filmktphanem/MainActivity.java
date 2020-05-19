package uren.com.filmktphanem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import uren.com.filmktphanem.FragmentControllers.FragNavController;
import uren.com.filmktphanem.FragmentControllers.FragNavTransactionOptions;
import uren.com.filmktphanem.FragmentControllers.FragmentHistory;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Fragments.Library.LibraryFragment;
import uren.com.filmktphanem.Fragments.Library.LibraryListFragment;
import uren.com.filmktphanem.Fragments.Movies.MovieDetailFragment;
import uren.com.filmktphanem.Fragments.Movies.MoviesFragment;
import uren.com.filmktphanem.Fragments.Search.SearchFragment;
import uren.com.filmktphanem.Utils.AdMobUtils;

import static uren.com.filmktphanem.Constants.StringConstants.ANIMATE_DOWN_TO_UP;
import static uren.com.filmktphanem.Constants.StringConstants.ANIMATE_LEFT_TO_RIGHT;
import static uren.com.filmktphanem.Constants.StringConstants.ANIMATE_RIGHT_TO_LEFT;
import static uren.com.filmktphanem.Constants.StringConstants.ANIMATE_UP_TO_DOWN;
import static uren.com.filmktphanem.FragmentControllers.FragNavController.TAB1;
import static uren.com.filmktphanem.FragmentControllers.FragNavController.TAB2;
import static uren.com.filmktphanem.FragmentControllers.FragNavController.TAB3;


public class MainActivity extends FragmentActivity implements
        BaseFragment.FragmentNavigation,
        FragNavController.TransactionListener,
        FragNavController.RootFragmentListener {

    public FrameLayout contentFrame;
    public LinearLayout profilePageMainLayout;
    public TabLayout bottomTabLayout;
    public LinearLayout tabMainLayout;

    public String ANIMATION_TAG;

    public FragNavTransactionOptions transactionOptions;

    public String[] TABS;

    private FragNavController mNavController;

    private FragmentHistory fragmentHistory;

    private AdView adView;

    private LinearLayout linearLayoutOne ;
    private LinearLayout linearLayout2 ;
    private LinearLayout linearLayout3;

    private ImageView imgv1;
    private ImageView imgv2;
    private ImageView imgv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fabric.with(this, new Crashlytics());

        initValues();

        fragmentHistory = new FragmentHistory();

        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.content_frame)
                .transactionListener(this)
                .rootFragmentListener(this, TABS.length)
                .build();

        switchTab(0);
        updateTabSelection(0);

        bottomTabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabSelectionControl(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mNavController.clearStack();
                tabSelectionControl(tab);
            }
        });

    }

    public void clearStackGivenIndex(int index) {
        mNavController.clearStackWithGivenIndex(index);
    }


    public void tabSelectionControl(TabLayout.Tab tab) {
        fragmentHistory.push(tab.getPosition());
        switchAndUpdateTabSelection(tab.getPosition());
    }

    private void initValues() {
        ButterKnife.bind(this);
        bottomTabLayout = findViewById(R.id.bottom_tab_layout);
        profilePageMainLayout = findViewById(R.id.profilePageMainLayout);
        contentFrame = findViewById(R.id.content_frame);
        tabMainLayout = findViewById(R.id.tabMainLayout);
        TABS = getResources().getStringArray(R.array.tab_name);
        adView = findViewById(R.id.adView);
        initTab();
        MobileAds.initialize(MainActivity.this, getResources().getString(R.string.ADMOB_APP_ID));
        AdMobUtils.loadBannerAd(adView);
    }


    private void initTab() {
        if (bottomTabLayout != null) {
            for (int i = 0; i < TABS.length; i++) {
                bottomTabLayout.addTab(bottomTabLayout.newTab());
                //TabLayout.Tab tab = bottomTabLayout.getTabAt(i);
            }
        }

        //@SuppressLint("InflateParams") View headerView = ((LayoutInflater) Objects.requireNonNull(getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
        //        .inflate(R.layout.custom_tab, null, false);

        View headerView = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_tab, null);

        linearLayoutOne = headerView.findViewById(R.id.ll);
        linearLayout2 = headerView.findViewById(R.id.ll2);
        linearLayout3 = headerView.findViewById(R.id.ll3);

        imgv1 = headerView.findViewById(R.id.imgv1);
        imgv2 = headerView.findViewById(R.id.imgv2);
        imgv3 = headerView.findViewById(R.id.imgv3);

        Objects.requireNonNull(bottomTabLayout.getTabAt(0)).setCustomView(linearLayoutOne);
        Objects.requireNonNull(bottomTabLayout.getTabAt(1)).setCustomView(linearLayout2);
        Objects.requireNonNull(bottomTabLayout.getTabAt(2)).setCustomView(linearLayout3);
    }

    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void switchTab(int position) {
        mNavController.switchTab(position);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (!mNavController.isRootFragment()) {
            setTransactionOption();
            mNavController.popFragment(transactionOptions);
        } else {

            if (fragmentHistory.isEmpty()) {
                super.onBackPressed();
            } else {

                if (fragmentHistory.getStackSize() > 1) {

                    int position = fragmentHistory.popPrevious();
                    switchAndUpdateTabSelection(position);
                } else {
                    switchAndUpdateTabSelection(0);
                    fragmentHistory.emptyStack();
                }
            }
        }
    }

    public void switchAndUpdateTabSelection(int position) {
        switchTab(position);
        updateTabSelection(position);
    }

    private void setTransactionOption() {
        if (transactionOptions == null) {
            transactionOptions = FragNavTransactionOptions.newBuilder().build();
        }

        if (ANIMATION_TAG != null) {
            switch (ANIMATION_TAG) {
                case ANIMATE_RIGHT_TO_LEFT:
                    transactionOptions.enterAnimation = R.anim.slide_from_right;
                    transactionOptions.exitAnimation = R.anim.slide_to_left;
                    transactionOptions.popEnterAnimation = R.anim.slide_from_left;
                    transactionOptions.popExitAnimation = R.anim.slide_to_right;
                    break;
                case ANIMATE_LEFT_TO_RIGHT:
                    transactionOptions.enterAnimation = R.anim.slide_from_left;
                    transactionOptions.exitAnimation = R.anim.slide_to_right;
                    transactionOptions.popEnterAnimation = R.anim.slide_from_right;
                    transactionOptions.popExitAnimation = R.anim.slide_to_left;
                    break;
                case ANIMATE_DOWN_TO_UP:
                    transactionOptions.enterAnimation = R.anim.slide_from_down;
                    transactionOptions.exitAnimation = R.anim.slide_to_up;
                    transactionOptions.popEnterAnimation = R.anim.slide_from_up;
                    transactionOptions.popExitAnimation = R.anim.slide_to_down;
                    break;
                case ANIMATE_UP_TO_DOWN:
                    transactionOptions.enterAnimation = R.anim.slide_from_up;
                    transactionOptions.exitAnimation = R.anim.slide_to_down;
                    transactionOptions.popEnterAnimation = R.anim.slide_from_down;
                    transactionOptions.popExitAnimation = R.anim.slide_to_up;
                    break;
                default:
                    transactionOptions = null;
            }
        } else
            transactionOptions = null;
    }

    public void updateTabSelection(int currentTab) {

        for (int i = 0; i < TABS.length; i++) {
            TabLayout.Tab selectedTab = bottomTabLayout.getTabAt(i);
            GradientDrawable drawable = (GradientDrawable)selectedTab.getCustomView().getBackground();

            if (currentTab != i)
                drawable.setColor(getResources().getColor(R.color.DarkGray));
            else {
                selectedTab.getCustomView().startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.tab_anim));

                switch (i){
                    case TAB1:
                        drawable.setColor(getResources().getColor(R.color.gplus_color_2));
                        imgv1.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.tab_rotate_anim));
                        break;
                    case TAB2:
                        drawable.setColor(getResources().getColor(R.color.gplus_color_3));
                        imgv2.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.tab_rotate_anim));
                        break;
                    case TAB3:
                        drawable.setColor(getResources().getColor(R.color.gplus_color_4));
                        imgv3.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.tab_rotate_anim));
                        break;
                    default:
                        drawable.setColor(getResources().getColor(R.color.DarkGray)); break;
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public void pushFragment(Fragment fragment) {
        if (mNavController != null) {
            mNavController.pushFragment(fragment);
        }
    }

    @Override
    public void pushFragment(Fragment fragment, String animationTag) {

        ANIMATION_TAG = animationTag;
        setTransactionOption();

        if (mNavController != null) {
            mNavController.pushFragment(fragment, transactionOptions);
        }
    }

    @Override
    public void onTabTransaction(Fragment fragment, int index) {

    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {

            case TAB1:
                return new MoviesFragment();
            case TAB2:
                return new SearchFragment();
            case TAB3:
                return new LibraryListFragment();

        }
        throw new IllegalStateException("Need to send an index that we know");
    }

    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType
            transactionType) {
    }
}