package pet.eaters.ca.petbnb;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;
import pet.eaters.ca.petbnb.pets.login.LoginFragment;
import pet.eaters.ca.petbnb.pets.postform.PetFormFragment;
import pet.eaters.ca.petbnb.pets.ui.PetsListFragment;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInterationListener{
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private Menu menu;
    private MenuItem login;
    private MenuItem logout;
    private LoginFragment loginFragment;
    private boolean isUserLogin = false;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private PetFormFragment petFormFragment;
    private PetsListFragment petsListFragment;
    private TextView textUserName;
    //private ImageView avatar;
    private CircleImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //https://codinginflow.com/tutorials/android/navigation-drawer/part-2-layouts
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //https://developer.android.com/training/implementing-navigation/nav-drawer#java
        navigationView = findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        login = menu.findItem(R.id.nav_login);
        logout = menu.findItem(R.id.nav_logout);
        if(isUserLogin) {
            login.setVisible(false);
            logout.setVisible(true);
        }
        else {
            login.setVisible(true);
            logout.setVisible(false);
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        switch (menuItem.getItemId())   {

                            case R.id.nav_login:
                                loginFragment = LoginFragment.newInstance();
                                fragmentManager = getSupportFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.content_frame, loginFragment).addToBackStack(null).commit();
                                break;

                            case R.id.nav_home:
                                petsListFragment = PetsListFragment.newInstance();
                                fragmentManager = getSupportFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.content_frame, petsListFragment).addToBackStack(null).commit();
                                break;

                            case R.id.nav_create_post:
                                petFormFragment = PetFormFragment.newInstance();
                                fragmentManager = getSupportFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.content_frame, petFormFragment).addToBackStack(null).commit();
                                break;

                            case R.id.nav_logout:
                                loginFragment.signOutUser();
                                login.setVisible(true);
                                logout.setVisible(false);
                                avatar.setImageResource(R.drawable.ic_user);
                                textUserName.setText("");
                                break;
                        }
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

    @Override
    public void getLoginStatus(boolean isLogin) {
        isUserLogin = isLogin;
        if(isUserLogin) {
            login.setVisible(false);
            logout.setVisible(true);
        }
        else {
            login.setVisible(true);
            logout.setVisible(false);
        }
    }

    @Override
    public void getUserName(String name) {
        textUserName = (TextView) findViewById(R.id.txtViewUser);
        textUserName.setText(name);

    }

    @Override
    public void getAvatar(Uri url) {
        avatar = (CircleImageView) findViewById(R.id.avatar);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_user);
        requestOptions.error(R.drawable.ic_user);

        Glide.with(this).setDefaultRequestOptions(requestOptions).
                load(url).into(avatar);
    }
}
