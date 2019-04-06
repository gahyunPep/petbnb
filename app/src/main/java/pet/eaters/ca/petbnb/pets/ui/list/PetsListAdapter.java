package pet.eaters.ca.petbnb.pets.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;

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
        TextView petListItemName;
        ImageView petListItemImage;
        Chip petListItemType;
        Chip petListItemAge;
        Chip petListItemGender;
        Chip petListItemSize;
        String[] petSizeArray = itemView.getContext().getResources().getStringArray(R.array.petSize_arr);


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
            petListItemName.setText(item.getName());
            petListItemType.setText(getPetType(item.getType()));
            petListItemAge.setText(petListItemAge.getContext().getString(R.string.yeas_old_fmt, item.getAge()));
            bindImage(item);
            bindGender(item);
            bindSize(item);
        }

        private void bindImage(Pet item) {
            petListItemImage.setImageResource(R.drawable.ic_image_black_24dp);
            petListItemImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (item.getImages().size() <= 0) {
                return;
            }

            Glide.with(petListItemImage.getContext())
                    .load(item.getImages().get(0))
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .centerCrop()
                    .into(petListItemImage);
        }

        private void bindSize(Pet item) {
            petListItemSize.setText(petSizeArray[item.getSize()]);
        }

        private void bindGender(Pet item) {
            int color = 0;
            int gender = 0;

            switch (item.getGender()) {
                case 0:
                    gender = R.string.female;
                    color = R.color.female_color;
                    break;
                case 1:
                    gender = R.string.male;
                    color = R.color.male_color;
                    break;
            }

            if (color != 0 && gender != 0) {
                petListItemGender.setVisibility(View.VISIBLE);
                petListItemGender.setChipBackgroundColorResource(color);
                petListItemGender.setText(gender);
            } else {
                petListItemGender.setVisibility(View.GONE);
            }
        }

        private String getPetType(String type) {
            switch (type) {
                case "1":
                    return getString(R.string.dog);
                case "2":
                    return getString(R.string.cat);
                case "3":
                default:
                    return getString(R.string.str_otherTypePet);
            }
        }

        private String getString(int id) {
            return itemView.getContext().getString(id);
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
