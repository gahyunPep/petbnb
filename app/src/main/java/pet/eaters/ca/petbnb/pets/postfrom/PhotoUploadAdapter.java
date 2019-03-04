package pet.eaters.ca.petbnb.pets.postfrom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import pet.eaters.ca.petbnb.R;

public class PhotoUploadAdapter extends RecyclerView.Adapter<PhotoUploadAdapter.ViewHolder> {
    private Bitmap[] dataSet;
    private LayoutInflater mInflater;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView photo;

        public ViewHolder(View v) {
            super(v);
            photo = v.findViewById(R.id.photo_item);
        }
    }

    public PhotoUploadAdapter(Context context, Bitmap[] myDataset) {
        this.mInflater = LayoutInflater.from(context);
        dataSet = myDataset;
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.photo.setImageBitmap(dataSet[position]);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(view);
    }

    public void updateArray(Bitmap[] photos_array){
        this.dataSet = photos_array;
        notifyDataSetChanged();
    }
}