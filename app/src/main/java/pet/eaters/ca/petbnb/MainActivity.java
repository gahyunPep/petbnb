package pet.eaters.ca.petbnb;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import de.hdodenhof.circleimageview.CircleImageView;
import pet.eaters.ca.petbnb.core.android.NavigationFragment;
import pet.eaters.ca.petbnb.core.ui.EventObserver;
import pet.eaters.ca.petbnb.pets.ui.QrCodeClickedListener;
import pet.eaters.ca.petbnb.pets.ui.list.PetsListFragment;
import pet.eaters.ca.petbnb.pets.ui.postform.PetPostFormActivity;

import static pet.eaters.ca.petbnb.MainActivityViewModel.RC_SIGN_IN;
import static pet.eaters.ca.petbnb.MainActivityViewModel.RC_SIGN_IN_AND_SCAN;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        NavigationFragment.NavigationActivity, QrCodeClickedListener {
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private TextView textUserName;
    private CircleImageView avatar;

    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        showHomeFragment();

        observeUser();
        observeMessage();
        observeAlert();
    }

    private void observeAlert() {
        viewModel.getAlert().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                if (message == null) return;
                showAlert(message);
            }
        });
    }

    private void observeMessage() {
        viewModel.getMessage().observe(this, new EventObserver<Integer>() {
            @Override
            public void onEventHappened(Integer value) {
                showMessage(value);
            }
        });
    }

    private void observeUser() {
        viewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser user) {
                showUser(user);
            }
        });
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.pet_sitting)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.dismissDialog();
                    }
                })
                .show();
    }

    private void showMessage(Integer value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

    private void showHomeFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null) {
            return;
        }

        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, PetsListFragment.newInstance()).commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        int index = fragmentManager.getBackStackEntryCount() - 1;
                        if (index >= 0) {
                            String name = fragmentManager
                                    .getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
                            try {
                                navigationView.setCheckedItem(Integer.valueOf(name));
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                });
            }
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
            showAvatar(user.getPhotoUrl());
            textUserName.setText(user.getDisplayName());
        }
    }

    private void showAvatar(@Nullable Uri url) {
        avatar.setImageResource(R.drawable.ic_user);
        if (url == null) {
            return;
        }

        Glide.with(this)
                .load(url)
                .into(avatar);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
            case RC_SIGN_IN_AND_SCAN:
                viewModel.loginResult(this, requestCode, resultCode);
                break;
            case IntentIntegrator.REQUEST_CODE:
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result == null) {
                    return;
                }
                String petId = result.getContents();
                if (petId == null) {
                    return;
                }

                viewModel.petIdScanned(petId);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.nav_logout:
                viewModel.logout(this);
                break;
            case R.id.nav_login:
                viewModel.login(this, RC_SIGN_IN);
                break;
            case R.id.nav_create_post:
                startActivity(new Intent(this, PetPostFormActivity.class));
                break;
        }
        return true;
    }

    //https://codinginflow.com/tutorials/android/navigation-drawer/part-2-layouts
    @Override
    public void initWith(Toolbar toolbar) {
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
    }

    @Override
    public void onScanQrCodeClicked() {
        viewModel.startScanner(this, RC_SIGN_IN_AND_SCAN);
    }
}
