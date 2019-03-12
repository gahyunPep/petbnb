package pet.eaters.ca.petbnb.pets.ui.postform;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import pet.eaters.ca.petbnb.R;

public class PhotoUploadAdapter extends RecyclerView.Adapter<PhotoUploadAdapter.ViewHolder> {
    private Bitmap[] bitmapArray;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView photoImageView;

        public ViewHolder(View v) {
            super(v);
            photoImageView = v.findViewById(R.id.photo_item);
        }
    }

    public PhotoUploadAdapter(Bitmap[] bitmapArray) {
        this.bitmapArray = bitmapArray;
    }

    @Override
    public int getItemCount() {
        return bitmapArray.length;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.photoImageView.setImageBitmap(bitmapArray[position]);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(view);
    }

    public void updateArray(Bitmap[] photos_array) {
        this.bitmapArray = photos_array;
        notifyDataSetChanged();
    }
}