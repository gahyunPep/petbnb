package pet.eaters.ca.petbnb.pets.ui.postform;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import pet.eaters.ca.petbnb.R;

import static pet.eaters.ca.petbnb.pets.ui.postform.PetFormViewModel.PET_DESC;
import static pet.eaters.ca.petbnb.pets.ui.postform.PetFormViewModel.PET_ELSE;
import static pet.eaters.ca.petbnb.pets.ui.postform.PetFormViewModel.PET_NAME;

public class PetFormFragment extends Fragment {

    private PetFormViewModel mViewModel;
    private EditText nameEditTxt;
    private TextInputLayout nameInputLayout;
    private EditText descEditTxt;
    private TextInputLayout descInputLayout;
    private RadioButton femaleBtn;
    private RadioButton maleBtn;

    private Spinner petTypeSpinner;
    private Spinner petAgeSpinner;
    private Spinner petSizeSpinner;

    private Button nextButton;

    public static PetFormFragment newInstance() {
        return new PetFormFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //https://youtu.be/CVME9yW54mY : Click next bring in another fragment
        View view = inflater.inflate(R.layout.pet_form_fragment, container, false);
        nextButton = view.findViewById(R.id.petFormNextBtn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PetOwnerFormFragment ownerFormFragment = new PetOwnerFormFragment();
                FragmentManager manager = getFragmentManager();
//                manager.beginTransaction()
//                        .replace(v.getParent().getId(), ownerFormFragment, ownerFormFragment.getTag())
//                        .commit();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PetFormViewModel.class);
        // TODO: Use the ViewModel

        View view = getView();
        assert view != null;
        Button nextBtn = view.findViewById(R.id.petFormNextBtn);
        nameEditTxt = view.findViewById(R.id.nameEditTxt);
        nameInputLayout = view.findViewById(R.id.nameTxtInputLayout);
        descEditTxt = view.findViewById(R.id.petDescEditTxt);
        descInputLayout = view.findViewById(R.id.descTxtInputLayout);
        femaleBtn = view.findViewById(R.id.femaleRadioBtn);
        maleBtn = view.findViewById(R.id.maleRadioBtn);

        petTypeSpinner = initSpinner((Spinner) view.findViewById(R.id.petTypeSpinner), getListFromResources(R.array.petType_arr));
        petAgeSpinner = initSpinner((Spinner) view.findViewById(R.id.petAgeSpinner), mViewModel.getAgeArrList(getString(R.string.str_age), getString(R.string.str_over30)));
        petSizeSpinner = initSpinner((Spinner) view.findViewById(R.id.petSizeSpinner), getListFromResources(R.array.petSize_arr));

        //input validation while it's writing
        nameEditTxt.addTextChangedListener(new NonEmptyTextWatcher(nameInputLayout, getString(R.string.str_petNameError)));
        descEditTxt.addTextChangedListener(new NonEmptyTextWatcher(descInputLayout, getString(R.string.str_petDescError)));

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFormValues();
            }
        });
    }

    private ArrayList<String> getListFromResources(int arr) {
        return new ArrayList<>(Arrays.asList(getResources().getStringArray(arr)));
    }

    private Spinner initSpinner(Spinner spinner, List<String> list) {
        ArrayAdapter<String> spinnerArrAdaptor = getArrAdapter(list);
        spinnerArrAdaptor.setDropDownViewResource(R.layout.pet_form_spinner_txtview);
        spinner.setAdapter(spinnerArrAdaptor);
        return spinner;
    }


    /**
     * A method gets values from the form and pass them to validation function
     */
    private void getFormValues() {
        String petName, petDesc;
        int petType, petAge, petSize, petSex;

        petName = nameEditTxt.getText().toString();
        petDesc = descEditTxt.getText().toString();
        petType = petTypeSpinner.getSelectedItemPosition();
        petAge = petAgeSpinner.getSelectedItemPosition();
        petSize = petSizeSpinner.getSelectedItemPosition();

        if (femaleBtn.isChecked()) {
            petSex = 0;
        } else if (maleBtn.isChecked()) {
            petSex = 1;
        } else {
            petSex = -1;
        }

        validateData(petName, petDesc, petType, petAge, petSize, petSex);
    }

    // get a map from view model check if it's empty
    private void validateData(String petName, String petDesc, int petType, int petAge, int petSize, int petSex) {
        Map<String, Integer> errors = mViewModel.validateData(petName, petDesc, petType, petAge, petSize, petSex);
        if (errors.isEmpty()) {
            //TODO go to next screen
        } else {
            bindErrors(errors);
        }
    }

    /**
     * A method to binding name and errors
     *
     * @param validationMap
     */
    private void bindErrors(Map<String, Integer> validationMap) {
        for (Map.Entry<String, Integer> entry : validationMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            switch (key) {
                case PET_NAME:
                    descInputLayout.setError(getString(value));
                    break;
                case PET_DESC:
                    nameInputLayout.setError(getString(value));
                    break;
                case PET_ELSE:
                    Snackbar.make(getView(), value, Snackbar.LENGTH_LONG).show();
                    break;
            }
        }
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
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
    }
    
}
