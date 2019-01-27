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

    public LiveData<List<String>> uploadFiles(List<String> files, String petId) {
        final MutableLiveData<List<String>> resultLD = new MutableLiveData<>();
        final List<String> result = new ArrayList<>();
        final int[] filesCount = {files.size()};
        if (files.isEmpty()) {
            resultLD.postValue(result);
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
                            filesCount[0]--;

                            if (filesCount[0] == 0) {
                                resultLD.postValue(result);
                            }
                        }
                    });
        }

        return resultLD;
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
