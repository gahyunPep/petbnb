package pet.eaters.ca.petbnb.pets.ui.details;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;
import java.util.List;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;
import pet.eaters.ca.petbnb.core.QRCodeGenAndReader;
import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.pets.data.Pet;
import pet.eaters.ca.petbnb.pets.data.PetsRepository;

/**
 * A simple {@link Fragment} subclass.
 */
public class PetDetailsFragment extends Fragment {
    private static String PET_ID = "PET_ID";

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout imageSlideIndicator;
    List<String > petImages;
    private TextView petNameAge;
    private TextView petInfo;
    private TextView petSizeType;
    private TextView petPhone;
    private TextView petCity;
    private ImageView petGender;
    LiveData<Result<Pet>> pet;
    private Integer genderData;
    private String petSize;
    private Integer petSizeData;
    private ImageView QRCodeImg;


    public static PetDetailsFragment newInstance(String petId) {
        PetDetailsFragment fragment = new PetDetailsFragment();
        Bundle args = new Bundle();
        args.putString(PET_ID, petId);
        fragment.setArguments(args);

        return fragment;
    }

    public PetDetailsFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_pet_details, container, false);
        viewPager = rootView.findViewById(R.id.viewPager);

        assert getArguments() != null;
        String petId = getArguments().getString(PET_ID);

        petNameAge = rootView.findViewById(R.id.pet_name_age);
        petInfo = rootView.findViewById(R.id.pet_info);
        petSizeType = rootView.findViewById(R.id.pet_size_type);
        petPhone = rootView.findViewById(R.id.pet_phone);
        petCity = rootView.findViewById(R.id.pet_city);
        petGender = rootView.findViewById(R.id.pet_gender);
        QRCodeImg = rootView.findViewById(R.id.QRImageView);

        imageSlideIndicator = rootView.findViewById(R.id.image_slide_indicator);
        imageSlideIndicator.setupWithViewPager(viewPager, true);
        PetsRepository repository = new PetsRepository();
        pet = repository.get(petId);
        pet.observe(getViewLifecycleOwner(), new Observer<Result<Pet>>() {
            @Override
            public void onChanged(Result<Pet> petResult) {
                Pet petData = petResult.getData();
                petSizeData = petData.getSize();
                switch (petSizeData) {
                    case 1:
                        petSize = "Big";
                        break;
                    case 2:
                        petSize = "Medium";
                        break;
                    case 3:
                        petSize = "Small";
                        break;
                }
                petNameAge.setText(petData.getName() + " , " + petData.getAge());
                petInfo.setText(petData.getInfo());
                petSizeType.setText(petSize + " " + petData.getType());
                petPhone.setText(petData.getPhone());
                petCity.setText(petData.getAddress());
                genderData = petData.getGender();


                if(genderData == 0) {
                    petGender.setImageResource(R.drawable.ic_female);
                }
                else if(genderData == 1) {
                    petGender.setImageResource(R.drawable.ic_male);
                }

                petImages = petData.getImages();


                viewPagerAdapter = new ViewPagerAdapter(petImages, getContext());
                viewPager.setAdapter(viewPagerAdapter);
            }
        });

        //TDO make async and replace with size from view
        Bitmap qrCode = new QRCodeGenAndReader().generateQRCode(petId, 320, 320);
        if (qrCode != null) {
            QRCodeImg.setImageBitmap(qrCode);
        }

        return rootView;
    }
}
