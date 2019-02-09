package pet.eaters.ca.petbnb.pets.postform;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pet.eaters.ca.petbnb.R;

public class PetFormFragment extends Fragment {

    private PetFormViewModel mViewModel;
    EditText nameEditTxt;
    TextInputLayout nameInputLayout;
    EditText descEditTxt;
    TextInputLayout descInputLayout;
    RadioButton femaleBtn;
    RadioButton maleBtn;

    Spinner petTypeSpinner;
    Spinner petAgeSpinner;
    Spinner petSizeSpinner;

    public static PetFormFragment newInstance() {
        return new PetFormFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.pet_form_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PetFormViewModel.class);
        // TODO: Use the ViewModel

        Button nextBtn = getView().findViewById(R.id.nextBtn);
        nameEditTxt = getView().findViewById(R.id.nameEditTxt);
        nameInputLayout = getView().findViewById(R.id.nameTxtInputLayout);
        descEditTxt = getView().findViewById(R.id.petDescEditTxt);
        descInputLayout = getView().findViewById(R.id.descTxtInputLayout);
        femaleBtn = getView().findViewById(R.id.femaleRadioBtn);
        maleBtn = getView().findViewById(R.id.maleRadioBtn);

        petTypeSpinner = getView().findViewById(R.id.petTypeSpinner);
        petAgeSpinner = getView().findViewById(R.id.petAgeSpinner);
        petSizeSpinner = getView().findViewById(R.id.petSizeSpinner);

        List<String> petTypeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.petType_arr)));
        List<String> ageList = getAgeArrList();
        List<String> petSizeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.petSize_arr)));

        ArrayAdapter<String> petTypeSpinnerArrAdaptor = getArrAdapter(getView(), petTypeList);
        ArrayAdapter<String> petAgeSpinnerArrAdaptor = getArrAdapter(getView(), ageList);
        ArrayAdapter<String> petSizeSpinnerArrAdaptor = getArrAdapter(getView(), petSizeList);

        petTypeSpinnerArrAdaptor.setDropDownViewResource(R.layout.pet_type_spinner_txtview);
        petAgeSpinnerArrAdaptor.setDropDownViewResource(R.layout.pet_type_spinner_txtview);
        petSizeSpinnerArrAdaptor.setDropDownViewResource(R.layout.pet_type_spinner_txtview);

        petTypeSpinner.setAdapter(petTypeSpinnerArrAdaptor);
        petAgeSpinner.setAdapter(petAgeSpinnerArrAdaptor);
        petSizeSpinner.setAdapter(petSizeSpinnerArrAdaptor);

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

        mViewModel.validateData(petName, petDesc, petType, petAge, petSize, petSex);
    }

    // this method need to be in the viewmodel
    // get a map from view model check if it's empty
    private void validateData(String petName, String petDesc, int petType, int petAge, int petSize, int petSex) {
        if (petName.isEmpty()) {
            descInputLayout.setError(getString(R.string.str_petDescError));
        }
        if (petDesc.isEmpty()) {
            nameInputLayout.setError(getString(R.string.str_petDescError));
        }
        if (petName.isEmpty() || petDesc.isEmpty() || petType == 0 || petAge == 0 || petSize == 0
                || ((petSex != 0) && (petSex != 1))) {
            Snackbar.make(getView(), getString(R.string.str_incompleteError), Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * A Method returning ArrAdapter
     *
     * @param petFormLayout
     * @param dataList
     * @return ArrayAdapter
     */
    private ArrayAdapter<String> getArrAdapter(View petFormLayout, List<String> dataList) {
        return new ArrayAdapter<String>(petFormLayout.getContext(), R.layout.pet_type_spinner_txtview, dataList) {
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

    /**
     * A method auto-generates pet age from 1-30
     *
     * @return ageArrList
     */
    private ArrayList<String> getAgeArrList() {
        ArrayList<String> ageArrList = new ArrayList<>(32);
        ageArrList.add(getString(R.string.str_age));
        for (int i = 1; i <= 30; i++) {
            ageArrList.add(String.valueOf(i));
        }
        ageArrList.add(getString(R.string.str_over30));
        return ageArrList;
    }

}
