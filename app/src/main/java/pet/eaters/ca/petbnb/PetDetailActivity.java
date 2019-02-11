package pet.eaters.ca.petbnb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class PetDetailActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout imageSlideIndicator;
    private List<String> petImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);
        petImages.add("https://i.barkpost.com/wp-content/uploads/2015/05/Golden-Retriever-Puppy.jpg?q=70&fit=crop&crop=entropy&w=808&h=500");
        petImages.add("https://imgc.allpostersimages.com/img/print/posters/lynn-m-stone-golden-retriever-puppy-in-bucket-canis-familiaris-illinois-usa_a-G-2636281-14258389.jpg");
        petImages.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSJRxkdbo0oLQsw5H4TdwOV9CFoHLxCs2rvHhCv2xFa8xoPXTuj");
        petImages.add("");
        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(petImages, this);
        viewPager.setAdapter(viewPagerAdapter);


        imageSlideIndicator = findViewById(R.id.image_slide_indicator);
        imageSlideIndicator.setupWithViewPager(viewPager, true);
    }
}
