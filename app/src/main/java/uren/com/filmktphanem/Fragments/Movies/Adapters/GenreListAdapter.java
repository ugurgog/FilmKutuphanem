package uren.com.filmktphanem.Fragments.Movies.Adapters;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Fragments.Movies.GenreMoviesFragment;
import uren.com.filmktphanem.Fragments.Movies.Models.Genre;
import uren.com.filmktphanem.R;

public class GenreListAdapter extends RecyclerView.Adapter<GenreListAdapter.GenreListHolder> {

    private Context context;
    private List<Genre> genreList;
    private BaseFragment.FragmentNavigation mFragmentNavigation;

    public GenreListAdapter(Context context, List<Genre> genreList, BaseFragment.FragmentNavigation mFragmentNavigation) {
        this.context = context;
        this.genreList = genreList;
        this.mFragmentNavigation = mFragmentNavigation;
    }

    @Override
    public GenreListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_genre_item, parent, false);

        return new GenreListHolder(itemView);
    }

    public class GenreListHolder extends RecyclerView.ViewHolder {

        LinearLayout llGenreItem;
        TextView tvGenre;
        Genre genre;
        int position;

        public GenreListHolder(View view) {
            super(view);

            llGenreItem = view.findViewById(R.id.llGenreItem);
            tvGenre = view.findViewById(R.id.tvGenre);

            llGenreItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragmentNavigation.pushFragment(new GenreMoviesFragment(genre));
                }
            });
        }

        public void setData(Genre genre, int position) {
            this.genre = genre;
            this.position = position;
            setGenreName();
        }

        private void setGenreName(){
            if(genre != null && genre.getGenre() != null){
                tvGenre.setText(genre.getGenre());
            }
        }
    }

    @Override
    public void onBindViewHolder(final GenreListHolder holder, final int position) {
            Genre genre = genreList.get(position);
            holder.setData(genre, position);
    }

    @Override
    public int getItemCount() {

        if(genreList != null)
            return genreList.size();
        else
            return 0;
    }
}