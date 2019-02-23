package uren.com.filmktphanem.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Fragments.Trending.MovieDetailFragment;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.models.Movie;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private List<Movie> movies;
    private BaseFragment.FragmentNavigation mFragmentNavigation;

    public static final int VIEW_PROG = 0;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_NULL = 2;

    public MovieRecyclerViewAdapter(Context context, List<Movie> movies, BaseFragment.FragmentNavigation mFragmentNavigation) {
        this.context = context;
        this.movies = movies;
        this.mFragmentNavigation = mFragmentNavigation;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.cardview_item_movie, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieRecyclerViewAdapter.MyViewHolder holder, final int position) {

        Picasso.get().load(movies.get(position).getPosterSmall()).into(holder.ivMoviePoster);

        // Set click listener
        holder.cvMovieItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFragmentNavigation.pushFragment(new MovieDetailFragment(movies.get(position).getMovieId()));

                /*Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra("MovieId", movies.get(position).getMovieId());
                context.startActivity(intent);*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cvMovieItem;
        ImageView ivMoviePoster;

        public MyViewHolder(View itemView) {
            super(itemView);
            // Binds the current view item's components
            cvMovieItem = itemView.findViewById(R.id.cv_movie_item);
            cvMovieItem.setBackgroundColor(Color.parseColor("#000000"));
            ivMoviePoster = itemView.findViewById(R.id.iv_movie_poster);

            // Set animation on imageview
            Animation fadeInAnimation = AnimationUtils.loadAnimation(cvMovieItem.getContext(), R.anim.fade_in);
            ivMoviePoster.startAnimation(fadeInAnimation); //Set animation to your ImageView
        }
    }
}
