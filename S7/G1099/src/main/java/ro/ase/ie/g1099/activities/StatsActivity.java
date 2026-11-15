package ro.ase.ie.g1099.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import ro.ase.ie.g1099.R;
import ro.ase.ie.g1099.model.Movie;

public class StatsActivity extends AppCompatActivity {

    private TextView tvHighestBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvHighestBudget), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvHighestBudget = findViewById(R.id.tvHighestBudget);

        Intent intent = getIntent();

        @SuppressWarnings("unchecked")
        ArrayList<Movie> movies = (ArrayList<Movie>) intent.getSerializableExtra("movies_list");

        if(movies != null && !movies.isEmpty()) {
            Movie topMovie = movies.get(0);

            for(Movie movie: movies) {
                if(movie.getBudget() > topMovie.getBudget()) {
                    topMovie = movie;
                }
            }

            String formattedBudget = String.format(java.util.Locale.US, "$%,.0f", topMovie.getBudget());
            tvHighestBudget.setText("Highest Budget:\n" + topMovie.getTitle() + "\n" + formattedBudget);

        } else {
            tvHighestBudget.setText("No data available.");
        }
    }
}