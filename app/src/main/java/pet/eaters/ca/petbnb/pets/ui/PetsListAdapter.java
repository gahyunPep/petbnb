package pet.eaters.ca.petbnb.pets.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.pets.data.Pet;

public class PetsListAdapter extends ListAdapter<Pet, PetsListAdapter.PetViewHolder> {
    public PetsListAdapter() {
        super(new PetDifCallback());
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_list_item, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class PetViewHolder extends RecyclerView.ViewHolder {
        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Pet item) {
            //TODO fill with real data
        }
    }

    static class PetDifCallback extends DiffUtil.ItemCallback<Pet> {

        @Override
        public boolean areItemsTheSame(@NonNull Pet oldItem, @NonNull Pet newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Pet oldItem, @NonNull Pet newItem) {
            return oldItem.getId() == newItem.getId();
        }
    }
}
