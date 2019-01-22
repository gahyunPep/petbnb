package pet.eaters.ca.petbnb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button mMapBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapBtn = findViewById(R.id.mapBtn);

        mMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToMapIntent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(moveToMapIntent);
            }
        });
    }
}
