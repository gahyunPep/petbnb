package pet.eaters.ca.petbnb.pets.ui.postform;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.pets.data.ImgUplaodIntentService;
import pet.eaters.ca.petbnb.pets.data.PetData;
import pet.eaters.ca.petbnb.pets.data.PetForm;
import pet.eaters.ca.petbnb.pets.data.PetOwnerForm;
import pet.eaters.ca.petbnb.pets.data.PetsRepository;

import static android.app.Activity.RESULT_OK;

public class PhotoUploadFragment extends Fragment {

    private PhotoUploadViewModel mViewModel;

    private RecyclerView photosRecyclerView;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_LOAD_GALLERY = 2;
    private final int REQUEST_ASK_PERMISSION = 3;

    private PetForm petForm;
    private PetOwnerForm petOwnerForm;

    private List<Bitmap> imageList;
    private List<String> imagePathList;
    private PhotoUploadAdapter photoUploadAdapter = new PhotoUploadAdapter();

    private final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private static final String PET_FORM_KEY = "petForm";
    private static final String PET_OWENER_FORM_KEY = "petOwnerForm";


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

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.pet_photo_form_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                generatePetData(petForm, petOwnerForm, imagePathList);
                return true;
            }
        });

        imageList = new ArrayList<>();
        imagePathList = new ArrayList<>();

        checkPermissions();

        photosRecyclerView = view.findViewById(R.id.photosView);
        photosRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));

        view.findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        view.findViewById(R.id.btn_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFromGallery();
            }
        });


        photoUploadAdapter.updateList(imageList);
        photosRecyclerView.setAdapter(photoUploadAdapter);
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    imageCapture(data);
                    break;
                case REQUEST_LOAD_GALLERY:
                    loadGallery(data);
                    break;
            }
        }
    }

    private void goBack() {
        getActivity().onBackPressed();
    }

    private void loadGallery(Intent data) {
        Uri imageUri = data.getData();
        try {
            Bitmap bitmapFromGallery = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            Log.v("GALLERY PATH", getPathFromUri(imageUri));
            imagePathList.add(getPathFromUri(imageUri));
            addToBitmapList(bitmapFromGallery);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void imageCapture(final Intent data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle extras = data.getExtras();
                Bitmap bitmapFromCamera = (Bitmap) extras.get("data");
                try {
                    File imageFile = createImageFile();
                    FileOutputStream iOut = new FileOutputStream(imageFile);
                    bitmapFromCamera.compress(Bitmap.CompressFormat.JPEG, 100, iOut);
                    iOut.flush();
                    iOut.close();
                    imagePathList.add(imageFile.getAbsolutePath());
                    final Bitmap bitmap = bitmapFromCamera;
                    photosRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            addToBitmapList(bitmap);
                        }
                    });
                } catch (IOException ex) {
                    Log.e("IMAGE FILE", ex.toString());
                }
            }
        }).start();
    }

    public void addToBitmapList(Bitmap bitmap) {
        imageList.add(bitmap);
        photoUploadAdapter.updateList(imageList);
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
        File storageDir = this.getContext().getFilesDir();

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
        String petType = petForm.petType + "";
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

        Pair<Double, Double> geoAddress = getGeoAddress(ownerAddress + ", " + ownerCity);
        double latitude = geoAddress.first;
        double longitude = geoAddress.second;
        String ownerId = ""; //TODO temp ownerID

        PetData petData = new PetData(petName, petDesc, petType, petSize, petImages, ownerPhone,
                ownerAddress, ownerZipCode, latitude, longitude, petAge, petGender, ownerId);
        populatePetData(petData);
    }

    private void populatePetData(PetData petData) {
        final String PET_ID_KEY = "petID";
        final String PET_IMG_KEY = "petImgs";
        final List<String> images = petData.getImages();
        List<String> emptyImgs = new ArrayList<>();
        petData.setImages(emptyImgs);
        PetsRepository petsRepository = new PetsRepository();
        petsRepository.post(petData).observe(getViewLifecycleOwner(), new Observer<Result<String>>() {
            @Override
            public void onChanged(Result<String> stringResult) {
                if (stringResult.getException() == null) {
                    String petId = stringResult.getData();
                    Intent uploadIntent = new Intent(getContext(), ImgUplaodIntentService.class);
                    uploadIntent.putExtra(PET_ID_KEY, petId);
                    uploadIntent.putStringArrayListExtra(PET_IMG_KEY, (ArrayList<String>) images);
                    getContext().startService(uploadIntent);
                    getActivity().finish();
                }
            }
        });
    }

    private Pair<Double, Double> getGeoAddress(String ownerAddress) {
        try {
            Geocoder coder = new Geocoder(getContext());
            List<Address> addresses = coder.getFromLocationName(ownerAddress, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new Pair<>(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException ignored) { }

        return new Pair<>(49.203664, -122.912863);
    }

    // Source: https://developer.here.com/documentation/android-premium/dev_guide/topics/request-android-permissions.html
    private void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<>();

        for (String permission : PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(getContext(), permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        if (!missingPermissions.isEmpty()) {
            final String[] permissions = missingPermissions.toArray(new String[missingPermissions.size()]);
            requestPermissions(permissions, REQUEST_ASK_PERMISSION);
        } else {
            final int[] grantResults = new int[PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_ASK_PERMISSION, PERMISSIONS, grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ASK_PERMISSION:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit fragment if permission not granted
                        Toast.makeText(getContext(), "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                // all permissions granted
                return;
        }
    }
}