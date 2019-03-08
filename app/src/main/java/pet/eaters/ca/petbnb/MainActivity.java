package pet.eaters.ca.petbnb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import pet.eaters.ca.petbnb.pets.ui.postform.PetFormFragment;
import pet.eaters.ca.petbnb.pets.ui.list.PetsListFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private TextView textUserName;
    private CircleImageView avatar;
    private static final int RC_SIGN_IN = 9001;

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
        navigationView.setNavigationItemSelectedListener(this);

        textUserName = navigationView.getHeaderView(0).findViewById(R.id.txtViewUser);
        avatar = navigationView.getHeaderView(0).findViewById(R.id.avatar);
        showUser(FirebaseAuth.getInstance().getCurrentUser());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showLoginButtons(boolean isLogin) {
        Menu menu = navigationView.getMenu();
        MenuItem login = menu.findItem(R.id.nav_login);
        MenuItem logout = menu.findItem(R.id.nav_logout);
        if (isLogin) {
            login.setVisible(false);
            logout.setVisible(true);
        } else {
            login.setVisible(true);
            logout.setVisible(false);
        }
    }

    private void showUser(FirebaseUser user) {
        if (user == null) {
            showLoginButtons(false);
            avatar.setImageResource(R.drawable.ic_user);
            textUserName.setText("");
        } else {
            showLoginButtons(true);
            textUserName.setText(user.getDisplayName());
            showAvatar(user.getPhotoUrl());
        }
    }

    private void showAvatar(Uri url) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user);
        Glide.with(this).setDefaultRequestOptions(requestOptions).
                load(url).into(avatar);
    }


    private void logout() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                showUser(user);
                if (user == null) {
                    Toast.makeText(getApplicationContext(), "User signed out", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()
                        ))
                        .setLogo(R.drawable.logo)      // Set logo drawable
                        .setTheme(R.style.AppTheme)      // Set theme
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    showUser(user);
                    Toast.makeText(this, "Successfully signed in", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to log in with your account", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_login:
                login();
                break;
            default:
                Fragment fragment = createFragmentForMenu(menuItem.getItemId());
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.content_frame, fragment)
                            .commit();
                }

        }
        return true;
    }

    private Fragment createFragmentForMenu(int id) {
        switch (id) {
            case R.id.nav_home:
                return PetsListFragment.newInstance();
            case R.id.nav_create_post:
                return PetFormFragment.newInstance();
        }
        return null;
    }
}
