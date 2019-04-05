package pet.eaters.ca.petbnb.pets.data;

import pet.eaters.ca.petbnb.core.Result;

public interface GenericRepository {

    interface Callback<T> {
        void onResult(Result<T> result);
    }
}
