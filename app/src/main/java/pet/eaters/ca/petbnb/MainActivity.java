package pet.eaters.ca.petbnb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import de.hdodenhof.circleimageview.CircleImageView;
import pet.eaters.ca.petbnb.core.NavigationFragment;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.pets.data.ScanRecord;
import pet.eaters.ca.petbnb.pets.data.ScanRepository;
import pet.eaters.ca.petbnb.pets.ui.list.PetsListFragment;
import pet.eaters.ca.petbnb.pets.ui.postform.PetPostFormActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        NavigationFragment.NavigationActivity {
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private TextView textUserName;
    private CircleImageView avatar;
    private static final int RC_SIGN_IN = 9001;
    private ScanRepository scanRepository;
    private boolean clickOnScan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showHomeFragment();
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
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    showUser(user);
                    Toast.makeText(this, "Successfully signed in", Toast.LENGTH_SHORT).show();
                    if (clickOnScan) {
                        startScanner();
                    }
                }
            } else {
                Toast.makeText(this, "Failed to log in with your account", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result == null) {
                return;
            }
            String petId = result.getContents();
            if (petId == null) {
                return;
            }

            petIdScanned(petId);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_login:
                login();
                break;
            case R.id.nav_qrcode:
                clickOnScan = true;
                startScanner();
                break;
            case R.id.nav_create_post:
                startActivity(new Intent(this, PetPostFormActivity.class));
                break;
        }
        return true;
    }

    private void startScanner() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            login();
            return;
        }

        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(getString(R.string.scanCodeDescription));
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    private void petIdScanned(final String petId) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login before you can scan the pet", Toast.LENGTH_LONG).show();
            return;
        }

        final String uid = user.getUid();
        final String scanId = uid + "+" + petId;

        scanRepository = new ScanRepository();

        scanRepository.get(scanId).observe(this, new Observer<Result<ScanRecord>>() {
            @Override
            public void onChanged(Result<ScanRecord> scanRecordResult) {
                ScanRecord scanRecord = scanRecordResult.getData();
                if (scanRecord != null) {
                    if (scanRecord.getTimestamps().size() % 2 == 0) {
                        scanRepository.update(scanId, new ScanRecord(scanRecord.getPetID(),
                                scanRecord.getUserID(), scanRecord.addTimestamps(getCurrentTime())));
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
                        alertDialogBuilder.setMessage("Start time is set!");
                        alertDialogBuilder.show();
                    } else {
                        scanRepository.update(scanId, new ScanRecord(scanRecord.getPetID(),
                                scanRecord.getUserID(), scanRecord.addTimestamps(getCurrentTime())));
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
                        int price = calculatePayment(scanRecord.getTimestamps().get(scanRecord.getTimestamps().size() - 1),
                                scanRecord.getTimestamps().get(scanRecord.getTimestamps().size() - 2));
                        alertDialogBuilder.setMessage("You will have to pay: $" + Integer.toString(price));
                        alertDialogBuilder.show();
                    }
                } else {
                    List<Long> timestamps = new ArrayList<>();
                    timestamps.add(getCurrentTime());
                    scanRepository.update(scanId, new ScanRecord(petId,
                            uid, timestamps));
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
                    alertDialogBuilder.setMessage("Start time is set!");
                    alertDialogBuilder.show();
                }
            }
        });
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public int calculatePayment(long timeCheckOut, long timeCheckIn) {
        long hour = (timeCheckOut - timeCheckIn) / (60 * 60 * 1000);
        return (int) hour * 2;
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
        showUser(FirebaseAuth.getInstance().getCurrentUser());
    }
}
