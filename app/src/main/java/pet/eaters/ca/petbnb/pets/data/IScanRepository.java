package pet.eaters.ca.petbnb.pets.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import pet.eaters.ca.petbnb.core.Result;

public interface IScanRepository {
    LiveData<Result<Void>> post(String scanId, ScanData scanData);

    LiveData<Result<Void>> update(String scanId, ScanData scanData);

    LiveData<Result<Void>> delete(String scanId);

    LiveData<Result<ScanRecord>> get(String scanId);
}
