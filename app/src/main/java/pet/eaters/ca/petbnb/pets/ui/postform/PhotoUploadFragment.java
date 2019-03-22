package pet.eaters.ca.petbnb.pets.ui.postform;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pet.eaters.ca.petbnb.R;
import pet.eaters.ca.petbnb.pets.data.PhotoStorage;

import static android.app.Activity.RESULT_OK;

public class PhotoUploadFragment extends Fragment {

    private PhotoUploadViewModel mViewModel;
    private Button addCameraBtn;
    private Button addGalleryBtn;
    private Button uploadBtn;
    private RecyclerView photosRecyclerView;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_LOAD_GALLERY = 2;
    private final int REQUEST_ASK_PERMISSION = 3;

    private List<Bitmap> imageList;
    private List<String> imagePathList;
    private PhotoUploadAdapter photoUploadAdapter = new PhotoUploadAdapter();

    private final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    public static PhotoUploadFragment newInstance() {
        return new PhotoUploadFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_upload_fragment, container, false);
        imageList = new ArrayList<>();
        imagePathList = new ArrayList<>();

        checkPermissions();

        photosRecyclerView = view.findViewById(R.id.photosView);
        photosRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));

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
                uploadToFireStore();
                Toast.makeText(getContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
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

    public void uploadToFireStore() {
        String samplePetId = "t5jVufrc7Nj1MJXZqjLs";
        PhotoStorage photoStorage = new PhotoStorage();
        photoStorage.uploadFiles(imagePathList, samplePetId);
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