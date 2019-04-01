package pet.eaters.ca.petbnb.pets.ui.maps;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.pets.data.Pet;
import pet.eaters.ca.petbnb.pets.ui.list.PetsListViewModel;

public class MapViewModel extends ViewModel {
    private PetsListViewModel listViewModel = new PetsListViewModel();
    private boolean isNeedToAskPermission = true;
    private boolean isPermissionGranted = false;

    public MapViewModel() {
    }

    public void retry() {
        listViewModel.retry();
    }

    public void refresh() {
        listViewModel.refresh();
    }

    public LiveData<Result<List<Pet>>> getPets() {
        return listViewModel.getPets();
    }

    public void mapLoaded() {
        refresh();
    }

    public boolean isNeedToAskPermission() {
        if (isNeedToAskPermission) {
            isNeedToAskPermission = false;
            return true;
        }

        return false;
    }


    public void permissionGranted() {
        isPermissionGranted = true;
    }

    public boolean isPermissionGranted() {
        return isPermissionGranted;
    }
}
