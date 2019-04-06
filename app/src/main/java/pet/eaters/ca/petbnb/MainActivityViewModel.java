package pet.eaters.ca.petbnb;

import android.app.Activity;
import android.content.Context;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.core.ui.Event;
import pet.eaters.ca.petbnb.pets.data.GenericRepository;
import pet.eaters.ca.petbnb.pets.data.ScanRecord;
import pet.eaters.ca.petbnb.pets.data.ScanRepository;

public class MainActivityViewModel extends ViewModel {
    static final int RC_SIGN_IN = 9001;
    static final int RC_SIGN_IN_AND_SCAN = 9002;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ScanRepository scanRepository = new ScanRepository();


    private MutableLiveData<FirebaseUser> user = new MutableLiveData<>();
    private MutableLiveData<Event<Integer>> message = new MutableLiveData<>();
    private MutableLiveData<String> alert = new MutableLiveData<>();


    public MainActivityViewModel() {
        user.setValue(auth.getCurrentUser());
    }

    public LiveData<FirebaseUser> getUser() {
        return user;
    }

    public LiveData<Event<Integer>> getMessage() {
        return message;
    }

    public LiveData<String> getAlert() {
        return alert;
    }


    public void dismissDialog() {
        alert.setValue(null);
    }

    public void logout(Context context) {
        AuthUI.getInstance().signOut(context).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                user.setValue(firebaseUser);
                if (firebaseUser == null) {
                    message.setValue(new Event<>(R.string.signed_out));
                }
            }
        });
    }

    public void login(AppCompatActivity activity, int code) {
        activity.startActivityForResult(
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
                code);
    }

    public void loginResult(AppCompatActivity activity, int requestCode, int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            FirebaseUser firebaseUser = auth.getCurrentUser();
            user.setValue(firebaseUser);
            if (firebaseUser != null) {
                message.setValue(new Event<>(R.string.login_success));

                if (requestCode == RC_SIGN_IN_AND_SCAN) {
                    startScanner(activity, requestCode);
                }
            }
        }
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == RC_SIGN_IN_AND_SCAN) {
                message.setValue(new Event<>(R.string.login_before_scan));
            } else {
                message.setValue(new Event<>(R.string.login_failed));
            }
        }
    }

    public void startScanner(AppCompatActivity activity, int loginRequestCode) {
        if (user.getValue() == null) {
            login(activity, loginRequestCode);
            return;
        }
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(activity.getString(R.string.scanCodeDescription));
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();

    }

    public void petIdScanned(final String petId) {
        FirebaseUser firebaseUser = user.getValue();
        if (firebaseUser == null) {
            message.setValue(new Event<>(R.string.login_before_scan));
            return;
        }

        final String uid = firebaseUser.getUid();
        final String scanId = uid + "+" + petId;

        scanRepository.get(scanId, new GenericRepository.Callback<ScanRecord>() {
            @Override
            public void onResult(Result<ScanRecord> scanRecordResult) {
                ScanRecord scanRecord = scanRecordResult.getData();
                if (scanRecord == null) {
                    scanRecord = new ScanRecord(petId, uid, new ArrayList<Long>());
                }

                scanRepository.update(scanId, scanRecord.addTimestamps(getCurrentTime()));
                alert.setValue(getMessage(scanRecord));
            }
        });
    }

    private String getMessage(ScanRecord scanRecord) {
        if (scanRecord.getTimestamps().size() % 2 == 0) {
            return "Start time is set!";
        } else {
            return "You will have to pay: $" + calculatePayment(scanRecord.getTimestamps());
        }
    }

    private int calculatePayment(List<Long> timestamps) {
        int size = timestamps.size();
        return calculatePayment(timestamps.get(size - 1), timestamps.get(size - 2));
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private int calculatePayment(long timeCheckOut, long timeCheckIn) {
        long hour = (timeCheckOut - timeCheckIn) / (60 * 60 * 1000);
        return (int) hour * 2;
    }
}
