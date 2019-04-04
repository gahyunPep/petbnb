package pet.eaters.ca.petbnb.pets.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.core.android.FragmentUtils;
import pet.eaters.ca.petbnb.core.android.NavigationFragment;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.pets.data.Pet;
import pet.eaters.ca.petbnb.pets.ui.details.PetDetailsFragment;
import pet.eaters.ca.petbnb.pets.ui.maps.MapFragment;

public class PetsListFragment extends NavigationFragment implements PetsListAdapter.OnPetClickListener {

    private PetsListViewModel mViewModel;
    private RecyclerView petsRecyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static PetsListFragment newInstance() {
        return new PetsListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pets_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        petsRecyclerView = view.findViewById(R.id.petsRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeContainer);
        progressBar = view.findViewById(R.id.pets_list_progress_bar);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.refresh();
            }
        });
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.pet_list_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.toolbar_map) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, MapFragment.newInstance())
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PetsListViewModel.class);
        final PetsListAdapter adapter = new PetsListAdapter();
        adapter.setPetClickListener(this);
        petsRecyclerView.setAdapter(adapter);

        mViewModel.getPets().observe(getViewLifecycleOwner(), new Observer<Result<List<Pet>>>() {
            @Override
            public void onChanged(Result<List<Pet>> listResult) {
                if (listResult.isSuccess()) {
                    showList(listResult.getData(), adapter);
                } else {
                    showError(listResult.getException());
                }
            }
        });

        mViewModel.getLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showList(List<Pet> data, PetsListAdapter adapter) {
        adapter.submitList(data);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showError(Exception exception) {
        FragmentUtils.showError(this, exception, R.string.retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.retry();
            }
        });
    }

    @Override
    public void onPetClicked(Pet item, int position) {
        if (getFragmentManager() == null) {
            return;
        }
        getFragmentManager().beginTransaction()
                .add(R.id.content_frame, PetDetailsFragment.newInstance(item.getId()))
                .addToBackStack(null)
                .commit();
    }
}
