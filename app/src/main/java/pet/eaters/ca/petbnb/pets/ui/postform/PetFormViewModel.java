package pet.eaters.ca.petbnb.pets.ui.postform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.ViewModel;
import pet.eaters.ca.petbnb.R;

public class PetFormViewModel extends ViewModel {
    public static final String PET_NAME ="petName";
    public static final String PET_DESC ="petDesc";
    public static final String PET_ELSE ="else";

    // TODO: Implement the ViewModel
    // this method need to be in the viewmodel

    public Map<String, Integer> validateData(String petName, String petDesc, int petType, int petAge, int petSize, int petSex) {
        Map<String, Integer> validateMap = new HashMap<>();

        if (petName.isEmpty()) {
            validateMap.put(PET_NAME, R.string.str_petNameError);
        }
        if (petDesc.isEmpty()) {
            validateMap.put(PET_DESC, R.string.str_petDescError);
        }
        if (petType == 0 || petAge == 0 || petSex == -1) {
            validateMap.put(PET_ELSE, R.string.str_incompleteError);
        }
        return validateMap;
    }

    /**
     * A method auto-generates pet age from 1-30
     *
     * @return ageArrList
     */
    public ArrayList<String> getAgeArrList(String title, String moreThan) {
        ArrayList<String> ageArrList = new ArrayList<>(32);
        ageArrList.add(title);
        for (int i = 1; i <= 30; i++) {
            ageArrList.add(String.valueOf(i));
        }
        ageArrList.add(moreThan);
        return ageArrList;
    }
}
