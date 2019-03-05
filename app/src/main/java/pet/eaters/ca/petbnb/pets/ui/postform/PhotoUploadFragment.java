package pet.eaters.ca.petbnb.pets.postfrom;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pet.eaters.ca.petbnb.R;

import static android.app.Activity.RESULT_OK;

public class PhotoUploadFragment extends Fragment {

    private PhotoUploadViewModel mViewModel;
    private Button addCameraBtn;
    private Button addGalleryBtn;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_LOAD_GALLERY = 2;

    public static PhotoUploadFragment newInstance() {
        return new PhotoUploadFragment();
    }

    Bitmap[] bitmapArray = new Bitmap[6];
    int nextIndex = 0;
    PhotoUploadAdapter photoUploadAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_upload_fragment, container, false);

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

        photoUploadAdapter = new PhotoUploadAdapter(view.getContext(), bitmapArray);
        photosView.setAdapter(photoUploadAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PhotoUploadViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public Lifecycle getLifecycle() {
        return super.getLifecycle();
    }

    public void takePicture() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    if (resultCode == RESULT_OK) {
                        Bundle extras = data.getExtras();
                        Bitmap bitmapFromCamera = (Bitmap) extras.get("data");
                        addToBitmapArray(bitmapFromCamera);
                    }
                    break;
                case REQUEST_LOAD_GALLERY:
                    if (resultCode == RESULT_OK) {
                        Uri imageUri = data.getData();
                        Bitmap bitmapFromGallery = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        addToBitmapArray(bitmapFromGallery);
                    }
                    break;

            }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        photoUploadAdapter.updateArray(bitmapArray);
        photoUploadAdapter.notifyDataSetChanged();
    }

    public void addFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_LOAD_GALLERY);
    }

    public void addToBitmapArray(Bitmap bitmap) {
        bitmapArray[nextIndex] = bitmap;
        ++nextIndex;
    }
}