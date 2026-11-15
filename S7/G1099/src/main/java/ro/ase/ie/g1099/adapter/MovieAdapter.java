package ro.ase.ie.g1099.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;

import ro.ase.ie.g1099.R;
import ro.ase.ie.g1099.model.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    public interface IMovieListener
    {
        void onMovieClick(Movie movie, int position);
        void onMovieLongClick(int position);
    }

    final ArrayList<Movie> movies;
    final IMovieListener listener;

    public MovieAdapter(ArrayList<Movie> movies, IMovieListener listener) {
        this.movies = movies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        holder.title.setText(movie.getTitle());
        holder.director.setText(movie.getDirector());

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy", java.util.Locale.getDefault());
        String year = movie.getRelease() != null ? sdf.format(movie.getRelease()) : "N/A";
        holder.subtitle.setText(movie.getGenre() + " · " + year);
        double budget = movie.getBudget();
        if(budget>100000000) {
            holder.facts.setTextColor(Color.RED);
        } else if (budget < 10000000) {
            holder.facts.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.facts.setTextColor(Color.parseColor("#9E9E9E"));
        }

        String formattedBudget = String.format(java.util.Locale.US, "$%,.0f", movie.getBudget());
        holder.facts.setText(movie.getDuration() + " min  ·  " + formattedBudget);

        holder.rating.setRating(movie.getRating());
        holder.ratingValue.setText(String.valueOf(movie.getRating()));

        holder.chipApproval.setText(movie.getApproval());
        holder.chipWatched.setVisibility(movie.isWatched() ? View.VISIBLE : View.GONE);
        holder.chipWatchList.setVisibility(movie.isOnWatchlist() ? View.VISIBLE : View.GONE);
        holder.chipBlockbuster.setVisibility(movie.getBudget() > 100000000 ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        final ImageView poster;
        final TextView director;
        final TextView title;
        final TextView subtitle;
        final RatingBar rating;
        final TextView ratingValue;
        final TextView facts;
        final Chip chipApproval;
        final Chip chipWatched;
        final Chip chipWatchList;
        final Chip chipBlockbuster;


        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.moviePoster);
            title = itemView.findViewById(R.id.movieTitle);
            subtitle = itemView.findViewById(R.id.movieSubtitle);
            rating = itemView.findViewById(R.id.movieRating);
            ratingValue = itemView.findViewById(R.id.movieRatingValue);
            facts = itemView.findViewById(R.id.movieFacts);
            chipApproval = itemView.findViewById(R.id.chipApproval);
            chipWatched= itemView.findViewById(R.id.chipWatched);
            chipWatchList = itemView.findViewById(R.id.chipWatchlist);
            chipBlockbuster = itemView.findViewById(R.id.chipBlockbuster);
            director = itemView.findViewById(R.id.directorName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAbsoluteAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        Movie movie = movies.get(pos);
                        listener.onMovieClick(movie,pos);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAbsoluteAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onMovieLongClick(pos);
                    }
                    return true;
                }
            });
        }
    }
}

// add a button, change x, switch the linear layout
// 2 requests, 30-40 min
