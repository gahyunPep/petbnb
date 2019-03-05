package pet.eaters.ca.petbnb.pets.postform;

import android.util.Patterns;

import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.ViewModel;
import pet.eaters.ca.petbnb.R;


public class PetOwnerFormViewModel extends ViewModel {

    public static final String OWNER_NAME = "name";
    public static final String OWNER_ADDRESS = "address";
    public static final String OWNER_CITY = "city";
    public static final String OWNER_ZIPCODE = "zipcode";
    public static final String OWNER_EMAIL = "email";
    public static final String OWNER_PHONE = "phone";
    public static final String OWNER_ELSE = "else";

    public Map<String, Integer> validateData(String name, String address, String city, String zipcode, String email, String phone, int province) {
        Map<String, Integer> validateMap = new HashMap<>();
        boolean isPhoneValid = Patterns.PHONE.matcher(phone).matches();
        boolean isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if(name.isEmpty()){
            validateMap.put(OWNER_NAME, R.string.str_ownerNameError);
        }
        if(address.isEmpty()){
            validateMap.put(OWNER_ADDRESS, R.string.str_ownerAddressError);
        }
        if(city.isEmpty()){
            validateMap.put(OWNER_CITY, R.string.str_ownerCityError);
        }
        if(zipcode.isEmpty()){
            validateMap.put(OWNER_ZIPCODE, R.string.str_ownerZipcodeError);
        }
        if(email.isEmpty() || !isEmailValid){
            validateMap.put(OWNER_EMAIL, R.string.str_ownerEmailError);
        }
        if(phone.isEmpty() || !isPhoneValid || (phone.length()<12 || phone.length()>=13) || !phone.contains("+")){
            validateMap.put(OWNER_PHONE, R.string.str_ownerPhoneError);
        }

        if(province==0){
            validateMap.put(OWNER_ELSE, R.string.str_incompleteError);
        }
        return validateMap;
    }

}
