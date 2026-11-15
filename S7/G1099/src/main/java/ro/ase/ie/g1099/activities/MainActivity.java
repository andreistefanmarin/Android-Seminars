package ro.ase.ie.g1099.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

    private FloatingActionButton fabSort;
    // private ListView listView;

    private FloatingActionButton fabToggle;
    private boolean isGrid = true; // We start in Grid mode
    private RecyclerView recyclerView;
    private ActivityResultLauncher<Intent> launcher;
    private final ArrayList<Movie> movies = new ArrayList<>();
    //private ArrayAdapter<Movie> movieAdapter;
    private MovieAdapter movieAdapter;
    private TextView stats;

    private Button btnClearAll;

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
        updateStats();

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
                                movieAdapter.notifyDataSetChanged();
                                updateStats();
                            }
                            else
                            {
                                movies.set(position, movie);
                                movieAdapter.notifyDataSetChanged();
                                updateStats();
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

        fabSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(movies, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie m1, Movie m2) {
                        return Float.compare(m2.getRating(), m1.getRating());
                    }
                });
                movieAdapter.notifyDataSetChanged();
            }
        });

        fabToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGrid = !isGrid;
                if(isGrid) {
                    recyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(MainActivity.this, 2));
                } else {
                    recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(MainActivity.this));
                }
            }
        });

        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Clear all movies?")
                        .setMessage("This action cannot be undone")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                movies.clear();
                                movieAdapter.notifyDataSetChanged();
                                updateStats();
                            }
                        }).show();
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

        fabInsert = findViewById(R.id.floatingActionButton);
        fabSort = findViewById(R.id.btnSortByRatings);
//         listView = findViewById(R.id.listView);
//         listView.setAdapter(movieAdapter);
        fabToggle = findViewById(R.id.btnToggleLayout);
        recyclerView = findViewById(R.id.rvMovies);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));
        recyclerView.setAdapter(movieAdapter);
        stats = findViewById(R.id.tvStats);
        btnClearAll = findViewById(R.id.btnClearAll);

    }

    private void updateStats() {
        int totalMovies = 0;
        double totalBudget = 0;
        for(Movie movie : movies) {
            totalMovies++;
            totalBudget += movie.getBudget();
        }
        stats.setText("Total Movies: " + totalMovies + " | Total Budget: $" + totalBudget);
    }

    private void addSampleMovies() {
        movies.add(new Movie(
                "Inception",
                "John Doe",
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
                "Anna Brick",
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
                "Michael Michelson",
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

    @Override
    public void onMovieLongClick(int position) {
        movies.remove(position);
        movieAdapter.notifyItemRemoved(position);
        updateStats();
    }
}