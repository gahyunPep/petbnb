package pet.eaters.ca.petbnb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

public class PetDetailActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ImageSliderAdapter imgSliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);
        viewPager = findViewById(R.id.image_slider);
        imgSliderAdapter = new ImageSliderAdapter(this);
        viewPager.setAdapter(imgSliderAdapter);
    }
}
