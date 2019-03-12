package pet.eaters.ca.petbnb.pets.ui.postform;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import pet.eaters.ca.petbnb.R;

public class PhotoUploadAdapter extends RecyclerView.Adapter<PhotoUploadAdapter.ViewHolder> {
    private List<Bitmap> bitmapList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView photoImageView;
        public TextView photoTextView;

        public ViewHolder(View v) {
            super(v);
            photoImageView = v.findViewById(R.id.photo_item);
            photoTextView = v.findViewById(R.id.photo_text);
        }
    }

    public PhotoUploadAdapter(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.photoImageView.setImageBitmap(bitmapList.get(position));
        holder.photoTextView.setText(Integer.toString(position + 1));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(view);
    }

    public void updateArray(List<Bitmap> photosList) {
        this.bitmapList = photosList;
        notifyDataSetChanged();
    }
}