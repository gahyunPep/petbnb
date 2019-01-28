package pet.eaters.ca.petbnb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button mMapBtn;
    Button mPetDtlBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapBtn = findViewById(R.id.mapBtn);
        mPetDtlBtn = findViewById(R.id.petDetailBtn);

        mMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToMapIntent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(moveToMapIntent);
            }
        });

        mPetDtlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PetDetailActivity.class));
            }
        });
    }
}
