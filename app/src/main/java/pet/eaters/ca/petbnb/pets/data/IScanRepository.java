package pet.eaters.ca.petbnb.pets.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import pet.eaters.ca.petbnb.core.Result;

public interface IScanRepository {
    LiveData<Result<Void>> post(ScanRecord scanData);

    LiveData<Result<Void>> update(String scanId, ScanRecord scanData);

    LiveData<Result<Void>> delete(String scanId);

    LiveData<Result<ScanRecord>> get(String scanId);
}
