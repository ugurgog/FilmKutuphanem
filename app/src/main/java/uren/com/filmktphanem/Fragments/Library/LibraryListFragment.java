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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Interfaces.OnLibraryEventCallback;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.ShapeUtil;
import uren.com.filmktphanem.data.FavoritesDbHelper;
import uren.com.filmktphanem.data.MyLibraryItem;

import static uren.com.filmktphanem.Constants.StringConstants.TYPE_ADDED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_DELETED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_UPDATED;

public class LibraryListFragment extends BaseFragment {

    View mView;

    public LibraryListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_library_list, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            addListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {

    }

    private void addListeners() {

    }
}