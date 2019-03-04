package pet.eaters.ca.petbnb;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;


import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import pet.eaters.ca.petbnb.pets.postfrom.PetPostFormActivity;
import pet.eaters.ca.petbnb.pets.postfrom.PhotoUploadFragment;

public class MainActivity extends AppCompatActivity {

    Button mMapBtn;
    Button mPetDtlBtn;
    Button mPostFormBtn;
    Button mPhotoUploadBtn;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //https://codinginflow.com/tutorials/android/navigation-drawer/part-2-layouts
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMapBtn = findViewById(R.id.mapBtn);
        mPetDtlBtn = findViewById(R.id.petDetailBtn);
        mPostFormBtn = findViewById(R.id.postFormBtn);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mPhotoUploadBtn = findViewById(R.id.photoUploadBtn);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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

        mPhotoUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhotoUploadFragment();
            }
        });


        //https://developer.android.com/training/implementing-navigation/nav-drawer#java
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void addPhotoUploadFragment(){
        mMapBtn.setVisibility(View.GONE);
        mPetDtlBtn.setVisibility(View.GONE);
        mPostFormBtn.setVisibility(View.GONE);
        mPhotoUploadBtn.setVisibility(View.GONE);

        PhotoUploadFragment photoUpload = PhotoUploadFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.photo_upload_container, photoUpload);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
