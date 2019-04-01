package pet.eaters.ca.petbnb.pets.ui.details;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import pet.eaters.ca.petbnb.R;

public class ViewPagerAdapter extends PagerAdapter {
    private List<String> viewPagerImages;
    public ViewPagerAdapter(List<String> viewPagerImages) {
        this.viewPagerImages = viewPagerImages;
    }

    @Override
    public int getCount() {
        int size = viewPagerImages.size();
        if (size == 0) {
            return 1;
        }

        return size;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        ImageView imageSlide = (ImageView) inflater.inflate(R.layout.view_pager_item, container, false);
        imageSlide.setImageResource(R.drawable.ic_image_black_24dp);

        if (!viewPagerImages.isEmpty()) {
            Glide.with(container.getContext())
                    .load(viewPagerImages.get(position))
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .into(imageSlide);
        }

        container.addView(imageSlide);
        return imageSlide;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
