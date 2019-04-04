package pet.eaters.ca.petbnb.pets.data;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import pet.eaters.ca.petbnb.core.Result;

public class ScanRepository implements IScanRepository {
    private static final String TAG = "ScanRepository";
    private static final String SCAN_COLLECTION ="scan";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference scan = db.collection(SCAN_COLLECTION);

    @Override
    public LiveData<Result<Void>> post(ScanRecord scanRecord) {
        final DocumentReference createdScan = scan.document();
        return executeTask(createdScan.set(scanRecord));
    }

    @Override
    public LiveData<Result<Void>> update(String scanId, ScanRecord scanRecord) {
        return executeTask(scan.document(scanId).set(scanRecord));
    }

    @Override
    public LiveData<Result<Void>> delete(String scanId) {
        return executeTask(scan.document(scanId).delete());
    }

    @Override
    public LiveData<Result<ScanRecord>> get(String scanId) {
        return executeTask(scan.document(scanId).get(), scanMapper());
    }

    private interface Mapper<F, T> {
        T map(F object);
    }

    private Mapper<DocumentSnapshot, ScanRecord> scanMapper() {
        return new Mapper<DocumentSnapshot, ScanRecord>() {
            @Override
            public ScanRecord map(DocumentSnapshot document) {
                return scanRecordFromDocument(document);
            }
        };
    }

    private ScanRecord scanRecordFromDocument(DocumentSnapshot document) {
        return document.toObject(ScanRecord.class);
    }

    private <T> Mapper<T, T> transparentMapper() {
        return new Mapper<T, T>() {
            @Override
            public T map(T document) {
                return document;
            }
        };
    }

    private <F, T> LiveData<Result<T>> executeTask(Task<F> task, Mapper<F, T> mapper) {
        final MutableLiveData<Result<T>> result = new MutableLiveData<>();
        task.addOnSuccessListener(successListener(result, mapper))
                .addOnFailureListener(failureListener(result));

        return result;
    }

    private <T> LiveData<Result<T>> executeTask(Task<T> task) {
        return executeTask(task, this.<T>transparentMapper());
    }

    private <F, T> OnSuccessListener<F> successListener(final MutableLiveData<Result<T>> result, final Mapper<F, T> mapper) {
        return new OnSuccessListener<F>() {
            @Override
            public void onSuccess(F o) {
                result.postValue(Result.success(mapper.map(o)));
            }
        };
    }

    private <T> OnFailureListener failureListener(final MutableLiveData<Result<T>> result) {
        return new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                result.postValue(Result.<T>failed(e));
            }
        };
    }


}
