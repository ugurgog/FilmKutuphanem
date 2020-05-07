package uren.com.filmktphanem.Fragments.Library;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Fragments.Library.Adapters.LibraryAdapter;
import uren.com.filmktphanem.Fragments.Search.SearchResultsFragment;
import uren.com.filmktphanem.Interfaces.ReturnCallback;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.AdMobUtils;
import uren.com.filmktphanem.adapters.MovieRecyclerViewAdapter;
import uren.com.filmktphanem.data.FavoritesContract;
import uren.com.filmktphanem.data.FavoritesDbHelper;
import uren.com.filmktphanem.data.MyLibraryItem;
import uren.com.filmktphanem.models.Movie;

import static uren.com.filmktphanem.Constants.StringConstants.LIB_ITEM_FAVORITES;
import static uren.com.filmktphanem.Constants.StringConstants.LIB_ITEM_WATCHED;
import static uren.com.filmktphanem.Constants.StringConstants.LIB_ITEM_WILL_WATCH;
import static uren.com.filmktphanem.Constants.StringConstants.ORDER_BY_ASC;
import static uren.com.filmktphanem.Constants.StringConstants.ORDER_BY_DESC;

@SuppressLint("ValidFragment")
public class LibraryFragment extends BaseFragment {

    View mView;

    @BindView(R.id.searchEdittext)
    EditText searchEdittext;
    @BindView(R.id.searchCancelImgv)
    ImageView searchCancelImgv;
    @BindView(R.id.searchResultTv)
    TextView searchResultTv;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvMovieType)
    TextView tvMovieType;
    @BindView(R.id.imgvSettings)
    ImageView imgvSettings;

    private String type;
    private FavoritesDbHelper dbHelper;

    private List<MyLibraryItem> myLibraryItemList;
    private LibraryAdapter libraryAdapter;
    private String orderByValue = ORDER_BY_DESC;

    public LibraryFragment(String type) {
        this.type = type;
    }

    @Override
    public void onStart() {
        //getActivity().findViewById(R.id.tabMainLayout).setVisibility(View.GONE);
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_library, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            setTitle();
            getLibraryList();
            addListeners();
        }
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        dbHelper = new FavoritesDbHelper(getContext());
        myLibraryItemList = new ArrayList<>();
    }

    private void setTitle() {
        switch (type) {
            case LIB_ITEM_FAVORITES:
                tvMovieType.setText(getResources().getString(R.string.favorite_movies));
                break;
            case LIB_ITEM_WATCHED:
                tvMovieType.setText(getResources().getString(R.string.watched_movies));
                break;
            case LIB_ITEM_WILL_WATCH:
                tvMovieType.setText(getResources().getString(R.string.will_watched_movies));
                break;
        }
    }

    private void getLibraryList() {
        switch (type) {
            case LIB_ITEM_FAVORITES:
                myLibraryItemList = dbHelper.getFavoritesItems(1, orderByValue);
                break;
            case LIB_ITEM_WATCHED:
                myLibraryItemList = dbHelper.getWatchedLibraryItems(1, orderByValue);
                break;
            case LIB_ITEM_WILL_WATCH:
                myLibraryItemList = dbHelper.getWillWatchLibraryItems(1, orderByValue);
                break;
        }

        libraryAdapter = new LibraryAdapter(getContext(), myLibraryItemList, mFragmentNavigation);
        recyclerView.setAdapter(libraryAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void addListeners() {
        searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.toString() != null) {
                    if (!s.toString().trim().isEmpty()) {
                        searchCancelImgv.setVisibility(View.VISIBLE);
                    } else {
                        searchCancelImgv.setVisibility(View.GONE);
                    }

                    if (libraryAdapter != null)
                        libraryAdapter.updateAdapter(s.toString(), new ReturnCallback() {
                            @Override
                            public void onReturn(Object object) {
                                int itemSize = (int) object;

                                if (itemSize == 0)
                                    searchResultTv.setVisibility(View.VISIBLE);
                                else
                                    searchResultTv.setVisibility(View.GONE);
                            }
                        });
                } else
                    searchCancelImgv.setVisibility(View.GONE);
            }
        });

        searchCancelImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdittext.setText("");
                searchCancelImgv.setVisibility(View.GONE);
            }
        });

        imgvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), imgvSettings);
                popupMenu.inflate(R.menu.menu_library_item);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.ascRate:
                                orderByValue = ORDER_BY_DESC;
                                getLibraryList();
                                break;
                            case R.id.descRate:
                                orderByValue = ORDER_BY_ASC;
                                getLibraryList();
                                break;
                            case R.id.basedName:
                                getLibraryList();
                                libraryAdapter.orderByName();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }


}