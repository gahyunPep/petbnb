package pet.eaters.ca.petbnb.pets.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import pet.eaters.ca.petbnb.core.Result;

public interface IPetsRepository extends GenericRepository {
    LiveData<Result<Void>> post(PetData pet);

    LiveData<Result<Void>> update(String petId, PetData petData);

    LiveData<Result<Void>> delete(String petId);

    LiveData<Result<Pet>> get(String petId);

    LiveData<Result<List<Pet>>> getPets();

    void getPets(Callback<List<Pet>> result);
}


