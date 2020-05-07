package uren.com.filmktphanem.Fragments.Library;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import uren.com.filmktphanem.Fragments.Library.Adapters.LibraryAllAdapter;
import uren.com.filmktphanem.Interfaces.ReturnCallback;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.AdMobUtils;
import uren.com.filmktphanem.data.FavoritesContract;
import uren.com.filmktphanem.data.FavoritesDbHelper;
import uren.com.filmktphanem.data.MyLibraryItem;

import static uren.com.filmktphanem.Constants.StringConstants.LIB_ITEM_FAVORITES;
import static uren.com.filmktphanem.Constants.StringConstants.LIB_ITEM_WATCHED;
import static uren.com.filmktphanem.Constants.StringConstants.LIB_ITEM_WILL_WATCH;
import static uren.com.filmktphanem.Constants.StringConstants.ORDER_BY_ASC;
import static uren.com.filmktphanem.Constants.StringConstants.ORDER_BY_DESC;
import static uren.com.filmktphanem.data.FavoritesContract.FavoritesEntry.COLUMN_IN_FAVORITES;

@SuppressLint("ValidFragment")
public class LibraryAllFragment extends BaseFragment {

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

    private FavoritesDbHelper dbHelper;

    private List<MyLibraryItem> myLibraryItemList;
    private LibraryAllAdapter libraryAllAdapter;
    private String orderByValue = ORDER_BY_DESC;
    private String orderByStr = FavoritesContract.FavoritesEntry.COLUMN_MY_RATE;

    public LibraryAllFragment() {

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
        tvMovieType.setText(getResources().getString(R.string.all_movies));
    }

    private void getLibraryList() {
        myLibraryItemList = dbHelper.getAllItemsByValue(orderByStr, orderByValue);
        libraryAllAdapter = new LibraryAllAdapter(getContext(), myLibraryItemList, mFragmentNavigation);
        recyclerView.setAdapter(libraryAllAdapter);
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

                    if (libraryAllAdapter != null)
                        libraryAllAdapter.updateAdapter(s.toString(), new ReturnCallback() {
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
                popupMenu.inflate(R.menu.menu_library_all_item);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.ascRate:
                                orderByStr = FavoritesContract.FavoritesEntry.COLUMN_MY_RATE;
                                orderByValue = ORDER_BY_DESC;
                                getLibraryList();
                                break;
                            case R.id.descRate:
                                orderByStr = FavoritesContract.FavoritesEntry.COLUMN_MY_RATE;
                                orderByValue = ORDER_BY_ASC;
                                getLibraryList();
                                break;

                            case R.id.favoritesFirst:
                                orderByStr = FavoritesContract.FavoritesEntry.COLUMN_IN_FAVORITES;
                                orderByValue = ORDER_BY_DESC;
                                getLibraryList();
                                break;

                            case R.id.watchedFirst:
                                orderByStr = FavoritesContract.FavoritesEntry.COLUMN_WATCHED;
                                orderByValue = ORDER_BY_DESC;
                                getLibraryList();
                                break;

                            case R.id.willWatchFirst:
                                orderByStr = FavoritesContract.FavoritesEntry.COLUMN_WILL_WATCH;
                                orderByValue = ORDER_BY_DESC;
                                getLibraryList();
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