package ro.ase.ie.g1099.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jspecify.annotations.Nullable;

import ro.ase.ie.g1099.R;

public class MainActivity extends AppCompatActivity {
    public static final @Nullable String EXTRA_MOVIE = "movie";
    public static final String EXTRA_POSITION = "position";
    private FloatingActionButton fabInsert;
    private ListView listView;
    private ActivityResultLauncher<Intent> launcher;

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

        fabInsert = findViewById(R.id.floatingActionButton);
        fabInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //adding a new movie via the ItemActivity
                Intent intent = new Intent(MainActivity.this, ItemActivity.class);
                launcher.launch(intent);
            }
        });

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        //handle response from child ItemActivity
                        if(o.getResultCode() == RESULT_OK)
                        {
                            Intent data = o.getData();
                            String response = data.getStringExtra("response");
                            Toast.makeText(MainActivity.this, response,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //updating an existing movie via the ItemActivity
                String item = (String) parent.getItemAtPosition(position);
                /*Toast.makeText(MainActivity.this, "You've clicked on " + item,
                        Toast.LENGTH_SHORT).show();*/
                Intent intent = new Intent(MainActivity.this, ItemActivity.class);
//                startActivity(intent);
                intent.putExtra("item", item);
                launcher.launch(intent);
            }
        });
    }
}