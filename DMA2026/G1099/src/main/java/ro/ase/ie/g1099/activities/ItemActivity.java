package ro.ase.ie.g1099.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ro.ase.ie.g1099.R;
import ro.ase.ie.g1099.model.Movie;

public class ItemActivity extends AppCompatActivity {
    private int position = -1;
    private EditText etRelease;
    private Calendar releaseCal = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
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

        etRelease = findViewById(R.id.etRelease);
        etRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReleaseDatePicker();
            }

        });

        Intent intent = getIntent();
        Movie movie = IntentCompat.getParcelableExtra(intent, MainActivity.EXTRA_MOVIE, Movie.class);
        if(movie != null) {
            position = intent.getIntExtra(MainActivity.EXTRA_POSITION, -1);
            setTitle("Update Movie");
        }

        Snackbar.make(findViewById(R.id.main), "Send response back to parent activity", Snackbar.LENGTH_SHORT)
                .setAction("Acknowledge", v -> {
                    // Intent intent = getIntent();
                    Bundle extras = intent.getExtras();
                    String item = extras.getString("item");
                    intent.putExtra("response", item.concat(" from ItemActivity"));
                    setResult(RESULT_OK, intent);
                    finish();
                }).show();
    }
    private void showReleaseDatePicker() {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                releaseCal.set(year, month, dayOfMonth);
                etRelease.setError(null);
                etRelease.setText(sdf.format(releaseCal.getTime()));
            }
        },
            releaseCal.get(Calendar.YEAR),
            releaseCal.get(Calendar.MONTH),
            releaseCal.get(Calendar.DAY_OF_MONTH)).show();
    }
}