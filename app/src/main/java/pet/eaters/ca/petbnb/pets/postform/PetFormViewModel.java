package pet.eaters.ca.petbnb.pets.postform;

import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.ViewModel;
import pet.eaters.ca.petbnb.R;

public class PetFormViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    // this method need to be in the viewmodel

    public Map validateData(String petName, String petDesc, int petType, int petAge, int petSize, int petSex) {
        Map<String, String> validateMap = new HashMap();

        if (petName.isEmpty()) {
            validateMap.put("petName", "pet name");
        }
        if (petDesc.isEmpty()) {
            validateMap.put("petDesc", "pet description");
        }
        if (petType == 0) {
            validateMap.put("petType", "pet type");
        }
        if (petAge == 0) {
            validateMap.put("petAge", "pet age");
        }
        if (petSize == 0) {
            validateMap.put("petSize", "pet size");
        }
        if (petSex == -1) {
            validateMap.put("petSex", "pet gender");
        }

        return validateMap;
    }
}
