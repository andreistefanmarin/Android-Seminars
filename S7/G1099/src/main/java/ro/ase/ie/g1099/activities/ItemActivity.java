package ro.ase.ie.g1099.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ro.ase.ie.g1099.R;
import ro.ase.ie.g1099.model.Movie;

public class ItemActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etRelease;
    private EditText etCountry;
    private Spinner spGenre;
    private EditText etBudget;
    private SeekBar sbDuration;
    private RatingBar rbRating;
    private MaterialButtonToggleGroup tgApproval;
    private CheckBox cbonWatchList;
    private MaterialSwitch swWatched;
    private EditText etPoster;
    private Button btMovieAction;
    private TextView tvBudgetValue;
    private Button btDeleteMovie;
    private TextView tvDurationValue;
    private TextView tvWatchedValue;

    private int position = -1;   // -1 = insert mode
    private Calendar releaseCal = Calendar.getInstance();
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

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
        initListeners();
        btDeleteMovie.setVisibility(View.GONE);

        Intent intent = getIntent();
        Movie existing = IntentCompat.getSerializableExtra(intent, MainActivity.MOVIE_EXTRA, Movie.class);
        if (existing != null) {
            btDeleteMovie.setVisibility(View.VISIBLE);
            position = intent.getIntExtra(MainActivity.POSITION_EXTRA, -1);
            fillForm(existing);
            setTitle("Edit movie");
            btMovieAction.setText("Update Movie");
        }
    }

    private void fillForm(Movie existing) {
        etTitle.setText(existing.getTitle());
        etRelease.setText(dateFormat.format(existing.getRelease()));
        etCountry.setText(existing.getCountry());
        etBudget.setText(String.valueOf(existing.getBudget()));
        sbDuration.setProgress(existing.getDuration());
        cbonWatchList.setChecked(existing.isOnWatchlist());
        swWatched.setChecked(existing.isWatched());
        rbRating.setRating(existing.getRating());
        etPoster.setText(existing.getPosterUrl());

        for (int i = 0; i < spGenre.getCount(); i++) {
            if (spGenre.getItemAtPosition(i).toString().equals(existing.getGenre())) {
                spGenre.setSelection(i);
                break;
            }
        }

        for (int index = 0; index < tgApproval.getChildCount(); index++) {
            com.google.android.material.button.MaterialButton btn =
                    (com.google.android.material.button.MaterialButton) tgApproval.getChildAt(index);
            if (btn.getText().toString().equals(existing.getApproval())) {
                tgApproval.check(btn.getId());
                break;
            }
        }
    }

    private void initControls() {

        etTitle = findViewById(R.id.etTitle);
        etRelease = findViewById(R.id.etRelease);
        etCountry = findViewById(R.id.etCountry);
        spGenre = findViewById(R.id.spGenre);
        tvBudgetValue = findViewById(R.id.tvBudgetValue);
        etBudget = findViewById(R.id.etBudget);
        sbDuration = findViewById(R.id.sbDuration);
        rbRating = findViewById(R.id.rbRating);
        tgApproval = findViewById(R.id.tgApproval);
        cbonWatchList = findViewById(R.id.cbOnWatchList);
        swWatched = findViewById(R.id.swWatched);
        etPoster = findViewById(R.id.etPoster);
        btMovieAction = findViewById(R.id.btnMovieAction);
        btDeleteMovie = findViewById(R.id.btnDeleteMovie);
        tvDurationValue = findViewById(R.id.tvDurationValue);
        tvWatchedValue = findViewById(R.id.tvWatchedValue);
    }

    private void initListeners() {
        etRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReleaseDatePicker();
            }
        });
        btMovieAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMovie();
            }
        });
        btDeleteMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMovie();
            }
        });
        swWatched.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                tvWatchedValue.setText(isChecked ? "Yes" : "No");
            }
        });
        sbDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvDurationValue.setText(progress + " min.");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        etBudget.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String input = s.toString().trim();

                if (input.isEmpty()) {
                    tvBudgetValue.setText("$0");
                    return;
                }

                try {
                    double parsed = Double.parseDouble(input);
                    String formatted = String.format(java.util.Locale.US, "$%,.0f", parsed);
                    tvBudgetValue.setText(formatted);
                } catch (NumberFormatException e) {
                    tvBudgetValue.setText("$0");
                }
            }
        });
    }

    private void deleteMovie() {
        Intent intent = new Intent();
        // MainActivity needs to know WHICH item to delete
        intent.putExtra(MainActivity.POSITION_EXTRA, position);
        // 100 is our custom code for "DELETE"
        setResult(100, intent);
        finish();
    }

    private void saveMovie() {
        String countryInput = etCountry.getText().toString().trim();
        if(countryInput.isEmpty()) {
            etCountry.setError("Country is required to save");
            etCountry.requestFocus();
            return;
        }
        Movie movie = new Movie();
        movie.setTitle(etTitle.getText().toString());
        movie.setRelease(releaseCal.getTime());
        movie.setCountry(countryInput);
        movie.setGenre((String) spGenre.getSelectedItem());
        movie.setBudget(Double.parseDouble(etBudget.getText().toString()));
        movie.setDuration(sbDuration.getProgress());
        movie.setRating(rbRating.getRating());
        movie.setWatched(swWatched.isChecked());
        movie.setOnWatchlist(cbonWatchList.isChecked());
        movie.setPosterUrl(etPoster.getText().toString());

        int checkedId = tgApproval.getCheckedButtonId();
        com.google.android.material.button.MaterialButton checkedButton = findViewById(checkedId);
        if (checkedButton != null) {
            movie.setApproval(checkedButton.getText().toString());
        }

        Intent intent = new Intent();
        intent.putExtra(MainActivity.MOVIE_EXTRA, movie);
        if (position != -1)
            intent.putExtra(MainActivity.POSITION_EXTRA, position);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showReleaseDatePicker() {
        String current = etRelease.getText().toString();
        if (!current.isEmpty()) {
            try {
                Date parsed = dateFormat.parse(current);
                if (parsed != null)
                    releaseCal.setTime(parsed);
            } catch (ParseException ignored) { }
        }

        new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        releaseCal.set(year, month, day);
                        etRelease.setError(null);
                        etRelease.setText(dateFormat.format(releaseCal.getTime()));
                    }
                },
                releaseCal.get(Calendar.YEAR),
                releaseCal.get(Calendar.MONTH),
                releaseCal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}
