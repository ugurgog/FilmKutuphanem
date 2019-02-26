package uren.com.filmktphanem.Fragments.Library.Adapters;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.List;

import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Fragments.Library.LibraryUpdateFragment;
import uren.com.filmktphanem.Interfaces.OnLibraryEventCallback;
import uren.com.filmktphanem.Interfaces.ReturnCallback;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.ShapeUtil;
import uren.com.filmktphanem.data.MyLibraryItem;

import static uren.com.filmktphanem.Constants.StringConstants.TYPE_DELETED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_UPDATED;

public class LibraryAllAdapter extends RecyclerView.Adapter<LibraryAllAdapter.LibraryHolder> implements Filterable {

    private Context context;
    private List<MyLibraryItem> myLibraryItemList;
    private List<MyLibraryItem> orgLibraryItemList;
    private BaseFragment.FragmentNavigation mFragmentNavigation;
    private ReturnCallback searchResultCallback;

    public LibraryAllAdapter(Context context, List<MyLibraryItem> myLibraryItemList, BaseFragment.FragmentNavigation mFragmentNavigation) {
        this.context = context;
        this.myLibraryItemList = myLibraryItemList;
        this.mFragmentNavigation = mFragmentNavigation;
        this.orgLibraryItemList = new ArrayList<>();
        orgLibraryItemList = myLibraryItemList;
    }

    @Override
    public LibraryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.library_all_item_layout, parent, false);
        return new LibraryHolder(itemView);
    }

    public class LibraryHolder extends RecyclerView.ViewHolder {

        int position;
        MyLibraryItem myLibraryItem;
        LinearLayout llMain;
        ImageView imgvPhoto;
        ScaleRatingBar scaleRatingBar;
        TextView tvMovieName;
        TextView tvCommentEx;
        TextView tvRate;
        Button btnFavorites;
        Button btnIzlendi;

        public LibraryHolder(View view) {
            super(view);

            llMain = view.findViewById(R.id.llMain);
            imgvPhoto = view.findViewById(R.id.imgvPhoto);
            scaleRatingBar = view.findViewById(R.id.scaleRatingBar);
            tvMovieName = view.findViewById(R.id.tvMovieName);
            tvCommentEx = view.findViewById(R.id.tvCommentEx);
            tvRate = view.findViewById(R.id.tvRate);
            btnFavorites = view.findViewById(R.id.btnFavorites);
            btnIzlendi = view.findViewById(R.id.btnIzlendi);

            llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragmentNavigation.pushFragment(new LibraryUpdateFragment(myLibraryItem, true,
                            new OnLibraryEventCallback() {
                                @Override
                                public void onReturn(String value) {
                                    if (value.equals(TYPE_DELETED)) {
                                        myLibraryItemList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, getItemCount());
                                    } else if (value.equals(TYPE_UPDATED)) {
                                        notifyItemChanged(position);
                                    }
                                }
                            }));
                }
            });
        }

        public void setData(MyLibraryItem myLibraryItem, int position) {
            this.myLibraryItem = myLibraryItem;
            this.position = position;
            setImage();
            setMovieName();
            setCommentArea();
            setRatingBar();
            setMyRateValue();
            setButtons();
        }

        private void setImage() {
            if (myLibraryItem != null && myLibraryItem.getPosterLarge() != null) {
                Glide.with(context)
                        .load(myLibraryItem.getPosterLarge())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imgvPhoto);
            }
            imgvPhoto.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.Black),
                    context.getResources().getColor(R.color.White), GradientDrawable.OVAL, 50, 2));
        }

        private void setMovieName() {
            if (myLibraryItem != null && myLibraryItem.getName() != null) {
                if (myLibraryItem.getName().length() > 40)
                    tvMovieName.setText(myLibraryItem.getName().substring(0, 40) + "...");
                else
                    tvMovieName.setText(myLibraryItem.getName());
            }
        }

        private void setCommentArea() {
            if (myLibraryItem != null && myLibraryItem.getMyComment() != null &&
                    !myLibraryItem.getMyComment().isEmpty()) {
                tvCommentEx.setText(context.getResources().getString(R.string.comment_exist));
                tvCommentEx.setTextColor(context.getResources().getColor(R.color.LightGreen));
            } else {
                tvCommentEx.setText(context.getResources().getString(R.string.comment_not_exist));
                tvCommentEx.setTextColor(context.getResources().getColor(R.color.Red));
            }
        }

        private void setRatingBar() {
            if (myLibraryItem != null) {
                scaleRatingBar.setRating(myLibraryItem.getMyRate());
            }
        }

        private void setMyRateValue() {
            if (myLibraryItem != null) {
                tvRate.setText(Float.toString(scaleRatingBar.getRating()));
            }
        }

        private void setButtons(){
            if (myLibraryItem != null){
                btnFavorites.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.Red),
                        0, GradientDrawable.RECTANGLE, 30, 0));

                if(myLibraryItem.getInFavorites() == 1)
                    btnFavorites.setVisibility(View.VISIBLE);
                else
                    btnFavorites.setVisibility(View.GONE);

                if(myLibraryItem.getWatched() == 1) {
                    btnIzlendi.setVisibility(View.VISIBLE);
                    btnIzlendi.setText(context.getResources().getString(R.string.in_watched));
                    btnIzlendi.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.DodgerBlue),
                            0, GradientDrawable.RECTANGLE, 30, 0));
                }else {
                    if(myLibraryItem.getWillWatch() == 1){
                        btnIzlendi.setVisibility(View.VISIBLE);
                        btnIzlendi.setText(context.getResources().getString(R.string.in_will_watched));
                        btnIzlendi.setBackground(ShapeUtil.getShape(context.getResources().getColor(R.color.MediumTurquoise),
                                0, GradientDrawable.RECTANGLE, 30, 0));
                    }else {
                        btnIzlendi.setVisibility(View.GONE);
                    }

                }
            }
        }
    }

    @Override
    public void onBindViewHolder(final LibraryHolder holder, final int position) {
        MyLibraryItem myLibraryItem = myLibraryItemList.get(position);
        holder.setData(myLibraryItem, position);
    }

    public void updateAdapter(String searchText, ReturnCallback searchResultCallback) {
        this.searchResultCallback = searchResultCallback;
        getFilter().filter(searchText);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchString = charSequence.toString();
                if (searchString.trim().isEmpty())
                    myLibraryItemList = orgLibraryItemList;
                else {
                    List<MyLibraryItem> tempList = new ArrayList<>();

                    for (MyLibraryItem myLibraryItem: orgLibraryItemList) {
                        if (myLibraryItem.getName().toLowerCase().contains(searchString.toLowerCase()))
                            tempList.add(myLibraryItem);
                    }
                    myLibraryItemList = tempList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = myLibraryItemList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                myLibraryItemList = (ArrayList<MyLibraryItem>) filterResults.values;
                notifyDataSetChanged();

                if (myLibraryItemList != null && myLibraryItemList.size() > 0)
                    searchResultCallback.onReturn(myLibraryItemList.size());
                else
                    searchResultCallback.onReturn(0);
            }
        };
    }

    @Override
    public int getItemCount() {

        if (myLibraryItemList != null && myLibraryItemList.size() > 0)
            return myLibraryItemList.size();
        else
            return 0;
    }
}