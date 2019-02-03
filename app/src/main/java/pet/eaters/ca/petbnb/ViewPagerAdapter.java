package pet.eaters.ca.petbnb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {

    public int[] viewPagerImages;
    public ViewPagerAdapter(int[] viewPagerImages) {

        this.viewPagerImages = viewPagerImages;
    }

    @Override
    public int getCount() {
        return viewPagerImages.length;
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
        imageSlide.setImageResource(viewPagerImages[position]);
        container.addView(imageSlide);
        return imageSlide;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
