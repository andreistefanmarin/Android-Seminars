package com.example.t1mockapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t1mockapp.R;
import com.example.t1mockapp.adapter.MovieAdapter;
import com.example.t1mockapp.model.Movie;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.IMovieListener {

    private FloatingActionButton fabInsert;
    private RecyclerView recyclerView;
    private final ArrayList<Movie> movies = new ArrayList<>();
    private ActivityResultLauncher<Intent> launcher;
    private MovieAdapter adapter; // Made this a class-level variable so we can refresh it

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addSampleMovies();
        initControls();

        // Setup the two-way radio for both Adding and Editing
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            Movie returnedMovie = (Movie) data.getSerializableExtra("movie_extra");
                            int position = data.getIntExtra("position_extra", -1);

                            if(returnedMovie != null) {
                                if (position == -1) {
                                    // It's a new movie
                                    movies.add(returnedMovie);
                                    Toast.makeText(MainActivity.this, "Added: " + returnedMovie.getTitle(), Toast.LENGTH_SHORT).show();
                                } else {
                                    // We are editing an existing movie
                                    movies.set(position, returnedMovie);
                                    Toast.makeText(MainActivity.this, "Updated: " + returnedMovie.getTitle(), Toast.LENGTH_SHORT).show();
                                }
                                // Tell the adapter the data changed so it redraws the list
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        fabInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemActivity.class);
                launcher.launch(intent);
            }
        });
    }

    private void initControls() {
        fabInsert = findViewById(R.id.fabInsert);
        recyclerView = findViewById(R.id.rvMovies);

        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        // Pass "this" because MainActivity is implementing the IMovieListener interface
        adapter = new MovieAdapter(movies, this);
        recyclerView.setAdapter(adapter);
    }

    private void addSampleMovies() {
        Movie m1 = new Movie();
        m1.setTitle("Inception");
        m1.setGenre("Sci-Fi");
        m1.setBudget(160000000);
        m1.setRating(4.5f);
        movies.add(m1);

        Movie m2 = new Movie();
        m2.setTitle("Toy Story");
        m2.setGenre("Animation");
        m2.setBudget(30000000);
        m2.setRating(2.5f); // Triggers the red background in Drill 2
        movies.add(m2);

        Movie m3 = new Movie();
        m3.setTitle("The Matrix");
        m3.setGenre("Action");
        m3.setBudget(63000000);
        m3.setRating(5.0f);
        movies.add(m3);
    }

    // This method catches the click from the Adapter and launches ItemActivity in Edit Mode
    @Override
    public void onMovieClick(Movie movie, int position) {
        Intent intent = new Intent(MainActivity.this, ItemActivity.class);
        intent.putExtra("movie_extra", movie);
        intent.putExtra("position_extra", position);
        launcher.launch(intent);
    }
}