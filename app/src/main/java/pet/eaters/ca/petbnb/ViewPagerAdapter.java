package pet.eaters.ca.petbnb;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {
    Activity activity;
    LayoutInflater inflater;
    public int[] viewPagerImages;

    public ViewPagerAdapter(Activity activity, int[] viewPagerImages) {
        this.activity = activity;
        this.viewPagerImages = viewPagerImages;
    }

    @Override
    public int getCount() {
        return viewPagerImages.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.view_pager_item, container, false);

        ImageView imgSlide = itemView.findViewById(R.id.pet_image_view);
        DisplayMetrics dis = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dis);

        imgSlide.setMinimumHeight(dis.heightPixels);
        imgSlide.setMinimumWidth(dis.widthPixels);
        imgSlide.setImageResource(viewPagerImages[position]);
        container.addView(itemView);
        return itemView;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }




}
