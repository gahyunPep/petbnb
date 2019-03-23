package pet.eaters.ca.petbnb.pets.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.pets.data.Pet;
import pet.eaters.ca.petbnb.pets.data.PetsRepository;
import pet.eaters.ca.petbnb.pets.ui.details.PetDetailsFragment;

public class PetsListFragment extends Fragment implements PetsListAdapter.OnPetClickListener {

    private PetsListViewModel mViewModel;
    private RecyclerView petsRecyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
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
        swipeRefreshLayout = view.findViewById(R.id.swipeContainer);
        progressBar = view.findViewById(R.id.pets_list_progress_bar);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllPets();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PetsListViewModel.class);
        adapter = new PetsListAdapter();
        adapter.setPetClickListener(this);
        petsRecyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        getAllPets();
    }

    @Override
    public void onPetClicked(Pet item, int position) {
        PetDetailsFragment fragment = PetDetailsFragment.newInstance(item.getId());
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
    }

    public void getAllPets() {
        //TODO move live data and repository to the ViewModel
        PetsRepository repository = new PetsRepository();
        pets = repository.getPets();

        pets.observe(getViewLifecycleOwner(), new Observer<Result<List<Pet>>>() {
            @Override
            public void onChanged(Result<List<Pet>> listResult) {
                List<Pet> data = listResult.getData();
                if (data != null) {
                    adapter.submitList(data);
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                } else {
                    //TODO error handling
                }
            }
        });
        // TODO: Use the ViewModel
    }
}
