package pet.eaters.ca.petbnb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class PetDetailActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout imageSlideIndicator;
    private int[] petImages = {R.drawable.pet, R.drawable.pet2, R.drawable.pet3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);
        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(this, petImages);
        viewPager.setAdapter(viewPagerAdapter);

        imageSlideIndicator = findViewById(R.id.image_slide_indicator);
        imageSlideIndicator.setupWithViewPager(viewPager, true);
    }
}
