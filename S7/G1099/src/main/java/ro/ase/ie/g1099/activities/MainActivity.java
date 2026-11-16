package ro.ase.ie.g1099.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ro.ase.ie.g1099.R;
import ro.ase.ie.g1099.adapter.MovieAdapter;
import ro.ase.ie.g1099.model.Movie;

public class MainActivity extends AppCompatActivity implements MovieAdapter.IMovieListener {

    protected static final String POSITION_EXTRA = "position";
    protected static final String MOVIE_EXTRA = "movie";
    private FloatingActionButton fabInsert;
    // private ListView listView;
    private FloatingActionButton fabStats;
    private RecyclerView recyclerView;
    private ActivityResultLauncher<Intent> launcher;
    private final ArrayList<Movie> movies = new ArrayList<>();
    //private ArrayAdapter<Movie> movieAdapter;
    private MovieAdapter movieAdapter;
    private Spinner spSort;
    private final ArrayList<Movie> originalMovies = new ArrayList<>();


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

        //movieAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, movies);
        movieAdapter = new MovieAdapter(movies, this);
        initControls();
        initListeners();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode() == RESULT_OK)
                        {
                            Intent intent = o.getData();
                            Movie movie = (Movie) intent.getSerializableExtra(MOVIE_EXTRA);
                            int position = intent.getIntExtra(POSITION_EXTRA, -1);
                            if(position == -1)
                            {
                                movies.add(movie);
                                originalMovies.add(movie);
                                movieAdapter.notifyDataSetChanged();
                            }
                            else
                            {
                                movies.set(position, movie);
                                movieAdapter.notifyDataSetChanged();
                            }
                        }
                        else if (o.getResultCode() == 100) {
                            Intent intent = o.getData();
                            if (intent != null) {
                                int position = intent.getIntExtra(POSITION_EXTRA, -1);
                                if (position != -1) {
                                    movies.remove(position); // Remove from underlying data
                                    movieAdapter.notifyItemRemoved(position); // Animate UI removal
                                }
                            }
                        }
                    }
                });
    }

    private void initListeners() {
        fabInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemActivity.class);
                launcher.launch(intent);
            }
        });

        fabStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                intent.putExtra("movies_list", movies);
                startActivity(intent);
            }
        });

        /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ItemActivity.class);
                intent.putExtra(MOVIE_EXTRA, movies.get(position));
                intent.putExtra(POSITION_EXTRA, position);
                launcher.launch(intent);
            }
        }); */
    }

    private void initControls() {
        spSort = findViewById(R.id.spSort);
        String[] options = {"As added", "Title A-Z", "Rating (best first)"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, options);
        spSort.setAdapter(sortAdapter);
        spSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    movies.clear();
                    movies.addAll(originalMovies);
                } else if (position == 1) {
                    java.util.Collections.sort(movies, new java.util.Comparator<Movie>() {
                        @Override
                        public int compare(Movie m1, Movie m2) {
                            return m1.getTitle().compareToIgnoreCase(m2.getTitle());
                        }
                    });
                } else if (position == 2) {
                    Collections.sort(movies, new Comparator<Movie>() {
                        @Override
                        public int compare(Movie o1, Movie o2) {
                            return Float.compare(o1.getRating(), o2.getRating());
                        }
                    });
                }
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        fabInsert = findViewById(R.id.floatingActionButton);
//         listView = findViewById(R.id.listView);
//         listView.setAdapter(movieAdapter);
        fabStats = findViewById(R.id.btnShowStats);
        recyclerView = findViewById(R.id.rvMovies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(movieAdapter);

    }

    private void addSampleMovies() {
        movies.add(new Movie(
                "Inception",
                new GregorianCalendar(2010, Calendar.JULY, 16).getTime(),
                "Sci-Fi",
                160000000,
                148,
                4.5f,
                true,
                "PG-13",
                false,
                "https://example.com/posters/inception.jpg"));
        originalMovies.add(new Movie(
                "Inception",
                new GregorianCalendar(2010, Calendar.JULY, 16).getTime(),
                "Sci-Fi",
                160000000,
                148,
                4.5f,
                true,
                "PG-13",
                false,
                "https://example.com/posters/inception.jpg"));

        movies.add(new Movie(
                "Toy Story",
                new GregorianCalendar(1995, Calendar.NOVEMBER, 22).getTime(),
                "Animation",
                30000000,
                81,
                4f,
                true,
                "G",
                false,
                "https://example.com/posters/toy-story.jpg"));
        originalMovies.add(new Movie(
                "Toy Story",
                new GregorianCalendar(1995, Calendar.NOVEMBER, 22).getTime(),
                "Animation",
                30000000,
                81,
                4f,
                true,
                "G",
                false,
                "https://example.com/posters/toy-story.jpg"));

        movies.add(new Movie(
                "The Matrix",
                new GregorianCalendar(1999, Calendar.MARCH, 31).getTime(),
                "Action",
                63000000,
                136,
                5f,
                false,
                "R",
                true,
                "https://example.com/posters/the-matrix.jpg"));
        originalMovies.add(new Movie(
                "The Matrix",
                new GregorianCalendar(1999, Calendar.MARCH, 31).getTime(),
                "Action",
                63000000,
                136,
                5f,
                false,
                "R",
                true,
                "https://example.com/posters/the-matrix.jpg"));
    }

    @Override
    public void onMovieClick(Movie movie, int position) {
        Intent intent = new Intent(MainActivity.this, ItemActivity.class);
        intent.putExtra(MOVIE_EXTRA, movies.get(position));
        intent.putExtra(POSITION_EXTRA, position);
        launcher.launch(intent);
    }
}