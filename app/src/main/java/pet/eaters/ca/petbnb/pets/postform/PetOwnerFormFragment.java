package pet.eaters.ca.petbnb.pets.postform;

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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pet.eaters.ca.petbnb.R;

public class PetOwnerFormFragment extends Fragment {

    private PetOwnerFormViewModel mViewModel;
    private TextInputLayout nameInputLayout, addressInputLayout, cityInputLayout;
    private TextInputLayout zipcodeInputLayout, emailInputLayout, phoneInputLayout;
    private EditText nameEditText, addressEditText, cityEditText, zipcodeEditText, emailEditText, phoneEditText;
    private Spinner provinceSpinner;
    private Button nextButton;

    public static PetOwnerFormFragment newInstance() {
        return new PetOwnerFormFragment();
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

        nextButton = view.findViewById(R.id.nextBtn);

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
