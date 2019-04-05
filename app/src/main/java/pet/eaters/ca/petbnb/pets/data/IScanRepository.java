package pet.eaters.ca.petbnb.pets.data;

import androidx.lifecycle.LiveData;
import pet.eaters.ca.petbnb.core.Result;

public interface IScanRepository extends GenericRepository {
    LiveData<Result<Void>> post(ScanRecord scanData);

    LiveData<Result<Void>> update(String scanId, ScanRecord scanData);

    LiveData<Result<Void>> delete(String scanId);

    LiveData<Result<ScanRecord>> get(String scanId);

    void get(String scanId, Callback<ScanRecord> callback);
}
