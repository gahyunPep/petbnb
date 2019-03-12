package pet.eaters.ca.petbnb.pets.ui.postform;

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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    List<Bitmap> bitmapList;
    PhotoUploadAdapter photoUploadAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_upload_fragment, container, false);
        bitmapList = new ArrayList<>();

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

        photoUploadAdapter = new PhotoUploadAdapter(bitmapList);
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
                    addToBitmapList(bitmapFromGallery);
                }
                break;

        }
        photoUploadAdapter.updateArray(bitmapList);
        photoUploadAdapter.notifyDataSetChanged();
    }

    public void addToBitmapList(Bitmap bitmap) {
        bitmapList.add(bitmap);
    }
}