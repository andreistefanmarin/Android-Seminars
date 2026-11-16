package ro.ase.ie.g1099.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Grid;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ro.ase.ie.g1099.R;
import ro.ase.ie.g1099.adapter.MovieAdapter;
import ro.ase.ie.g1099.model.Movie;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieListener {

    protected static final String POSITION_EXTRA = "position";
    protected static final String MOVIE_EXTRA = "movie";
    private FloatingActionButton fabInsert;
    private RecyclerView recyclerView;
    private ActivityResultLauncher<Intent> launcher;
    private final List<Movie> movies = new ArrayList<>();
    //    private ArrayAdapter<Movie> movieAdapter;
    private MovieAdapter movieAdapter;
    private MaterialSwitch swLayoutToggle;

    private EditText etSearch;
    private final List<Movie> allMovies = new ArrayList<>();

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
        movieAdapter = new MovieAdapter(movies, this);

//        movieAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, movies);
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
                                movieAdapter.notifyDataSetChanged();
                            }
                            else
                            {
                                movies.set(position, movie);
                                movieAdapter.notifyDataSetChanged();
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

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().toLowerCase().trim();
                movies.clear();

                if(query.isEmpty()) {
                    movies.addAll(allMovies);
                } else {
                    for(Movie movie: allMovies) {
                        if(movie.getTitle().toLowerCase().contains(query)) {
                            movies.add(movie);
                        }
                    }
                }

                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/

    }

    private void initControls() {
        etSearch = findViewById(R.id.etSearch);
        fabInsert = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.rvMovies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(movieAdapter);
        swLayoutToggle = findViewById(R.id.swLayoutToggle);
        swLayoutToggle.setOnCheckedChangeListener( ((buttonView, isChecked) -> {
            if(isChecked) {
                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
            } else {
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            }
        }));
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
        allMovies.addAll(movies);
    }

    @Override
    public void onMovieClick(Movie movie, int position) {
        Intent intent = new Intent(MainActivity.this, ItemActivity.class);
        intent.putExtra(MOVIE_EXTRA, movies.get(position));
        intent.putExtra(POSITION_EXTRA, position);
        launcher.launch(intent);
    }

    @Override
    public void onMovieLongClick(Movie movie, int position) {
        movies.remove(position);
        movieAdapter.notifyItemRemoved(position);
        Snackbar.make(recyclerView, movie.getTitle() + " deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", v -> {
                    movies.add(position, movie);
                    movieAdapter.notifyItemInserted(position);
        }).show();
    }
}