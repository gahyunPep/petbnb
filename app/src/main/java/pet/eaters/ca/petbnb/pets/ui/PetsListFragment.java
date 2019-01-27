package pet.eaters.ca.petbnb.pets.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.pets.data.Pet;
import pet.eaters.ca.petbnb.pets.data.PetsRepository;

public class PetsListFragment extends Fragment {

    private PetsListViewModel mViewModel;
    private RecyclerView petsRecyclerView;
    PetsListAdapter adapter;
    LiveData<Result<List<Pet>>> pets;

    public static PetsListFragment newInstance() {
        return new PetsListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pets_list_fragment, container, false);
        petsRecyclerView = view.findViewById(R.id.petsRecyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PetsListViewModel.class);
        adapter = new PetsListAdapter();
        petsRecyclerView.setAdapter(adapter);

        //TODO move live data and repository to the ViewModel
        PetsRepository repository = new PetsRepository();
        pets = repository.getPets();
        pets.observe(getViewLifecycleOwner(), new Observer<Result<List<Pet>>>() {
            @Override
            public void onChanged(Result<List<Pet>> listResult) {
                List<Pet> data = listResult.getData();
                if (data != null) {
                    adapter.submitList(data);
                } else {
                    //TODO error handling
                }
            }
        });
        // TODO: Use the ViewModel
    }
}
