package com.example.t1mockapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t1mockapp.R;
import com.example.t1mockapp.model.Movie;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    // 1. Define the communication channel
    public interface IMovieListener {
        void onMovieClick(Movie movie, int position);
    }

    private final ArrayList<Movie> movies;
    private final IMovieListener listener;

    // 2. Require the listener when the adapter is created
    public MovieAdapter(ArrayList<Movie> movies, IMovieListener listener) {
        this.movies = movies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getGenre());

        // Background color logic (Drill 2)
        if(movie.getRating() < 3.0) {
            holder.rowContainer.setBackgroundColor(android.graphics.Color.parseColor("#FFCDD2")); // Light red
        } else {
            holder.rowContainer.setBackgroundColor(android.graphics.Color.TRANSPARENT); // Default
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    // The robot hands for a single row
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView genre;
        public android.widget.LinearLayout rowContainer;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvRowTitle);
            genre = itemView.findViewById(R.id.tvRowGenre);
            rowContainer = itemView.findViewById(R.id.rowContainer);

            // 3. Listen for the user tapping this specific row
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAbsoluteAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        // Send the data back to MainActivity
                        listener.onMovieClick(movies.get(pos), pos);
                    }
                }
            });
        }
    }
}