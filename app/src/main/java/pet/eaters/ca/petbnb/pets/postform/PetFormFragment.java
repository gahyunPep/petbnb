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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pet.eaters.ca.petbnb.R;

public class PetFormFragment extends Fragment {

    private PetFormViewModel mViewModel;

    public static PetFormFragment newInstance() {
        return new PetFormFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View petFormLayout = inflater.inflate(R.layout.pet_form_fragment, container, false);

        Button nextBtn = petFormLayout.findViewById(R.id.nextBtn);
        final EditText nameEditTxt = petFormLayout.findViewById(R.id.nameEditTxt);
        final TextInputLayout nameInputLayout = petFormLayout.findViewById(R.id.nameTxtInputLayout);
        EditText descEditTxt = petFormLayout.findViewById(R.id.petDescEditTxt);
        final TextInputLayout descInputLayout = petFormLayout.findViewById(R.id.descTxtInputLayout);


        Spinner petTypeSpinner = petFormLayout.findViewById(R.id.petTypeSpinner);
        Spinner petAgeSpinner = petFormLayout.findViewById(R.id.petAgeSpinner);
        Spinner petSizeSpinner = petFormLayout.findViewById(R.id.petSizeSpinner);

        final List<String> petTypeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.petType_arr)));
        final List<String> ageList = getAgeArrList();
        final List<String> petSizeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.petSize_arr)));

        ArrayAdapter<String> petTypeSpinnerArrAdaptor = getArrAdapter(petFormLayout, petTypeList);
        ArrayAdapter<String> petAgeSpinnerArrAdaptor = getArrAdapter(petFormLayout, ageList);
        ArrayAdapter<String> petSizeSpinnerArrAdaptor = getArrAdapter(petFormLayout, petSizeList);

        petTypeSpinnerArrAdaptor.setDropDownViewResource(R.layout.pet_type_spinner_txtview);
        petAgeSpinnerArrAdaptor.setDropDownViewResource(R.layout.pet_type_spinner_txtview);
        petSizeSpinnerArrAdaptor.setDropDownViewResource(R.layout.pet_type_spinner_txtview);

        petTypeSpinner.setAdapter(petTypeSpinnerArrAdaptor);
        petAgeSpinner.setAdapter(petAgeSpinnerArrAdaptor);
        petSizeSpinner.setAdapter(petSizeSpinnerArrAdaptor);

        petAgeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        petTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // do something when one of them is selected
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //input validation while it's writing
        nameEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    nameInputLayout.setError(getString(R.string.str_petNameError));
                } else{
                    nameInputLayout.setErrorEnabled(false);
                }
            }
        });

        //input validation while it's writing pet description
        descEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    descInputLayout.setError(getString(R.string.str_petDescError));
                } else{
                    descInputLayout.setErrorEnabled(false);
                }
            }
        });

        // once next btn clicked check input validation
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameEditTxt.equals(null)){

                }
            }
        });

        return petFormLayout;
    }

    /**
     * A Method returning ArrAdapter
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
     * @return ageArrList
     */
    private ArrayList<String> getAgeArrList() {
        ArrayList<String> ageArrList = new ArrayList<>(32);
        ageArrList.add(getString(R.string.str_age));
        for(int i=1; i<= 30; i++){
            ageArrList.add(String.valueOf(i));
        }
        ageArrList.add(getString(R.string.str_over30));
        return ageArrList;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PetFormViewModel.class);
        // TODO: Use the ViewModel
    }

}
