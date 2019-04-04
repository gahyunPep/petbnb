package pet.eaters.ca.petbnb.pets.ui.details;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.core.FragmentUtils;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.pets.data.Pet;

/**
 * A simple {@link Fragment} subclass.
 */
public class PetDetailsFragment extends Fragment {
    private static String PET_ID = "PET_ID";

    private PetDetailsViewModel viewModel;

    private ViewPager viewPager;
    private TextView petNameAge;
    private TextView petInfo;
    private TextView petSizeType;
    private TextView petPhone;
    private TextView petCity;
    private ImageView qrCodeImg;
    private Toolbar toolbar;


    public static PetDetailsFragment newInstance(String petId) {
        PetDetailsFragment fragment = new PetDetailsFragment();
        Bundle args = new Bundle();
        args.putString(PET_ID, petId);
        fragment.setArguments(args);

        return fragment;
    }

    public PetDetailsFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pet_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.viewPager);
        petNameAge = view.findViewById(R.id.pet_name_age);
        petInfo = view.findViewById(R.id.pet_info);
        petSizeType = view.findViewById(R.id.pet_size_type);
        petPhone = view.findViewById(R.id.pet_phone);
        petCity = view.findViewById(R.id.pet_city);
        qrCodeImg = view.findViewById(R.id.QRImageView);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        view.<TabLayout>findViewById(R.id.image_slide_indicator).setupWithViewPager(viewPager, true);

        viewModel = ViewModelProviders.of(this, new PetDetailsViewModel.Factory(getPetId())).get(PetDetailsViewModel.class);
        observePet();
        observeQrCode();
    }

    private void observePet() {
        viewModel.getPet().observe(getViewLifecycleOwner(), new Observer<Result<Pet>>() {
            @Override
            public void onChanged(Result<Pet> result) {
                if (result.isSuccess()) {
                    showPet(result.getData());
                } else {
                    showError(result.getException());
                }
            }
        });

    }

    private void observeQrCode() {
        viewModel.getQrCode(gerQrCodeSize()).observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                if (bitmap == null) return;

                qrCodeImg.setImageBitmap(bitmap);
            }
        });
    }

    private String getPetId() {
        assert getArguments() != null;
        return getArguments().getString(PET_ID);
    }

    @SuppressLint("DefaultLocale")
    private void showPet(Pet pet) {
        toolbar.setTitle(pet.getName());


        petNameAge.setText(String.format("%s, %d", pet.getName(), pet.getAge()));
        petNameAge.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, getGenderIcon(pet.getGender()), 0);


        petInfo.setText(pet.getInfo());
        petSizeType.setText(String.format("%s %s", petSizeToString(pet.getSize()), pet.getType()));
        petPhone.setText(pet.getPhone());
        petCity.setText(pet.getAddress());

        viewPager.setAdapter(new ViewPagerAdapter(pet.getImages()));
    }

    private void showError(Exception exception) {
        FragmentUtils.showError(this, exception);
    }

    private void goBack() {
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    private String petSizeToString(int size) {
        String[] sizes = getResources().getStringArray(R.array.petSize_arr);
        int index = Math.min(Math.max(0, size), sizes.length - 1);
        return sizes[index];
    }

    @DrawableRes
    private int getGenderIcon(int gender) {
        if (gender == 1) {
            return R.drawable.ic_male;
        }

        return R.drawable.ic_female;
    }

    private int gerQrCodeSize() {
        return getResources().getDimensionPixelOffset(R.dimen.qr_code_size);
    }
}
