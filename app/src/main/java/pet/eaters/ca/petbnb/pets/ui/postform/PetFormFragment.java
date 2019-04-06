package pet.eaters.ca.petbnb.pets.ui.postform;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.pets.data.PetForm;

import static pet.eaters.ca.petbnb.pets.ui.postform.PetFormViewModel.PET_DESC;
import static pet.eaters.ca.petbnb.pets.ui.postform.PetFormViewModel.PET_ELSE;
import static pet.eaters.ca.petbnb.pets.ui.postform.PetFormViewModel.PET_NAME;

public class PetFormFragment extends Fragment {

    private PetFormViewModel mViewModel;
    private EditText nameEditTxt;
    private TextInputLayout nameInputLayout;
    private EditText descEditTxt;
    private TextInputLayout descInputLayout;

    private Spinner petAgeSpinner;

    private Toolbar toolbar;
    private TabLayout genderTabLayout;
    private TabLayout petTypeTabLayout;
    private TabLayout petSizeTabLayout;
    private PetSizeView petSize;

    public static PetFormFragment newInstance() {
        return new PetFormFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pet_form_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PetFormViewModel.class);
        // TODO: Use the ViewModel

        View view = getView();
        assert view != null;
        nameEditTxt = view.findViewById(R.id.nameEditTxt);
        nameInputLayout = view.findViewById(R.id.nameTxtInputLayout);
        descEditTxt = view.findViewById(R.id.petDescEditTxt);
        descInputLayout = view.findViewById(R.id.descTxtInputLayout);

        genderTabLayout = view.findViewById(R.id.genderTabLayout);
        petTypeTabLayout = view.findViewById(R.id.petTypeTabLayout);
        petSizeTabLayout = view.findViewById(R.id.petSizeTabLayout);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.pet_form_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getFormValues();
                return true;
            }
        });

        petSize = view.findViewById(R.id.petSizeView);
        petSize.setSizeListener(new PetSizeView.PetSizeListener() {
            @Override
            public void onPetSizeChanged(int petSize) {
                //TODO add checks
                petSizeTabLayout.getTabAt(petSize).select();
            }
        });

        genderTabLayout.addOnTabSelectedListener(new AbstractTabListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                petSize.setGender(tab.getPosition());
            }
        });

        petTypeTabLayout.addOnTabSelectedListener(new AbstractTabListener()  {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                petSize.setPetType(tab.getPosition());
            }
        });

        petSizeTabLayout.addOnTabSelectedListener(new AbstractTabListener()  {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                petSize.setPetSize(tab.getPosition());
            }
        });


        petAgeSpinner = initSpinner((Spinner) view.findViewById(R.id.petAgeSpinner), mViewModel.getAgeArrList(getString(R.string.str_age), getString(R.string.str_over30)));

        //input validation while it's writing
        nameEditTxt.addTextChangedListener(new NonEmptyTextWatcher(nameInputLayout, getString(R.string.str_petNameError)));
        descEditTxt.addTextChangedListener(new NonEmptyTextWatcher(descInputLayout, getString(R.string.str_petDescError)));
    }

    private void goBack() {
        getActivity().onBackPressed();
    }

    private void goToNextFormFragment(PetForm petForm) {
        getFragmentManager().beginTransaction()
                .replace(R.id.formFragmentContainer, PetOwnerFormFragment.newInstance(petForm))
                .addToBackStack(null)
                .commit();
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
        int petType, petAge, petSize, petGender;

        petName = nameEditTxt.getText().toString().trim();
        petDesc = descEditTxt.getText().toString().trim();


        petType = petTypeTabLayout.getSelectedTabPosition();
        petAge = petAgeSpinner.getSelectedItemPosition();
        petSize = petSizeTabLayout.getSelectedTabPosition();

        petGender = genderTabLayout.getSelectedTabPosition();

        validateData(petName, petDesc, petType, petAge, petSize, petGender);
    }

    // get a map from view model check if it's empty
    private void validateData(String petName, String petDesc, int petType, int petAge, int petSize, int petGender) {
        Map<String, Integer> errors = mViewModel.validateData(petName, petDesc, petType, petAge, petSize, petGender);
        if (errors.isEmpty()) {
            PetForm petForm = new PetForm(petName, petGender, petType, petAge, petSize, petDesc);
            goToNextFormFragment(petForm);
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
                    nameInputLayout.setError(getString(value));
                    break;
                case PET_DESC:
                    descInputLayout.setError(getString(value));
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

    static abstract class AbstractTabListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) { }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) { }

        @Override
        public void onTabReselected(TabLayout.Tab tab) { }
    }
}
