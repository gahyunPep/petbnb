package pet.eaters.ca.petbnb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {


    private List<String> viewPagerImages;
    private Context context;
    public ViewPagerAdapter(List<String> viewPagerImages, Context context) {

        this.viewPagerImages = viewPagerImages;
        this.context = context;
    }

    @Override
    public int getCount() {
        return viewPagerImages.size();
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

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.image_error);
        requestOptions.error(R.drawable.image_error);

        Glide.with(context).setDefaultRequestOptions(requestOptions).
                load(viewPagerImages.get(position)).into(imageSlide);

        container.addView(imageSlide);
        return imageSlide;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
