package pet.eaters.ca.petbnb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ImageSliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public int[] lst_images = {
            R.drawable.pet, R.drawable.pet2, R.drawable.pet3
    };

    public ImageSliderAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return lst_images.length;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(container.getContext().LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.image_slider, container, false);
        ImageView imgSlide = view.findViewById(R.id.pet_image_view);
        imgSlide.setImageResource(lst_images[position]);
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (LinearLayout) object);
    }
}
