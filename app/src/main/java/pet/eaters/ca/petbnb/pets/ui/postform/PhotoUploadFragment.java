package pet.eaters.ca.petbnb.pets.ui.postform;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.pets.data.PetData;
import pet.eaters.ca.petbnb.pets.data.PetForm;
import pet.eaters.ca.petbnb.pets.data.PetOwnerForm;
import pet.eaters.ca.petbnb.pets.data.PetsRepository;
import pet.eaters.ca.petbnb.pets.data.PhotoStorage;

import static android.app.Activity.RESULT_OK;

public class PhotoUploadFragment extends Fragment {

    private PhotoUploadViewModel mViewModel;
    private Button addCameraBtn;
    private Button addGalleryBtn;
    private Button uploadBtn;

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_LOAD_GALLERY = 2;
    private PetForm petForm;
    private PetOwnerForm petOwnerForm;

    private static final String PET_FORM_KEY = "petForm";
    private static final String PET_OWENER_FORM_KEY = "petOwnerForm";

    List<Bitmap> imageList;
    List<String> imagePathList;
    PhotoUploadAdapter photoUploadAdapter;


    public static PhotoUploadFragment newInstance(PetForm petForm, PetOwnerForm petOwnerForm) {
        PhotoUploadFragment photoUploadFragment = new PhotoUploadFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(PET_FORM_KEY, petForm);
        arguments.putParcelable(PET_OWENER_FORM_KEY, petOwnerForm);
        photoUploadFragment.setArguments(arguments);
        return photoUploadFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        petForm = getArguments().getParcelable(PET_FORM_KEY);
        petOwnerForm = getArguments().getParcelable(PET_OWENER_FORM_KEY);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_upload_fragment, container, false);
        imageList = new ArrayList<>();
        imagePathList = new ArrayList<>();

        RecyclerView photosView = view.findViewById(R.id.photosView);
        photosView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));

        addCameraBtn = view.findViewById(R.id.btn_camera);
        addCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        addGalleryBtn = view.findViewById(R.id.btn_gallery);
        addGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFromGallery();
            }
        });

        uploadBtn = view.findViewById(R.id.btn_upload);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePetData(petForm, petOwnerForm, imagePathList);
                Toast.makeText(getContext(),"Upload Successful", Toast.LENGTH_SHORT).show();
            }
        });

        photoUploadAdapter = new PhotoUploadAdapter(imageList);
        photosView.setAdapter(photoUploadAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PhotoUploadViewModel.class);
        // TODO: Use the ViewModel
    }

    public void takePicture() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
    }

    public void addFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_LOAD_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap bitmapFromCamera = (Bitmap) extras.get("data");
                    File imageFile = null;
                    try {
                        imageFile = createImageFile();
                        FileOutputStream iOut = new FileOutputStream(imageFile);
                        bitmapFromCamera.compress(Bitmap.CompressFormat.JPEG, 100, iOut);
                        iOut.flush();
                        iOut.close();
                    } catch (IOException ex) {
                        Log.e("IMAGE FILE", ex.toString());
                    }
                    imagePathList.add(imageFile.getAbsolutePath());
                    Log.v("CAMERA PATH", imageFile.getAbsolutePath());
                    addToBitmapList(bitmapFromCamera);
                }
                break;
            case REQUEST_LOAD_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    Bitmap bitmapFromGallery = null;
                    try {
                        bitmapFromGallery = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.v("GALLERY PATH", getPathFromUri(imageUri));
                    imagePathList.add(getPathFromUri(imageUri));
                    addToBitmapList(bitmapFromGallery);
                }
                break;

        }
        photoUploadAdapter.updateArray(imageList);
        photoUploadAdapter.notifyDataSetChanged();
    }

    public void addToBitmapList(Bitmap bitmap) {
        imageList.add(bitmap);
    }

    public String getPathFromUri(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                this.getContext(),
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public void generatePetData(PetForm petForm, PetOwnerForm petOwnerForm, List<String> images) {
        String petName = petForm.petName;
        int petGender = petForm.petGender;
        int petType = petForm.petType;
        int petAge = petForm.petAge;
        int petSize = petForm.petSize;
        String petDesc = petForm.petDesc;
        String ownerName = petOwnerForm.ownerName;
        String ownerAddress = petOwnerForm.ownerAddress;
        String ownerCity = petOwnerForm.ownerCity;
        int ownerProvince = petOwnerForm.ownerProvince;
        String ownerZipCode = petOwnerForm.ownerZipCode;
        String ownerEmail = petOwnerForm.ownerEmail;
        String ownerPhone = petOwnerForm.ownerPhone;
        List<String> petImages = images;
        Address geoAddress = getGeoAddress(ownerAddress + ", " + ownerCity);
        double latitude = geoAddress.getLatitude();
        double longitude = geoAddress.getLongitude();
        String ownerId = ""; //TODO temp ownerID

        PetData petData = new PetData(petName, petDesc, "1", petSize, petImages, ownerPhone,
                ownerAddress, ownerZipCode, latitude, longitude, petAge, petGender, ownerId);
        populatePetData(petData);
    }

    private void populatePetData(PetData petData) {
        PetsRepository petsRepository = new PetsRepository();
        petsRepository.post(petData).observe(getViewLifecycleOwner(), new Observer<Result<Void>>() {
            @Override
            public void onChanged(Result<Void> voidResult) {
                if(voidResult.getException() == null){
                    getActivity().finish();
                }
            }
        });
    }

    private Address getGeoAddress(String ownerAddress){
        Geocoder coder = new Geocoder(getContext());
        List<Address> address;
        Address geoAddress = null;
        try {
            address = coder.getFromLocationName(ownerAddress, 1);
            geoAddress = address.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return geoAddress;
    }
}