package pet.eaters.ca.petbnb.pets.data;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.lifecycle.LiveData;
import pet.eaters.ca.petbnb.core.Result;

public class ScanRepository extends AbstractRepository implements IScanRepository {
    private static final String SCAN_COLLECTION ="scan";

    public ScanRepository() {
        super(SCAN_COLLECTION);
    }

    @Override
    public LiveData<Result<Void>> post(ScanRecord scanRecord) {
        final DocumentReference createdScan = collection.document();
        return executeTask(createdScan.set(scanRecord));
    }

    @Override
    public LiveData<Result<Void>> update(String scanId, ScanRecord scanRecord) {
        return executeTask(collection.document(scanId).set(scanRecord));
    }

    @Override
    public LiveData<Result<Void>> delete(String scanId) {
        return executeTask(collection.document(scanId).delete());
    }

    @Override
    public LiveData<Result<ScanRecord>> get(String scanId) {
        return executeTask(collection.document(scanId).get(), scanMapper());
    }

    @Override
    public void get(String scanId, Callback<ScanRecord> callback) {
        executeTask(collection.document(scanId).get(), scanMapper(), callback);
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
}
