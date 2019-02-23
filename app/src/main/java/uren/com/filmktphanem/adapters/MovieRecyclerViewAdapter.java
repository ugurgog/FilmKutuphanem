package uren.com.filmktphanem.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Fragments.Movies.MovieDetailFragment;
import uren.com.filmktphanem.Interfaces.ReturnCallback;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.models.Movie;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter implements Filterable {

    private Context context;
    private List<Movie> movieList;
    private List<Movie> orgMovieList;
    private BaseFragment.FragmentNavigation mFragmentNavigation;
    View view;
    private ReturnCallback searchResultCallback;

    public static final int VIEW_PROG = 0;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_NULL = 2;

    public MovieRecyclerViewAdapter(Context context,BaseFragment.FragmentNavigation mFragmentNavigation) {
        this.context = context;
        this.movieList = new ArrayList<Movie>();
        this.orgMovieList = new ArrayList<Movie>();
        this.mFragmentNavigation = mFragmentNavigation;
    }

    @Override
    public int getItemViewType(int position) {
        if (movieList.size() > 0 && position >= 0) {
            return movieList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        } else {
            return VIEW_NULL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_item_movie, parent, false);

            viewHolder = new MovieRecyclerViewAdapter.MyViewHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            viewHolder = new MovieRecyclerViewAdapter.ProgressViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyViewHolder) {
            Movie movie = movieList.get(position);
            ((MyViewHolder) holder).setData(movie, position);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return ((movieList != null) ? movieList.size() : 0);
    }

    public void updateAdapterWithPosition(int position) {
        notifyItemChanged(position);
    }

    public void addAll(List<Movie> addedMovieList) {
        if (addedMovieList != null) {
            movieList.addAll(addedMovieList);
            orgMovieList.addAll(addedMovieList);
            notifyItemRangeInserted(movieList.size(), movieList.size() + addedMovieList.size());
        }
    }

    public void addProgressLoading() {
        if (getItemViewType(movieList.size() - 1) != VIEW_PROG) {
            movieList.add(null);
            notifyItemInserted(movieList.size() - 1);
        }
    }

    public void removeProgressLoading() {
        if (getItemViewType(movieList.size() - 1) == VIEW_PROG) {
            movieList.remove(movieList.size() - 1);
            notifyItemRemoved(movieList.size());
        }
    }

    public boolean isShowingProgressLoading() {
        if (getItemViewType(movieList.size() - 1) == VIEW_PROG)
            return true;
        else
            return false;
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBarLoading);
        }
    }

    public long getItemId(int position) {
        return position;
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
                FilterResults filterResults = new FilterResults();

                String searchString = charSequence.toString();

                if (searchString.trim().isEmpty()) {
                    movieList.clear();
                    movieList.addAll(orgMovieList);
                } else {
                    List<Movie> tempMovieList = new ArrayList<>();

                    for (Movie movie : orgMovieList) {
                        if (movie.getTitle() != null &&
                                movie.getTitle().toLowerCase().contains(searchString.toLowerCase()))
                            tempMovieList.add(movie);
                    }
                    movieList.clear();
                    movieList.addAll(tempMovieList);
                }

                filterResults.values = movieList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                movieList = (ArrayList<Movie>) filterResults.values;
                notifyDataSetChanged();

                if (movieList != null && movieList.size() > 0)
                    searchResultCallback.onReturn(movieList.size());
                else
                    searchResultCallback.onReturn(0);
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cvMovieItem;
        ImageView ivMoviePoster;
        Movie movie;
        int position;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            cvMovieItem = view.findViewById(R.id.cv_movie_item);
            cvMovieItem.setBackgroundColor(Color.parseColor("#000000"));
            ivMoviePoster = view.findViewById(R.id.iv_movie_poster);

            // Set animation on imageview
            Animation fadeInAnimation = AnimationUtils.loadAnimation(cvMovieItem.getContext(), R.anim.fade_in);
            ivMoviePoster.startAnimation(fadeInAnimation); //Set animation to your ImageView

            cvMovieItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFragmentNavigation.pushFragment(new MovieDetailFragment(movieList.get(position).getMovieId()));
                }
            });
        }

        public void setData(Movie movie, int position) {
            this.movie = movie;
            this.position = position;
            Glide.with(context)
                    .load(movie.getPosterSmall())
                    .apply(RequestOptions.fitCenterTransform())
                    .into(ivMoviePoster);
        }
    }
}
