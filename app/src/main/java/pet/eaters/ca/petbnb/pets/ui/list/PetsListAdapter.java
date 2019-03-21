package pet.eaters.ca.petbnb.pets.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.pets.data.Pet;

public class PetsListAdapter extends ListAdapter<Pet, PetsListAdapter.PetViewHolder> {
    @Nullable
    private OnPetClickListener petClickListener;

    public PetsListAdapter() {
        super(new PetDifCallback());
    }

    interface OnPetClickListener {
        void onPetClicked(Pet item, int position);
    }


    public void setPetClickListener(@Nullable OnPetClickListener petClickListener) {
        this.petClickListener = petClickListener;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_list_item, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        holder.bind(getItem(position), createCLickListener(getItem(position), position));
    }

    private View.OnClickListener createCLickListener(final Pet item, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (petClickListener != null) {
                    petClickListener.onPetClicked(item, position);
                }
            }
        };
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        public TextView petListItemName;
        public ImageView petListItemImage;
        public Chip petListItemType;
        public Chip petListItemAge;
        public Chip petListItemGender;
        public Chip petListItemSize;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petListItemName = itemView.findViewById(R.id.petListItemName);
            petListItemImage = itemView.findViewById(R.id.petListItemImage);
            petListItemType = itemView.findViewById(R.id.petListItemType);
            petListItemAge = itemView.findViewById(R.id.petListItemAge);
            petListItemGender = itemView.findViewById(R.id.petListItemGender);
            petListItemSize = itemView.findViewById(R.id.petListItemSize);
        }

        public void bind(Pet item, View.OnClickListener clickListener) {
            itemView.setOnClickListener(clickListener);
            //TODO fill with real data
            petListItemName.setText(item.getName());
            List<String> imageUrlList = item.getImages();
            if (imageUrlList.size() > 0) {
                Glide.with(itemView.getContext())
                        .load(imageUrlList.get(0))
                        .into(petListItemImage);
            }
//            petListItemType.setText(item.getType());
////          petListItemAge.setText(item.getAge()+" years old");
//            petListItemGender.setText(item.getGender());
//            petListItemSize.setText(item.getSize());

        }
    }

    static class PetDifCallback extends DiffUtil.ItemCallback<Pet> {

        @Override
        public boolean areItemsTheSame(@NonNull Pet oldItem, @NonNull Pet newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Pet oldItem, @NonNull Pet newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
    }
}
