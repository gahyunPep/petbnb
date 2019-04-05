package pet.eaters.ca.petbnb.pets.data;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.core.Result;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class ImgUplaodIntentService extends IntentService {

    private final String PET_ID_KEY = "petID";
    private final String PET_IMG_KEY = "petImgs";

    private NotificationManager notifier;
    private static final String NOTIFICATION_CHANNEL_ID = "photo_upload_channel";
    private static final String NOTIFICATION_CHANNEL2_ID = "photo_uploaded_channel" ;
    private static final int PHOTO_UPLOAD_NOTIFY = 0x1001;

    public ImgUplaodIntentService() {
        super("ImgUplaodIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel(NOTIFICATION_CHANNEL_ID, false);
        createChannel(NOTIFICATION_CHANNEL2_ID, true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }


        final String petId = intent.getStringExtra(PET_ID_KEY);
        List<String> petImages = intent.getStringArrayListExtra(PET_IMG_KEY);
        PhotoStorage photoStorage = new PhotoStorage();
        final PetsRepository repository = new PetsRepository();
        photoStorage.uploadFiles(petImages, petId, new PhotoStorage.Callback() {
            @Override
            public void onUploadFinish(final List<String> result) {
                repository.get(petId, new IPetsRepository.Callback<Pet>() {
                    @Override
                    public void onResult(Result<Pet> petResult) {
                        if (!petResult.isSuccess()) return;
                        Pet pet = petResult.getData();
                        pet.setImages(result);
                        repository.update(pet.getId(), pet.getData(), new IPetsRepository.Callback<Void>() {
                            @Override
                            public void onResult(Result<Void> result) {
                                notifier.cancel(PHOTO_UPLOAD_NOTIFY);
                                sendNotification(getString(R.string.uploaded_msg), NOTIFICATION_CHANNEL2_ID);
                            }
                        });
                    }
                });
            }

            @Override
            public void onPhotoUploaded(int size, int left) {
                int uploaded = size-left;
                String message = uploaded+"/"+size+"photo(s) have been uploaded";
                sendNotification(message, NOTIFICATION_CHANNEL_ID);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void sendNotification(String contentTxt, String notificationChannelId) {
        Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext(), notificationChannelId);
        notificationBuilder.setTicker("This is ticker");
        notificationBuilder.setSmallIcon(R.drawable.ic_action_name);
        notificationBuilder.setContentTitle("PetBnB photo upload");
        notificationBuilder.setContentText(contentTxt);
        notificationBuilder.setAutoCancel(true);

        Notification notification = notificationBuilder.build();
        notifier.notify(PHOTO_UPLOAD_NOTIFY, notification);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel(String notificationChannelId, boolean needVibration) {
        NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId
                , "Photo Upload Notification", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("PetBnB Photo Upload Channel");
        notificationChannel.enableVibration(false);
        if(needVibration){
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 100});
        }
        notifier.createNotificationChannel(notificationChannel); // this ables notification messages
    }

}
