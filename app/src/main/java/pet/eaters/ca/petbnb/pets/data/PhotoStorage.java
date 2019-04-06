package pet.eaters.ca.petbnb.pets.data;

import android.net.Uri;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class PhotoStorage {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference reference = storage.getReference();

    interface Callback {
        void onUploadFinish(List<String> result);

        void onPhotoUploaded(int size, int left);
    }

    public void uploadFiles(final List<String> files, String petId, final Callback callback) {
        final List<String> result = new ArrayList<>();
        final int[] filesLeft = {files.size()};
        if (files.isEmpty()) {
            callback.onUploadFinish(result);
        }

        for (String filePath : files) {
            Uri file = Uri.fromFile(new File(filePath));
            final StorageReference refToStore = reference.child(petId).child("images/" + file.getLastPathSegment());
            refToStore.putFile(file)
                    .continueWithTask(getDownloadUrl(refToStore))
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                result.add(task.getResult().toString());
                            }
                            filesLeft[0]--;

                            if (filesLeft[0] == 0) {
                                callback.onUploadFinish(result);
                            } {
                                callback.onPhotoUploaded(files.size(), filesLeft[0]);
                            }
                        }
                    });
        }
    }

    private Continuation<UploadTask.TaskSnapshot, Task<Uri>> getDownloadUrl(final StorageReference refToStore) {
        return new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return refToStore.getDownloadUrl();
            }
        };
    }
}
