package pet.eaters.ca.petbnb.pets.data;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ImgUplaodIntentService extends IntentService {

    private final String PET_ID_KEY = "petID";
    private final String PET_IMG_KEY = "petImgs";

    public ImgUplaodIntentService() {
        super("ImgUplaodIntentService");
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
//            imgFiles.observe(new Observer() {
//                @Override
//                public void update(Observable o, Object arg) {
//
//                }
//            });
            // post the imges under specific pet
        }
    }

}
