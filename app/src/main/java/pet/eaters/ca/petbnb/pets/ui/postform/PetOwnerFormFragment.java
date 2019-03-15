package pet.eaters.ca.petbnb.pets.ui.postform;

import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.pets.data.PetForm;
import pet.eaters.ca.petbnb.pets.data.PetOwnerForm;

import static pet.eaters.ca.petbnb.pets.ui.postform.PetOwnerFormViewModel.OWNER_ADDRESS;
import static pet.eaters.ca.petbnb.pets.ui.postform.PetOwnerFormViewModel.OWNER_CITY;
import static pet.eaters.ca.petbnb.pets.ui.postform.PetOwnerFormViewModel.OWNER_ELSE;
import static pet.eaters.ca.petbnb.pets.ui.postform.PetOwnerFormViewModel.OWNER_EMAIL;
import static pet.eaters.ca.petbnb.pets.ui.postform.PetOwnerFormViewModel.OWNER_NAME;
import static pet.eaters.ca.petbnb.pets.ui.postform.PetOwnerFormViewModel.OWNER_PHONE;
import static pet.eaters.ca.petbnb.pets.ui.postform.PetOwnerFormViewModel.OWNER_ZIPCODE;

public class PetOwnerFormFragment extends Fragment {

    private PetOwnerFormViewModel mViewModel;
    private TextInputLayout nameInputLayout, addressInputLayout, cityInputLayout;
    private TextInputLayout zipcodeInputLayout, emailInputLayout, phoneInputLayout;
    private EditText nameEditText, addressEditText, cityEditText, zipcodeEditText, emailEditText, phoneEditText;
    private Spinner provinceSpinner;
    private Button nextButton;

    private PetForm petForm;

    private static final String PET_FORM_KEY = "petForm";

    public static PetOwnerFormFragment newInstance(PetForm petForm) {
        PetOwnerFormFragment fragment = new PetOwnerFormFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(PET_FORM_KEY, petForm);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            petForm = getArguments().getParcelable(PET_FORM_KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pet_owner_form_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PetOwnerFormViewModel.class);
        // TODO: Use the ViewModel

        View view = getView();
        assert view != null;
        nameInputLayout = view.findViewById(R.id.nameTxtInputLayout);
        addressInputLayout = view.findViewById(R.id.addressTxtInputLayout);
        cityInputLayout = view.findViewById(R.id.cityTxtInputLayout);
        zipcodeInputLayout = view.findViewById(R.id.zipcodeTxtInputLayout);
        emailInputLayout = view.findViewById(R.id.emailTxtInputLayout);
        phoneInputLayout = view.findViewById(R.id.phoneTxtInputLayout);

        nameEditText = view.findViewById(R.id.nameEditTxt);
        addressEditText = view.findViewById(R.id.addressEditTxt);
        cityEditText = view.findViewById(R.id.cityEditTxt);
        zipcodeEditText = view.findViewById(R.id.zipcodeEditTxt);
        emailEditText = view.findViewById(R.id.emailEditTxt);
        phoneEditText = view.findViewById(R.id.phoneEditTxt);

        provinceSpinner = initSpinner((Spinner) view.findViewById(R.id.provinceSpinner), getListFromResources(R.array.province_arr));

        nextButton = view.findViewById(R.id.formNextBtn);

        //input validation while it's writing
        nameEditText.addTextChangedListener(new NonEmptyTextWatcher(nameInputLayout,getString(R.string.str_ownerNameError)));
        addressEditText.addTextChangedListener(new NonEmptyTextWatcher(addressInputLayout, getString(R.string.str_ownerAddressError)));
        cityEditText.addTextChangedListener(new NonEmptyTextWatcher(cityInputLayout, getString(R.string.str_ownerCityError)));
        zipcodeEditText.addTextChangedListener(new NonEmptyTextWatcher(zipcodeInputLayout, getString(R.string.str_ownerZipcodeError)));
        emailEditText.addTextChangedListener(new NonEmptyTextWatcher(emailInputLayout, getString(R.string.str_ownerEmailError)));
        phoneEditText.addTextChangedListener(new NonEmptyTextWatcher(phoneInputLayout, getString(R.string.str_ownerPhoneError)));
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("+1")){
                    s.append("+1", 0, 2);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFormValues();
            }
        });
    }

    private void goToNextFormFragment(PetForm petForm, PetOwnerForm petOwnerForm) {
        getFragmentManager().beginTransaction()
                .replace(R.id.formFragmentContainer, PhotoUploadFragment.newInstance(petForm, petOwnerForm))
                .addToBackStack(null)
                .commit();
    }

    private void getFormValues() {
        // name address city province zipcode email phone
        String name, address, city, zipcode, email, phone;
        int province;

        name = nameEditText.getText().toString().trim();
        address = addressEditText.getText().toString().trim();
        city = cityEditText.getText().toString().trim();
        zipcode = zipcodeEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        phone = phoneEditText.getText().toString().trim();;
        province = provinceSpinner.getSelectedItemPosition();
        
        validateData(name, address, city, zipcode, email, phone,province);
    }

    private void validateData(String name, String address, String city, String zipcode, String email, String phone, int province) {
        Map<String, Integer> errors = mViewModel.validateData(name, address, city, zipcode, email, phone, province);
        if(errors.isEmpty()){
            PetOwnerForm petOwnerForm = new PetOwnerForm(name, address, city, province, zipcode, email, phone);
            goToNextFormFragment(petForm, petOwnerForm);
        }else{
            bindErrors(errors);
        }
    }

    private void bindErrors(Map<String, Integer> validationMap) {
        for(Map.Entry<String, Integer> entry :  validationMap.entrySet()){
            String key = entry.getKey();
            Integer value = entry.getValue();
            switch(key){
                case OWNER_NAME:
                    nameInputLayout.setError(getString(value));
                    break;
                case OWNER_ADDRESS:
                    addressInputLayout.setError(getString(value));
                    break;
                case OWNER_CITY:
                    cityInputLayout.setError(getString(value));
                    break;
                case OWNER_ZIPCODE:
                    zipcodeInputLayout.setError(getString(value));
                    break;
                case OWNER_EMAIL:
                    emailInputLayout.setError(getString(value));
                    break;
                case OWNER_PHONE:
                    phoneInputLayout.setError(getString(value));
                    break;
                case OWNER_ELSE:
                    Snackbar.make(getView(),getString(value),Snackbar.LENGTH_LONG).show();
                    break;
            }
        }

    }

    private Spinner initSpinner(Spinner spinner, List<String> list) {
        ArrayAdapter<String> spinnerArrAdaptor = getArrAdapter(list);
        spinnerArrAdaptor.setDropDownViewResource(R.layout.pet_form_spinner_txtview);
        spinner.setAdapter(spinnerArrAdaptor);
        return spinner;
    }

    private ArrayList<String> getListFromResources(int arr) {
        return new ArrayList<>(Arrays.asList(getResources().getStringArray(arr)));
    }

    /**
     * A Method returning ArrAdapter
     *
     * @param dataList
     * @return ArrayAdapter
     */
    private ArrayAdapter<String> getArrAdapter(List<String> dataList) {
        return new ArrayAdapter<String>(getContext(), R.layout.pet_form_spinner_txtview, dataList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                if (isEnabled(position)) {
                    textView.setTextColor(Color.BLACK);
                } else {
                    textView.setTextColor(Color.GRAY);
                }
                return textView;
            }
        };
    }
}
