package pet.eaters.ca.petbnb.pets.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import pet.eaters.ca.petbnb.core.Result;

public interface IPetsRepository {
    LiveData<Result<String>> post(PetData pet);

    LiveData<Result<Void>> update(String petId, PetData petData);

    void update(String petId, PetData petData, UpdateCallback callback);

    LiveData<Result<Void>> delete(String petId);

    LiveData<Result<Pet>> get(String petId);

    void get(String petId, GetCallback callback);

    LiveData<Result<List<Pet>>> getPets();

    interface UpdateCallback {
        void onUpdated();
    }

    interface GetCallback {
        void onGet(Pet pet);
    }
}

