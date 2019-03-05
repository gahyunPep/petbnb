package pet.eaters.ca.petbnb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import pet.eaters.ca.petbnb.pets.ui.postform.PetPostFormActivity;

public class MainActivity extends AppCompatActivity {

    Button mMapBtn;
    Button mPetDtlBtn;
    Button mPostFormBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapBtn = findViewById(R.id.mapBtn);
        mPetDtlBtn = findViewById(R.id.petDetailBtn);
        mPostFormBtn = findViewById(R.id.postFormBtn);


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

        mPostFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PetPostFormActivity.class));
            }
        });

    }
}
