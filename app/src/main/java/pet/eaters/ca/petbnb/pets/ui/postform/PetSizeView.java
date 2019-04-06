package pet.eaters.ca.petbnb.pets.ui.postform;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import pet.eaters.ca.petbnb.R;

public class PetSizeView extends View {

    private Paint femalePaint;
    private Paint malePaint;
    private Drawable dog;
    private Drawable cat;
    private Drawable other;

    private int gender = 0;
    private int petType = 0;
    private int petSize = 0;

    private final float[] mPointerPosition = new float[1];
    private PetSizeListener sizeListener;

    public PetSizeView(Context context) {
        super(context);
        init();
    }

    public PetSizeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PetSizeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        femalePaint = getPaintColor(R.color.female_color);
        malePaint = getPaintColor(R.color.male_color);

        dog = getResources().getDrawable(R.drawable.ic_dog);
        cat = getResources().getDrawable(R.drawable.ic_cat);
        other = getResources().getDrawable(R.drawable.ic_other_animal);


        final GestureDetector detector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                mPointerPosition[0] = e2.getX();
                invalidate();
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int h = getHeight();
        int w = getWidth();

        int x = Math.max((int) mPointerPosition[0], 0);
        int petSize = Math.min(h, x + h / 4);

        float slope = ((float) (h / w)) * (w - h) / w;
        int left = Math.min(x, w - petSize);
        int top = Math.max((h - (int) (slope * x)) - petSize, 0);
        int right = Math.min(left + petSize, w);
        int bottom = h;

        Drawable petDrawable = getPetDrawable();

        petDrawable.setBounds(left, top, right, bottom);
        petDrawable.setColorFilter(getPaint().getColor(), PorterDuff.Mode.SRC_IN);
        petDrawable.draw(canvas);

        if (x < firstThird()) {
            setPetSizeInt(0);
        } else if (x < secondThird()) {
            setPetSizeInt(1);
        } else {
            setPetSizeInt(2);
        }
    }

    private int firstThird() {
        return getWidth() / 6;
    }

    private int secondThird() {
        return getWidth() / 3;
    }

    private void setPetSizeInt(int petSize) {
        if (this.petSize != petSize) {
            this.petSize = petSize;
            if (sizeListener != null) {
                sizeListener.onPetSizeChanged(petSize);
            }
        }
    }

    public void setPetSize(int petSize) {
        this.petSize = petSize;
        switch (petSize) {
            case 1:
                mPointerPosition[0] = firstThird() + 1;
                break;
            case 2:
                mPointerPosition[0] = secondThird() + 1;
                break;
            case 0:
            default:
                mPointerPosition[0] = 0;
                break;
        }

        invalidate();
    }

    public void setSizeListener(PetSizeListener sizeListener) {
        this.sizeListener = sizeListener;
    }

    private Paint getPaint() {
        if (gender == 1) {
            return malePaint;
        }
        return femalePaint;
    }

    private Drawable getPetDrawable() {
        switch (petType) {
            case 0:
                return dog;
            case 1:
                return cat;
            default:
                return other;
        }
    }

    public void setGender(int gender) {
        this.gender = gender;
        invalidate();
    }

    public void setPetType(int petType) {
        this.petType = petType;
        invalidate();
    }

    public int getPetSize() {
        return petSize;
    }

    private Paint getPaintColor(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(color));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(40f);

        return paint;
    }

    interface PetSizeListener {
        void onPetSizeChanged(int petSize);
    }
}
