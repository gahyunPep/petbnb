package pet.eaters.ca.petbnb.pets.ui.list;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import pet.eaters.ca.petbnb.PetBnbApplication;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.pets.data.IPetsRepository;
import pet.eaters.ca.petbnb.pets.data.Pet;
import pet.eaters.ca.petbnb.pets.data.PetsRepository;

public class PetsListViewModel extends ViewModel  {
    private PetsRepository repository = PetBnbApplication.petsRepository;

    private MutableLiveData<Result<List<Pet>>> pets = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public PetsListViewModel() {
        loadPets();

    }

    public void retry() {
        loadPets();
    }

    public void refresh() {
        loadPets(false);
    }

    private void loadPets(boolean showLoading) {
        loading.setValue(showLoading);
        repository.getPets(new IPetsRepository.Callback<List<Pet>>() {
            @Override
            public void onResult(Result<List<Pet>> result) {
                loading.setValue(false);
                if (result.isSuccess()) {
                    if (result.getData().isEmpty()) {
                        pets.setValue(Result.<List<Pet>>failed(new IllegalStateException("Couldn't retrieve the list, try again")));
                        return;
                    }
                }

                pets.setValue(result);
            }
        });
    }

    private void loadPets() {
        loadPets(true);
    }

    public LiveData<Result<List<Pet>>> getPets() {
        return pets;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }
}
