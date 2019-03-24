package pet.eaters.ca.petbnb.pets.data;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import androidx.lifecycle.LiveData;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UploadPicturesIntentService extends IntentService {
//    // TODO: Rename actions, choose action names that describe tasks that this
//    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
//    private static final String ACTION_FOO = "pet.eaters.ca.petbnb.pets.data.action.FOO";
//    private static final String ACTION_BAZ = "pet.eaters.ca.petbnb.pets.data.action.BAZ";
//
//    // TODO: Rename parameters
//    private static final String EXTRA_PARAM1 = "pet.eaters.ca.petbnb.pets.data.extra.PARAM1";
//    private static final String EXTRA_PARAM2 = "pet.eaters.ca.petbnb.pets.data.extra.PARAM2";


//    /**
//     * Starts this service to perform action Foo with the given parameters. If
//     * the service is already performing a task this action will be queued.
//     *
//     * @see IntentService
//     */
//    // TODO: Customize helper method
//    public static void startActionFoo(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, UploadPicturesIntentService.class);
//        intent.setAction(ACTION_FOO);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }
//
//    /**
//     * Starts this service to perform action Baz with the given parameters. If
//     * the service is already performing a task this action will be queued.
//     *
//     * @see IntentService
//     */
//    // TODO: Customize helper method
//    public static void startActionBaz(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, UploadPicturesIntentService.class);
//        intent.setAction(ACTION_BAZ);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }


//    /**
//     * Handle action Foo in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionFoo(String param1, String param2) {
//        // TODO: Handle action Foo
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    /**
//     * Handle action Baz in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionBaz(String param1, String param2) {
//        // TODO: Handle action Baz
//        throw new UnsupportedOperationException("Not yet implemented");
//    }

    private final String PET_ID_KEY = "petID";
    private final String PET_IMG_KEY = "petImgs";

    public UploadPicturesIntentService() {
        super("UploadPicturesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String petId;
        List<String> petImages = new ArrayList<String>();
        PhotoStorage photoStorage = new PhotoStorage();
        if (intent != null) {
            petId = intent.getStringExtra(PET_ID_KEY);
            petImages = intent.getStringArrayListExtra(PET_IMG_KEY);
            // post imges to firebase useing petid and petImages
            // get imges url
            LiveData<List<String>> imgFiles = photoStorage.uploadFiles(petImages, petId);
            imgFiles.observe(new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    
                }
            });
            // post the imges under specific pet
        }

    }

}
