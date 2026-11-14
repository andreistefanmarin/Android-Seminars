package com.example.t1mockapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.t1mockapp.R;
import com.example.t1mockapp.model.Movie;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ItemActivity extends AppCompatActivity {
    private EditText etTitle, etRelease;
    private EditText etDirector;

    private Button btnSave;

    private Calendar releaseCal = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // Add position variable to track if we are inserting (-1) or editing (>= 0)
    private int position = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initControls();

        // 1. EDIT CHECK MUST HAPPEN WHEN ACTIVITY OPENS
        Intent intent = getIntent();
        Movie existingMovie = (Movie) intent.getSerializableExtra("movie_extra");
        if (existingMovie != null) {
            position = intent.getIntExtra("position_extra", -1);
            fillForm(existingMovie);
        }

        etRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReleaseDatePicker();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String dateString = etRelease.getText().toString();
                String director = etDirector.getText().toString();
                Movie newMovie = new Movie();
                newMovie.setTitle(title);
                newMovie.setDirector(director);

                // Parse the String date back into a Date object
                try {
                    if (!dateString.isEmpty()) {
                        newMovie.setRelease(sdf.parse(dateString));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Pack the intent to send back to MainActivity
                Intent replyIntent = new Intent();
                replyIntent.putExtra("movie_extra", newMovie);
                if (position != -1) {
                    replyIntent.putExtra("position_extra", position);
                }

                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }

    private void fillForm(Movie existingMovie) {
        etTitle.setText(existingMovie.getTitle());
        etDirector.setText(existingMovie.getDirector());
        if (existingMovie.getRelease() != null) {
            etRelease.setText(sdf.format(existingMovie.getRelease()));
            releaseCal.setTime(existingMovie.getRelease());
        }
    }

    private void showReleaseDatePicker() {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                releaseCal.set(year, month, dayOfMonth);
                etRelease.setText(sdf.format(releaseCal.getTime()));
            }
        },
                releaseCal.get(Calendar.YEAR),
                releaseCal.get(Calendar.MONTH),
                releaseCal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void initControls() {
        etTitle = findViewById(R.id.etTitle);
        etRelease = findViewById(R.id.etRelease);
        btnSave = findViewById(R.id.btnSave);
        etDirector = findViewById(R.id.etDirector);
    }
}